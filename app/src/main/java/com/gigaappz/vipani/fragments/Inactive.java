package com.gigaappz.vipani.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.CustomRecyclerAdapter;
import com.gigaappz.vipani.PersonUtils;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.adapters.UserRecyclerAdapter;
import com.gigaappz.vipani.adapters.Userinactiveadapter;
import com.gigaappz.vipani.adapters.Userpendingadapter;
import com.gigaappz.vipani.interfaces.UserLongPressListener;
import com.gigaappz.vipani.interfaces.myListener;
import com.gigaappz.vipani.models.UserModel;
import com.gigaappz.vipani.utils.AppConstants;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

//import static com.gigaappz.vipani.activity.MarketActivity.isAdmin;

public class Inactive extends Fragment implements UserLongPressListener {

    public static com.gigaappz.vipani.interfaces.myListener myListener;
    private UserRecyclerAdapter adapter;
    Userinactiveadapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    private Context context;
    List<PersonUtils> personUtilsList;
    SwipeRefreshLayout refreshLayout;
//    public static List<UserModel> userModels = new ArrayList<>();
    private RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    TextView nocontent;
    KProgressHUD hud;
    public Inactive() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.fragment_inactive, container, false);

        recyclerView    = rootView.findViewById(R.id.user_recycler);
        nocontent    = rootView.findViewById(R.id.nocontent);
        refreshLayout = rootView.findViewById(R.id.user_inactive_refresh_layout);

        nocontent.setVisibility(View.GONE);
        recyclerView.setHasFixedSize(true);

        initList();

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initList();
            }
        });
        //Adding Data into ArrayList
        if (AppConstants.IS_ADMIN) {
            mAdapter.setOnLongPressListener(this);
        }
        myListener=new myListener() {
            @Override
            public void updateView(boolean success, String message) {
                /*UserModel user=new UserModel();
                user.setUserMobile(message);
                userModels.add(user);*/
//                adapter.notifyDataSetChanged();
            }
        };
//        createDummyList();
        // prepareList();
        return rootView;
    }

    private void initList() {
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        sharedPreferences=getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        personUtilsList = new ArrayList<>();
        getusers("g*Rg3I0");
        mAdapter = new Userinactiveadapter(getActivity(), personUtilsList);

        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void getusers(final String token) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        personUtilsList.clear();
        hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setLabel("Loading Data")
                .show();
        String urlJsonObj = "http://tradewatch.xyz/blockedUsers.php";
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

                    if (response.getString("responseStatus").equalsIgnoreCase("false")) {
                        nocontent.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else{
                        nocontent.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                        Calendar start = Calendar.getInstance();
                    Calendar start1 = Calendar.getInstance();
                    Calendar start2 = Calendar.getInstance();
                    Date today = new Date();
                    SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                    String dateToStr = format.format(today);
                    JSONArray cast = response.getJSONArray("data");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject users = cast.getJSONObject(i);
                        String paydate = "";
                        String joined = "";
                        String payexp = "";
                        if (users.getString("payment_date").equalsIgnoreCase("")) {
                            paydate = "Nil";
                        } else {
                            start.setTimeInMillis(Integer.parseInt(users.getString("payment_date")) * 1000L);
                            paydate = DateFormat.format("dd-MM-yyyy", start).toString();
                        }

                        if (users.getString("payment_expiry").equalsIgnoreCase("")) {
                            payexp = "Nil";
                        } else {
                            start1.setTimeInMillis(Integer.parseInt(users.getString("payment_expiry")) * 1000L);
                            payexp = DateFormat.format("dd-MM-yyyy", start1).toString();
                        }

                        if (users.getString("joined_on").equalsIgnoreCase("")) {
                            joined = "Nil";
                        } else {
                            start2.setTimeInMillis(Integer.parseInt(users.getString("joined_on")) * 1000L);
                            joined = DateFormat.format("dd-MM-yyyy", start2).toString();
                        }


                        String dayleft = getDaysBetweenDates(dateToStr, payexp);
                        String reason=users.getString("disable_comment");
                        personUtilsList.add(new PersonUtils(users.getString("user_id"), reason, joined, payexp, paydate, dayleft + " Days Left"));
                    }
                    mAdapter.notifyDataSetChanged();
                }
                    if (hud.isShowing()){
                        hud.dismiss();
                    }


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

    public static String getDaysBetweenDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        Date startDate, endDate;
        long numberOfDays = 0;
        try {
            startDate = dateFormat.parse(start);
            endDate = dateFormat.parse(end);
            numberOfDays = getUnitBetweenDates(startDate, endDate, TimeUnit.DAYS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return numberOfDays+"";
    }
    private static long getUnitBetweenDates(Date startDate, Date endDate, TimeUnit unit) {
        long timeDiff = endDate.getTime() - startDate.getTime();
        return unit.convert(timeDiff, TimeUnit.MILLISECONDS);
    }
    @Override
    public void onUserLongPressListener(int position) {
        //// TODO: 04-Sep-18 dialog
        removealert(position);
    }

    public void unblockuser(final String token,String userid) {

        String urlJsonObj = "http://tradewatch.xyz/unblockUser.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("user_id", userid);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        getusers(token);
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
    private void removealert(final int position){
        new FancyGifDialog.Builder(getActivity())
                .setTitle("UnBlock")
                .setMessage("Do you really want to UnBlock this user?")
                .setNegativeBtnText("Cancel")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("UnBlock")
                .setNegativeBtnBackground("#8b0101")  //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        unblockuser("g*Rg3I0",personUtilsList.get(position).getPersonName());
                        //Toast.makeText(MainActivity.this,"Ok",Toast.LENGTH_SHORT).show();
                        /*mAdapter.removeItem(position);
                        mAdapter.notifyDataSetChanged();
                        Toast.makeText(context, "item removed", Toast.LENGTH_SHORT).show();*/


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
}
