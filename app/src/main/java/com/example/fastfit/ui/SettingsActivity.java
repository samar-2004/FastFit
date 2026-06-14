package com.example.fastfit.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fastfit.R;
import com.example.fastfit.auth.LoginActivity;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Prefs;
import com.example.fastfit.data.Repo;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.messaging.FirebaseMessaging;

import android.content.Intent;

public class SettingsActivity extends AppCompatActivity {

    private static final String TOPIC_PROMOS = "promotions";
    private final Repo repo = Repo.get();
    private Prefs prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, bars.bottom);
            return insets;
        });

        prefs = new Prefs(this);

        SwitchMaterial switchDark = findViewById(R.id.switchDarkMode);
        SwitchMaterial switchNotif = findViewById(R.id.switchNotifications);
        switchDark.setChecked(prefs.isDarkMode());
        switchNotif.setChecked(prefs.isNotificationsEnabled());

        switchDark.setOnCheckedChangeListener((b, checked) -> {
            prefs.setDarkMode(checked);
            AppCompatDelegate.setDefaultNightMode(checked
                    ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        switchNotif.setOnCheckedChangeListener((b, checked) -> {
            prefs.setNotificationsEnabled(checked);
            if (checked) FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_PROMOS);
            else FirebaseMessaging.getInstance().unsubscribeFromTopic(TOPIC_PROMOS);
            Toast.makeText(this, checked ? "Notifications on" : "Notifications off",
                    Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.rowChangePassword).setOnClickListener(v -> changePassword());
        findViewById(R.id.rowAbout).setOnClickListener(v -> showAbout());
        findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
    }

    private void changePassword() {
        if (repo.currentUser() == null || repo.currentUser().getEmail() == null) {
            Toast.makeText(this, "No email on file.", Toast.LENGTH_SHORT).show();
            return;
        }
        String email = repo.currentUser().getEmail();
        new AlertDialog.Builder(this)
                .setTitle("Change password")
                .setMessage("We'll email a password reset link to " + email + ".")
                .setPositiveButton("Send", (d, w) -> repo.sendPasswordReset(email, new Callback<Void>() {
                    @Override public void onSuccess(Void data) {
                        Toast.makeText(SettingsActivity.this, "Reset link sent.", Toast.LENGTH_LONG).show();
                    }
                    @Override public void onError(String message) {
                        Toast.makeText(SettingsActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAbout() {
        new AlertDialog.Builder(this)
                .setTitle("About FASTFit")
                .setMessage("FASTFit — Your Smart Gym Companion.\n\nVersion 1.0\n© 2026 FASTFit")
                .setPositiveButton("OK", null)
                .show();
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (d, w) -> {
                    repo.signOut();
                    Intent intent = new Intent(this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
