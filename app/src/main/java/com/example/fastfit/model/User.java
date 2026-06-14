package com.example.fastfit.model;

import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

/** Firestore user profile document (collection: users/{uid}). */
public class User {
    private String uid;
    private String name;
    private String email;
    private String photoUrl;
    private String goal;          // e.g. "Build Muscle", "Lose Weight"
    private String gender;
    private double weight;        // kg
    private double height;        // cm
    private int totalWorkouts;
    private int totalMinutes;
    private int streak;
    @ServerTimestamp
    private Date createdAt;

    public User() { }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.goal = "Stay Fit";
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getGoal() { return goal; }
    public void setGoal(String goal) { this.goal = goal; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public double getWeight() { return weight; }
    public void setWeight(double weight) { this.weight = weight; }

    public double getHeight() { return height; }
    public void setHeight(double height) { this.height = height; }

    public int getTotalWorkouts() { return totalWorkouts; }
    public void setTotalWorkouts(int totalWorkouts) { this.totalWorkouts = totalWorkouts; }

    public int getTotalMinutes() { return totalMinutes; }
    public void setTotalMinutes(int totalMinutes) { this.totalMinutes = totalMinutes; }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }

    @Exclude
    public String getFirstName() {
        if (name == null || name.trim().isEmpty()) return "Athlete";
        return name.trim().split("\\s+")[0];
    }
}
