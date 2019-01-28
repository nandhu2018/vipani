package com.gigaappz.vipani.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.ConnectivityReceiver;
import com.gigaappz.vipani.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class Pinregforgot extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    EditText pin1,name,company,place,pincode;
    Button submit;
    KProgressHUD hud;
    AutoCompleteTextView district;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pinreg);
        pin1=(EditText) findViewById(R.id.pin1);
        name=(EditText) findViewById(R.id.name);
        company=(EditText) findViewById(R.id.company);
        place=(EditText) findViewById(R.id.place);
        district=(AutoCompleteTextView) findViewById(R.id.district);
        pincode=(EditText) findViewById(R.id.pincode);
        submit=findViewById(R.id.btn_verify_pin);
        String[] districts = getResources().getStringArray(R.array.district);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, districts);
        district.setAdapter(adapter);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pin1.getText().length()==4){
                    if (name.getText().toString().equalsIgnoreCase("")){
                        name.setError("Enter Name");
                    }else if (company.getText().toString().equalsIgnoreCase("")){
                        company.setError("Enter Company");
                    }else if (place.getText().toString().equalsIgnoreCase("")){
                        place.setError("Enter Place");
                    }else if (district.getText().toString().equalsIgnoreCase("")){
                        district.setError("Enter District");
                    }else if (pincode.getText().toString().equalsIgnoreCase("")){
                        pincode.setError("Enter Pincode");
                    }else {
                        submit.setEnabled(false);
                        hud = KProgressHUD.create(Pinregforgot.this)
                                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                .setCancellable(false)
                                .setLabel("Registering Pin")
                                .show();
                        makeJsonRequest(getIntent().getStringExtra("mobile"),pin1.getText().toString(),name.getText().toString(),company.getText().toString(),place.getText().toString());
                    }

                }else{

                    Toasty.error(Pinregforgot.this, "Enter 4 digit pin correctly", Toast.LENGTH_SHORT, true).show();
                }
            }
        });

    }

    public void makeJsonRequest(final String mobile, final String pin, final String name, final String company, final String place) {

        String urlJsonObj = "http://tradewatch.xyz/api/signUpStep1.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", "qp^&#ss");
            obj.put("name", name);
            obj.put("mobile", mobile);
            obj.put("shop_name", company);
            obj.put("district", district.getText().toString());
            obj.put("place", place);
            obj.put("pincode", pincode.getText().toString());
            obj.put("password", pin);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    // Parsing json object response
                    // response will be a json object

                    boolean status=response.getBoolean("responseStatus");
                    if (status){
                        /*SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                        editor.putString("mobile",mobile);
                        editor.putString("pin",pin);
                        editor.apply();*/
                        if (hud.isShowing()){
                            hud.dismiss();
                        }
                        Toasty.success(Pinregforgot.this, "Pin Registered", Toast.LENGTH_SHORT, true).show();
                        startActivity(new Intent(Pinregforgot.this,Login.class));
                        finish();
                    }else {
                        if (hud.isShowing()){
                            hud.dismiss();
                        }
                        submit.setEnabled(true);
                        Toasty.error(Pinregforgot.this, "Failed", Toast.LENGTH_SHORT, true).show();
                    }
                    /*SharedPreferences.Editor editor = getSharedPreferences("login", MODE_PRIVATE).edit();
                    editor.putString("mobile",mobile);
                    editor.putString("pin",pin);
                    editor.apply();*/
                  /*  if (hud.isShowing()){
                        hud.dismiss();
                    }
                    Toasty.success(Pinreg.this, "Pin Registered", Toast.LENGTH_SHORT, true).show();
                    startActivity(new Intent(Pinreg.this,Login.class));
                    finish();*/
                    submit.setEnabled(true);

                    //txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                    submit.setEnabled(true);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // hide the progress dialog
                submit.setEnabled(true);
                if (hud.isShowing()){
                    hud.dismiss();
                }
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }


    // Showing the status in Snackbar
    private void showSnack(boolean isConnected) {
        String message;
        int color;
        if (isConnected) {
            message = "Good! Connected to Internet";
            color = Color.GREEN;
        } else {
            message = "Sorry! Not connected to internet";
            color = Color.RED;
        }

        Snackbar snackbar = Snackbar
                .make(findViewById(R.id.fab), message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(color);
        snackbar.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register connection status listener
        //AppController.getInstance().setConnectivityListener(this);
    }

    /**
     * Callback will be triggered when there is change in
     * network connection
     */
    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected);
    }
}
