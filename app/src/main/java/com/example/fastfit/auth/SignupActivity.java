package com.example.fastfit.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.fastfit.HomeActivity;
import com.example.fastfit.R;
import com.example.fastfit.data.Callback;
import com.example.fastfit.data.Repo;
import com.example.fastfit.util.PasswordToggle;
import com.example.fastfit.util.Validate;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {

    private EditText etName, etEmail, etPassword, etConfirmPassword;
    private Button btnSignUp;
    private ProgressBar progressBar;
    private final Repo repo = Repo.get();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressBar = findViewById(R.id.progressBar);
        ImageView toggle = findViewById(R.id.btnTogglePassword);
        PasswordToggle.attach(toggle, etPassword);

        btnSignUp.setOnClickListener(v -> attemptSignup());
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.tvGoToLogin).setOnClickListener(v -> finish());
    }

    private void attemptSignup() {
        if (!Validate.notEmpty(etName, "Name")
                || !Validate.email(etEmail)
                || !Validate.password(etPassword)) return;

        if (!etPassword.getText().toString().equals(etConfirmPassword.getText().toString())) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        setLoading(true);
        repo.signUp(etName.getText().toString(), etEmail.getText().toString(),
                etPassword.getText().toString(), new Callback<FirebaseUser>() {
                    @Override public void onSuccess(FirebaseUser data) {
                        Toast.makeText(SignupActivity.this,
                                "Welcome to FASTFit!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                    @Override public void onError(String message) {
                        setLoading(false);
                        Toast.makeText(SignupActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        btnSignUp.setEnabled(!loading);
    }
}
