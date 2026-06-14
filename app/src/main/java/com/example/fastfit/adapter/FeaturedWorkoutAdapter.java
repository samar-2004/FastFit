package com.example.fastfit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fastfit.R;
import com.example.fastfit.model.Workout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Compact horizontal cards for the home "Featured" carousel. */
public class FeaturedWorkoutAdapter extends RecyclerView.Adapter<FeaturedWorkoutAdapter.VH> {

    public interface Listener { void onOpen(Workout workout); }

    private final List<Workout> items = new ArrayList<>();
    private final Listener listener;

    public FeaturedWorkoutAdapter(Listener listener) { this.listener = listener; }

    public void submit(List<Workout> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_workout_featured, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Workout w = items.get(position);
        h.name.setText(w.getName());
        h.info.setText(String.format(Locale.US, "%d min • %d kcal",
                w.getDurationMin(), w.getCalories()));
        Glide.with(h.img.getContext())
                .load(w.getDisplayImage())
                .placeholder(R.color.light_grey)
                .centerCrop()
                .into(h.img);
        h.itemView.setOnClickListener(v -> listener.onOpen(w));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView img;
        final TextView name, info;
        VH(@NonNull View v) {
            super(v);
            img = v.findViewById(R.id.imgWorkout);
            name = v.findViewById(R.id.tvWorkoutName);
            info = v.findViewById(R.id.tvWorkoutInfo);
        }
    }
}
