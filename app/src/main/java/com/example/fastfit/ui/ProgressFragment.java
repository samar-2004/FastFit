package com.example.fastfit.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfit.R;
import com.example.fastfit.adapter.ProgressAdapter;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.model.ProgressEntry;
import com.example.fastfit.model.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProgressFragment extends Fragment {

    private final Repo repo = Repo.get();
    private TextView tvStatWorkouts, tvStatMinutes, tvStatStreak, tvEmpty;
    private ProgressAdapter adapter;
    private BarChart barChart;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_progress, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        tvStatWorkouts = v.findViewById(R.id.tvStatWorkouts);
        tvStatMinutes = v.findViewById(R.id.tvStatMinutes);
        tvStatStreak = v.findViewById(R.id.tvStatStreak);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        barChart = v.findViewById(R.id.barChart);

        RecyclerView rv = v.findViewById(R.id.rvProgress);
        adapter = new ProgressAdapter();
        rv.setAdapter(adapter);

        ((Button) v.findViewById(R.id.btnLogWorkout)).setOnClickListener(x -> showLogWorkoutDialog());
        ((Button) v.findViewById(R.id.btnAddWeight)).setOnClickListener(x -> showAddWeightDialog());

        styleChart();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadStats();
        loadProgress();
    }

    private void loadStats() {
        repo.getUserProfile(new Callback<User>() {
            @Override public void onSuccess(User u) {
                if (!isAdded()) return;
                tvStatWorkouts.setText(String.valueOf(u.getTotalWorkouts()));
                tvStatMinutes.setText(String.valueOf(u.getTotalMinutes()));
                tvStatStreak.setText(String.valueOf(u.getStreak()));
            }
            @Override public void onError(String message) { }
        });
    }

    private void loadProgress() {
        repo.getProgress(new Callback<List<ProgressEntry>>() {
            @Override public void onSuccess(List<ProgressEntry> data) {
                if (!isAdded()) return;
                adapter.submit(data);
                tvEmpty.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
                buildChart(data);
            }
            @Override public void onError(String message) {
                if (isAdded()) tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void styleChart() {
        Description desc = new Description();
        desc.setText("");
        barChart.setDescription(desc);
        barChart.getLegend().setEnabled(false);
        barChart.setDrawGridBackground(false);
        barChart.setScaleEnabled(false);
        barChart.setDoubleTapToZoomEnabled(false);

        XAxis x = barChart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setDrawGridLines(false);
        x.setGranularity(1f);
        x.setTextColor(Color.GRAY);

        YAxis left = barChart.getAxisLeft();
        left.setAxisMinimum(0f);
        left.setDrawGridLines(true);
        left.setGridColor(Color.parseColor("#EEEEEE"));
        left.setTextColor(Color.GRAY);
        barChart.getAxisRight().setEnabled(false);
    }

    /** Aggregate workout minutes per day for the last 7 days. */
    private void buildChart(List<ProgressEntry> entries) {
        String[] dayLabels = new String[7];
        float[] totals = new float[7];
        Map<String, Integer> dayIndex = new HashMap<>();
        SimpleDateFormat key = new SimpleDateFormat("yyyyMMdd", Locale.US);
        SimpleDateFormat label = new SimpleDateFormat("EEE", Locale.US);

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -6);
        for (int i = 0; i < 7; i++) {
            dayLabels[i] = label.format(cal.getTime());
            dayIndex.put(key.format(cal.getTime()), i);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }

        for (ProgressEntry e : entries) {
            if (!ProgressEntry.TYPE_WORKOUT.equals(e.getType()) || e.getTimestamp() == null) continue;
            Integer idx = dayIndex.get(key.format(e.getTimestamp()));
            if (idx != null) totals[idx] += (float) e.getValue();
        }

        List<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0; i < 7; i++) barEntries.add(new BarEntry(i, totals[i]));

        BarDataSet set = new BarDataSet(barEntries, "Minutes");
        set.setColor(Color.parseColor("#dc000a"));
        set.setDrawValues(false);
        BarData barData = new BarData(set);
        barData.setBarWidth(0.5f);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(dayLabels));
        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.animateY(700);
        barChart.invalidate();
    }

    private void showLogWorkoutDialog() {
        LinearLayout layout = dialogContainer();
        final EditText etName = dialogField("Workout name", false);
        final EditText etMinutes = dialogField("Minutes", true);
        layout.addView(etName);
        layout.addView(etMinutes);

        new AlertDialog.Builder(requireContext())
                .setTitle("Log Workout")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String minsStr = etMinutes.getText().toString().trim();
                    if (name.isEmpty() || minsStr.isEmpty()) {
                        toast("Please fill in both fields"); return;
                    }
                    int mins;
                    try { mins = Integer.parseInt(minsStr); } catch (NumberFormatException e) { toast("Invalid minutes"); return; }
                    repo.addProgress(new ProgressEntry(ProgressEntry.TYPE_WORKOUT, name, mins),
                            refreshCallback("Workout logged!"));
                    repo.addWorkoutStats(mins, silent());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showAddWeightDialog() {
        LinearLayout layout = dialogContainer();
        final EditText etWeight = dialogField("Weight (kg)", true);
        layout.addView(etWeight);

        new AlertDialog.Builder(requireContext())
                .setTitle("Add Weight")
                .setView(layout)
                .setPositiveButton("Save", (d, w) -> {
                    String s = etWeight.getText().toString().trim();
                    if (s.isEmpty()) { toast("Please enter a weight"); return; }
                    double kg;
                    try { kg = Double.parseDouble(s); } catch (NumberFormatException e) { toast("Invalid weight"); return; }
                    repo.addProgress(new ProgressEntry(ProgressEntry.TYPE_WEIGHT, "Body weight", kg),
                            refreshCallback("Weight saved!"));
                    Map<String, Object> update = new HashMap<>();
                    update.put("weight", kg);
                    repo.updateUserProfile(update, silent());
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // -- small UI helpers --
    private LinearLayout dialogContainer() {
        LinearLayout layout = new LinearLayout(requireContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        layout.setPadding(pad, pad / 2, pad, 0);
        return layout;
    }

    private EditText dialogField(String hint, boolean numeric) {
        EditText et = new EditText(requireContext());
        et.setHint(hint);
        if (numeric) et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER
                | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        return et;
    }

    private Callback<Void> refreshCallback(final String successMsg) {
        return new Callback<Void>() {
            @Override public void onSuccess(Void data) { toast(successMsg); loadProgress(); loadStats(); }
            @Override public void onError(String message) { toast(message); }
        };
    }

    private <T> Callback<T> silent() {
        return new Callback<T>() {
            @Override public void onSuccess(T data) { }
            @Override public void onError(String message) { }
        };
    }

    private void toast(String msg) {
        if (isAdded()) Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}
