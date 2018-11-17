package com.gigaappz.vipani.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.ConnectivityReceiver;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.models.NewsValueModel;
import com.gigaappz.vipani.utils.AppConstants;

import static com.gigaappz.vipani.utils.AppConstants.NEWS_CONTENT_URL;

public class NewsContent extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);

        final WebView webView = findViewById(R.id.webview);
        SwipeRefreshLayout refreshLayout = findViewById(R.id.news_content_refresh_layout);

        webView.setWebViewClient(new CustomWebView());

        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        getNewsDetailsFromServer();

        webView.loadUrl(model.getNewsURL());

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl(model.getNewsURL());
            }
        });
    }

    NewsValueModel model;
    private void getNewsDetailsFromServer() {
        model = new NewsValueModel();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        model = (NewsValueModel) bundle.getSerializable("NEWS_VALUE");
    }

    /**
     * to open all links in same webview
     */
    private class CustomWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        AppConstants.SELECTED_TAB = 1;
        Intent intent = new Intent(NewsContent.this, NewMarketActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        finish();
        startActivity(intent);
    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.GREEN;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        //AppController.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
