package com.druglogger.app;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Checks for medication safety warnings before logging a dose.
 *
 * Warnings:
 * 1. Too soon since last dose (based on time_required_between_doses)
 * 2. Exceeding maximum daily dosage
 * 3. Concerning drug interactions with other recently logged medications
 * 4. Colestipol was taken less than 4 hours ago (absorption interference)
 */
public class WarningChecker {

    /**
     * Represents a previously logged medication entry with timing info.
     */
    public static class RecentLog {
        public final String medicationName;
        public final double dose;
        public final long timestampMillis;

        public RecentLog(String medicationName, double dose, long timestampMillis) {
            this.medicationName = medicationName;
            this.dose = dose;
            this.timestampMillis = timestampMillis;
        }
    }

    /**
     * Check all warnings for a proposed medication dose.
     *
     * @param medication  The medication to be logged
     * @param dose        The dose to be logged
     * @param recentLogs  Recent log entries (from Google Sheets and/or local DB)
     * @param allMedications All medications from medlist.json (for interaction checks)
     * @return List of warning messages (empty if no warnings)
     */
    public static List<String> checkWarnings(
            MedicationInfo medication,
            double dose,
            List<RecentLog> recentLogs,
            List<MedicationInfo> allMedications) {

        List<String> warnings = new ArrayList<>();
        long now = System.currentTimeMillis();

        // 1. Check "too soon since last dose"
        checkTooSoon(medication, recentLogs, now, warnings);

        // 2. Check "exceed maximum daily dose"
        checkMaxDailyDose(medication, dose, recentLogs, now, warnings);

        // 3. Check "concerning interactions" with other recently taken meds
        checkInteractions(medication, recentLogs, allMedications, warnings);

        // 4. Check "colestipol taken less than 4 hours ago"
        checkColestipolTiming(medication, recentLogs, now, warnings);

        return warnings;
    }

    private static void checkTooSoon(MedicationInfo medication,
                                     List<RecentLog> recentLogs,
                                     long now,
                                     List<String> warnings) {
        String timeBetween = medication.getTimeRequiredBetweenDoses();
        if (timeBetween == null || timeBetween.isEmpty()) return;

        double requiredHours = parseHoursFromTimeString(timeBetween);
        if (requiredHours <= 0) return;

        long requiredMillis = (long) (requiredHours * 3600_000L);
        String medName = medication.getGenericName().toLowerCase();

        for (RecentLog log : recentLogs) {
            if (namesMatch(log.medicationName.toLowerCase(), medName)) {
                long elapsed = now - log.timestampMillis;
                if (elapsed < requiredMillis && elapsed >= 0) {
                    double hoursAgo = elapsed / 3600_000.0;
                    warnings.add(String.format(
                            "⚠ Too soon: You last took %s %.1f hours ago. Recommended interval: %s.",
                            medication.getGenericName(),
                            hoursAgo,
                            timeBetween));
                    return;
                }
            }
        }
    }

    private static void checkMaxDailyDose(MedicationInfo medication,
                                           double proposedDose,
                                           List<RecentLog> recentLogs,
                                           long now,
                                           List<String> warnings) {
        String maxDaily = medication.getMaximumDailyDosage();
        if (maxDaily == null || maxDaily.isEmpty()) return;

        double maxDose = parseFirstNumber(maxDaily);
        if (maxDose <= 0) return;

        long oneDayAgo = now - 24 * 3600_000L;
        String medName = medication.getGenericName().toLowerCase();
        double totalToday = proposedDose;

        for (RecentLog log : recentLogs) {
            if (log.timestampMillis >= oneDayAgo) {
                if (namesMatch(log.medicationName.toLowerCase(), medName)) {
                    totalToday += log.dose;
                }
            }
        }

        if (totalToday > maxDose) {
            warnings.add(String.format(
                    "⚠ Exceeds max daily dose: This dose would bring your 24h total to %.1f %s. Maximum: %s.",
                    totalToday,
                    medication.getDoseUnit(),
                    maxDaily));
        }
    }

