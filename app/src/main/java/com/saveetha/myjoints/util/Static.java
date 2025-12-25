package com.saveetha.myjoints.util;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;

import com.saveetha.myjoints.R;

import org.json.JSONObject;

import okhttp3.ResponseBody;

public interface Static {

    static AlertDialog showProgress(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_progress);
        builder.setCancelable(true);
        AlertDialog progress = builder.create();
        progress.setCanceledOnTouchOutside(false);
        return progress;
    }

    static void toast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    static void showError(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    static void showResponse(Context context, String message) {
        new AlertDialog.Builder(context)
                .setTitle("Response")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    static void showErrorResponse(Context context, ResponseBody error) {
        String message;
        try {
            JSONObject obj = new JSONObject(error.string());
            message = obj.getString("message");
        } catch (Exception e) {
            message = e.getMessage();
        }
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }


}
