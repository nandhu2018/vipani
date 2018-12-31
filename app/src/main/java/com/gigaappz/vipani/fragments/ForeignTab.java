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
import android.widget.ListView;
import android.widget.TextView;
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
import com.gigaappz.vipani.adapters.ForeignRecyclerAdapter;
import com.gigaappz.vipani.interfaces.CustomOnClick;
import com.gigaappz.vipani.interfaces.DomesticLongPressListener;
import com.gigaappz.vipani.models.Domestic;
import com.gigaappz.vipani.models.DomesticValueModel;
import com.gigaappz.vipani.models.Domesticflash;
import com.gigaappz.vipani.models.ForeignValueModel;
import com.gigaappz.vipani.utils.AppConstants;
import com.gigaappz.vipani.utils.BottomOffsetDecoration;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;

import static com.gigaappz.vipani.utils.AppConstants.IS_ADMIN;


public class ForeignTab extends Fragment implements DomesticLongPressListener,CustomOnClick {
    SharedPreferences sharedPreferences;
    KProgressHUD hud;
    String urlJsonObj="";
    SwipeRefreshLayout refreshLayout;
    ArrayList<String> listItems=new ArrayList<String>();
    private DatabaseReference mFirebaseDatabase,mFirebaseDatabase1;
    private FirebaseDatabase mFirebaseInstance;
    ArrayAdapter<String> recentupdate;
    public ForeignTab(){
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
        View rootView   = inflater.inflate(R.layout.foreign_tab, container, false);
        recyclerView    = rootView.findViewById(R.id.domestic_recycler);
        refreshLayout   = rootView.findViewById(R.id.domestic_tab_refresh_layout);
        recentupdate = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase = mFirebaseInstance.getReference("foreign");

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Domestic domestic=dataSnapshot.getValue(Domestic.class);
                initList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        sharedPreferences=getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);

        float offset = context.getResources().getDimension(R.dimen.recycler_bottom_offset);
        BottomOffsetDecoration offsetDecoration = new BottomOffsetDecoration((int) offset);
        recyclerView.addItemDecoration(offsetDecoration);

