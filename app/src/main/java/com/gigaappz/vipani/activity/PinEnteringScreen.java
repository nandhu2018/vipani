package com.gigaappz.vipani.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.pinlockview.IndicatorDots;
import com.andrognito.pinlockview.PinLockListener;
import com.andrognito.pinlockview.PinLockView;
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

public class PinEnteringScreen extends AppCompatActivity implements ConnectivityReceiver.ConnectivityReceiverListener {
    public static final String TAG = "PinLockView";

    private PinLockView mPinLockView;
    private IndicatorDots mIndicatorDots;
    SharedPreferences sharedPreferences;
    KProgressHUD hud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pin_entering_screen);

        mPinLockView = (PinLockView) findViewById(R.id.pin_lock_view);
        mIndicatorDots = (IndicatorDots) findViewById(R.id.indicator_dots);

        mPinLockView.attachIndicatorDots(mIndicatorDots);
        mPinLockView.setPinLockListener(mPinLockListener);

        sharedPreferences=getSharedPreferences("login", Context.MODE_PRIVATE);

        //mPinLockView.setCustomKeySet(new int[]{2, 3, 1, 5, 9, 6, 7, 0, 8, 4});
        //mPinLockView.enableLayoutShuffling();
        mPinLockView.setPinLength(4);
        mPinLockView.setTextColor(ContextCompat.getColor(this, R.color.white));

        mIndicatorDots.setIndicatorType(IndicatorDots.IndicatorType.FILL_WITH_ANIMATION);

    }
    private PinLockListener mPinLockListener = new PinLockListener() {
        @Override
        public void onComplete(String pin) {

            if (sharedPreferences.contains("pin")){
                hud = KProgressHUD.create(PinEnteringScreen.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setLabel("Verifying Pin")
                        .show();
                if (sharedPreferences.getString("pin","").equalsIgnoreCase(pin)){

                    makeJsonRequest(sharedPreferences.getString("mobile",""),pin);

                }else{
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                    Toasty.error(PinEnteringScreen.this, "Wrong pin", Toast.LENGTH_SHORT, true).show();
                }
            }

        }

        @Override
        public void onEmpty() {
            Log.d(TAG, "Pin empty");
        }

        @Override
        public void onPinChange(int pinLength, String intermediatePin) {
            Log.d(TAG, "Pin changed, new length " + pinLength + " with intermediate pin " + intermediatePin);
        }
    };


    public void makeJsonRequest(final String mobile, final String pin) {

        String urlJsonObj = "http://tradewatch.xyz/api/login.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", "qp^&#ss");
            obj.put("userId", mobile);
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
                        SharedPreferences.Editor editor = getSharedPreferences("token", MODE_PRIVATE).edit();
                        editor.putString("token",response.getString("authToken"));
                        editor.apply();
                        if (hud.isShowing()){
                            hud.dismiss();
                        }

                        Toasty.success(PinEnteringScreen.this, "Logged In", Toast.LENGTH_SHORT, true).show();
                        // TODO: 09-Oct-18 updated
                        startActivity(new Intent(PinEnteringScreen.this,NewMarketActivity.class));
                        finish();
                    }else {
                        if (hud.isShowing()){
                            hud.dismiss();
                        }
                        Toasty.error(PinEnteringScreen.this, "Failed", Toast.LENGTH_SHORT, true).show();
                    }


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
                // hide the progress dialog
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
