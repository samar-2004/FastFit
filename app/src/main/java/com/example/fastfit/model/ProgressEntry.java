package com.example.fastfit.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/** A logged activity (collection: users/{uid}/progress/{id}). */
public class ProgressEntry {
    public static final String TYPE_WORKOUT = "workout";
    public static final String TYPE_WEIGHT = "weight";

    private String id;
    private String type;          // "workout" | "weight"
    private String label;         // workout name or note
    private double value;         // minutes (workout) or kg (weight)
    @ServerTimestamp
    private Date timestamp;

    public ProgressEntry() { }

    public ProgressEntry(String type, String label, double value) {
        this.type = type;
        this.label = label;
        this.value = value;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public double getValue() { return value; }
    public void setValue(double value) { this.value = value; }

    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }
}
