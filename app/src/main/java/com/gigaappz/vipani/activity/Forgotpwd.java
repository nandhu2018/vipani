package com.gigaappz.vipani.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gigaappz.vipani.ConnectivityReceiver;
import com.gigaappz.vipani.PrefManager;
import com.gigaappz.vipani.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.stfalcon.smsverifycatcher.OnSmsCatchListener;
import com.stfalcon.smsverifycatcher.SmsVerifyCatcher;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class Forgotpwd extends AppCompatActivity implements View.OnClickListener, ConnectivityReceiver.ConnectivityReceiverListener {
    private ViewPager viewPager;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode,otpval;
    private Button btnRequestSms, btnVerifyOtp,btnVerifypin;
    private EditText inputName, inputEmail, inputMobile, inputOtp,enterpin,reenterpin;
    private ProgressBar progressBar;
    private PrefManager pref;
    private ImageButton btnEditMobile;
    private TextView txtEditMobile;
    private ViewPagerAdapter adapter;
    private LinearLayout layoutEditMobile;
    SmsVerifyCatcher smsVerifyCatcher;
    KProgressHUD hud;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);
        inputName = (EditText) findViewById(R.id.inputName);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputMobile = (EditText) findViewById(R.id.inputMobile);
        inputOtp = (EditText) findViewById(R.id.inputOtp);
        enterpin = (EditText) findViewById(R.id.pin1);
        reenterpin = (EditText) findViewById(R.id.pin2);
        btnRequestSms = (Button) findViewById(R.id.btn_request_sms);
        btnVerifyOtp = (Button) findViewById(R.id.btn_verify_otp);
        btnVerifypin = (Button) findViewById(R.id.verifypin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnEditMobile = (ImageButton) findViewById(R.id.btn_edit_mobile);
        txtEditMobile = (TextView) findViewById(R.id.txt_edit_mobile);
        layoutEditMobile = (LinearLayout) findViewById(R.id.layout_edit_mobile);

        StartFirebaseLogin();

        smsVerifyCatcher = new SmsVerifyCatcher(this, new OnSmsCatchListener<String>() {
            @Override
            public void onSmsCatch(String message) {
                String code = parseCode(message);//Parse verification code
                inputOtp.setText(code);//set code in edit text
                //then you can send verification code to server
            }
        });

        //smsVerifyCatcher.setPhoneNumberFilter("56161174");
        // view click listeners
        //Toast.makeText(this, ""+, Toast.LENGTH_SHORT).show();
        btnEditMobile.setOnClickListener(this);
        btnRequestSms.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);
        btnVerifypin.setOnClickListener(this);
        pref = new PrefManager(this);
// hiding the edit mobile number
        layoutEditMobile.setVisibility(View.GONE);
// Checking for user session
        // if user is already logged in, take him to main activity
        if (pref.isLoggedIn()) {
            Toast.makeText(this, "already logged in", Toast.LENGTH_SHORT).show();
        }

        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (pref.isWaitingForSms()) {
            viewPager.setCurrentItem(1);
            layoutEditMobile.setVisibility(View.VISIBLE);
        }

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        // hiding the edit mobile number
        layoutEditMobile.setVisibility(View.GONE);
    }

    /**
     * Parse verification code
     *
     * @param message sms message
     * @return only four numbers from massage string
     */
    private String parseCode(String message) {
        Pattern p = Pattern.compile("\\b\\d{6}\\b");
        Matcher m = p.matcher(message);
        String code = "";
        while (m.find()) {
            code = m.group(0);
        }
        return code;
    }



    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layout_sms;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;

            }
            return findViewById(resId);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_request_sms:
                hud = KProgressHUD.create(Forgotpwd.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setLabel("Sending otp")
                        .show();
                validateForm();
                break;

            case R.id.btn_verify_otp:
                hud = KProgressHUD.create(Forgotpwd.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .setLabel("Verifying otp")
                        .show();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, inputOtp.getText().toString());
                SigninWithPhone(credential);


                break;

            case R.id.btn_edit_mobile:
                viewPager.setCurrentItem(0);
                layoutEditMobile.setVisibility(View.GONE);
                pref.setIsWaitingForSms(false);
                break;
            case R.id.verifypin:
               // Toast.makeText(this, "Registration Success", Toast.LENGTH_SHORT).show();

                break;
        }
    }

    /**
     * Validating user details form
     */
    private void validateForm() {
        /*String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();*/
        String mobile = inputMobile.getText().toString().trim();

        /*// validating empty name and email
        if (name.length() == 0 || email.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter your details", Toast.LENGTH_SHORT).show();
            return;
        }*/

        // validating mobile number
        // it should be of 10 digits length
        if (isValidPhoneNumber(mobile)) {

            // request for sms
            //progressBar.setVisibility(View.VISIBLE);


            // saving the mobile number in shared preferences
            pref.setMobileNumber(mobile);
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    "+91"+inputMobile.getText().toString(),                     // Phone number to verify
                    60,                           // Timeout duration
                    TimeUnit.SECONDS,                // Unit of timeout
                    Forgotpwd.this,        // Activity (for callback binding)
                    mCallback);




            txtEditMobile.setText(pref.getMobileNumber());
            layoutEditMobile.setVisibility(View.VISIBLE);
        } else {
            Toasty.error(Forgotpwd.this, "Please enter valid mobile number", Toast.LENGTH_SHORT, true).show();
        }
    }
    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }



    private void StartFirebaseLogin() {
        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
              //  Toast.makeText(Registration.this,"verification completed",Toast.LENGTH_SHORT).show();
                // moving the screen to next pager item i.e otp screen
                // boolean flag saying device is waiting for sms
                pref.setIsWaitingForSms(true);

            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                if (hud.isShowing()){
                    hud.dismiss();
                }
                Toasty.error(Forgotpwd.this, "verification failed"+e.getMessage(), Toast.LENGTH_SHORT, true).show();
            }
            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                // hiding the progress bar
                progressBar.setVisibility(View.GONE);
                viewPager.setCurrentItem(1);
                if (hud.isShowing()){
                    hud.dismiss();
                }
                //Toast.makeText(Registration.this,"Code sent", Toast.LENGTH_SHORT).show();
                Toasty.success(Forgotpwd.this, "Success!", Toast.LENGTH_SHORT, true).show();
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
                            Toasty.success(Forgotpwd.this, "Success!", Toast.LENGTH_SHORT, true).show();
                            FirebaseAuth.getInstance().signOut();
                            Intent pinreg=new Intent(Forgotpwd.this,PinEnteringScreen.class);
                            pinreg.putExtra("mobile",inputMobile.getText().toString());
                            startActivity(pinreg);

                            finish();
                        } else {
                            if (hud.isShowing()){
                                hud.dismiss();
                            }
                            Toasty.error(Forgotpwd.this, "Incorrect OTP", Toast.LENGTH_SHORT, true).show();
                        }
                    }
                });
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
       // AppController.getInstance().setConnectivityListener(this);
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
