package com.example.athleo;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.content.pm.PackageManager;
import android.Manifest;
import android.os.Build;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private ProgressBar progressBar;
    private com.google.firebase.firestore.ListenerRegistration announcementListener;
    private com.google.firebase.firestore.ListenerRegistration taskListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressBar = findViewById(R.id.main_progress_bar);
        
        requestNotificationPermission();

        loadUserRole();
        setupGlobalNotificationListeners();
    }

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }
    }

    private void setupGlobalNotificationListeners() {
        FirebaseMessaging.getInstance().subscribeToTopic("announcements");

        long loginTime = System.currentTimeMillis();
        announcementListener = db.collection("Announcements")
                .whereGreaterThan("timestamp", loginTime)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    for (com.google.firebase.firestore.DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                            String title = dc.getDocument().getString("title");
                            String body = dc.getDocument().getString("message");
                            if (title != null && body != null) {
                                showLocalNotification(title, body);
                            }
                        }
                    }
                });
    }

    private void showLocalNotification(String title, String messageBody) {
        String CHANNEL_ID = "athleo_local_alerts";
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        android.app.PendingIntent pendingIntent = android.app.PendingIntent.getActivity(this, 0, intent,
                android.app.PendingIntent.FLAG_ONE_SHOT | android.app.PendingIntent.FLAG_IMMUTABLE);

        androidx.core.app.NotificationCompat.Builder notificationBuilder =
                new androidx.core.app.NotificationCompat.Builder(this, CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            android.app.NotificationChannel channel = new android.app.NotificationChannel(CHANNEL_ID,
                    "Athleo Dashboard Alerts",
                    android.app.NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify((int) System.currentTimeMillis() % 10000, notificationBuilder.build());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (announcementListener != null) {
            announcementListener.remove();
        }
        if (taskListener != null) {
            taskListener.remove();
        }
    }

    private void setupTaskNotificationListener(String userId, String userRole) {
        if (!"Student".equals(userRole)) return; // Only notify students about new tasks

        long loginTime = System.currentTimeMillis();
        taskListener = db.collection("Tasks")
                .whereGreaterThan("timestamp", loginTime)
                .addSnapshotListener((value, error) -> {
                    if (error != null || value == null) return;
                    for (com.google.firebase.firestore.DocumentChange dc : value.getDocumentChanges()) {
                        if (dc.getType() == com.google.firebase.firestore.DocumentChange.Type.ADDED) {
                            String assignedTo = dc.getDocument().getString("assignedTo");
                            if ("All".equals(assignedTo) || userId.equals(assignedTo)) {
                                String title = dc.getDocument().getString("title");
                                if (title != null) {
                                    showLocalNotification("New Task Assigned", "You have a new task: " + title);
                                }
                            }
                        }
                    }
                });
    }

    private void loadUserRole() {
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
            progressBar.setVisibility(View.GONE);
            if (documentSnapshot.exists()) {
                String role = documentSnapshot.getString("role");
                setupDashboardForRole(role);
                setupTaskNotificationListener(uid, role);
            } else {
                Toast.makeText(this, "Role not found. Please contact admin.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Failed to load user role.", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupDashboardForRole(String role) {
        if (role == null) {
            return;
        }

        androidx.fragment.app.Fragment selectedFragment = null;

        switch (role) {
            case "Technical Director":
                selectedFragment = new TechDirectorDashboardFragment();
                break;
            case "Head Coach":
                selectedFragment = new HeadCoachDashboardFragment();
                break;
            case "Trainer":
                selectedFragment = new TrainerDashboardFragment();
                break;
            case "Student":
                selectedFragment = new StudentDashboardFragment();
                break;
            default:
                break;
        }

        if (selectedFragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            mAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}