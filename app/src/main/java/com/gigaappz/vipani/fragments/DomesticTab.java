package com.gigaappz.vipani.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.activity.AddDomestic;
import com.gigaappz.vipani.activity.Login;
import com.gigaappz.vipani.activity.NewMarketActivity;
import com.gigaappz.vipani.adapters.DomesticRecyclerAdapter;
import com.gigaappz.vipani.interfaces.DomesticLongPressListener;
import com.gigaappz.vipani.models.DomesticValueModel;
import com.gigaappz.vipani.utils.AppConstants;
import com.gigaappz.vipani.utils.BottomOffsetDecoration;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

import static com.gigaappz.vipani.utils.AppConstants.IS_ADMIN;


public class DomesticTab extends Fragment implements DomesticLongPressListener {
    SharedPreferences sharedPreferences;
    KProgressHUD hud;
    String urlJsonObj="";
    SwipeRefreshLayout refreshLayout;
    public DomesticTab(){
        //Empty Constructor
    }

    Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    private RecyclerView recyclerView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.domestic_tab, container, false);
        recyclerView    = rootView.findViewById(R.id.domestic_recycler);
        refreshLayout   = rootView.findViewById(R.id.domestic_tab_refresh_layout);
        sharedPreferences=getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);

        float offset = context.getResources().getDimension(R.dimen.recycler_bottom_offset);
        BottomOffsetDecoration offsetDecoration = new BottomOffsetDecoration((int) offset);
        recyclerView.addItemDecoration(offsetDecoration);

        initList();
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {
                            initList1();
                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });
        return rootView;
    }

    private void initList() {
        if (IS_ADMIN){
            urlJsonObj = "http://tradewatch.xyz/getDomesticPriceAdmin.php";
            getdomesticcategorylist("g*Rg3I0");
        }else{
            urlJsonObj = "http://tradewatch.xyz/getDomesticPrice.php";
            getdomesticcategorylist(sharedPreferences.getString("token",""));
        }

        //createDummyList();
        prepareList();
    }
    private void initList1() {
        if (IS_ADMIN){
            urlJsonObj = "http://tradewatch.xyz/getDomesticPriceAdmin.php";
            getdomesticcategorylist1("g*Rg3I0");
        }else{
            urlJsonObj = "http://tradewatch.xyz/getDomesticPrice.php";
            getdomesticcategorylist1(sharedPreferences.getString("token",""));
        }

        //createDummyList();
        prepareList();
    }
    private DomesticRecyclerAdapter adapter;
    public static List<DomesticValueModel> domesticValueModels = new ArrayList<>();
    private void prepareList() {
        adapter = new DomesticRecyclerAdapter(context);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if (IS_ADMIN) {
            adapter.setLongPressListener(this);
        }
    }

    public void getdomesticcategorylist(final String token) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        domesticValueModels.clear();
        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setLabel("Loading Data")
                .show();

        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);


        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar start = Calendar.getInstance();
                    JSONArray cast=response.getJSONArray("data");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject domcategory = cast.getJSONObject(i);
                        DomesticValueModel model    = new DomesticValueModel();
                        if (IS_ADMIN)
                        {
                            model.setId(domcategory.getString("id"));
                        }else {
                            model.setId("");
                        }

                        model.setProfit(Double.parseDouble(domcategory.getString("price_difference"))>=0);
                        model.setHeadText(domcategory.getString("main_title"));
                        model.setSubHeadText(domcategory.getString("sub_title"));
                        model.setValueText(domcategory.getString("price"));
                       // model.setValueRateText(i+"%");
                        //model.setTime(String.valueOf(domcategory.getInt("time")));
                        //String dateString = formatter.format(new Date(domcategory.getInt("time")));

                        //start.setTimeInMillis( domcategory.getInt("time")*1000L );
                       //model.setTime(DateFormat.format("dd-MM-yyyy hh:mm:ss", start).toString());
                        model.setTime(domcategory.getString("time"));
                        model.setValueDiffText(domcategory.getString("price_difference"));
                        /*if (i%3==0){
                            model.setValueSubText("sub value "+i);
                        }*/

                        domesticValueModels.add(model);
                    }
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                adapter.notifyDataSetChanged();


                } catch (JSONException e) {

                    // progressBar.setVisibility(View.GONE);
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
                if (hud.isShowing()){
                    hud.dismiss();
                }
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }
    public void getdomesticcategorylist1(final String token) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        domesticValueModels.clear();


        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);


        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar start = Calendar.getInstance();
                    JSONArray cast=response.getJSONArray("data");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject domcategory = cast.getJSONObject(i);
                        DomesticValueModel model    = new DomesticValueModel();
                        if (IS_ADMIN)
                        {
                            model.setId(domcategory.getString("id"));
                        }else {
                            model.setId("");
                        }

                        model.setProfit(Double.parseDouble(domcategory.getString("price_difference"))>=0);
                        model.setHeadText(domcategory.getString("main_title"));
                        model.setSubHeadText(domcategory.getString("sub_title"));
                        model.setValueText(domcategory.getString("price"));
                        // model.setValueRateText(i+"%");
                        //model.setTime(String.valueOf(domcategory.getInt("time")));
                        //String dateString = formatter.format(new Date(domcategory.getInt("time")));

                        //start.setTimeInMillis( domcategory.getInt("time")*1000L );
                        //model.setTime(DateFormat.format("dd-MM-yyyy hh:mm:ss", start).toString());
                        model.setTime(domcategory.getString("time"));
                        model.setValueDiffText(domcategory.getString("price_difference"));
                        /*if (i%3==0){
                            model.setValueSubText("sub value "+i);
                        }*/

                        domesticValueModels.add(model);
                    }

                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {

                    // progressBar.setVisibility(View.GONE);

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog

            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    @Override
    public void onDomesticLongPressListener(String id) {
        // TODO: 9/2/2018 dialog
        removealert(id);
    }

    private void removealert(final String position){
        new FancyGifDialog.Builder(getActivity())
                .setTitle("Edit")
                .setMessage("Do you really want to edit this commodity?")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Edit")
                .setNegativeBtnBackground("#8b0101")
                .setGifResource(R.drawable.delete)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        customalert(position);
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

    public void editDomestic(final String token,String id,String price) {

        String urlJsonObj = "http://tradewatch.xyz/editDomesticPrice.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", id);
            obj.put("price", price);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                       getdomesticcategorylist(token);
                    }

                } catch (JSONException e) {
                    // progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }
    public void customalert(final String pos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);

        final EditText reasonText = dialog.findViewById(R.id.daysedit);

        reasonText.setHint("Price");

        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String price = inputLayout.getEditText().getText().toString();
                editDomestic("g*Rg3I0", pos, price);

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
