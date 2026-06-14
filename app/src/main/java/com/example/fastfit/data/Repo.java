package com.example.fastfit.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.fastfit.model.Booking;
import com.example.fastfit.model.ProgressEntry;
import com.example.fastfit.model.User;
import com.example.fastfit.model.Workout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Single entry-point to the Firebase backend (Auth + Cloud Firestore).
 *
 * Firestore layout:
 *   users/{uid}                         -> User profile
 *   users/{uid}/bookings/{bookingId}    -> Booking
 *   users/{uid}/progress/{entryId}      -> ProgressEntry
 *   workouts/{workoutId}                -> Workout catalog (auto-seeded on first run)
 */
public class Repo {

    private static Repo instance;

    private final FirebaseAuth auth;
    private final FirebaseFirestore db;

    private Repo() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public static synchronized Repo get() {
        if (instance == null) instance = new Repo();
        return instance;
    }

    // ----------------------------------------------------------------- Auth

    @Nullable
    public FirebaseUser currentUser() {
        return auth.getCurrentUser();
    }

    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    public String uid() {
        FirebaseUser u = auth.getCurrentUser();
        return u != null ? u.getUid() : null;
    }

    public void signUp(final String name, final String email, String password,
                       final Callback<FirebaseUser> cb) {
        auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(result -> {
                    FirebaseUser fbUser = result.getUser();
                    if (fbUser == null) {
                        cb.onError("Account created but user is null.");
                        return;
                    }
                    User profile = new User(fbUser.getUid(), name.trim(), email.trim());
                    db.collection("users").document(fbUser.getUid())
                            .set(profile, SetOptions.merge())
                            .addOnSuccessListener(v -> cb.onSuccess(fbUser))
                            .addOnFailureListener(e -> cb.onSuccess(fbUser)); // auth ok even if doc write retries
                })
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    public void signIn(String email, String password, final Callback<FirebaseUser> cb) {
        auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener(result -> cb.onSuccess(result.getUser()))
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    public void signInWithGoogle(String idToken, final Callback<FirebaseUser> cb) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(result -> {
                    FirebaseUser fbUser = result.getUser();
                    if (fbUser == null) {
                        cb.onError("Google sign-in failed.");
                        return;
                    }
                    // Ensure a profile document exists.
                    ensureUserDoc(fbUser, () -> cb.onSuccess(fbUser));
                })
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    private void ensureUserDoc(final FirebaseUser fbUser, final Runnable done) {
        db.collection("users").document(fbUser.getUid()).get()
                .addOnSuccessListener(snap -> {
                    if (!snap.exists()) {
                        String name = fbUser.getDisplayName() != null ? fbUser.getDisplayName() : "Athlete";
                        User profile = new User(fbUser.getUid(), name, fbUser.getEmail());
                        if (fbUser.getPhotoUrl() != null)
                            profile.setPhotoUrl(fbUser.getPhotoUrl().toString());
                        db.collection("users").document(fbUser.getUid())
                                .set(profile, SetOptions.merge())
                                .addOnCompleteListener(t -> done.run());
                    } else {
                        done.run();
                    }
                })
                .addOnFailureListener(e -> done.run());
    }

    public void sendPasswordReset(String email, final Callback<Void> cb) {
        auth.sendPasswordResetEmail(email.trim())
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    public void signOut() {
        auth.signOut();
    }

    // ------------------------------------------------------------- User doc

    public void getUserProfile(final Callback<User> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        db.collection("users").document(uid).get()
                .addOnSuccessListener(snap -> {
                    User user = snap.toObject(User.class);
                    if (user == null) {
                        FirebaseUser fb = currentUser();
                        user = new User(uid, fb != null && fb.getDisplayName() != null
                                ? fb.getDisplayName() : "Athlete",
                                fb != null ? fb.getEmail() : "");
                    }
                    cb.onSuccess(user);
                })
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    public void updateUserProfile(Map<String, Object> updates, final Callback<Void> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        db.collection("users").document(uid).set(updates, SetOptions.merge())
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    /** Bump the user's aggregate stats after completing a session. */
    public void addWorkoutStats(int minutes, final Callback<Void> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        Map<String, Object> updates = new HashMap<>();
        updates.put("totalWorkouts", FieldValue.increment(1));
        updates.put("totalMinutes", FieldValue.increment(minutes));
        db.collection("users").document(uid).set(updates, SetOptions.merge())
                .addOnSuccessListener(v -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    /** Uploads a profile photo to Storage and returns its download URL. */
    public void uploadAvatar(Uri imageUri, final Callback<String> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        StorageReference ref = FirebaseStorage.getInstance().getReference()
                .child("avatars/" + uid + ".jpg");
        ref.putFile(imageUri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful() && task.getException() != null) {
                        throw task.getException();
                    }
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(uri -> cb.onSuccess(uri.toString()))
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    // ------------------------------------------------------------ Workouts

    public void getWorkouts(final Callback<List<Workout>> cb) {
        db.collection("workouts").orderBy("order", Query.Direction.ASCENDING).get()
                .addOnSuccessListener(query -> {
                    List<Workout> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Workout w = doc.toObject(Workout.class);
                        w.setId(doc.getId());
                        list.add(w);
                    }
                    if (list.isEmpty()) {
                        // First run: seed the catalog, return the local defaults immediately.
                        List<Workout> seed = defaultWorkouts();
                        seedWorkouts(seed);
                        cb.onSuccess(seed);
                    } else {
                        cb.onSuccess(list);
                    }
                })
                .addOnFailureListener(e -> cb.onSuccess(defaultWorkouts())); // offline-friendly fallback
    }

    private void seedWorkouts(List<Workout> seed) {
        for (Workout w : seed) {
            db.collection("workouts").document(w.getId()).set(w, SetOptions.merge());
        }
    }

    /** The starter catalog — also used as an offline fallback. */
    public List<Workout> defaultWorkouts() {
        List<Workout> list = new ArrayList<>();
        Workout legPress = new Workout("leg_press", "Leg Press", "Legs",
                "4 Sets × 12 Reps | 45 min",
                "The leg press targets your quads, hamstrings and glutes. Keep your back flat against the pad and push through your heels for full activation.",
                "IZxyjW7MPJQ", 45, 4, 12, 320);
        legPress.setOrder(1);
        Workout lat = new Workout("lat_pulldown", "Lat Pulldown", "Back",
                "3 Sets × 10 Reps | 30 min",
                "Lat pulldowns build a wide, strong back. Pull the bar to your upper chest while squeezing your shoulder blades together.",
                "CAwf7n6Luuc", 30, 3, 10, 210);
        lat.setOrder(2);
        Workout chest = new Workout("chest_press", "Chest Press", "Chest",
                "4 Sets × 8 Reps | 40 min",
                "The chest press develops your pectorals, shoulders and triceps. Control the weight on the way down and press explosively.",
                "xUm0BiZCWlQ", 40, 4, 8, 280);
        chest.setOrder(3);
        Workout squat = new Workout("barbell_squat", "Barbell Squat", "Legs",
                "5 Sets × 5 Reps | 50 min",
                "The king of leg exercises. Brace your core, keep your chest up and drive through the floor to stand.",
                "ultWZbUMPL8", 50, 5, 5, 400);
        squat.setOrder(4);
        Workout deadlift = new Workout("deadlift", "Deadlift", "Back",
                "4 Sets × 6 Reps | 45 min",
                "A full-body strength builder. Keep the bar close, hinge at the hips and lift with a neutral spine.",
                "op9kVnSso6Q", 45, 4, 6, 380);
        deadlift.setOrder(5);
        Workout shoulder = new Workout("shoulder_press", "Shoulder Press", "Shoulders",
                "4 Sets × 10 Reps | 30 min",
                "Press overhead to build strong, capped shoulders. Avoid arching your lower back as you press.",
                "qEwKCR5JCog", 30, 4, 10, 240);
        shoulder.setOrder(6);
        list.addAll(Arrays.asList(legPress, lat, chest, squat, deadlift, shoulder));
        return list;
    }

    // ------------------------------------------------------------- Bookings

    public void createBooking(Booking booking, final Callback<String> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        booking.setUserId(uid);
        db.collection("users").document(uid).collection("bookings")
                .add(booking)
                .addOnSuccessListener(ref -> {
                    ref.update("id", ref.getId());
                    cb.onSuccess(ref.getId());
                })
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    public void getBookings(final Callback<List<Booking>> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        db.collection("users").document(uid).collection("bookings")
                .orderBy("createdAt", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(query -> {
                    List<Booking> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        Booking b = doc.toObject(Booking.class);
                        b.setId(doc.getId());
                        list.add(b);
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    // ------------------------------------------------------------- Progress

    public void addProgress(ProgressEntry entry, final Callback<Void> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        db.collection("users").document(uid).collection("progress")
                .add(entry)
                .addOnSuccessListener(ref -> cb.onSuccess(null))
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    public void getProgress(final Callback<List<ProgressEntry>> cb) {
        String uid = uid();
        if (uid == null) { cb.onError("Not signed in."); return; }
        db.collection("users").document(uid).collection("progress")
                .orderBy("timestamp", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(query -> {
                    List<ProgressEntry> list = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : query) {
                        ProgressEntry p = doc.toObject(ProgressEntry.class);
                        p.setId(doc.getId());
                        list.add(p);
                    }
                    cb.onSuccess(list);
                })
                .addOnFailureListener(e -> cb.onError(friendly(e.getMessage())));
    }

    // -------------------------------------------------------------- Helpers

    /** Turn raw Firebase exception text into something user-facing. */
    private String friendly(@NonNull String raw) {
        if (raw == null) return "Something went wrong. Please try again.";
        String r = raw.toLowerCase();
        if (r.contains("password is invalid") || r.contains("invalid-credential")
                || r.contains("incorrect"))
            return "Incorrect email or password.";
        if (r.contains("no user record") || r.contains("user-not-found"))
            return "No account found with this email.";
        if (r.contains("email address is already") || r.contains("email-already"))
            return "An account already exists with this email.";
        if (r.contains("badly formatted") || r.contains("invalid-email"))
            return "Please enter a valid email address.";
        if (r.contains("at least 6") || r.contains("weak-password"))
            return "Password should be at least 6 characters.";
        if (r.contains("network"))
            return "Network error. Check your connection and try again.";
        return raw;
    }
}
