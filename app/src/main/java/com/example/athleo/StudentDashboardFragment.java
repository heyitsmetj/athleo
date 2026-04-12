package com.example.athleo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class StudentDashboardFragment extends Fragment {
    private WebView webView;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_student_dashboard, container, false);

        webView = view.findViewById(R.id.webViewDashboard);
        db = FirebaseFirestore.getInstance();

        setupWebView();

        return view;
    }

    private void setupWebView() {
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new android.webkit.WebChromeClient());
        webView.addJavascriptInterface(new WebAppInterface(getContext(), webView), "Android");
        webView.loadUrl("file:///android_asset/stitch/student_dashboard_dark_3/code.html");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.evaluateJavascript("if(window.Android) window.Android.loadUserProfile();", null);
        }
    }
}
