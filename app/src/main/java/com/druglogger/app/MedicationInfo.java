package com.druglogger.app;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing a medication entry from medlist.json.
 */
public class MedicationInfo {
    private String genericName;
    private List<String> brandNames;
    private List<String> onLabelUses;
    private List<String> offLabelUses;
    private List<Double> doses;
    private String doseUnit;
    private String maximumDailyDosage;
    private String timeRequiredBetweenDoses;
    private String halfLife;
    private List<Interaction> interactions;

    public static class Interaction {
        private String drug;
        private String interaction;

        public Interaction(String drug, String interaction) {
            this.drug = drug;
            this.interaction = interaction;
        }

        public String getDrug() { return drug; }
        public String getInteraction() { return interaction; }
    }

    public static MedicationInfo fromJson(JSONObject json) throws JSONException {
        MedicationInfo info = new MedicationInfo();
        info.genericName = json.getString("generic_name");
        info.brandNames = jsonArrayToStringList(json.getJSONArray("brand_names"));

        JSONObject indication = json.getJSONObject("indication");
        info.onLabelUses = jsonArrayToStringList(indication.getJSONArray("on_label"));
        info.offLabelUses = jsonArrayToStringList(indication.getJSONArray("off_label"));

        JSONArray dosesArr = json.getJSONArray("doses");
        info.doses = new ArrayList<>();
        for (int i = 0; i < dosesArr.length(); i++) {
            info.doses.add(dosesArr.getDouble(i));
        }

        info.doseUnit = json.optString("dose_unit", "mg");
        info.maximumDailyDosage = json.optString("maximum_daily_dosage", "");
        info.timeRequiredBetweenDoses = json.optString("time_required_between_doses", "");
        info.halfLife = json.optString("half_life", "");

        info.interactions = new ArrayList<>();
        JSONArray interArr = json.optJSONArray("interactions_with_other_drugs_on_this_list");
        if (interArr != null) {
            for (int i = 0; i < interArr.length(); i++) {
                JSONObject inter = interArr.getJSONObject(i);
                info.interactions.add(new Interaction(
                        inter.getString("drug"),
                        inter.getString("interaction")
                ));
            }
        }

        return info;
    }

    private static List<String> jsonArrayToStringList(JSONArray arr) throws JSONException {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            list.add(arr.getString(i));
        }
        return list;
    }

    /** Returns a display name like "Methylphenidate HCl (Extended-Release) (Concerta, Ritalin LA)" */
    public String getDisplayName() {
        StringBuilder sb = new StringBuilder(genericName);
        if (brandNames != null && !brandNames.isEmpty()) {
            sb.append(" (");
            sb.append(brandNames.get(0));
            if (brandNames.size() > 1) {
                sb.append(", ...");
            }
            sb.append(")");
        }
        return sb.toString();
    }

    /** Returns medication name in the format expected by the Google Sheets logger. */
    public String getSheetName() {
        StringBuilder sb = new StringBuilder(genericName);
        if (brandNames != null && !brandNames.isEmpty()) {
            sb.append(" [");
            for (int i = 0; i < brandNames.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(brandNames.get(i));
            }
            sb.append("]");
        }
        return sb.toString();
    }

    public String getGenericName() { return genericName; }
    public List<String> getBrandNames() { return brandNames; }
    public List<String> getOnLabelUses() { return onLabelUses; }
    public List<String> getOffLabelUses() { return offLabelUses; }
    public List<Double> getDoses() { return doses; }
    public String getDoseUnit() { return doseUnit; }
    public String getMaximumDailyDosage() { return maximumDailyDosage; }
    public String getTimeRequiredBetweenDoses() { return timeRequiredBetweenDoses; }
    public String getHalfLife() { return halfLife; }
    public List<Interaction> getInteractions() { return interactions; }

    /** Returns all reasons (on-label + off-label) combined. */
    public List<String> getAllReasons() {
        List<String> all = new ArrayList<>();
        for (String s : onLabelUses) {
            all.add("[On-label] " + s);
        }
        for (String s : offLabelUses) {
            all.add("[Off-label] " + s);
        }
        return all;
    }
}
