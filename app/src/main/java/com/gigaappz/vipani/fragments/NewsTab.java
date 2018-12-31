package com.gigaappz.vipani.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.activity.NewsUploader;
import com.gigaappz.vipani.activity.Newsedit;
import com.gigaappz.vipani.adapters.NewsRecyclerAdapter;
import com.gigaappz.vipani.interfaces.NewsLongPressListener;
import com.gigaappz.vipani.models.Domestic;
import com.gigaappz.vipani.models.NewsValueModel;
import com.gigaappz.vipani.utils.AppConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.gigaappz.vipani.utils.AppConstants.NEWS_FROM_DEVICE;
import static com.gigaappz.vipani.utils.AppConstants.NEWS_FROM_URL;
import static com.gigaappz.vipani.utils.AppConstants.NEWS_KEY;

public class NewsTab extends Fragment implements NewsLongPressListener {

    private NewsRecyclerAdapter adapter;
    SwipeRefreshLayout refreshLayout;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    public NewsTab(){
//        Empty Constructor
    }

    private Context context;
    KProgressHUD hud;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.news_tab, container, false);
        recyclerView    = rootView.findViewById(R.id.news_recycler);
        refreshLayout = rootView.findViewById(R.id.news_refresh_layout);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("news1");

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Domestic domestic=dataSnapshot.getValue(Domestic.class);
                prepareNewsListFromAPI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       /* hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .show();*/

//        prepareNewsListFromAPI();

