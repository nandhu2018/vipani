package com.gigaappz.vipani.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.gigaappz.vipani.HttpRequest;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.activity.NewsContent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;


public class ForeignTab extends Fragment {
    Handler uiHandler = new Handler();
    WebView webView;
    private ProgressDialog progDailog;
    public ForeignTab() {
//        Empty Constructor
    }

    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.foreign_tab, container, false);
        //SwipeRefreshLayout refreshLayout = rootView.findViewById(R.id.fragment_second_refresh_layout);
        webView = rootView.findViewById(R.id.webview);
        //webView.setWebViewClient(new CustomWebView());
        progDailog = ProgressDialog.show(getActivity(), "Loading","", true);
        progDailog.setCancelable(false);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                progDailog.show();
                view.loadUrl(url);

                return true;
            }
          /*  @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // ignore ssl error
                if (handler != null){
                    handler.proceed();
                } else {
                    super.onReceivedSslError(view, null, error);
                }
            }*/
            @Override
            public void onPageFinished(WebView view, final String url) {
                progDailog.dismiss();
            }
        });

        webView.loadUrl("https://www.tocom.or.jp/market/kobetu/rubber.html");

        /*new BackgroundWorker().execute();
        new AsyncTask<String, Void, String>(){
            protected String doInBackground(String[] params) {
                try {
                    return new HttpRequest(params[0]).prepare().sendAndReadString();
                } catch (Exception e) {
                    //Log.e("***Web View - manipulated content - ERROR***", e.getMessage());
                    return null;//to promote null further
                }
            }
            protected void onPostExecute(String result) {
                if(result==null)return;//Error logged, don't load anything
                result=result.concat("<script>onload=function(){document.getElementById('siteHeader').style.display='none';}</script>");
                webView.loadData(result, "text/html", "UTF-8");
            }
        }.execute("https://www.tocom.or.jp/market/kobetu/rubber.html");*/

       // webView.loadUrl("https://www.tocom.or.jp/market/kobetu/rubber.html");

        return rootView;
    }

    private class CustomWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

        }
    }

    private class BackgroundWorker extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            getDarewod();
            return null;
        }

        public void getDarewod() {

            try {
                Document htmlDocument = Jsoup.connect("http://darebee.com/").get();
                Element element = htmlDocument.select("#gkHeaderMod > div.darewod").first();

                // replace body with selected element
                htmlDocument.body().empty().append(element.toString());
                final String html = htmlDocument.toString();

                uiHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadData(html, "text/html", "UTF-8");
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
