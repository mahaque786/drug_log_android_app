package com.druglogger.app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = new DrugLogDatabase(this);
        entries = new ArrayList<>();

        recyclerView = findViewById(R.id.recycler_view);
        addButton = findViewById(R.id.add_button);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DrugEntryAdapter(entries, this::deleteEntry);
        recyclerView.setAdapter(adapter);

        addButton.setOnClickListener(v -> showAddEntryDialog());

        loadEntries();
    }

    private void showAddEntryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_entry, null);

        EditText drugNameInput = dialogView.findViewById(R.id.drug_name_input);
        EditText dosageInput = dialogView.findViewById(R.id.dosage_input);
        EditText notesInput = dialogView.findViewById(R.id.notes_input);

        builder.setView(dialogView)
                .setTitle("Add Drug Entry")
                .setPositiveButton("Add", (dialog, which) -> {
                    String drugName = drugNameInput.getText().toString().trim();
                    String dosage = dosageInput.getText().toString().trim();
                    String notes = notesInput.getText().toString().trim();

                    if (drugName.isEmpty()) {
                        Toast.makeText(this, "Drug name is required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    DrugEntry entry = new DrugEntry(drugName, dosage, notes);
                    long id = database.addEntry(entry);
                    entry.setId(id);

                    loadEntries();
                    Toast.makeText(this, "Entry added", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
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
