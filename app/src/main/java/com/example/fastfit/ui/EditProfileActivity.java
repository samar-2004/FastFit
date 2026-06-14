package com.example.fastfit.ui;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.fastfit.R;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.model.User;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity {

    private final Repo repo = Repo.get();
    private EditText etName, etGoal, etWeight, etHeight;
    private CircleImageView imgAvatar;
    private Button btnSave;
    private ProgressBar progressBar;
    private Uri pickedImage;

    private final ActivityResultLauncher<PickVisualMediaRequest> imagePicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    pickedImage = uri;
                    Glide.with(this).load(uri).into(imgAvatar);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, bars.top, 0, bars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etGoal = findViewById(R.id.etGoal);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        imgAvatar = findViewById(R.id.imgAvatar);
        btnSave = findViewById(R.id.btnSave);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnChangePhoto).setOnClickListener(v -> imagePicker.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                        .build()));
        btnSave.setOnClickListener(v -> save());

        loadCurrent();
    }

    private void loadCurrent() {
        repo.getUserProfile(new Callback<User>() {
            @Override public void onSuccess(User u) {
                etName.setText(u.getName());
                etGoal.setText(u.getGoal());
                if (u.getWeight() > 0) etWeight.setText(String.valueOf(u.getWeight()));
                if (u.getHeight() > 0) etHeight.setText(String.valueOf(u.getHeight()));
                if (u.getPhotoUrl() != null && !u.getPhotoUrl().isEmpty()) {
                    Glide.with(EditProfileActivity.this).load(u.getPhotoUrl())
                            .placeholder(R.drawable.ic_person).into(imgAvatar);
                }
            }
            @Override public void onError(String message) { }
        });
    }

    private void save() {
        String name = etName.getText().toString().trim();
        if (name.isEmpty()) { etName.setError("Name is required"); return; }
        setLoading(true);

        if (pickedImage != null) {
            repo.uploadAvatar(pickedImage, new Callback<String>() {
                @Override public void onSuccess(String url) { persist(name, url); }
                @Override public void onError(String message) {
                    // Still save text fields even if the image upload fails.
                    Toast.makeText(EditProfileActivity.this,
                            "Photo upload failed, saving details only.", Toast.LENGTH_SHORT).show();
                    persist(name, null);
                }
            });
        } else {
            persist(name, null);
        }
    }

    private void persist(String name, String photoUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("name", name);
        updates.put("goal", textOrDefault(etGoal, "Stay Fit"));
        updates.put("weight", parseOrZero(etWeight));
        updates.put("height", parseOrZero(etHeight));
        if (photoUrl != null) updates.put("photoUrl", photoUrl);

        repo.updateUserProfile(updates, new Callback<Void>() {
            @Override public void onSuccess(Void data) {
                setLoading(false);
                Toast.makeText(EditProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override public void onError(String message) {
                setLoading(false);
                Toast.makeText(EditProfileActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private String textOrDefault(EditText et, String def) {
        String v = et.getText().toString().trim();
        return v.isEmpty() ? def : v;
    }

    private double parseOrZero(EditText et) {
        try { return Double.parseDouble(et.getText().toString().trim()); }
        catch (NumberFormatException e) { return 0; }
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSave.setEnabled(!loading);
    }
}
