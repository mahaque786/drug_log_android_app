package com.druglogger.app;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and provides access to medication data from medlist.json in assets.
 */
public class MedListRepository {

    private final List<MedicationInfo> medications;

    public MedListRepository(Context context) {
        medications = new ArrayList<>();
        try {
            InputStream is = context.getAssets().open("medlist.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            JSONObject root = new JSONObject(json);
            JSONArray medsArray = root.getJSONArray("medications");
            for (int i = 0; i < medsArray.length(); i++) {
                medications.add(MedicationInfo.fromJson(medsArray.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<MedicationInfo> getAllMedications() {
        return medications;
    }

    /** Returns medication names suitable for display in a spinner. */
    public List<String> getMedicationNames() {
        List<String> names = new ArrayList<>();
        for (MedicationInfo med : medications) {
            names.add(med.getDisplayName());
        }
        return names;
    }

    /** Find a MedicationInfo by its display name. */
    public MedicationInfo findByDisplayName(String displayName) {
        for (MedicationInfo med : medications) {
            if (med.getDisplayName().equals(displayName)) {
                return med;
            }
        }
        return null;
    }

    /** Find a MedicationInfo by its generic name (case-insensitive partial match). */
    public MedicationInfo findByGenericName(String genericName) {
        for (MedicationInfo med : medications) {
            if (med.getGenericName().equalsIgnoreCase(genericName)) {
                return med;
            }
        }
        // Try partial match
        String lower = genericName.toLowerCase();
        for (MedicationInfo med : medications) {
            if (med.getGenericName().toLowerCase().contains(lower)) {
                return med;
            }
        }
        return null;
    }
}