        initList();
        /*final Handler handler = new Handler();
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
        timer.schedule(doAsynchronousTask, 0, 10000);*/
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
            urlJsonObj = "http://tradewatch.xyz/api/getIntPriceAdmin.php";
            getdomesticcategorylist("g*Rg3I0");
        }else{
            urlJsonObj = "http://tradewatch.xyz/api/getIntPrice.php";
            getdomesticcategorylist(sharedPreferences.getString("token",""));
        }

        //createDummyList();
        prepareList();
    }
    private void initList1() {
        if (IS_ADMIN){
            urlJsonObj = "http://tradewatch.xyz/api/getDomesticPriceAdmin.php";
            getdomesticcategorylist1("g*Rg3I0");
        }else{
            urlJsonObj = "http://tradewatch.xyz/api/getDomesticPrice.php";
            getdomesticcategorylist1(sharedPreferences.getString("token",""));
        }

        //createDummyList();
        prepareList();
    }
    private ForeignRecyclerAdapter adapter;
    public static List<ForeignValueModel> foreignValueModels = new ArrayList<>();
    private void prepareList() {
        adapter = new ForeignRecyclerAdapter(context);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        if (IS_ADMIN) {
            adapter.setLongPressListener(this);
        }
        adapter.setOnItemClickListener(this);
    }

    public void getdomesticcategorylist(final String token) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        foreignValueModels.clear();
        /*hud = KProgressHUD.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setLabel("Loading Data")
                .show();
*/
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
                    foreignValueModels.clear();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar start = Calendar.getInstance();
                    JSONArray cast=response.getJSONArray("data");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject domcategory = cast.getJSONObject(i);
                        ForeignValueModel model    = new ForeignValueModel();
                        /*if (IS_ADMIN)
                        {
                            model.setId(domcategory.getString("id"));
                        }else {
                            model.setId("");
                        }*/
                        model.setId(domcategory.getString("id"));
                        model.setProfit(Double.parseDouble(domcategory.getString("price_difference"))>=0);
                        model.setHeadText(domcategory.getString("main_title"));
                        model.setSubHeadText(domcategory.getString("sub_title"));
                        model.setValueText(domcategory.getString("price"));
                        model.setValueSubText(domcategory.getString("udf1"));

                        // model.setValueRateText(i+"%");
                        //model.setTime(String.valueOf(domcategory.getInt("time")));
                        // String dateString = formatter.format(new Date(domcategory.getString("abstime")));
                        if (domcategory.getString("abstime").equalsIgnoreCase("")){
                            model.setTimetext(domcategory.getString("abstime"));
                        }else {
                            start.setTimeInMillis( Long.parseLong(domcategory.getString("abstime"))*1000L );
                            //model.setTime(DateFormat.format("dd-MM-yyyy hh:mm:ss", start).toString());
                            model.setTimetext(DateFormat.format("hh:mm:ss a", start).toString());
                        }

                        model.setTime(domcategory.getString("time"));
                        model.setValueDiffText(domcategory.getString("price_difference"));
                        /*if (i%3==0){
                            model.setValueSubText("sub value "+i);
                        }*/

                        foreignValueModels.add(model);
                    }
                    /*if (hud.isShowing()){
                        hud.dismiss();
                    }*/
                    adapter.notifyDataSetChanged();


                } catch (JSONException e) {
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    // progressBar.setVisibility(View.GONE);
                    /*if (hud.isShowing()){
                        hud.dismiss();
                    }*/
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
                /*if (hud.isShowing()){
                    hud.dismiss();
                }*/
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }
    public void getdomesticcategorylist1(final String token) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        foreignValueModels.clear();


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
                        ForeignValueModel model    = new ForeignValueModel();
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
                        model.setValueSubText(domcategory.getString("udf1"));
                        if (domcategory.getString("abstime").equalsIgnoreCase("")){
                            model.setTimetext(domcategory.getString("abstime"));
                        }else {
                            start.setTimeInMillis( Long.parseLong(domcategory.getString("abstime"))*1000L );
                            //model.setTime(DateFormat.format("dd-MM-yyyy hh:mm:ss", start).toString());
                            model.setTimetext(DateFormat.format("hh:mm:ss a", start).toString());
                        }

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

                        foreignValueModels.add(model);
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
                .setTitle("Delete")
                .setMessage("Do you really want to delete this commodity?")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Delete")
                .setNegativeBtnBackground("#8b0101")
                .setGifResource(R.drawable.delete)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        deletedomestic("g*Rg3I0",position);
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

    public void editDomestic(final String token, final String id, final String price,final String remarks) {

        String urlJsonObj = "http://tradewatch.xyz/api/editIntPrice.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", id);
            obj.put("price", price);
            obj.put("udf1", remarks);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        mFirebaseInstance = FirebaseDatabase.getInstance();
                        mFirebaseDatabase = mFirebaseInstance.getReference("domestic");
                        Domestic name=new Domestic();
                        name.setName(id+""+token+""+price);
                        mFirebaseDatabase.setValue(name);
                       /* mFirebaseDatabase1 = mFirebaseInstance.getReference("domesticupdate");
                        Domesticflash domesticflash=new Domesticflash();
                        domesticflash.setId(id);
                        mFirebaseDatabase1.setValue(domesticflash);*/
                        getdomesticcategorylist(token);
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
    public void getupdatelist(final String token, final String id, final ListView listview) {

        String urlJsonObj = "http://tradewatch.xyz/api/getIntEditHistory.php";
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

                listItems.clear();
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar start = Calendar.getInstance();
                    JSONArray cast=response.getJSONArray("data");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject domcategory = cast.getJSONObject(i);
                        start.setTimeInMillis( Long.parseLong(domcategory.getString("time"))*1000L );
                        //model.setTime(DateFormat.format("dd-MM-yyyy hh:mm:ss", start).toString());
                        listItems.add(DateFormat.format("dd/MM/yyyy hh:mm:ss a", start).toString()+"\t\t Price:"+domcategory.getString("price"));
                        // listItems.add(domcategory.getString("price"));
                    }
                    recentupdate=new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_list_item_1,
                            listItems);
                    listview.setAdapter(recentupdate);


                } catch (JSONException e) {

                    // progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void customalert(final String pos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);
        final TextInputLayout remark = dialog.findViewById(R.id.remarks);
        final EditText reasonText = dialog.findViewById(R.id.daysedit);
        remark.setVisibility(View.VISIBLE);
        reasonText.setHint("Price");
        reasonText.setText(foreignValueModels.get(Integer.parseInt(pos)).getValueText());

        remark.getEditText().setText(foreignValueModels.get(Integer.parseInt(pos)).getValueSubText());
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
                String remarks = remark.getEditText().getText().toString();
                editDomestic("g*Rg3I0", foreignValueModels.get(Integer.parseInt(pos)).getId(), price,remarks);

                dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void customlist(final String pos) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.domesticdetailalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView heading=(TextView)dialog.findViewById(R.id.heading);
        final TextView subheading=(TextView)dialog.findViewById(R.id.subheading);
        final TextView date=(TextView)dialog.findViewById(R.id.date);
        final TextView price=(TextView)dialog.findViewById(R.id.price);
        final ListView listView=(ListView) dialog.findViewById(R.id.listview);
        heading.setText(foreignValueModels.get(Integer.parseInt(pos)).getHeadText());
        subheading.setText(foreignValueModels.get(Integer.parseInt(pos)).getSubHeadText());
        date.setText(foreignValueModels.get(Integer.parseInt(pos)).getTime());
        price.setText("Price :"+foreignValueModels.get(Integer.parseInt(pos)).getValueText());
        getupdatelist("g*Rg3I0",foreignValueModels.get(Integer.parseInt(pos)).getId(),listView);


        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        if (IS_ADMIN){
            cancelButton.setVisibility(View.VISIBLE);
        }else {
            cancelButton.setVisibility(View.GONE);
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customalert(pos);
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }
    @Override
    public void onItemClick(View view, int position) {
        switch (view.getId())
        {
            case R.id.linear1:

                customlist(String.valueOf(position));
                break;
        }
    }
    public void deletedomestic(final String token, final String id) {

        String urlJsonObj = "http://tradewatch.xyz/api/deleteIntPrice.php";
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
                        mFirebaseDatabase = mFirebaseInstance.getReference("foreign");
                        Domestic name=new Domestic();
                        name.setName(id+""+token);
                        mFirebaseDatabase.setValue(name);

                        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        getdomesticcategorylist(token);
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
}
