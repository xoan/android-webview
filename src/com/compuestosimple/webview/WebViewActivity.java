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
import android.webkit.WebView;
import android.widget.Toast;

public class WebViewActivity extends Activity {

    protected WebView mWebView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mWebView = (WebView) findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.addJavascriptInterface(new JavascriptInterface(this), "intern");
        mWebView.setWebChromeClient(new MyWebChromeClient());
        mWebView.setScrollBarStyle(WebView.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadUrl("file:///android_asset/index.html");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = super.getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String id;

        switch (item.getItemId()) {
            case R.id.close:
                id = "close";
            break;
            case R.id.help:
                id = "help";
            break;
            default:
                return super.onOptionsItemSelected(item);
        }

        mWebView.loadUrl("javascript:extern.fireDOMEvent('menuitem', { item: '" + id + "' });");
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mWebView.loadUrl("javascript:extern.fireDOMEvent('backbutton');");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class JavascriptInterface {
        private Context mContext;

        JavascriptInterface(Context context) {
            mContext = context;
        }

        public void finish() {
            ((Activity) mContext).finish();
        }

        public void toast(String message) {
            Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        private static final String TAG = "WebView";

        @Override
        public boolean onConsoleMessage(ConsoleMessage cm) {
            Log.d(TAG, cm.sourceId() + ": Line " + cm.lineNumber() + " : " + cm.message());
            return true;
        }
    }
}
