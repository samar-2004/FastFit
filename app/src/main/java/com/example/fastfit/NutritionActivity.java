package com.example.fastfit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class NutritionActivity extends AppCompatActivity {

    private static final double PRICE_PROTEIN = 8.99;
    private static final double PRICE_ENERGY  = 3.49;
    private static final double PRICE_SALAD   = 6.99;
    private static final double PRICE_JUICE   = 4.99;


    private int qtyProtein = 0, qtyEnergy = 0, qtySalad = 0, qtyJuice = 0;

    private TextView tvQtyProtein, tvQtyEnergy, tvQtySalad, tvQtyJuice, tvTotal;
    private String workoutName;
    private String workoutId;
    private ArrayList<String> selectedSlots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nutrition);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        workoutName   = getIntent().getStringExtra("workout_name");
        workoutId     = getIntent().getStringExtra("workout_id");
        selectedSlots = getIntent().getStringArrayListExtra("selected_slots");

        tvQtyProtein = findViewById(R.id.tvQtyProtein);
        tvQtyEnergy  = findViewById(R.id.tvQtyEnergy);
        tvQtySalad   = findViewById(R.id.tvQtySalad);
        tvQtyJuice   = findViewById(R.id.tvQtyJuice);
        tvTotal      = findViewById(R.id.tvTotal);


        TextView btnIncProtein = findViewById(R.id.btnIncProtein);
        TextView btnDecProtein = findViewById(R.id.btnDecProtein);
        TextView btnIncEnergy  = findViewById(R.id.btnIncEnergy);
        TextView btnDecEnergy  = findViewById(R.id.btnDecEnergy);
        TextView btnIncSalad   = findViewById(R.id.btnIncSalad);
        TextView btnDecSalad   = findViewById(R.id.btnDecSalad);
        TextView btnIncJuice   = findViewById(R.id.btnIncJuice);
        TextView btnDecJuice   = findViewById(R.id.btnDecJuice);

        Button btnConfirm = findViewById(R.id.btnConfirmNutrition);

        btnIncProtein.setOnClickListener(v -> { qtyProtein++; updateQty(); });
        btnDecProtein.setOnClickListener(v -> { if (qtyProtein > 0) { qtyProtein--; updateQty(); } });
        btnIncEnergy.setOnClickListener(v  -> { qtyEnergy++;  updateQty(); });
        btnDecEnergy.setOnClickListener(v  -> { if (qtyEnergy > 0)  { qtyEnergy--;  updateQty(); } });
        btnIncSalad.setOnClickListener(v   -> { qtySalad++;   updateQty(); });
        btnDecSalad.setOnClickListener(v   -> { if (qtySalad > 0)   { qtySalad--;   updateQty(); } });
        btnIncJuice.setOnClickListener(v   -> { qtyJuice++;   updateQty(); });
        btnDecJuice.setOnClickListener(v   -> { if (qtyJuice > 0)   { qtyJuice--;   updateQty(); } });

        btnConfirm.setOnClickListener(v -> {
            double nutritionTotal = calculateTotal();
            Intent intent = new Intent(NutritionActivity.this, WorkoutSummaryActivity.class);
            intent.putExtra("workout_name", workoutName);
            intent.putExtra("workout_id", workoutId);
            intent.putStringArrayListExtra("selected_slots", selectedSlots);
            intent.putExtra("nutrition_total", nutritionTotal);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    @SuppressLint("DefaultLocale")
    private void updateQty() {
        tvQtyProtein.setText(String.valueOf(qtyProtein));
        tvQtyEnergy.setText(String.valueOf(qtyEnergy));
        tvQtySalad.setText(String.valueOf(qtySalad));
        tvQtyJuice.setText(String.valueOf(qtyJuice));
        tvTotal.setText(String.format("$%.2f", calculateTotal()));
    }

    private double calculateTotal() {
        return (qtyProtein * PRICE_PROTEIN)
                + (qtyEnergy  * PRICE_ENERGY)
                + (qtySalad   * PRICE_SALAD)
                + (qtyJuice   * PRICE_JUICE);
    }

}
