package com.gigaappz.vipani.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import com.gigaappz.vipani.activity.AddDomestic;
import com.gigaappz.vipani.adapters.UserRecyclerAdapter;
import com.gigaappz.vipani.interfaces.RefreshUsersList;
import com.gigaappz.vipani.interfaces.UserLongPressListener;
import com.gigaappz.vipani.interfaces.myListener;
import com.gigaappz.vipani.models.DomesticValueModel;
import com.gigaappz.vipani.models.UserModel;
import com.gigaappz.vipani.utils.AppConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

import es.dmoral.toasty.Toasty;


public class UsersTab extends Fragment implements UserLongPressListener {
    public static myListener myListener;
    private UserRecyclerAdapter adapter;
    CustomRecyclerAdapter mAdapter;
    SharedPreferences sharedPreferences;
    KProgressHUD hud;
    RecyclerView.LayoutManager layoutManager;
    SwipeRefreshLayout refreshLayout;
    TextView nocontent;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    List<PersonUtils> personUtilsList;
    public static List<UserModel> userModels = new ArrayList<>();
    public UsersTab(){
//        Empty Constructor
    }

    private Context context;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();


    }

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView   = inflater.inflate(R.layout.fragment_users_tab, container, false);

        recyclerView    = rootView.findViewById(R.id.user_recycler);
        nocontent    = rootView.findViewById(R.id.nocontent);
        refreshLayout = rootView.findViewById(R.id.users_refresh_layout);

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
        nocontent.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());

        recyclerView.setLayoutManager(layoutManager);
        sharedPreferences=getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);

        personUtilsList = new ArrayList<>();

        /*personUtilsList.add(new PersonUtils("Todd Miller","Project Manager"));
        personUtilsList.add(new PersonUtils("Bradley Matthews","Senior Developer"));
        personUtilsList.add(new PersonUtils("Harley Gibson","Lead Developer"));
        personUtilsList.add(new PersonUtils("Gary Thompson","Lead Developer"));
        personUtilsList.add(new PersonUtils("Corey Williamson","UI/UX Developer"));
        personUtilsList.add(new PersonUtils("Samuel Jones","Front-End Developer"));
        personUtilsList.add(new PersonUtils("Michael Read","Backend Developer"));
        personUtilsList.add(new PersonUtils("Robert Phillips","Android Developer"));
        personUtilsList.add(new PersonUtils("Albert Stewart","Web Developer"));
        personUtilsList.add(new PersonUtils("Wayne Diaz","Junior Developer"));*/
        getusers("g*Rg3I0");
        mAdapter = new CustomRecyclerAdapter(getActivity(), personUtilsList);

        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    public void getusers(final String token) {
        if (refreshLayout.isRefreshing()){
            refreshLayout.setRefreshing(false);
        }
        personUtilsList.clear();
       /* hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setLabel("Loading Data")
                .show();*/
        String urlJsonObj = "http://tradewatch.xyz/api/activeUsers.php";
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
                            personUtilsList.add(new PersonUtils(users.getString("mobile"), "", joined, payexp, paydate, dayleft + " Days Left",users.getString("id"),users.getString("name"),users.getString("place"),users.getString("shop_name")));
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                    /*if (hud.isShowing()){
                        hud.dismiss();
                    }*/


                } catch (JSONException e) {

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
               /* if (hud.isShowing()){
                    hud.dismiss();
                }*/
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }

    public void remuser(final String token,String userid,String reason) {

        String urlJsonObj = "http://tradewatch.xyz/api/blockUser.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", userid);
            obj.put("comment", reason);

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
    private void prepareList() {
      /*  adapter = new UserRecyclerAdapter(context);
        LinearLayoutManager manager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        float offset = context.getResources().getDimension(R.dimen.recycler_bottom_offset);
        BottomOffsetDecoration offsetDecoration = new BottomOffsetDecoration((int) offset);
        recyclerView.addItemDecoration(offsetDecoration);*/
        adapter = new UserRecyclerAdapter(getActivity(),userModels);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        if (AppConstants.IS_ADMIN) {
            adapter.setOnLongPressListener(this);
        }
    }

   /* private List<UserModel> createDummyList() {
        for (int i=0; i<20; i++){
            UserModel model = new UserModel();
            model.setUserMobile("99995555"+i);
            //model.setUserName("name"+i);
            userModels.add(model);
        }
        return userModels;
    }*/
    @Override
    public void onUserLongPressListener(int position) {
        //// TODO: 04-Sep-18 dialog
       removealert(position);

    }
    public void makeadmin(final String token,String userid) {

        String urlJsonObj = "http://tradewatch.xyz/api/makeAsAdmin.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", userid);

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

    public void customalert(final String user){
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);

        final EditText reasonText = dialog.findViewById(R.id.daysedit);

        reasonText.setHint("Enter Reason");
        reasonText.setKeyListener(DigitsKeyListener.getInstance("asdfghjklqwertyuiopzxcvbnmZXCVBNMASDFGHJKLQWERTYUIOP., "));

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
                String reason   = inputLayout.getEditText().getText().toString();
                remuser("g*Rg3I0",user,reason);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void removealert(final int position){
        new FancyGifDialog.Builder(getActivity())
                .setTitle("Manage User")
                .setMessage("Select An Option")
                .setNegativeBtnText("Make As Admin")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Remove User")
                .setNegativeBtnBackground("#8b0101")  //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        //Toast.makeText(MainActivity.this,"Ok",Toast.LENGTH_SHORT).show();

                        customalert(personUtilsList.get(position).getId());
                        //mAdapter.removeItem(position);
                        //Toast.makeText(context, "item removed", Toast.LENGTH_SHORT).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                          makeadmin("g*Rg3I0",personUtilsList.get(position).getId());
                        //Toast.makeText(MainActivity.this,"Cancel",Toast.LENGTH_SHORT).show();
                    }
                })
                .build();
    }

    private static RefreshUsersList refreshUsersList;
    public void onRefreshUsersList(RefreshUsersList refreshUsersList){
        UsersTab.refreshUsersList = refreshUsersList;
    }

}
