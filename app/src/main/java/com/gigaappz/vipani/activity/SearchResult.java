package com.gigaappz.vipani.activity;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.gigaappz.vipani.models.UserModel;

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

public class SearchResult extends AppCompatActivity {
    private RecyclerView recyclerView;
    TextView nocontent;
    CustomRecyclerAdapter mAdapter;
    RecyclerView.LayoutManager layoutManager;
    List<PersonUtils> personUtilsList;
    ProgressBar progressBar;
    ImageView back;
    public static List<UserModel> userModels = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        recyclerView    =findViewById(R.id.recycler);
        nocontent    = findViewById(R.id.nocontent);
        back    = (ImageView) findViewById(R.id.image);
        progressBar    = (ProgressBar) findViewById(R.id.progressBar);

        nocontent.setVisibility(View.GONE);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(SearchResult.this);

        recyclerView.setLayoutManager(layoutManager);
        personUtilsList = new ArrayList<>();

        getusers("g*Rg3I0",getIntent().getStringExtra("query"));
        mAdapter = new CustomRecyclerAdapter(SearchResult.this, personUtilsList);

        recyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void getusers(final String token,final String query) {

        personUtilsList.clear();
       /* hud = KProgressHUD.create(getActivity())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setLabel("Loading Data")
                .show();*/
        String urlJsonObj = "http://tradewatch.xyz/api/searchUser.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("query", query);
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
                        progressBar.setVisibility(View.GONE);
                        nocontent.setVisibility(View.GONE);
                    }
                    /*if (hud.isShowing()){
                        hud.dismiss();
                    }*/


                } catch (JSONException e) {
                    Toast.makeText(SearchResult.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    nocontent.setVisibility(View.VISIBLE);
                    // progressBar.setVisibility(View.GONE);
                    /*if (hud.isShowing()){
                        hud.dismiss();
                    }*/
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SearchResult.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
                nocontent.setVisibility(View.VISIBLE);
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

}
