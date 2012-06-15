package com.compuestosimple.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage.QuotaUpdater;
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewActivity extends Activity {

    private WebView myWebView;
    private WebSettings myWebSettings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        myWebView = (WebView) findViewById(R.id.webview);
        myWebSettings = myWebView.getSettings();

        String databasePath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();

        myWebSettings.setJavaScriptEnabled(true);
        myWebSettings.setDatabaseEnabled(true);
        myWebSettings.setDatabasePath(databasePath);

        myWebView.addJavascriptInterface(new JavascriptInterface(this), "intern");
        myWebView.setWebChromeClient(new MyWebChromeClient());
        myWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
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
            case R.id.close:
                item_id = "close";
            break;
            case R.id.help:
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
        private Context myContext;

        JavascriptInterface(Context context) {
            myContext = context;
        }

        public void finish() {
            ((Activity) myContext).finish();
        }

        public void toast(String message) {
            Toast.makeText(myContext, message, Toast.LENGTH_LONG).show();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
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
    }
}
