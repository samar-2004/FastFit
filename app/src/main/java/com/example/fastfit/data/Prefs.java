package com.example.fastfit.data;

import android.content.Context;
import android.content.SharedPreferences;

/** Lightweight SharedPreferences wrapper for local app preferences. */
public class Prefs {
    private static final String FILE = "fastfit_prefs";
    private static final String KEY_ONBOARDED = "onboarded";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_NOTIFICATIONS = "notifications";

    private final SharedPreferences sp;

    public Prefs(Context context) {
        sp = context.getApplicationContext().getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    public boolean isOnboarded() { return sp.getBoolean(KEY_ONBOARDED, false); }
    public void setOnboarded(boolean v) { sp.edit().putBoolean(KEY_ONBOARDED, v).apply(); }

    public boolean isDarkMode() { return sp.getBoolean(KEY_DARK_MODE, false); }
    public void setDarkMode(boolean v) { sp.edit().putBoolean(KEY_DARK_MODE, v).apply(); }

    public boolean isNotificationsEnabled() { return sp.getBoolean(KEY_NOTIFICATIONS, true); }
    public void setNotificationsEnabled(boolean v) { sp.edit().putBoolean(KEY_NOTIFICATIONS, v).apply(); }
}
