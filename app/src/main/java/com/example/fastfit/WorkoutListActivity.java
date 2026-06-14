package com.example.fastfit;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class WorkoutListActivity extends AppCompatActivity {

    private static final String URL_LEG_PRESS = "https://www.youtube.com/watch?v=IZxyjW7MPJQ";
    private static final String URL_LAT_PULLDOWN = "https://www.youtube.com/watch?v=CAwf7n6Luuc";
    private static final String URL_CHEST_PRESS = "https://www.youtube.com/watch?v=xUm0BiZCWlQ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_workout_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnBookLegPress = findViewById(R.id.btnBookLegPress);
        Button btnTutorialLegPress = findViewById(R.id.btnTutorialLegPress);

        btnBookLegPress.setOnClickListener(v -> openSlotBooking("Leg Press"));
        btnTutorialLegPress.setOnClickListener(v -> openYouTube(URL_LEG_PRESS));

        Button btnBookLatPulldown = findViewById(R.id.btnBookLatPulldown);
        Button btnTutorialLatPulldown = findViewById(R.id.btnTutorialLatPulldown);

        btnBookLatPulldown.setOnClickListener(v -> openSlotBooking("Lat Pulldown"));
        btnTutorialLatPulldown.setOnClickListener(v -> openYouTube(URL_LAT_PULLDOWN));

        Button btnBookChestPress = findViewById(R.id.btnBookChestPress);
        Button btnTutorialChestPress = findViewById(R.id.btnTutorialChestPress);

        btnBookChestPress.setOnClickListener(v -> openSlotBooking("Chest Press"));
        btnTutorialChestPress.setOnClickListener(v -> openYouTube(URL_CHEST_PRESS));
    }

    private void openSlotBooking(String workoutName) {
        Intent intent = new Intent(WorkoutListActivity.this, SlotBookingActivity.class);
        intent.putExtra("workout_name", workoutName);
        startActivity(intent);
    }

    private void openYouTube(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setPackage("com.google.android.youtube");
            startActivity(intent);
        } catch (Exception e) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        }
    }
}


