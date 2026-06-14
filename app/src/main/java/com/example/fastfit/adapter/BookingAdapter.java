package com.example.fastfit.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfit.R;
import com.example.fastfit.model.Booking;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.VH> {

    private final List<Booking> items = new ArrayList<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy", Locale.US);

    public void submit(List<Booking> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Booking b = items.get(position);
        h.workout.setText(b.getWorkoutName());
        h.slots.setText(b.getSlots() != null ? TextUtils.join(", ", b.getSlots()) : "—");
        h.date.setText(b.getCreatedAt() != null ? fmt.format(b.getCreatedAt()) : "");
        h.total.setText(String.format(Locale.US, "$%.2f", b.getNutritionTotal()));
        String status = b.getStatus() != null ? b.getStatus() : "upcoming";
        h.status.setText(status.substring(0, 1).toUpperCase() + status.substring(1));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView workout, slots, date, total, status;
        VH(@NonNull View v) {
            super(v);
            workout = v.findViewById(R.id.tvBookingWorkout);
            slots = v.findViewById(R.id.tvBookingSlots);
            date = v.findViewById(R.id.tvBookingDate);
            total = v.findViewById(R.id.tvBookingTotal);
            status = v.findViewById(R.id.tvBookingStatus);
        }
    }
}
