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


public class Userspending extends Fragment implements UserLongPressListener {

    public static com.gigaappz.vipani.interfaces.myListener myListener;
    private UserRecyclerAdapter adapter;
    Userpendingadapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    private Context context;
    List<PersonUtils> personUtilsList;
    public static List<UserModel> userModels = new ArrayList<>();
    private RecyclerView recyclerView;
    SharedPreferences sharedPreferences;
    KProgressHUD hud;
    TextView nocontent;
    SwipeRefreshLayout refreshLayout;
    public long dayleft=0;
    public Userspending() {
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

    public void initList() {
        nocontent.setVisibility(View.GONE);
        sharedPreferences=getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);

        personUtilsList = new ArrayList<>();

        getusers("g*Rg3I0");
        mAdapter = new Userpendingadapter(getActivity(), personUtilsList);

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
        String urlJsonObj = "http://tradewatch.xyz/pendingPaymentUsers.php";
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


                            if (users.getString("joined_on").equalsIgnoreCase("")) {
                                joined = "Nil";
                            } else {
                                start2.setTimeInMillis(Integer.parseInt(users.getString("joined_on")) * 1000L);
                                joined = DateFormat.format("dd-MM-yyyy", start2).toString();
                            }


                            String dayleft = getDaysBetweenDates(dateToStr, payexp);
                            personUtilsList.add(new PersonUtils(users.getString("user_id"), "", joined, "Nil", "Nil", dayleft + " Days Left"));
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
       // removealert(position);
    }
    private void removealert(final int position){
        new FancyGifDialog.Builder(getActivity())
                .setTitle("Remove")
                .setMessage("Do you really want to delete this user?")
                .setNegativeBtnText("Cancel")
                .setNegativeBtnBackground("#8b0101")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Remove")
                .setGifResource(R.drawable.delete)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        //Toast.makeText(MainActivity.this,"Ok",Toast.LENGTH_SHORT).show();

                        mAdapter.removeItem(position);
                        Toast.makeText(context, "item removed", Toast.LENGTH_SHORT).show();
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
