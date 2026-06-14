package com.example.fastfit.util;

import android.util.Patterns;
import android.widget.EditText;

/** Tiny input-validation helpers used by the auth screens. */
public final class Validate {
    private Validate() { }

    public static boolean email(EditText field) {
        String v = field.getText().toString().trim();
        if (v.isEmpty()) { field.setError("Email is required"); field.requestFocus(); return false; }
        if (!Patterns.EMAIL_ADDRESS.matcher(v).matches()) {
            field.setError("Enter a valid email"); field.requestFocus(); return false;
        }
        return true;
    }

    public static boolean notEmpty(EditText field, String label) {
        if (field.getText().toString().trim().isEmpty()) {
            field.setError(label + " is required"); field.requestFocus(); return false;
        }
        return true;
    }

    public static boolean password(EditText field) {
        String v = field.getText().toString();
        if (v.isEmpty()) { field.setError("Password is required"); field.requestFocus(); return false; }
        if (v.length() < 6) {
            field.setError("At least 6 characters"); field.requestFocus(); return false;
        }
        return true;
    }
}
