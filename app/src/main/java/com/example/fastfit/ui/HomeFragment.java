package com.example.fastfit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfit.ExerciseDetailActivity;
import com.example.fastfit.HomeActivity;
import com.example.fastfit.R;
import com.example.fastfit.adapter.BookingAdapter;
import com.example.fastfit.adapter.FeaturedWorkoutAdapter;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.model.Booking;
import com.example.fastfit.model.User;
import com.example.fastfit.model.Workout;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private final Repo repo = Repo.get();
    private TextView tvGreeting, tvUserName, tvStatWorkouts, tvStatMinutes, tvStatStreak, tvEmptyBookings;
    private FeaturedWorkoutAdapter featuredAdapter;
    private BookingAdapter bookingAdapter;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        tvGreeting = v.findViewById(R.id.tvGreeting);
        tvUserName = v.findViewById(R.id.tvUserName);
        tvStatWorkouts = v.findViewById(R.id.tvStatWorkouts);
        tvStatMinutes = v.findViewById(R.id.tvStatMinutes);
        tvStatStreak = v.findViewById(R.id.tvStatStreak);
        tvEmptyBookings = v.findViewById(R.id.tvEmptyBookings);

        tvGreeting.setText(greetingForHour());

        RecyclerView rvFeatured = v.findViewById(R.id.rvFeatured);
        featuredAdapter = new FeaturedWorkoutAdapter(this::openDetail);
        rvFeatured.setAdapter(featuredAdapter);

        RecyclerView rvBookings = v.findViewById(R.id.rvBookings);
        bookingAdapter = new BookingAdapter();
        rvBookings.setAdapter(bookingAdapter);

        v.findViewById(R.id.actionBook).setOnClickListener(x -> goToTab(R.id.nav_workouts));
        v.findViewById(R.id.actionWorkouts).setOnClickListener(x -> goToTab(R.id.nav_workouts));
        v.findViewById(R.id.actionHistory).setOnClickListener(x -> openHistory());
        v.findViewById(R.id.tvViewAllBookings).setOnClickListener(x -> openHistory());
        v.findViewById(R.id.btnNotifications).setOnClickListener(x -> goToTab(R.id.nav_progress));

        loadWorkouts();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUser();
        loadBookings();
    }

    private void loadUser() {
        repo.getUserProfile(new Callback<User>() {
            @Override public void onSuccess(User u) {
                if (!isAdded()) return;
                tvUserName.setText(u.getFirstName());
                tvStatWorkouts.setText(String.valueOf(u.getTotalWorkouts()));
                tvStatMinutes.setText(String.valueOf(u.getTotalMinutes()));
                tvStatStreak.setText(String.valueOf(u.getStreak()));
            }
            @Override public void onError(String message) { /* keep defaults */ }
        });
    }

    private void loadWorkouts() {
        repo.getWorkouts(new Callback<List<Workout>>() {
            @Override public void onSuccess(List<Workout> data) {
                if (isAdded()) featuredAdapter.submit(data);
            }
            @Override public void onError(String message) { }
        });
    }

    private void loadBookings() {
        repo.getBookings(new Callback<List<Booking>>() {
            @Override public void onSuccess(List<Booking> data) {
                if (!isAdded()) return;
                List<Booking> top = data.size() > 3 ? data.subList(0, 3) : data;
                bookingAdapter.submit(top);
                tvEmptyBookings.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String message) {
                if (isAdded()) tvEmptyBookings.setVisibility(View.VISIBLE);
            }
        });
    }

    private void openDetail(Workout w) {
        Intent intent = new Intent(getActivity(), ExerciseDetailActivity.class);
        intent.putExtra(ExerciseDetailActivity.EXTRA_WORKOUT, w);
        startActivity(intent);
    }

    private void openHistory() {
        startActivity(new Intent(getActivity(), com.example.fastfit.ui.BookingHistoryActivity.class));
    }

    private void goToTab(int navId) {
        if (getActivity() instanceof HomeActivity) {
            ((HomeActivity) getActivity()).selectTab(navId);
        }
    }

    private String greetingForHour() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour < 12) return getString(R.string.greeting_morning);
        if (hour < 18) return getString(R.string.greeting_afternoon);
        return getString(R.string.greeting_evening);
    }
}
