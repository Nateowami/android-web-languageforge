package org.sil.languageforgeweb;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
    private View mContentView;
    private View mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        mContentView = findViewById(R.id.webview);
        mProgressBar = findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.VISIBLE);

        WebView view = (WebView) mContentView;

        // Setup Progress Indicator
        ProgressBar mProgress = (ProgressBar) mProgressBar;
        mProgress.setIndeterminate(true);
        view.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                mProgressBar.setVisibility(View.VISIBLE);
                if(progress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
        view.setWebViewClient(new WebViewClient());

        // Restore last URL
        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        String currentUrl = settings.getString("currentUrl", null);


        if (TextUtils.isEmpty(currentUrl)) {
            view.loadUrl("https://languageforge.org");
        } else {
            view.loadUrl(currentUrl);
        }

        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setAllowContentAccess(true);

        // ask for permission if necessary
        if (ContextCompat.checkSelfPermission(FullscreenActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FullscreenActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }

    }

    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences settings = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        WebView view = (WebView) mContentView;
        editor.putString("currentUrl", view.getUrl());

        // Commit the edits!
        editor.commit();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    WebView view = (WebView) mContentView;
                    if (view.canGoBack()) {
                        view.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
