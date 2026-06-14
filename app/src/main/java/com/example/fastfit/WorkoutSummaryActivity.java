package com.example.fastfit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.model.Booking;
import com.example.fastfit.model.ProgressEntry;
import com.example.fastfit.model.Workout;

import java.util.ArrayList;

public class WorkoutSummaryActivity extends AppCompatActivity {

    private static final String HARDCODED_PHONE = "+923163854271";

    private String workoutName;
    private String workoutId;
    private ArrayList<String> selectedSlots;
    private double nutritionTotal;
    private boolean bookingSaved = false;
    private final Repo repo = Repo.get();
    private Button btnConfirmBooking;

    private final ActivityResultLauncher<String> smsPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    sendSms();
                } else {
                    Toast.makeText(this, "SMS permission denied.", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_summary);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        workoutName = getIntent().getStringExtra("workout_name");
        workoutId = getIntent().getStringExtra("workout_id");
        selectedSlots = getIntent().getStringArrayListExtra("selected_slots");
        nutritionTotal = getIntent().getDoubleExtra("nutrition_total", 0.0);


        TextView tvWorkout = findViewById(R.id.tvSummaryWorkout);
        TextView tvSlots = findViewById(R.id.tvSummarySlots);
        TextView tvNutrition = findViewById(R.id.tvSummaryNutrition);
        TextView tvTotalPrice = findViewById(R.id.tvSummaryTotalPrice);


        tvWorkout.setText(workoutName != null ? workoutName : "—");
        if (selectedSlots != null && !selectedSlots.isEmpty()) {
            tvSlots.setText(String.join(", ", selectedSlots));
        } else {
            tvSlots.setText("—");
        }
        tvNutrition.setText(String.format("$%.2f", nutritionTotal));
        tvTotalPrice.setText(String.format("$%.2f", nutritionTotal));

        Button btnSendPlan = findViewById(R.id.btnSendPlan);
        Button btnSendSms = findViewById(R.id.btnSendSms);
        btnConfirmBooking = findViewById(R.id.btnConfirmBooking);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        btnConfirmBooking.setOnClickListener(v -> confirmBooking());
        btnSendPlan.setOnClickListener(v -> sharePlan());
        btnSendSms.setOnClickListener(v -> requestAndSendSms());
    }

    /** Persists the booking + a progress entry and bumps the user's stats. */
    private void confirmBooking() {
        if (bookingSaved) { goHome(); return; }
        if (workoutName == null || workoutName.isEmpty()
                || selectedSlots == null || selectedSlots.isEmpty()) {
            Toast.makeText(this, "Nothing to confirm.", Toast.LENGTH_SHORT).show();
            return;
        }
        btnConfirmBooking.setEnabled(false);
        btnConfirmBooking.setText("SAVING…");

        Booking booking = new Booking(repo.uid(), workoutId, workoutName, selectedSlots, nutritionTotal);
        repo.createBooking(booking, new Callback<String>() {
            @Override public void onSuccess(String id) {
                bookingSaved = true;
                int minutes = estimateMinutes();
                repo.addProgress(new ProgressEntry(ProgressEntry.TYPE_WORKOUT, workoutName, minutes),
                        noop());
                repo.addWorkoutStats(minutes, noop());
                Toast.makeText(WorkoutSummaryActivity.this,
                        "Booking confirmed!", Toast.LENGTH_SHORT).show();
                btnConfirmBooking.setText("BOOKING CONFIRMED");
                goHome();
            }
            @Override public void onError(String message) {
                btnConfirmBooking.setEnabled(true);
                btnConfirmBooking.setText("CONFIRM BOOKING");
                Toast.makeText(WorkoutSummaryActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private int estimateMinutes() {
        for (Workout w : repo.defaultWorkouts()) {
            if (w.getId().equals(workoutId) || w.getName().equalsIgnoreCase(workoutName)) {
                return w.getDurationMin();
            }
        }
        return 30;
    }

    private void goHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private <T> Callback<T> noop() {
        return new Callback<T>() {
            @Override public void onSuccess(T data) { }
            @Override public void onError(String message) { }
        };
    }

    @SuppressLint("DefaultLocale")
    private String buildSummaryText() {
        String slots = (selectedSlots != null && !selectedSlots.isEmpty())
                ? String.join(", ", selectedSlots) : "None";
        return "FASTFit Workout Plan\n\n"
                + "Workout: " + (workoutName != null ? workoutName : "—") + "\n"
                + "Slots: " + slots + "\n"
                + "Nutrition Total: " + String.format("$%.2f", nutritionTotal) + "\n\n"
                + "Let's crush it! ";
    }

    private void sharePlan() {
        if (workoutName == null || workoutName.isEmpty()) {
            Toast.makeText(this, "Data is missing. Cannot share.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "My FASTFit Workout Plan");
        shareIntent.putExtra(Intent.EXTRA_TEXT, buildSummaryText());
        startActivity(Intent.createChooser(shareIntent, "Share via"));
    }

    private void requestAndSendSms() {
        if (workoutName == null || workoutName.isEmpty()) {
            Toast.makeText(this, "Data is missing. Cannot send SMS.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            sendSms();
        } else {
            smsPermissionLauncher.launch(Manifest.permission.SEND_SMS);
        }
    }

    private void sendSms() {
        try {
            @SuppressLint("DefaultLocale") String message = "FASTFit Plan: " + workoutName
                    + " | Slots: " + String.join(", ", selectedSlots != null ? selectedSlots : new ArrayList<>())
                    + " | Nutrition: " + String.format("$%.2f", nutritionTotal);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(HARDCODED_PHONE, null, message, null, null);
            Toast.makeText(this, "SMS sent successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to send SMS: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

