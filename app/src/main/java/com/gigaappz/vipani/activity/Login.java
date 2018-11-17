package com.gigaappz.vipani.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.R;
import com.google.android.gms.common.internal.ISignInButtonCreator;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

import static com.gigaappz.vipani.utils.AppConstants.IS_ADMIN;

public class Login extends AppCompatActivity {
    Button login;
    TextView forgotpsw;
    TextInputLayout mobile,password;
    KProgressHUD hud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login=(Button)findViewById(R.id.login_btn);
        forgotpsw=(TextView) findViewById(R.id.forgot_pass);
        mobile=(TextInputLayout) findViewById(R.id.login_mobile);
        password=(TextInputLayout) findViewById(R.id.login_password);
        
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hud = KProgressHUD.create(Login.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setLabel("Please Wait")
                        .show();
                makeJsonRequest(mobile.getEditText().getText().toString(),password.getEditText().getText().toString());

            }
        });
        forgotpsw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,Main2Activity.class));
                finish();
            }
        });
    }

    public void makeJsonRequest(final String mobile, final String pin) {

        String urlJsonObj = "http://tradewatch.xyz/login.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", "qp^&#ss");
            obj.put("user_id", mobile);
            obj.put("password", pin);


        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    if (response.has("authToken")){
                        SharedPreferences.Editor editor = getSharedPreferences("token", MODE_PRIVATE).edit();
                        editor.putString("token",response.getString("authToken"));
                        editor.apply();
                    }else {
                        SharedPreferences.Editor editor = getSharedPreferences("token", MODE_PRIVATE).edit();
                        editor.putString("token","qp^&#ss");
                        editor.apply();
                    }

                    SharedPreferences.Editor editor1 = getSharedPreferences("login", MODE_PRIVATE).edit();
                    editor1.putString("mobile",mobile);
                    editor1.putString("pin",pin);
                    editor1.apply();
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                    switch (response.getString("responseCode")){
                        case "000":IS_ADMIN=false;
                            if (response.getInt("daysRemaining")>10){
                                startActivity(new Intent(Login.this,NewMarketActivity.class));
                                finish();
                            }else if (response.getInt("daysRemaining")<10&response.getInt("daysRemaining")>0){
                                showpaymentalertlessten(response.getInt("daysRemaining"));
                            }else {
                                showpaymentalert();
                            }
                            break;
                        case "010":IS_ADMIN=false;
                            Toasty.error(Login.this, "Invalid credentials", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "020":IS_ADMIN=true;
                            SharedPreferences.Editor editor = getSharedPreferences("admin", MODE_PRIVATE).edit();
                            editor.putString("admin","true");
                            editor.apply();
                            startActivity(new Intent(Login.this,NewMarketActivity.class));
                            //Toasty.success(Login.this,"Admin Login", Toast.LENGTH_SHORT, true).show();
                            finish();
                            break;
                        case "021":IS_ADMIN=false;
                            Toasty.error(Login.this, "User Disabled", Toast.LENGTH_SHORT, true).show();
                            break;
                        case "022":IS_ADMIN=false;
                            success();
                            //Toasty.success(Login.this, "New User registration", Toast.LENGTH_SHORT, true).show();
                            break;
                        default:IS_ADMIN=false;
                            //startActivity(new Intent(Flash.this,NewMarketActivity.class));
                            Toasty.error(Login.this, "Login Error", Toast.LENGTH_SHORT, true).show();
                            //finish();
                    }
                    // Parsing json object response
                    // response will be a json object

                    /*String status=response.getString("responseStatus");
                    if (status.equalsIgnoreCase("true")){
                        SharedPreferences.Editor editor = getSharedPreferences("token", MODE_PRIVATE).edit();
                        editor.putString("token",response.getString("authToken"));
                        editor.apply();
                        if (hud.isShowing()){
                            hud.dismiss();
                        }

                        SharedPreferences.Editor editor1 = getSharedPreferences("login", MODE_PRIVATE).edit();
                        editor1.putString("mobile",mobile);
                        editor1.putString("pin",pin);
                        editor1.apply();
                        //Toasty.success(Login.this, "Logged In", Toast.LENGTH_SHORT, true).show();
                        switch (response.getString("responseCode")){
                            case "000":
                                if (response.getInt("daysRemaining")>10){
                                    startActivity(new Intent(Login.this,MarketActivity.class));
                                    finish();
                                }else if (response.getInt("daysRemaining")<10&response.getInt("daysRemaining")>0){
                                    showpaymentalertlessten(response.getInt("daysRemaining"));
                                }else {
                                    showpaymentalert();
                                }
                                break;
                            case "010":Toasty.error(Login.this, "Invalid credentials", Toast.LENGTH_SHORT, true).show();
                                    break;
                            case "020":Toasty.success(Login.this,"Admin Login", Toast.LENGTH_SHORT, true).show();
                                break;
                            case "021":Toasty.error(Login.this, "User Disabled", Toast.LENGTH_SHORT, true).show();
                                break;
                            case "022":Toasty.error(Login.this, "New User registration", Toast.LENGTH_SHORT, true).show();
                                break;

                        }

                    }else {
                        if (hud.isShowing()){
                            hud.dismiss();
                        }
                        Toasty.error(Login.this, "Invalid credentials", Toast.LENGTH_SHORT, true).show();
                    }*/


                    //txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (hud.isShowing()){
                    hud.dismiss();
                }
                makeJsonRequest(mobile,pin);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }
    private void showpaymentalertlessten(final int remaining){
        new FancyGifDialog.Builder(Login.this)
                .setTitle("Payment Alert")
                .setMessage("You have "+remaining+" Days remaining.Do you want to extend your account?")
                .setNegativeBtnText("Extend")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Skip")
                .setNegativeBtnBackground("#8b0101")
                .setGifResource(R.drawable.pay)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        // TODO: 09-Oct-18 updated
                        startActivity(new Intent(Login.this,NewMarketActivity.class));
                        finish();
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Toasty.success(Login.this,"Extend account", Toast.LENGTH_SHORT, true).show();
                    }
                })
                .build();
    }
    private void showpaymentalert(){
        new FancyGifDialog.Builder(Login.this)
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
                        Toasty.success(Login.this,"Payment Page", Toast.LENGTH_SHORT, true).show();
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
    private void success(){

        if (hud.isShowing()){
            hud.dismiss();
        }


        new FancyGifDialog.Builder(Login.this)
                .setTitle("Pending Approval")
                .setMessage("Welcome to Vipani,as you are new to the application.The admin need to approve your account.Please wait...")
                .setPositiveBtnBackground("#3fb551")
                .setPositiveBtnText("Exit")
                .setNegativeBtnBackground("#8b0101")  //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        // Toasty.success(Flash.this,"Payment Page", Toast.LENGTH_SHORT, true).show();
                        finish();

                    }
                })

                .build();
    }
}
