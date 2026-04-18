package com.example.athleo;

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

public class TechDirectorDashboardFragment extends Fragment {
    private WebView webView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tech_director_dashboard, container, false);

        webView = view.findViewById(R.id.webViewDashboard);
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
        webView.loadUrl("file:///android_asset/stitch/director_dashboard_2/code.html");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null) {
            webView.evaluateJavascript("if(window.Android) { window.Android.loadUserProfile(); window.Android.loadFinancials(); }", null);
        }
    }
}
