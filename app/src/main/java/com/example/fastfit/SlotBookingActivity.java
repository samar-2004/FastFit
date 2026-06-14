package com.example.fastfit;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class SlotBookingActivity extends AppCompatActivity {

    private String workoutName;
    private String workoutId;
    private final ArrayList<String> selectedSlots = new ArrayList<>();

    private LinearLayout btnMorning, btnAfternoon, btnEvening;
    private TextView tvMorningLabel, tvAfternoonLabel, tvEveningLabel;
    private TextView tvSelectedSlots;
    private Button btnProceedToNutrition, btnConfirmWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_slot_booking);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        workoutName = getIntent().getStringExtra("workout_name");
        workoutId = getIntent().getStringExtra("workout_id");

        TextView tvWorkoutName = findViewById(R.id.tvWorkoutName);
        tvWorkoutName.setText(workoutName);

        btnMorning   = findViewById(R.id.btnMorning);
        btnAfternoon = findViewById(R.id.btnAfternoon);
        btnEvening   = findViewById(R.id.btnEvening);

        tvMorningLabel   = findViewById(R.id.tvMorningLabel);
        tvAfternoonLabel = findViewById(R.id.tvAfternoonLabel);
        tvEveningLabel   = findViewById(R.id.tvEveningLabel);

        tvSelectedSlots       = findViewById(R.id.tvSelectedSlots);
        btnProceedToNutrition = findViewById(R.id.btnProceedToNutrition);
        btnConfirmWorkout     = findViewById(R.id.btnConfirmWorkout);

        btnMorning.setOnClickListener(v   -> toggleSlot("Morning",   btnMorning,   tvMorningLabel));
        btnAfternoon.setOnClickListener(v -> toggleSlot("Afternoon", btnAfternoon, tvAfternoonLabel));
        btnEvening.setOnClickListener(v   -> toggleSlot("Evening",   btnEvening,   tvEveningLabel));

        btnProceedToNutrition.setOnClickListener(v -> {
            if (selectedSlots.isEmpty()) {
                Toast.makeText(this, "Please select at least one slot.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(SlotBookingActivity.this, NutritionActivity.class);
            intent.putExtra("workout_name", workoutName);
            intent.putExtra("workout_id", workoutId);
            intent.putStringArrayListExtra("selected_slots", selectedSlots);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        btnConfirmWorkout.setOnClickListener(v -> {
            if (selectedSlots.isEmpty()) {
                Toast.makeText(this, "Please select at least one slot.", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(SlotBookingActivity.this, WorkoutSummaryActivity.class);
            intent.putExtra("workout_name", workoutName);
            intent.putExtra("workout_id", workoutId);
            intent.putStringArrayListExtra("selected_slots", selectedSlots);
            intent.putExtra("nutrition_total", 0.0);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void toggleSlot(String slotName, LinearLayout card, TextView label) {
        if (selectedSlots.contains(slotName)) {
            selectedSlots.remove(slotName);
            card.setBackgroundResource(R.drawable.slot_card_selector);
            label.setTextColor(0xFF000000);
        } else {
            selectedSlots.add(slotName);
            card.setBackgroundResource(R.drawable.slot_card_selected_bg);
            label.setTextColor(0xFFFFFFFF);
        }
        updateUI();
    }

    private void updateUI() {
        tvSelectedSlots.setText(String.valueOf(selectedSlots.size()));
        if (selectedSlots.isEmpty()) {
            btnProceedToNutrition.setEnabled(false);
            btnProceedToNutrition.setBackgroundTintList(
                ColorStateList.valueOf(0xFFBBBBBB));
        } else {
            btnProceedToNutrition.setEnabled(true);
            btnProceedToNutrition.setBackgroundTintList(
                ColorStateList.valueOf(0xFFdc000a));
        }
    }
}

