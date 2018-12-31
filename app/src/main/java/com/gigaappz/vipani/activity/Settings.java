package com.gigaappz.vipani.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;

public class Settings extends AppCompatActivity {
    EditText name,company,district,place,pincode,mobile;
    TextView verify;
    Button save,logout,chngpass;
    ImageView back;
    String curmobile="";
    KProgressHUD hud;
    FirebaseAuth auth;
    private String verificationCode,otpval;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    Dialog dialog;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        name=(EditText)findViewById(R.id.name);
        company=(EditText)findViewById(R.id.company);
        district=(EditText)findViewById(R.id.district);
        place=(EditText)findViewById(R.id.place);
        pincode=(EditText)findViewById(R.id.pincode);
        mobile=(EditText)findViewById(R.id.mobile);
        verify=(TextView) findViewById(R.id.verify);
        save=(Button) findViewById(R.id.save);
        chngpass=(Button) findViewById(R.id.chngpass);
        logout=(Button)findViewById(R.id.logout);
        back=(ImageView) findViewById(R.id.back);
        sharedPreferences= getSharedPreferences("token", Context.MODE_PRIVATE);
        getUserDetails(sharedPreferences.getString("token", ""));
        StartFirebaseLogin();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this,NewMarketActivity.class));
                finish();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor pref = getSharedPreferences("token", 0).edit();
                pref.clear();
                pref.apply();
                SharedPreferences.Editor pref1 = getSharedPreferences("login", 0).edit();
                pref1.clear();
                pref1.apply();
                SharedPreferences.Editor pref2 = getSharedPreferences("admin", 0).edit();
                pref2.clear();
                pref2.apply();
                SharedPreferences.Editor pref3 = getSharedPreferences("flashdetails", 0).edit();
                pref3.clear();
                pref3.apply();
                startActivity(new Intent(Settings.this, Login.class));
                finish();
            }
        });
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mobile.getText().toString().equalsIgnoreCase("")){
                    Toast.makeText(Settings.this, "Enter valid mobile", Toast.LENGTH_SHORT).show();
                }else {
                    hud = KProgressHUD.create(Settings.this)
                            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                            .setCancellable(false)
                            .setLabel("Sending otp")
                            .show();

                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91"+mobile.getText().toString(),                     // Phone number to verify
                            60,                           // Timeout duration
                            TimeUnit.SECONDS,                // Unit of timeout
                            Settings.this,        // Activity (for callback binding)
                            mCallback);

                }

            }
        });


        chngpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changepass();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equalsIgnoreCase("")){
                    name.setError("Enter Name");
                }else if (company.getText().toString().equalsIgnoreCase("")){
                    company.setError("Enter Company");
                }else if (district.getText().toString().equalsIgnoreCase("")){
                    district.setError("Enter District");
                }
                else if (place.getText().toString().equalsIgnoreCase("")){
                    place.setError("Enter Place");
                }
                else if (pincode.getText().toString().equalsIgnoreCase("")){
                    pincode.setError("Enter Pincode");
                }
                else if (mobile.getText().toString().equalsIgnoreCase("")){
                    mobile.setError("Enter Mobile");
                }else if (!mobile.getText().toString().equalsIgnoreCase(curmobile)){
                    Toast.makeText(Settings.this, "Verify new mobile number before saving settings", Toast.LENGTH_SHORT).show();
                }else {
                    updateUserDetails(sharedPreferences.getString("token", ""));
                }
            }
        });



    }
    private void getUserDetails(String userid) {
        String urlJsonObj = "http://tradewatch.xyz/api/getMyDetails.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", userid);
        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        JSONArray dataArray = response.getJSONArray("data");
                        JSONObject data = dataArray.getJSONObject(0);
                        name.setText(data.getString("name"));
                        company.setText(data.getString("shop_name"));
                        district.setText(data.getString("district"));
                        place.setText(data.getString("place"));
                        pincode.setText(data.getString("pincode"));
                        mobile.setText(data.getString("mobile"));
                        curmobile=data.getString("mobile");

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
    private void updateUserDetails(String userid) {
        String urlJsonObj = "http://tradewatch.xyz/api/editMyDetails.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", userid);
            obj.put("name", name.getText().toString());
            obj.put("mobile", mobile.getText().toString());
            obj.put("shop_name", company.getText().toString());
            obj.put("district", district.getText().toString());
            obj.put("place", place.getText().toString());
            obj.put("pincode", pincode.getText().toString());
        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        SharedPreferences.Editor editor = getSharedPreferences("token", MODE_PRIVATE).edit();
                        editor.putString("token",response.getString("authToken"));
                        editor.apply();
                        Toast.makeText(Settings.this, "Updated Data Successfully", Toast.LENGTH_SHORT).show();
                        getUserDetails(response.getString("authToken"));
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
    public void otpverify() {
        dialog = new Dialog(Settings.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);
        final TextInputLayout remark = dialog.findViewById(R.id.remarks);
        final EditText reasonText = dialog.findViewById(R.id.daysedit);
        // remark.setVisibility(View.VISIBLE);
        reasonText.setHint("Enter Otp Here");
        reasonText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);
        okButton.setText("Verify");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String term = inputLayout.getEditText().getText().toString();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, term);
                SigninWithPhone(credential);
                //dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void changepass() {
        final Dialog dialog1 = new Dialog(Settings.this);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setCancelable(false);
        dialog1.setContentView(R.layout.customalert);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog1.findViewById(R.id.days);
        final TextInputLayout remark = dialog1.findViewById(R.id.remarks);
        final EditText reasonText = dialog1.findViewById(R.id.daysedit);
         remark.setVisibility(View.VISIBLE);
        reasonText.setHint("Current Password");
        remark.getEditText().setHint("New Password");
        reasonText.setKeyListener(DigitsKeyListener.getInstance("1234567890"));
        Button okButton = (Button) dialog1.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog1.findViewById(R.id.cancel_button);
        okButton.setText("Change");
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current = inputLayout.getEditText().getText().toString();
                String newpass = remark.getEditText().getText().toString();
                changepassword(sharedPreferences.getString("token",""),current,newpass);
                //dialog.dismiss();
            }
        });

        dialog1.show();
    }
    private void changepassword(String userid,String current,String newpassword) {
        String urlJsonObj = "http://tradewatch.xyz/api/changeMyPassword.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", userid);
            obj.put("current_password", current);
            obj.put("new_password", newpassword);
        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        Toast.makeText(Settings.this, "Password updated", Toast.LENGTH_SHORT).show();

                    }else {
                        Toast.makeText(Settings.this, ""+response.getString("responseMesssage"), Toast.LENGTH_SHORT).show();
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
    @Override
    public void onBackPressed() {
        startActivity(new Intent(Settings.this,NewMarketActivity.class));
        finish();
    }
    private void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                //otpverify();

            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (hud.isShowing()){
                    hud.dismiss();
                }
                Toasty.error(Settings.this, "verification failed"+e.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                if (hud.isShowing()){
                    hud.dismiss();
                }
                //Toast.makeText(Registration.this,"Code sent", Toast.LENGTH_SHORT).show();
                Toasty.success(Settings.this, "Code Sent!", Toast.LENGTH_SHORT, true).show();
                otpverify();
            }
        };
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (hud.isShowing()){
                                hud.dismiss();
                            }
                            Toasty.success(Settings.this, "Success!", Toast.LENGTH_SHORT, true).show();
                            FirebaseAuth.getInstance().signOut();
                            curmobile=mobile.getText().toString();
                            if (!isFinishing()){
                                dialog.dismiss();
                            }
                        } else {
                            if (hud.isShowing()){
                                hud.dismiss();
                            }
                            Toasty.error(Settings.this, "Incorrect OTP", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
    }
}
