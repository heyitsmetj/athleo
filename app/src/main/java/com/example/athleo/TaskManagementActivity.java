package com.example.athleo;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TaskManagementActivity extends AppCompatActivity {

    private WebView webView;
    private String pendingTaskId;

    public void setPendingTaskId(String id) {
        this.pendingTaskId = id;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_management);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        webView = findViewById(R.id.webView);
        setupWebView();
    }

    private void setupWebView() {
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new android.webkit.WebChromeClient());
        webView.addJavascriptInterface(new WebAppInterface(this, webView), "Android");
        webView.loadUrl("file:///android_asset/stitch/task_management_hub_dark/code.html");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && pendingTaskId != null) {
            Uri fileUri = data.getData();
            if (fileUri != null) {
                // Persistent permissions for cross-user/cross-session access on same device
                try {
                    getContentResolver().takePersistableUriPermission(fileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } catch (Exception e) {}
                
                // We store the full Uri to allow opening it from the trainer interface
                FirestoreRepository.getInstance().completeTask(pendingTaskId, fileUri.toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Task Completed with File!", Toast.LENGTH_SHORT).show();
                            webView.evaluateJavascript("if(window.loadTasks) loadTasks();", null);
                        }
                    });
            }
            pendingTaskId = null;
        }
    }
}
