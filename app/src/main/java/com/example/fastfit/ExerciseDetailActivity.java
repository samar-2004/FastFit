package com.example.fastfit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fastfit.model.Workout;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;

import java.util.Locale;

/** Shows a workout with an in-app YouTube tutorial and a "book" action. */
public class ExerciseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_WORKOUT = "extra_workout";

    private Workout workout;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_exercise_detail);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, bars.bottom);
            return insets;
        });

        workout = (Workout) getIntent().getSerializableExtra(EXTRA_WORKOUT);
        if (workout == null) { finish(); return; }

        ((TextView) findViewById(R.id.tvName)).setText(workout.getName());
        ((TextView) findViewById(R.id.tvInfo)).setText(workout.getInfo());
        ((TextView) findViewById(R.id.tvChipMuscle)).setText(workout.getMuscleGroup());
        ((TextView) findViewById(R.id.tvDescription)).setText(workout.getDescription());
        ((TextView) findViewById(R.id.tvSets)).setText(String.valueOf(workout.getSets()));
        ((TextView) findViewById(R.id.tvReps)).setText(String.valueOf(workout.getReps()));
        ((TextView) findViewById(R.id.tvDuration)).setText(String.valueOf(workout.getDurationMin()));
        ((TextView) findViewById(R.id.tvCalories)).setText(String.valueOf(workout.getCalories()));

        setupPlayer();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        Button btnBook = findViewById(R.id.btnBookWorkout);
        btnBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, SlotBookingActivity.class);
            intent.putExtra("workout_id", workout.getId());
            intent.putExtra("workout_name", workout.getName());
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
    }

    private void setupPlayer() {
        YouTubePlayerView playerView = findViewById(R.id.youtubePlayerView);
        // The view manages its own lifecycle once registered as an observer.
        getLifecycle().addObserver(playerView);
        final String videoId = workout.getYoutubeId();
        playerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                if (videoId != null && !videoId.isEmpty()) {
                    // cueVideo loads without auto-playing.
                    youTubePlayer.cueVideo(videoId, 0);
                }
            }
        });
    }
}