    private static void checkInteractions(MedicationInfo medication,
                                           List<RecentLog> recentLogs,
                                           List<MedicationInfo> allMedications,
                                           List<String> warnings) {
        if (medication.getInteractions() == null || medication.getInteractions().isEmpty()) return;

        // Determine which medications have been taken recently (last 24h)
        long oneDayAgo = System.currentTimeMillis() - 24 * 3600_000L;
        List<String> recentMedNames = new ArrayList<>();
        for (RecentLog log : recentLogs) {
            if (log.timestampMillis >= oneDayAgo) {
                recentMedNames.add(log.medicationName.toLowerCase());
            }
        }

        for (MedicationInfo.Interaction interaction : medication.getInteractions()) {
            String interactingDrug = interaction.getDrug().toLowerCase();
            for (String recentMed : recentMedNames) {
                // Check if the recently taken med matches the interacting drug
                if (namesMatch(recentMed, interactingDrug)) {
                    warnings.add(String.format(
                            "⚠ Interaction with %s: %s",
                            interaction.getDrug(),
                            interaction.getInteraction()));
                    break;
                }
            }
        }
    }

    private static void checkColestipolTiming(MedicationInfo medication,
                                               List<RecentLog> recentLogs,
                                               long now,
                                               List<String> warnings) {
        // If the user is taking colestipol itself, no need for this warning
        if (medication.getGenericName().toLowerCase().contains("colestipol")) return;

        long fourHoursAgo = now - 4 * 3600_000L;

        for (RecentLog log : recentLogs) {
            if (log.medicationName.toLowerCase().contains("colestipol")) {
                if (log.timestampMillis >= fourHoursAgo && log.timestampMillis <= now) {
                    double hoursAgo = (now - log.timestampMillis) / 3600_000.0;
                    warnings.add(String.format(
                            "⚠ Colestipol was taken %.1f hours ago. It can reduce absorption of other medications. "
                                    + "Wait at least 4 hours after colestipol before taking other oral medications.",
                            hoursAgo));
                    return;
                }
            }
        }
    }

    /** Attempt to parse the first number of hours from a time description string. */
    static double parseHoursFromTimeString(String s) {
        if (s == null) return 0;
        String lower = s.toLowerCase();

        // Look for "X hours" or "X-Y hours" patterns
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+)?)(?:\\s*-\\s*(\\d+(?:\\.\\d+)?))?\\s*hour");
        Matcher m = p.matcher(lower);
        if (m.find()) {
            double first = Double.parseDouble(m.group(1));
            if (m.group(2) != null) {
                // Use the lower bound of the range for safety
                return first;
            }
            return first;
        }

        return 0;
    }

    /** Extract the first number from a string like "72 mg/day" -> 72.0 */
    static double parseFirstNumber(String s) {
        if (s == null) return 0;
        Pattern p = Pattern.compile("(\\d+(?:\\.\\d+)?)");
        Matcher m = p.matcher(s);
        if (m.find()) {
            return Double.parseDouble(m.group(1));
        }
        return 0;
    }

    /** Check if two medication names refer to the same drug (fuzzy matching). */
    private static boolean namesMatch(String a, String b) {
        if (a.equals(b)) return true;

        // Extract first significant word (generic name root) for comparison
        String aRoot = extractFirstWord(a);
        String bRoot = extractFirstWord(b);

        // Require substantial match: roots must be >= 4 chars and equal
        if (aRoot.length() >= 4 && bRoot.length() >= 4) {
            if (aRoot.equals(bRoot)) return true;
        }

        // Check if one fully contains the other (for names like "Colestipol HCl" vs "colestipol")
        if (a.equals(b) || a.startsWith(b + " ") || b.startsWith(a + " ")) return true;

        return false;
    }

    private static String extractFirstWord(String name) {
        if (name == null || name.trim().isEmpty()) return "";
        return name.trim().split("\\s+")[0];
    }
}
