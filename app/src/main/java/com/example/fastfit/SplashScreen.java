package com.example.fastfit;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fastfit.auth.LoginActivity;
import com.example.fastfit.data.Prefs;
import com.example.fastfit.data.Repo;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {

    private static final int SPLASH_DURATION = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ImageView logo = findViewById(R.id.logo);
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_animation);
        logo.startAnimation(logoAnim);

        TextView appName = findViewById(R.id.appName);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.text_splash_animation);
        appName.startAnimation(textAnim);

        new Handler(Looper.getMainLooper()).postDelayed(this::routeNext, SPLASH_DURATION);
    }

    /** Decide the first real screen based on onboarding + auth state. */
    private void routeNext() {
        Prefs prefs = new Prefs(this);
        Intent intent;
        if (!prefs.isOnboarded()) {
            intent = new Intent(this, OnboardingActivity.class);
        } else if (Repo.get().isLoggedIn()) {
            intent = new Intent(this, HomeActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}

