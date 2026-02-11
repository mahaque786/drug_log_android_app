package com.druglogger.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private DrugEntryAdapter adapter;
    private DrugLogDatabase database;
    private List<DrugEntry> entries;
    private FloatingActionButton addButton;

    private MedListRepository medListRepository;
    private GoogleSheetsLogger sheetsLogger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DrugLogDatabase(this);
        entries = new ArrayList<>();
        medListRepository = new MedListRepository(this);
        sheetsLogger = new GoogleSheetsLogger();

        recyclerView = findViewById(R.id.recycler_view);
        addButton = findViewById(R.id.add_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DrugEntryAdapter(entries, this::deleteEntry);
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showLogMedicationDialog());

        loadEntries();
    }

    private void showLogMedicationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_log_medication, null);

        Spinner medSpinner = dialogView.findViewById(R.id.medication_spinner);
        Spinner doseSpinner = dialogView.findViewById(R.id.dose_spinner);
        Spinner reasonSpinner = dialogView.findViewById(R.id.reason_spinner);

        // Populate medication spinner
        List<String> medNames = medListRepository.getMedicationNames();
        medNames.add(0, "-- Select Medication --");
        ArrayAdapter<String> medAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, medNames);
        medAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        medSpinner.setAdapter(medAdapter);

        // When medication is selected, update dose and reason spinners
        medSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    doseSpinner.setAdapter(new ArrayAdapter<>(
                            MainActivity.this, android.R.layout.simple_spinner_item,
                            new String[]{"-- Select Dose --"}));
                    reasonSpinner.setAdapter(new ArrayAdapter<>(
                            MainActivity.this, android.R.layout.simple_spinner_item,
                            new String[]{"-- Select Reason --"}));
                    return;
                }

                MedicationInfo med = medListRepository.findByDisplayName(medNames.get(position));
                if (med == null) return;

                // Populate dose spinner
                List<String> doseOptions = new ArrayList<>();
                doseOptions.add("-- Select Dose --");
                for (Double d : med.getDoses()) {
                    String formatted = (d == Math.floor(d)) ?
                            String.valueOf((int) d.doubleValue()) : String.valueOf(d);
                    doseOptions.add(formatted + " " + med.getDoseUnit());
                }
                ArrayAdapter<String> doseAdapter = new ArrayAdapter<>(
                        MainActivity.this, android.R.layout.simple_spinner_item, doseOptions);
                doseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                doseSpinner.setAdapter(doseAdapter);

                // Populate reason spinner
                List<String> reasons = new ArrayList<>();
                reasons.add("-- Select Reason --");
                reasons.addAll(med.getAllReasons());
                ArrayAdapter<String> reasonAdapter = new ArrayAdapter<>(
                        MainActivity.this, android.R.layout.simple_spinner_item, reasons);
                reasonAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                reasonSpinner.setAdapter(reasonAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        builder.setView(dialogView)
                .setTitle("Log Medication")
                .setPositiveButton("Log", null) // Set to null; we override below
                .setNegativeButton("Cancel", null)
                .create();

        AlertDialog dialog = builder.create();
        dialog.show();

        // Override positive button to prevent auto-dismiss on validation failure
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            int medPos = medSpinner.getSelectedItemPosition();
            int dosePos = doseSpinner.getSelectedItemPosition();
            int reasonPos = reasonSpinner.getSelectedItemPosition();

            if (medPos == 0) {
                Toast.makeText(this, "Please select a medication", Toast.LENGTH_SHORT).show();
                return;
            }
            if (dosePos == 0) {
                Toast.makeText(this, "Please select a dose", Toast.LENGTH_SHORT).show();
                return;
            }
            if (reasonPos == 0) {
                Toast.makeText(this, "Please select a reason", Toast.LENGTH_SHORT).show();
                return;
            }

            MedicationInfo selectedMed = medListRepository.findByDisplayName(
                    medNames.get(medPos));
            if (selectedMed == null) return;

            double selectedDose = selectedMed.getDoses().get(dosePos - 1);
            String selectedReason = selectedMed.getAllReasons().get(reasonPos - 1);

            // Build recent logs for warning checks from local database
            List<WarningChecker.RecentLog> recentLogs = buildRecentLogs();

            List<String> warnings = WarningChecker.checkWarnings(
                    selectedMed, selectedDose, recentLogs,
                    medListRepository.getAllMedications());

            if (!warnings.isEmpty()) {
                showWarningDialog(selectedMed, selectedDose, selectedReason, warnings, dialog);
            } else {
                proceedWithLogging(selectedMed, selectedDose, selectedReason, dialog);
            }
        });
    }

    private void showWarningDialog(MedicationInfo med, double dose, String reason,
                                   List<String> warnings, AlertDialog parentDialog) {
        StringBuilder message = new StringBuilder();
        for (String w : warnings) {
            message.append(w).append("\n\n");
        }
        message.append("Do you want to proceed anyway?");

        new AlertDialog.Builder(this)
                .setTitle("⚠ Safety Warnings")
                .setMessage(message.toString())
                .setPositiveButton("Proceed Anyway", (d, which) -> {
                    proceedWithLogging(med, dose, reason, parentDialog);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void proceedWithLogging(MedicationInfo med, double dose, String reason,
                                    AlertDialog parentDialog) {
        String medName = med.getSheetName();
        String doseStr = ((dose == Math.floor(dose)) ?
                String.valueOf((int) dose) : String.valueOf(dose)) + " " + med.getDoseUnit();

        // Save locally
        DrugEntry entry = new DrugEntry(med.getGenericName(), doseStr, reason);
        long id = database.addEntry(entry);
        entry.setId(id);
        loadEntries();

        // Log to Google Sheets
        sheetsLogger.logMedication(medName, dose, reason,
                new GoogleSheetsLogger.LogCallback() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this,
                                "Logged to Google Sheets", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(String errorMessage) {
                        Toast.makeText(MainActivity.this,
                                "Saved locally. Sheets sync failed: " + errorMessage,
                                Toast.LENGTH_LONG).show();
                    }
                });

        Toast.makeText(this, "Medication logged", Toast.LENGTH_SHORT).show();
        parentDialog.dismiss();
    }

    /**
     * Build recent logs from the local database for warning checks.
     */
    private List<WarningChecker.RecentLog> buildRecentLogs() {
        List<WarningChecker.RecentLog> recentLogs = new ArrayList<>();
        List<DrugEntry> allEntries = database.getAllEntries();
        for (DrugEntry e : allEntries) {
            double dose = parseDoseFromString(e.getDosage());
            recentLogs.add(new WarningChecker.RecentLog(
                    e.getDrugName(), dose, e.getTimestamp()));
        }
        return recentLogs;
    }

    /** Parse numeric dose from a string like "50 mg" -> 50.0 */
    private double parseDoseFromString(String doseStr) {
        if (doseStr == null || doseStr.isEmpty()) return 0;
        try {
            String numPart = doseStr.replaceAll("[^\\d.]", "");
            return numPart.isEmpty() ? 0 : Double.parseDouble(numPart);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void deleteEntry(DrugEntry entry) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    database.deleteEntry(entry.getId());
                    loadEntries();
                    Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void loadEntries() {
        entries = database.getAllEntries();
        adapter.updateEntries(entries);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (database != null) {
            database.close();
        }
    }
}
