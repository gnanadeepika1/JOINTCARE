package com.example.myjoints;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InvestigationHistoryActivity extends AppCompatActivity {

    private static final String TAG = "InvestHistory";

    private ImageView backBtn;
    private TextView tvPatientId;
    private RecyclerView rvInvestigations;
    private FloatingActionButton btnAddInvestigation;

    private final List<InvestigationItem> items = new ArrayList<>();
    private InvestigationHistoryAdapter adapter;
    private String patientId;

    // Doctor: use SAME backend as patient
    private static final String BASE_URL               = "http://172.25.109.58/jointcare/";
    private static final String ADD_INVESTIGATION_URL  = BASE_URL + "add_investigation.php";
    private static final String GET_INVESTIGATIONS_URL = BASE_URL + "get_investigations.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_investigation_history);

        backBtn             = findViewById(R.id.back_btn);
        tvPatientId         = findViewById(R.id.tvPatientId);
        rvInvestigations    = findViewById(R.id.rvInvestigations);
        btnAddInvestigation = findViewById(R.id.btnAddInvestigation);

        backBtn.setOnClickListener(v -> onBackPressed());

        // get patient_id from MedicalRecordsActivity
        Intent intent = getIntent();
        patientId = (intent != null) ? intent.getStringExtra("patient_id") : null;

        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this, "No patient ID provided", Toast.LENGTH_LONG).show();
            Log.e(TAG, "patientId is null/empty");
            tvPatientId.setText("Patient ID: -");
        } else {
            tvPatientId.setText("Patient ID: " + patientId);
            Log.d(TAG, "patientId used = " + patientId);
        }

        rvInvestigations.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        );
        adapter = new InvestigationHistoryAdapter(this, items);
        rvInvestigations.setAdapter(adapter);

        // load existing investigations
        loadInvestigationsFromServer();

        btnAddInvestigation.setOnClickListener(v -> {
            if (TextUtils.isEmpty(patientId)) {
                Toast.makeText(this, "Missing patient ID", Toast.LENGTH_LONG).show();
            } else {
                showAddInvestigationDialog();
            }
        });
    }

    // ---------------------- DIALOG ----------------------
    private void showAddInvestigationDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_investigation, null);

        EditText etHb                 = dialogView.findViewById(R.id.etHb);
        EditText etTlc                = dialogView.findViewById(R.id.etTlc);
        EditText etDc                 = dialogView.findViewById(R.id.etDc);
        EditText etPlatelet           = dialogView.findViewById(R.id.etPlatelet);
        EditText etEsr                = dialogView.findViewById(R.id.etEsr);
        EditText etCrp                = dialogView.findViewById(R.id.etCrp);
        EditText etLftTotalBilirubin  = dialogView.findViewById(R.id.etLftTotalBilirubin);
        EditText etLftDirectBilirubin = dialogView.findViewById(R.id.etLftDirectBilirubin);
        EditText etAst                = dialogView.findViewById(R.id.etAst);
        EditText etAlt                = dialogView.findViewById(R.id.etAlt);
        EditText etAlbumin            = dialogView.findViewById(R.id.etAlbumin);
        EditText etTotalProtein       = dialogView.findViewById(R.id.etTotalProtein);
        EditText etGgt                = dialogView.findViewById(R.id.etGgt);
        EditText etUrea               = dialogView.findViewById(R.id.etUrea);
        EditText etCreatinine         = dialogView.findViewById(R.id.etCreatinine);
        EditText etUricAcid           = dialogView.findViewById(R.id.etUricAcid);
        EditText etUrineRoutine       = dialogView.findViewById(R.id.etUrineRoutine);
        EditText etUrinePcr           = dialogView.findViewById(R.id.etUrinePcr);
        EditText etRaFactor           = dialogView.findViewById(R.id.etRaFactor);
        EditText etAntiCcp            = dialogView.findViewById(R.id.etAntiCcp);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Add Investigation")
                .setView(dialogView)
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Add", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnAdd = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnAdd.setOnClickListener((View v) -> {
                String hb           = etHb.getText().toString().trim();
                String tlc          = etTlc.getText().toString().trim();
                String dc           = etDc.getText().toString().trim();
                String platelet     = etPlatelet.getText().toString().trim();
                String esr          = etEsr.getText().toString().trim();
                String crp          = etCrp.getText().toString().trim();
                String lftTotal     = etLftTotalBilirubin.getText().toString().trim();
                String lftDirect    = etLftDirectBilirubin.getText().toString().trim();
                String ast          = etAst.getText().toString().trim();
                String alt          = etAlt.getText().toString().trim();
                String albumin      = etAlbumin.getText().toString().trim();
                String totalProtein = etTotalProtein.getText().toString().trim();
                String ggt          = etGgt.getText().toString().trim();
                String urea         = etUrea.getText().toString().trim();
                String creatinine   = etCreatinine.getText().toString().trim();
                String uricAcid     = etUricAcid.getText().toString().trim();
                String urineRoutine = etUrineRoutine.getText().toString().trim();
                String urinePcr     = etUrinePcr.getText().toString().trim();
                String raFactor     = etRaFactor.getText().toString().trim();
                String antiCcp      = etAntiCcp.getText().toString().trim();

                if (TextUtils.isEmpty(hb) &&
                        TextUtils.isEmpty(tlc) &&
                        TextUtils.isEmpty(dc) &&
                        TextUtils.isEmpty(platelet) &&
                        TextUtils.isEmpty(esr) &&
                        TextUtils.isEmpty(crp) &&
                        TextUtils.isEmpty(lftTotal) &&
                        TextUtils.isEmpty(lftDirect) &&
                        TextUtils.isEmpty(ast) &&
                        TextUtils.isEmpty(alt) &&
                        TextUtils.isEmpty(albumin) &&
                        TextUtils.isEmpty(totalProtein) &&
                        TextUtils.isEmpty(ggt) &&
                        TextUtils.isEmpty(urea) &&
                        TextUtils.isEmpty(creatinine) &&
                        TextUtils.isEmpty(uricAcid) &&
                        TextUtils.isEmpty(urineRoutine) &&
                        TextUtils.isEmpty(urinePcr) &&
                        TextUtils.isEmpty(raFactor) &&
                        TextUtils.isEmpty(antiCcp)) {
                    Toast.makeText(this,
                            "Please fill at least one field",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                addInvestigationToServer(
                        hb, tlc, dc, platelet,
                        esr, crp,
                        lftTotal, lftDirect,
                        ast, alt, albumin, totalProtein, ggt,
                        urea, creatinine, uricAcid,
                        urineRoutine, urinePcr,
                        raFactor, antiCcp
                );
                dialog.dismiss();
            });
        });

        dialog.show();
    }

    private void addInvestigationToServer(
            String hb, String tlc, String dc, String platelet,
            String esr, String crp,
            String lftTotal, String lftDirect,
            String ast, String alt, String albumin, String totalProtein, String ggt,
            String urea, String creatinine, String uricAcid,
            String urineRoutine, String urinePcr,
            String raFactor, String antiCcp
    ) {
        if (TextUtils.isEmpty(patientId)) {
            Toast.makeText(this, "Missing patient ID", Toast.LENGTH_LONG).show();
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(ADD_INVESTIGATION_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                body.put("hb", hb);
                body.put("total_leukocyte", tlc);
                body.put("differential_count", dc);
                body.put("platelet_count", platelet);
                body.put("esr", esr);
                body.put("crp", crp);
                body.put("lft_total_bilirubin", lftTotal);
                body.put("lft_direct_bilirubin", lftDirect);
                body.put("ast", ast);
                body.put("alt", alt);
                body.put("albumin", albumin);
                body.put("total_protein", totalProtein);
                body.put("ggt", ggt);
                body.put("urea", urea);
                body.put("creatinine", creatinine);
                body.put("uric_acid", uricAcid);
                body.put("urine_routine", urineRoutine);
                body.put("urine_pcr", urinePcr);
                body.put("ra_factor", raFactor);
                body.put("anti_ccp", antiCcp);

                String jsonBody = body.toString();
                Log.d(TAG, "addInvestigationToServer request: " + jsonBody);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                Log.d(TAG, "addInvestigationToServer HTTP code: " + code);

                InputStream is = (code < 400) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                String respStr = sb.toString();
                Log.d(TAG, "addInvestigationToServer response: " + respStr);

                JSONObject response = new JSONObject(respStr);

                runOnUiThread(() -> {
                    if (response.optBoolean("success")) {
                        Toast.makeText(this, "Investigation added", Toast.LENGTH_SHORT).show();
                        loadInvestigationsFromServer();
                    } else {
                        Toast.makeText(this,
                                response.optString("message", "Failed to add"),
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "Error adding investigation", e);
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error adding investigation",
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void loadInvestigationsFromServer() {
        if (TextUtils.isEmpty(patientId)) {
            Log.e(TAG, "loadInvestigationsFromServer: patientId empty, skipping");
            return;
        }

        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(GET_INVESTIGATIONS_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                JSONObject body = new JSONObject();
                body.put("patient_id", patientId);
                String jsonBody = body.toString();
                Log.d(TAG, "loadInvestigationsFromServer request: " + jsonBody);

                OutputStream os = conn.getOutputStream();
                os.write(jsonBody.getBytes("UTF-8"));
                os.flush();
                os.close();

                int code = conn.getResponseCode();
                Log.d(TAG, "loadInvestigationsFromServer HTTP code: " + code);

                InputStream is = (code < 400) ? conn.getInputStream() : conn.getErrorStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                br.close();

                String respStr = sb.toString();
                Log.d(TAG, "loadInvestigationsFromServer response: " + respStr);

                JSONObject response = new JSONObject(respStr);

                if (response.optBoolean("success")) {
                    JSONArray arr = response.optJSONArray("investigations");
                    items.clear();
                    int count = 0;
                    if (arr != null) {
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject obj = arr.getJSONObject(i);
                            List<String> details = new ArrayList<>();

                            addIfNotEmpty(details, "Hb", obj.optString("hb", ""));
                            addIfNotEmpty(details, "Total Leukocyte Count", obj.optString("total_leukocyte", ""));
                            addIfNotEmpty(details, "Differential Count", obj.optString("differential_count", ""));
                            addIfNotEmpty(details, "Platelet Count", obj.optString("platelet_count", ""));
                            addIfNotEmpty(details, "ESR", obj.optString("esr", ""));
                            addIfNotEmpty(details, "CRP", obj.optString("crp", ""));
                            addIfNotEmpty(details, "LFT Total Bilirubin", obj.optString("lft_total_bilirubin", ""));
                            addIfNotEmpty(details, "LFT Direct Bilirubin", obj.optString("lft_direct_bilirubin", ""));
                            addIfNotEmpty(details, "AST", obj.optString("ast", ""));
                            addIfNotEmpty(details, "ALT", obj.optString("alt", ""));
                            addIfNotEmpty(details, "Albumin", obj.optString("albumin", ""));
                            addIfNotEmpty(details, "Total Protein", obj.optString("total_protein", ""));
                            addIfNotEmpty(details, "GGT", obj.optString("ggt", ""));
                            addIfNotEmpty(details, "Urea", obj.optString("urea", ""));
                            addIfNotEmpty(details, "Creatinine", obj.optString("creatinine", ""));
                            addIfNotEmpty(details, "Uric Acid", obj.optString("uric_acid", ""));
                            addIfNotEmpty(details, "Urine Routine", obj.optString("urine_routine", ""));
                            addIfNotEmpty(details, "Urine PCR", obj.optString("urine_pcr", ""));
                            addIfNotEmpty(details, "RA Factor", obj.optString("ra_factor", ""));
                            addIfNotEmpty(details, "ANTI CCP", obj.optString("anti_ccp", ""));
                            addIfNotEmpty(details, "created_at", obj.optString("created_at", ""));

                            items.add(new InvestigationItem("Investigation", details));
                            count++;
                        }
                    }
                    int finalCount = count;

                    runOnUiThread(() -> {
                        adapter.notifyDataSetChanged();
                        Toast.makeText(this,
                                "Loaded " + finalCount + " investigations",
                                Toast.LENGTH_SHORT).show();
                    });
                } else {
                    String msg = response.optString("message", "Failed to load investigations");
                    runOnUiThread(() ->
                            Toast.makeText(this, msg, Toast.LENGTH_LONG).show());
                }

            } catch (Exception e) {
                Log.e(TAG, "Error loading investigations", e);
                runOnUiThread(() ->
                        Toast.makeText(this,
                                "Error loading investigations",
                                Toast.LENGTH_LONG).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private void addIfNotEmpty(List<String> list, String label, String value) {
        if (!TextUtils.isEmpty(value)) {
            String v = value.trim();
            if (!v.isEmpty()) {
                list.add(label + ": " + v);
            }
        }
    }

    @SuppressWarnings("unused")
    private List<InvestigationItem> buildSampleData() {
        List<InvestigationItem> list = new ArrayList<>();
        list.add(new InvestigationItem(
                "Investigation",
                Arrays.asList(
                        "_id: 68d8684ad0b017721cb9932b",
                        "created_at: 2025-09-27T22:42:18.544Z"
                )
        ));
        list.add(new InvestigationItem(
                "Investigation",
                Arrays.asList(
                        "_id: 68d86803d0b017721cb99329",
                        "Total_leukocyte_count: 3",
                        "created_at: 2025-09-27T22:41:07.359Z"
                )
        ));
        return list;
    }
}
