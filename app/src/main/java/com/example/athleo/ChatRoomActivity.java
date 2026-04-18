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

public class ChatRoomActivity extends AppCompatActivity {

    private WebView webView;
    private String groupId;
    private String groupName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_room);

        groupId = getIntent().getStringExtra("GROUP_ID");
        groupName = getIntent().getStringExtra("GROUP_NAME");

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
        
        // Pass the activity to WebAppInterface so we can extract groupId if needed
        WebAppInterface webAppInterface = new WebAppInterface(this, webView);
        webView.addJavascriptInterface(webAppInterface, "Android");
        
        webView.loadUrl("file:///android_asset/stitch/community_chat_room_dark/code.html");

        // Inject the details into JS once loaded
        webView.setWebViewClient(new android.webkit.WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                webView.evaluateJavascript("if(window.initChat) window.initChat('" + groupId + "', '" + groupName + "')", null);
            }
        });
    }

    public String getGroupId() {
        return groupId;
    }
}
