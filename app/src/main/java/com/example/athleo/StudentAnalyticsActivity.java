package com.example.athleo;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class StudentAnalyticsActivity extends AppCompatActivity {

    private WebView webView;
    private String studentId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_student_analytics);

        studentId = getIntent().getStringExtra("STUDENT_ID");

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
        webView.addJavascriptInterface(new WebAppInterface(this, webView), "Android");
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String id = getIntent().getStringExtra("STUDENT_ID");
                String tab = getIntent().getStringExtra("INITIAL_TAB");
                if (tab == null) tab = "overview";
                webView.evaluateJavascript("window.studentId = '" + (id != null ? id : "me") + "';", null);
                webView.evaluateJavascript("if(window.switchTab) window.switchTab('" + tab + "');", null);
            }
        });
        webView.loadUrl("file:///android_asset/stitch/student_analytics_dark/code.html");
    }
}
