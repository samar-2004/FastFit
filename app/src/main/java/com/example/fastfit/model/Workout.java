package com.example.fastfit.model;

import com.google.firebase.firestore.Exclude;

import java.io.Serializable;

/** A workout/exercise (collection: workouts/{id}). */
public class Workout implements Serializable {
    private String id;
    private String name;
    private String muscleGroup;
    private String info;          // short "3 Sets x 12 Reps | 45 min"
    private String description;
    private String youtubeId;     // in-app tutorial video id
    private String imageUrl;      // optional remote image
    private int durationMin;
    private int sets;
    private int reps;
    private int calories;
    private int order;

    public Workout() { }

    public Workout(String id, String name, String muscleGroup, String info,
                   String description, String youtubeId, int durationMin,
                   int sets, int reps, int calories) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.info = info;
        this.description = description;
        this.youtubeId = youtubeId;
        this.durationMin = durationMin;
        this.sets = sets;
        this.reps = reps;
        this.calories = calories;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getInfo() { return info; }
    public void setInfo(String info) { this.info = info; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getYoutubeId() { return youtubeId; }
    public void setYoutubeId(String youtubeId) { this.youtubeId = youtubeId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public int getSets() { return sets; }
    public void setSets(int sets) { this.sets = sets; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public int getOrder() { return order; }
    public void setOrder(int order) { this.order = order; }

    /** Falls back to the YouTube thumbnail when no explicit image is set. */
    @Exclude
    public String getDisplayImage() {
        if (imageUrl != null && !imageUrl.isEmpty()) return imageUrl;
        if (youtubeId != null && !youtubeId.isEmpty())
            return "https://img.youtube.com/vi/" + youtubeId + "/hqdefault.jpg";
        return null;
    }
}
