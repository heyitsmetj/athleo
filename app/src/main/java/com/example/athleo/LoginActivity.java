package com.example.athleo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.webkit.WebSettings;
import android.webkit.WebView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = findViewById(R.id.webViewLogin);
        progressBar = findViewById(R.id.progressBar);

        if (mAuth.getCurrentUser() != null) {
            checkUserRoleAndRedirect(mAuth.getCurrentUser().getUid());
        }

        setupWebView();
    }

    private void setupWebView() {
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this, webView), "Android");
        webView.loadUrl("file:///android_asset/stitch/login_screen_dark/code.html");
    }
    
    private void sendErrorToWeb(String message) {
        runOnUiThread(() -> {
            webView.evaluateJavascript("if(window.loginError) window.loginError('" + message + "');", null);
        });
    }

    public void loginFromWeb(String email, String password) {
        runOnUiThread(() -> {
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                sendErrorToWeb("Email and Password are required");
                return;
            }

            // Disable native progress bar as requested by user
            progressBar.setVisibility(View.GONE);
            
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String uid = mAuth.getCurrentUser().getUid();
                        checkUserRoleAndRedirect(uid);
                    } else {
                        progressBar.setVisibility(View.GONE);
                        String errorMsg = "Login failed. Please check your credentials.";
                        if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                            errorMsg = "No account found with this email.";
                        } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            errorMsg = "Invalid email or password.";
                        }
                        sendErrorToWeb(errorMsg.replace("'", "\\'"));
                    }
                });
        });
    }

    private void checkUserRoleAndRedirect(String uid) {
        // Ensure progress bar is hidden
        progressBar.setVisibility(View.GONE);
        db.collection("Users").document(uid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String role = document.getString("role");
                            redirectBasedOnRole(role);
                        } else {
                            progressBar.setVisibility(View.GONE);
                            sendErrorToWeb("User profile not found. Contact Admin.");
                            mAuth.signOut();
                        }
                    } else {
                        progressBar.setVisibility(View.GONE);
                        sendErrorToWeb("Network error. Failed to fetch user role.");
                    }
                });
    }

    private void redirectBasedOnRole(String role) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
