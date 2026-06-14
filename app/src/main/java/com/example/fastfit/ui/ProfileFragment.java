package com.example.fastfit.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.fastfit.R;
import com.example.fastfit.auth.LoginActivity;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private final Repo repo = Repo.get();
    private CircleImageView imgAvatar;
    private TextView tvName, tvEmail, tvGoal;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        imgAvatar = v.findViewById(R.id.imgAvatar);
        tvName = v.findViewById(R.id.tvName);
        tvEmail = v.findViewById(R.id.tvEmail);
        tvGoal = v.findViewById(R.id.tvGoal);

        v.findViewById(R.id.rowEditProfile).setOnClickListener(x ->
                startActivity(new Intent(getActivity(), EditProfileActivity.class)));
        v.findViewById(R.id.rowBookings).setOnClickListener(x ->
                startActivity(new Intent(getActivity(), BookingHistoryActivity.class)));
        v.findViewById(R.id.rowSettings).setOnClickListener(x ->
                startActivity(new Intent(getActivity(), SettingsActivity.class)));
        v.findViewById(R.id.rowAbout).setOnClickListener(x -> showAbout());
        v.findViewById(R.id.btnLogout).setOnClickListener(x -> confirmLogout());
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUser();
    }

    private void loadUser() {
        repo.getUserProfile(new Callback<User>() {
            @Override public void onSuccess(User u) {
                if (!isAdded()) return;
                tvName.setText(u.getName() != null ? u.getName() : "Athlete");
                tvEmail.setText(u.getEmail());
                tvGoal.setText("🎯 " + (u.getGoal() != null ? u.getGoal() : "Stay Fit"));
                if (u.getPhotoUrl() != null && !u.getPhotoUrl().isEmpty()) {
                    Glide.with(ProfileFragment.this)
                            .load(u.getPhotoUrl())
                            .placeholder(R.drawable.ic_person)
                            .into(imgAvatar);
                }
            }
            @Override public void onError(String message) { }
        });
    }

    private void showAbout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("About FASTFit")
                .setMessage("FASTFit — Your Smart Gym Companion.\n\n"
                        + "Book machine-based workouts, watch in-app tutorials, plan your "
                        + "nutrition and track your progress.\n\nVersion 1.0\n© 2026 FASTFit")
                .setPositiveButton("OK", null)
                .show();
    }

    private void confirmLogout() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Log out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Log Out", (d, w) -> {
                    repo.signOut();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    requireActivity().finish();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
