package com.druglogger.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DrugEntry {
    private long id;
    private String drugName;
    private String dosage;
    private String notes;
    private long timestamp;

    public DrugEntry(long id, String drugName, String dosage, String notes, long timestamp) {
        this.id = id;
        this.drugName = drugName;
        this.dosage = dosage;
        this.notes = notes;
        this.timestamp = timestamp;
    }

    public DrugEntry(String drugName, String dosage, String notes) {
        this.drugName = drugName;
        this.dosage = dosage;
        this.notes = notes;
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDrugName() {
        return drugName;
    }

    public void setDrugName(String drugName) {
        this.drugName = drugName;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
