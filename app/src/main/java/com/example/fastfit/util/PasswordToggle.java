package com.example.fastfit.util;

import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.fastfit.R;

/** Wires an eye icon to show/hide a password field. */
public final class PasswordToggle {
    private PasswordToggle() { }

    public static void attach(final ImageView toggle, final EditText field) {
        toggle.setOnClickListener(v -> {
            boolean hidden = field.getTransformationMethod() instanceof PasswordTransformationMethod;
            if (hidden) {
                field.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                toggle.setImageResource(R.drawable.ic_visibility_off);
            } else {
                field.setTransformationMethod(PasswordTransformationMethod.getInstance());
                toggle.setImageResource(R.drawable.ic_visibility);
            }
            field.setSelection(field.getText().length());
        });
    }
}
