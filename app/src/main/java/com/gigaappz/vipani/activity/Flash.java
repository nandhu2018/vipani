package com.gigaappz.vipani.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.Mainpage;
import com.gigaappz.vipani.R;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

import static com.gigaappz.vipani.utils.AppConstants.IS_ADMIN;

public class Flash extends AppCompatActivity {
    ProgressBar progressBar;
    SharedPreferences sharedPreferences, sharedPreferences1, sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        progressBar = findViewById(R.id.progress_bar);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        sharedPreferences1 = getSharedPreferences("token", Context.MODE_PRIVATE);
        sharedPreferences2 = getSharedPreferences("admin", Context.MODE_PRIVATE);
        String mob = sharedPreferences.getString("mobile", "");
        String pin = sharedPreferences.getString("pin", "");
        //Toast.makeText(this, "" + mob + pin, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.GONE);
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                if (isNetworkConnected()) {
                    logcheck();
                } else {
                    internetError();
                }


            }
        }, 2000);

    }
    private void logcheck() {
        if (sharedPreferences.contains("mobile") && sharedPreferences.contains("pin")) {
            if (sharedPreferences2.contains("admin")) {
                IS_ADMIN = true;
                startActivity(new Intent(Flash.this, NewMarketActivity.class));
                finish();
            } else {
                makeJsonRequest(sharedPreferences1.getString("token", ""));
            }

                /*startActivity(new Intent(Flash.this,MarketActivity.class));
                progressBar.setVisibility(View.GONE);
                finish();*/
        } else {
            startActivity(new Intent(Flash.this, Mainpage.class));
            progressBar.setVisibility(View.GONE);
            finish();
        }
    }

    private void internetError() {
        if (!Flash.this.isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Flash.this);
            builder.setTitle("Network Connection Problem");
            builder.setMessage("Check Your internet connection to continue");
            builder.setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    if (isNetworkConnected()) {

                        logcheck();
                    } else {
                        internetError();
                    }

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }


    public void makeJsonRequest(final String token) {

        String urlJsonObj = "http://tradewatch.xyz/regularLogin.php";
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
                    // Parsing json object response
                    // response will be a json object
                    progressBar.setVisibility(View.GONE);

                    switch (response.getString("responseCode")) {
                        case "000":
                            IS_ADMIN = false;
                            /*if (response.getInt("daysRemaining") > 10) {
                                startActivity(new Intent(Flash.this, NewMarketActivity.class));
                                finish();
                            } else if (response.getInt("daysRemaining") < 10 & response.getInt("daysRemaining") > 0) {
                                showpaymentalertlessten(response.getInt("daysRemaining"));
                            } else {
                                showpaymentalert();
                            }*/
                            startActivity(new Intent(Flash.this, NewMarketActivity.class));
                            finish();
                            break;
                        case "010":
                            IS_ADMIN = false;
                            Toasty.error(Flash.this, "Invalid credentials", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "020":
                            IS_ADMIN = true;
                            startActivity(new Intent(Flash.this, NewMarketActivity.class));
                            // Toasty.success(Flash.this,"Admin Login", Toast.LENGTH_SHORT, true).show();
                            finish();
                            break;
                        case "021":
                            IS_ADMIN = false;
                            Toasty.error(Flash.this, "User Disabled", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "022":
                            IS_ADMIN = false;
                            success();
                            //Toasty.success(Flash.this, "New User registration", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:
                            IS_ADMIN = false;
                            //startActivity(new Intent(Flash.this,NewMarketActivity.class));
                            Toasty.error(Flash.this, "Login Error", Toast.LENGTH_SHORT, true).show();
                            //finish();
                    }


                    //txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    private void showpaymentalertlessten(final int remaining) {
        new FancyGifDialog.Builder(Flash.this)
                .setTitle("Payment Alert")
                .setMessage("You have " + remaining + " Days remaining.Do you want to extend your account?")
                .setNegativeBtnText("Extend")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Skip")
                .setNegativeBtnBackground("#8b0101")
                .setGifResource(R.drawable.pay)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        startActivity(new Intent(Flash.this, NewMarketActivity.class));
                        finish();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toasty.success(Flash.this, "Extend account", Toast.LENGTH_SHORT, true).show();
                    }
                })
                .build();
    }

    private void showpaymentalert() {
        new FancyGifDialog.Builder(Flash.this)
                .setTitle("Payment Alert")
                .setMessage("Your Account is expired please pay to proceed")
                .setNegativeBtnText("Exit")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Pay Now")
                .setNegativeBtnBackground("#8b0101")
                .setGifResource(R.drawable.pay)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toasty.success(Flash.this, "Payment Page", Toast.LENGTH_SHORT, true).show();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        finish();
                    }
                })
                .build();
    }

    private void success() {

        new FancyGifDialog.Builder(Flash.this)
                .setTitle("Pending Approval")
                .setMessage("Welcome to Vipani,as you are new to the application.The admin need to approve your account.Please wait...")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Exit")
                .setNegativeBtnBackground("#8b0101")  //Pass your Gif here
                .isCancellable(true)
                .setNegativeBtnText("Contact Us")
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {

                    }
                })
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        // Toasty.success(Flash.this,"Payment Page", Toast.LENGTH_SHORT, true).show();
                        finish();

                    }
                })

                .build();
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
