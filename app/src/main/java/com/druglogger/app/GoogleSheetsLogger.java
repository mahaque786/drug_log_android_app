package com.druglogger.app;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Sends medication log entries to the Google Apps Script web app
 * and retrieves existing logs.
 */
public class GoogleSheetsLogger {

    private static final String SCRIPT_URL =
            "https://script.google.com/macros/s/AKfycbwNeAFxg6IpfzQf9iDxAx3spavPA0cwNvwvAEFKdBEgt4OmndJREHGT10TOVUVFt4Nsbg/exec";

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface LogCallback {
        void onSuccess();
        void onError(String errorMessage);
    }

    public interface FetchLogsCallback {
        void onSuccess(List<LogEntry> logs);
        void onError(String errorMessage);
    }

    public static class LogEntry {
        public final String timestamp;
        public final String medicationName;
        public final double dose;
        public final String reason;

        public LogEntry(String timestamp, String medicationName, double dose, String reason) {
            this.timestamp = timestamp;
            this.medicationName = medicationName;
            this.dose = dose;
            this.reason = reason;
        }
    }

    /**
     * Posts a medication log entry to the Google Sheet.
     */
    public void logMedication(String medicationName, double dose, String reason, LogCallback callback) {
        executor.execute(() -> {
            try {
                JSONObject body = new JSONObject();
                body.put("action", "log");
                body.put("medicationName", medicationName);
                body.put("dose", dose);
                body.put("reason", reason);

                String response = doPost(body.toString());
                JSONObject result = new JSONObject(response);

                if (result.optBoolean("success", false)) {
                    mainHandler.post(callback::onSuccess);
                } else {
                    String error = result.optString("error", "Unknown error");
                    mainHandler.post(() -> callback.onError(error));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    /**
     * Fetches existing logs from the Google Sheet.
     */
    public void fetchLogs(FetchLogsCallback callback) {
        executor.execute(() -> {
            try {
                String response = doGet("?action=logs");
                JSONObject result = new JSONObject(response);

                if (result.optBoolean("success", false)) {
                    JSONArray logsArr = result.getJSONArray("logs");
                    List<LogEntry> logs = new ArrayList<>();
                    for (int i = 0; i < logsArr.length(); i++) {
                        JSONObject log = logsArr.getJSONObject(i);
                        logs.add(new LogEntry(
                                log.optString("timestamp", ""),
                                log.optString("medicationName", ""),
                                log.optDouble("dose", 0),
                                log.optString("reason", "")
                        ));
                    }
                    mainHandler.post(() -> callback.onSuccess(logs));
                } else {
                    String error = result.optString("error", "Unknown error");
                    mainHandler.post(() -> callback.onError(error));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    private String doPost(String jsonBody) throws Exception {
        URL url = new URL(SCRIPT_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
        }

        return readResponse(conn);
    }

    private String doGet(String queryString) throws Exception {
        URL url = new URL(SCRIPT_URL + queryString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setInstanceFollowRedirects(true);
        conn.setRequestMethod("GET");

        return readResponse(conn);
    }

    private String readResponse(HttpURLConnection conn) throws Exception {
        // Follow redirects for Google Apps Script (HTTP -> HTTPS redirect)
        int status = conn.getResponseCode();
        if (status == HttpURLConnection.HTTP_MOVED_TEMP
                || status == HttpURLConnection.HTTP_MOVED_PERM
                || status == HttpURLConnection.HTTP_SEE_OTHER) {
            String newUrl = conn.getHeaderField("Location");
            conn.disconnect();
            URL redirectUrl = new URL(newUrl);
            HttpsURLConnection redirectConn = (HttpsURLConnection) redirectUrl.openConnection();
            redirectConn.setRequestMethod("GET");
            conn = redirectConn;
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            conn.disconnect();
        }
        return sb.toString();
    }
}