//        adaptList();

        prepareNewsListFromAPI();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                prepareNewsListFromAPI();
            }
        });
        return rootView;
    }

    /*private void getFirebaseNewsList(){
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference().child(NEWS_KEY);
        firebase.addValueEventListener(this);
    }
*/
    private List<NewsValueModel> newsValueModels = new ArrayList<>();
    private void adaptList(){
        adapter = new NewsRecyclerAdapter(context, newsValueModels);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        /*float offset = context.getResources().getDimension(R.dimen.recycler_bottom_offset);
        BottomOffsetDecoration offsetDecoration = new BottomOffsetDecoration((int) offset);
        recyclerView.addItemDecoration(offsetDecoration);*/
        adapter.notifyDataSetChanged();

        if (AppConstants.IS_ADMIN) {
            adapter.setOnNewsLongPressListener(this);
        }
       // manager.scrollToPositionWithOffset(newsValueModels.size()-1,newsValueModels.size());
    }

    /*@Override
    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        newsValueModels.clear();
        if (dataSnapshot.exists()) {
            for (DataSnapshot news : dataSnapshot.getChildren()) {
                NewsValueModel model = news.getValue(NewsValueModel.class);
                newsValueModels.add(model);
            }
            adaptList();
        }
        adapter.notifyDataSetChanged();
        if (hud.isShowing()){
            hud.dismiss();
        }

    }

    @Override
    public void onCancelled(@NonNull DatabaseError databaseError) {
        Toast.makeText(context, "Unable to process", Toast.LENGTH_SHORT).show();
        if (hud.isShowing()){
            hud.dismiss();
        }
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }

    }*/

    @Override
    public void onNewsLongPressListener(int position) {
        // TODO: 9/19/2018 dialog
        //adapter.removeItem(position);
        //Toast.makeText(context, "item removed", Toast.LENGTH_SHORT).show();
        //removealert(String.valueOf(position));
        newsalert(String.valueOf(position));

    }

    public void newsalert( final String position){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.newseditalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        Button delete = (Button) dialog.findViewById(R.id.delete);
        Button edit = (Button) dialog.findViewById(R.id.edit_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletedomestic("g*Rg3I0",newsValueModels.get(Integer.parseInt(position)).getId(),newsValueModels.get(Integer.parseInt(position)).getNewsHead());
                dialog.dismiss();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), Newsedit.class);
                intent.putExtra("head",newsValueModels.get(Integer.parseInt(position)).getNewsHead());
                intent.putExtra("id",newsValueModels.get(Integer.parseInt(position)).getId());
                intent.putExtra("body",newsValueModels.get(Integer.parseInt(position)).getNewsContent());
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void removealert(final String position){
        new FancyGifDialog.Builder(getActivity())
                .setTitle("Delete")
                .setMessage("Do you really want to delete this News?")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Delete")
                .setNegativeBtnBackground("#8b0101")
                .setGifResource(R.drawable.delete)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        deletedomestic("g*Rg3I0",newsValueModels.get(Integer.parseInt(position)).getId(),newsValueModels.get(Integer.parseInt(position)).getNewsHead());
                        //Toast.makeText(MainActivity.this,"Ok",Toast.LENGTH_SHORT).show();
                        //adapter.removeItem(position);
                        //Toast.makeText(context, "item removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        //Toast.makeText(MainActivity.this,"Cancel",Toast.LENGTH_SHORT).show();

                    }
                })
                .build();
    }
    public void deletedomestic(final String token, final String id,final String head) {

        String urlJsonObj = "http://tradewatch.xyz/api/deleteNews.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", id);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        mFirebaseInstance = FirebaseDatabase.getInstance();
                        mFirebaseDatabase = mFirebaseInstance.getReference("news1");
                        Domestic name=new Domestic();
                        name.setName(head);
                        mFirebaseDatabase.setValue(name);
                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(context, ""+response.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    // progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    private String newsAPIURL  = "http://tradewatch.xyz/api/getNewsList.php";
    private void prepareNewsListFromAPI(){

        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", "g*Rg3I0");


        } catch (JSONException e) {
        }
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                newsAPIURL, obj, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (refreshLayout.isRefreshing()){
                        refreshLayout.setRefreshing(false);
                    }
                    newsValueModels = new ArrayList<>();
                    newsValueModels.clear();
                    JSONArray jsonArray = response.getJSONArray("data");

                    for (int i=0; i<jsonArray.length(); i++){
                        JSONObject jsonObject   = jsonArray.getJSONObject(i);
                        NewsValueModel model    = new NewsValueModel();
                        model.setId(jsonObject.getString("id"));
                        model.setIsURL(jsonObject.getString("type"));
                        model.setNewsURL(jsonObject.getString("url"));
                        model.setNewsHead(jsonObject.getString("title"));
                        model.setNewsContent(jsonObject.getString("body"));
                        if (model.getIsURL().equalsIgnoreCase(NEWS_FROM_DEVICE)) {
                            model.setPicURL("http://tradewatch.xyz/api/" + jsonObject.getString("image"));
                            model.setAuthor(jsonObject.getString("author"));


//                            adapter.notifyDataSetChanged();
                        } else {
                            model.setPicURL(jsonObject.getString("author"));
                            model.setAuthor("");

                            /*NewsTab.GetWebDetails details = new GetWebDetails();
                            details.execute(model.getNewsURL());*/
                        }
                        newsValueModels.add(model);
                    }

//                    Collections.reverse(newsValueModels);

                    /*String status   = response.getString("responseStatus");
                    if (status.equalsIgnoreCase("true")){*/
//                        int totalItems  = response.getInt("totalResults");
                    // TODO: 12-Nov-18 uncomment if error
                        /*JSONArray jsonArray = response.getJSONArray("data");
                        for (int i=0; i<jsonArray.length(); i++){
                            JSONObject object   = jsonArray.getJSONObject(i);
                            NewsValueModel model    = new NewsValueModel();
                            model.setIsURL(object.getInt("type"));
                            model.setNewsHead(object.getString("title"));
                            model.setNewsContent(object.getString("body"));
                            *//*if (object.getString("url").equalsIgnoreCase("")){*//*
                                model.setPicURL("http://tradewatch.xyz/api/"+object.getString("image"));

                           *//* }else{
                                model.setPicURL("https://www.newstm.in/images/astro/astroicons/mithunam.png");
                            }*//*

                            model.setAuthor(object.getString("author"));
                            model.setNewsURL(object.getString("url"));
                            model.setTimeStamp(object.getString("time"));*/

                            /*model.setNewsHead(object.getString("title"));
                            model.setNewsContent(object.getString("description"));
                            if (object.getString("urlToImage").length()>0) {
                                model.setPicURL(object.getString("urlToImage"));
                            } else {
                                model.setPicURL("null");
                            }
                            newsValueModels.add(model);*/
                    /*if (hud.isShowing()){
                        hud.dismiss();
                    }*/

                    Log.e("JSONResponse", ""+response);
                    adaptList();
//                    }
                } catch (JSONException e) {
                   /* if (hud.isShowing()){
                        hud.dismiss();
                    }*/
                    Log.e("NewsTab", "Json parsing error: "+e.getMessage()
                            +"\n response size "+response.length());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError e) {
                /*if (hud.isShowing()){
                    hud.dismiss();
                }*/
                Log.e("NewsTab", "Json response error: "+e.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(objectRequest);
    }

    public class GetWebDetails extends AsyncTask<String, Void, Void> {

        private NewsValueModel model = new NewsValueModel();
        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];

            Document document   = getDocumet(url);

            Elements title  = document.select("meta[property=og:title");
            if (title!=null && title.size() > 0) {
                model.setNewsHead(title.attr("content"));
            } else {
                model.setNewsHead(document.title());
            }

            Elements imageUrl   = document.select("meta[property=og:image");
            if (imageUrl!=null && imageUrl.size()>0){
                model.setPicURL(imageUrl.attr("content"));
            } else {
                String stringDocument = document.toString();
                Pattern pattern = Pattern.compile("<.*?apple-touch-icon.*?>");
                Matcher matcher = pattern.matcher(stringDocument);
                if (matcher.find()) {
                    String data = stringDocument.substring(matcher.start() + 1, matcher.end() - 2);
                    data = data.substring(data.lastIndexOf("\"") + 1, data.length());
                    model.setPicURL(data);
                } else {
                    pattern = Pattern.compile("\".*?favicon.ico.*?\"");
                    matcher = pattern.matcher(stringDocument);
                    if (matcher.find()) {
                        String data = stringDocument.substring(matcher.start() + 1, matcher.end() - 1);
                        data = data.substring(data.lastIndexOf("\"") + 1, data.length());
                        model.setPicURL(data);
                    }
                }
            }

            model.setIsURL(NEWS_FROM_URL);
            model.setNewsURL(baseUrl);

            return null;
        }

        private String baseUrl;
        private Document getDocumet(String url) {

            Document document   = null;
            try {
                document = Jsoup.connect(url).get();
                baseUrl = url;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                if (!url.contains("http://") && !url.contains("https://")){
                    document = getDocumet("https://"+url);
                }
            }
            return document;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (model.getNewsHead()!=null) {
                if (!model.getNewsHead().isEmpty()) {
                    //new FirebaseManupulator(NEWS_KEY).uploadNews(model);
                    // TODO: 12-Nov-18 uncomment if error
//                    addNews("g*Rg3I0","0",model.getNewsHead(),model.getNewsURL(),"",model.getPicURL(),"");

//                    addNews("g*Rg3I0", newsFrom, model.getNewsHead(), model.getAuthor(), model.getNewsContent(), model.getPicURL(), model.getNewsURL());
                    newsValueModels.add(model);
                    adapter.notifyDataSetChanged();
                }
            } else {
//                Toast.makeText(NewsUploader.this, "Invalid URL", Toast.LENGTH_SHORT).show();

            }
        }
    }
}
