package com.compuestosimple.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class WebViewActivity extends Activity {
    private Context myContext;
    private View mySplashView;
    private WebView myWebView;
    private WebSettings myWebSettings;
    private String databasePath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myContext = this;
        mySplashView = findViewById(R.id.splash_view);
        myWebView = (WebView) findViewById(R.id.web_view);
        myWebSettings = myWebView.getSettings();
        databasePath = getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();

        myWebSettings.setJavaScriptEnabled(true);
        myWebSettings.setDatabaseEnabled(true);
        myWebSettings.setDatabasePath(databasePath);

        myWebView.addJavascriptInterface(new JavascriptInterface(), "intern");

        myWebView.setWebViewClient(new WebViewClient() {
            private Animation out;
            private Animation in;

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mySplashView.getVisibility() == View.VISIBLE) {
                    out = AnimationUtils.loadAnimation(myContext, android.R.anim.fade_out);
                    in = AnimationUtils.loadAnimation(myContext, android.R.anim.fade_in);
                    mySplashView.startAnimation(out); mySplashView.setVisibility(View.GONE);
                    myWebView.startAnimation(in); myWebView.setVisibility(View.VISIBLE);
                }
            }
        });

        myWebView.setWebChromeClient(new WebChromeClient() {
            private static final String TAG = "WebView";

            @Override
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG, cm.sourceId() + ": Line " + cm.lineNumber() + " : " + cm.message());
                return true;
            }

            @Override
            public void onExceededDatabaseQuota(String url, String databaseIdentifier, long currentQuota, long estimatedSize,
                long totalUsedQuota, QuotaUpdater quotaUpdater) {
                quotaUpdater.updateQuota(estimatedSize * 2);
            }
        });

        myWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = super.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String item_id;

        switch (item.getItemId()) {
            case R.id.close_item:
                item_id = "close";
            break;
            case R.id.help_item:
                item_id = "help";
            break;
            default:
                return super.onOptionsItemSelected(item);
        }

        myWebView.loadUrl("javascript:extern.fireDOMEvent('menuitem', { item_id: '" + item_id + "' });");
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            event.startTracking();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myWebView.loadUrl("javascript:extern.fireDOMEvent('backbutton');");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    private class JavascriptInterface {
        public void finish() {
            ((Activity) myContext).finish();
        }

        public void toast(String message) {
            Toast.makeText(myContext, message, Toast.LENGTH_LONG).show();
        }
    }
}
