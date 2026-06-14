package com.example.fastfit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fastfit.ExerciseDetailActivity;
import com.example.fastfit.R;
import com.example.fastfit.SlotBookingActivity;
import com.example.fastfit.adapter.WorkoutAdapter;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WorkoutsFragment extends Fragment implements WorkoutAdapter.Listener {

    private final Repo repo = Repo.get();
    private WorkoutAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private final List<Workout> all = new ArrayList<>();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_workouts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        RecyclerView rv = v.findViewById(R.id.rvWorkouts);
        swipeRefresh = v.findViewById(R.id.swipeRefresh);
        progressBar = v.findViewById(R.id.progressBar);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        EditText etSearch = v.findViewById(R.id.etSearch);

        adapter = new WorkoutAdapter(this);
        rv.setAdapter(adapter);

        swipeRefresh.setColorSchemeResources(R.color.red);
        swipeRefresh.setOnRefreshListener(this::loadWorkouts);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { filter(s.toString()); }
            @Override public void afterTextChanged(Editable s) { }
        });

        loadWorkouts();
    }

    private void loadWorkouts() {
        if (!swipeRefresh.isRefreshing()) progressBar.setVisibility(View.VISIBLE);
        repo.getWorkouts(new Callback<List<Workout>>() {
            @Override public void onSuccess(List<Workout> data) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                all.clear();
                all.addAll(data);
                adapter.submit(all);
                tvEmpty.setVisibility(all.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String message) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void filter(String query) {
        String q = query.trim().toLowerCase(Locale.US);
        if (q.isEmpty()) { adapter.submit(all); tvEmpty.setVisibility(all.isEmpty() ? View.VISIBLE : View.GONE); return; }
        List<Workout> filtered = new ArrayList<>();
        for (Workout w : all) {
            if (w.getName().toLowerCase(Locale.US).contains(q)
                    || (w.getMuscleGroup() != null && w.getMuscleGroup().toLowerCase(Locale.US).contains(q))) {
                filtered.add(w);
            }
        }
        adapter.submit(filtered);
        tvEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBook(Workout workout) {
        Intent intent = new Intent(getActivity(), SlotBookingActivity.class);
        intent.putExtra("workout_id", workout.getId());
        intent.putExtra("workout_name", workout.getName());
        startActivity(intent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onTutorial(Workout workout) { onOpen(workout); }

    @Override
    public void onOpen(Workout workout) {
        Intent intent = new Intent(getActivity(), ExerciseDetailActivity.class);
        intent.putExtra(ExerciseDetailActivity.EXTRA_WORKOUT, workout);
        startActivity(intent);
    }
}
