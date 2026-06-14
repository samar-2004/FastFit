package com.example.fastfit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fastfit.R;
import com.example.fastfit.model.ProgressEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.List;

public class ProgressAdapter extends RecyclerView.Adapter<ProgressAdapter.VH> {

    private final List<ProgressEntry> items = new ArrayList<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.US);

    public void submit(List<ProgressEntry> data) {
        items.clear();
        if (data != null) items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_progress, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ProgressEntry p = items.get(position);
        h.label.setText(p.getLabel());
        h.date.setText(p.getTimestamp() != null ? fmt.format(p.getTimestamp()) : "");
        if (ProgressEntry.TYPE_WEIGHT.equals(p.getType())) {
            h.value.setText(String.format(Locale.US, "%.1f kg", p.getValue()));
            h.icon.setImageResource(R.drawable.ic_progress);
        } else {
            h.value.setText(String.format(Locale.US, "%d min", (int) p.getValue()));
            h.icon.setImageResource(R.drawable.ic_fire);
        }
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView icon;
        final TextView label, date, value;
        VH(@NonNull View v) {
            super(v);
            icon = v.findViewById(R.id.imgProgressIcon);
            label = v.findViewById(R.id.tvProgressLabel);
            date = v.findViewById(R.id.tvProgressDate);
            value = v.findViewById(R.id.tvProgressValue);
        }
    }
}
