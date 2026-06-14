package com.example.fastfit.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** A booked session (collection: users/{uid}/bookings/{id}). */
public class Booking {
    private String id;
    private String userId;
    private String workoutId;
    private String workoutName;
    private List<String> slots = new ArrayList<>();
    private double nutritionTotal;
    private String status;        // "upcoming" | "completed"
    @ServerTimestamp
    private Date createdAt;

    public Booking() { }

    public Booking(String userId, String workoutId, String workoutName,
                   List<String> slots, double nutritionTotal) {
        this.userId = userId;
        this.workoutId = workoutId;
        this.workoutName = workoutName;
        this.slots = slots;
        this.nutritionTotal = nutritionTotal;
        this.status = "upcoming";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getWorkoutId() { return workoutId; }
    public void setWorkoutId(String workoutId) { this.workoutId = workoutId; }

    public String getWorkoutName() { return workoutName; }
    public void setWorkoutName(String workoutName) { this.workoutName = workoutName; }

    public List<String> getSlots() { return slots; }
    public void setSlots(List<String> slots) { this.slots = slots; }

    public double getNutritionTotal() { return nutritionTotal; }
    public void setNutritionTotal(double nutritionTotal) { this.nutritionTotal = nutritionTotal; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
}
