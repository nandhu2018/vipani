package com.gigaappz.vipani.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.http.SslError;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gigaappz.vipani.NewsContent;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.utils.AppConstants;

/**
 * A simple {@link Fragment} subclass.
 */
public class Foreignweb extends Fragment {
    WebView webView;
    private ProgressDialog progDailog;
    TextView textView;
    Button refresh;
    ProgressBar progressBar;
    public Foreignweb() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_foreignweb, container, false);
        webView=view.findViewById(R.id.webview);
        textView=view.findViewById(R.id.text);
        refresh=view.findViewById(R.id.refresh);
        progressBar=view.findViewById(R.id.progressBar);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setLoadWithOverviewMode(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setSupportZoom(true);
                webView.getSettings().setBuiltInZoomControls(true);
                webView.setWebViewClient(new CustomWebView());
                webView.loadUrl("http://www.rakayang.net/tocompriceAll.php");
            }
        });




        /*webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });*/

        //webView.loadUrl("tocom.or.jp/market/kobetu/rubber.html");
        return view;
    }
    private class CustomWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            webView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
            // ignore ssl error
           /* if (handler != null){
                handler.proceed();
            } else {
                super.onReceivedSslError(view, null, error);
            }*/
            progressBar.setVisibility(View.GONE);
            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Load International market data");
            builder.setPositiveButton("load", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.proceed();
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
            builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handler.cancel();
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    refresh.setVisibility(View.VISIBLE);
                    webView.setVisibility(View.GONE);
                }
            });
            final AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

}
