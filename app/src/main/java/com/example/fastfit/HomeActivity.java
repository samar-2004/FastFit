package com.example.fastfit;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.fastfit.ui.HomeFragment;
import com.example.fastfit.ui.ProfileFragment;
import com.example.fastfit.ui.ProgressFragment;
import com.example.fastfit.ui.WorkoutsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/** Main shell hosting the four primary tabs via a BottomNavigationView. */
public class HomeActivity extends AppCompatActivity {

    public static final String EXTRA_TARGET_TAB = "target_tab";

    private final Fragment homeFragment = new HomeFragment();
    private final Fragment workoutsFragment = new WorkoutsFragment();
    private final Fragment progressFragment = new ProgressFragment();
    private final Fragment profileFragment = new ProfileFragment();
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment active = homeFragment;

    private BottomNavigationView bottomNav;

    private final ActivityResultLauncher<String> notifPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> { });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, bars.bottom);
            return insets;
        });

        bottomNav = findViewById(R.id.bottomNav);

        // Add all fragments once; toggle visibility to preserve their state.
        fm.beginTransaction()
                .add(R.id.fragmentContainer, profileFragment, "profile").hide(profileFragment)
                .add(R.id.fragmentContainer, progressFragment, "progress").hide(progressFragment)
                .add(R.id.fragmentContainer, workoutsFragment, "workouts").hide(workoutsFragment)
                .add(R.id.fragmentContainer, homeFragment, "home")
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) return switchTo(homeFragment);
            if (id == R.id.nav_workouts) return switchTo(workoutsFragment);
            if (id == R.id.nav_progress) return switchTo(progressFragment);
            if (id == R.id.nav_profile) return switchTo(profileFragment);
            return false;
        });

        int target = getIntent().getIntExtra(EXTRA_TARGET_TAB, R.id.nav_home);
        bottomNav.setSelectedItemId(target);

        maybeRequestNotificationPermission();
    }

    private void maybeRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private boolean switchTo(Fragment target) {
        if (target == active) return true;
        fm.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, android.R.anim.fade_out)
                .hide(active).show(target)
                .commit();
        active = target;
        return true;
    }

    /** Lets fragments jump to another tab (e.g. "View all" -> Workouts). */
    public void selectTab(int navItemId) {
        bottomNav.setSelectedItemId(navItemId);
    }
}
