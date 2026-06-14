package com.example.fastfit.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.fastfit.R;
import com.example.fastfit.adapter.BookingAdapter;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.model.Booking;

import java.util.List;

public class BookingHistoryActivity extends AppCompatActivity {

    private final Repo repo = Repo.get();
    private BookingAdapter adapter;
    private SwipeRefreshLayout swipeRefresh;
    private ProgressBar progressBar;
    private View emptyState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, bars.bottom);
            return insets;
        });

        swipeRefresh = findViewById(R.id.swipeRefresh);
        progressBar = findViewById(R.id.progressBar);
        emptyState = findViewById(R.id.emptyState);
        RecyclerView rv = findViewById(R.id.rvBookings);
        adapter = new BookingAdapter();
        rv.setAdapter(adapter);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        swipeRefresh.setColorSchemeResources(R.color.red);
        swipeRefresh.setOnRefreshListener(this::loadBookings);

        loadBookings();
    }

    private void loadBookings() {
        if (!swipeRefresh.isRefreshing()) progressBar.setVisibility(View.VISIBLE);
        repo.getBookings(new Callback<List<Booking>>() {
            @Override public void onSuccess(List<Booking> data) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                adapter.submit(data);
                emptyState.setVisibility(data.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onError(String message) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                emptyState.setVisibility(View.VISIBLE);
            }
        });
    }
}
