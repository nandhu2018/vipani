package com.gigaappz.vipani.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.fragments.UsersTab;
import com.gigaappz.vipani.interfaces.NewUserDataAdded;
import com.gigaappz.vipani.utils.AppConstants;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.dmoral.toasty.Toasty;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

public class AddCustomer extends AppCompatActivity {
    TextInputLayout mobile,password,days,refered,name,company,place;
    Button adduser;
    int day, month, year;
    KProgressHUD hud;
    ArrayList<String> items=new ArrayList<>();
    ArrayList<Integer> ids=new ArrayList<>();
    SpinnerDialog spinnerDialog;
    String idref="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_user);
        mobile=(TextInputLayout) findViewById(R.id.mobile);
        password=(TextInputLayout) findViewById(R.id.password);
        days=(TextInputLayout) findViewById(R.id.days);
        refered=(TextInputLayout) findViewById(R.id.refered);
        name=(TextInputLayout) findViewById(R.id.name);
        company=(TextInputLayout) findViewById(R.id.company);
        place=(TextInputLayout) findViewById(R.id.place);
        adduser=(Button)findViewById(R.id.adduser);

        /*items.add("Mumbai");
        items.add("Delhi");
        items.add("Bengaluru");
        items.add("Hyderabad");
        items.add("Ahmedabad");
        items.add("Chennai");
        items.add("Kolkata");
        items.add("Surat");
        items.add("Pune");
        items.add("Jaipur");
        items.add("Lucknow");
        items.add("Kanpur");*/

        getallcontacts();
       // spinnerDialog=new SpinnerDialog(AddCustomer.this,items,"Select or Search User","Close");// With No Animation
        spinnerDialog=new SpinnerDialog(AddCustomer.this,items,"Select or Search User",R.style.DialogAnimations_SmileWindow,"Close");// With 	Animation
        spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
            @Override
            public void onClick(String item, int position) {
                //Toast.makeText(AddCustomer.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                refered.getEditText().setText(item);
                idref=ids.get(position).toString();
                //selectedItems.setText(item + " Position: " + position);
            }
        });

        refered.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerDialog.showSpinerDialog();
            }
        });
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hud = KProgressHUD.create(AddCustomer.this)
                        .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                        .setCancellable(false)
                        .show();
                addUser("g*Rg3I0",mobile.getEditText().getText().toString(),password.getEditText().getText().toString(),days.getEditText().getText().toString(),idref);
                //hud.dismiss();

                /*UsersTab.myListener.updateView(true,mobile.getEditText().getText().toString());
                AppConstants.SELECTED_TAB =2;
                // TODO: 09-Oct-18 updated*/

            }
        });
        /*days.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // showDialog(0);

                // Get Current Date
               *//* final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
*//*

                final DatePickerDialog datePickerDialog = new DatePickerDialog(AddCustomer.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                days.getEditText().setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);


                            }
                        }, year, month, day);



                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                //datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
                datePickerDialog.show();




            }
        });*/
    }

    @Override
    public void onBackPressed() {
        AppConstants.SELECTED_TAB =2;
        startActivity(new Intent(AddCustomer.this,NewMarketActivity.class));
        finish();
    }

    private void getallcontacts(){
        SharedPreferences sharedPreferences=getSharedPreferences("token", Context.MODE_PRIVATE);

        //String url="http://tradewatch.xyz/api/allMobileNumbers.php";
        String url="http://tradewatch.xyz/api/userDetails.php";
        JSONObject object=new JSONObject();
        try {
            //object.put("auth",sharedPreferences.getString("token",""));
            object.put("auth","g*Rg3I0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray cast = response.getJSONArray("data");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject mobile = cast.getJSONObject(i);
                        items.add(mobile.getString("mobile_no"));
                        ids.add(mobile.getInt("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        AppController.getInstance().addToRequestQueue(request);

    }

    public void addUser(final String token,String mobile,String password,String days,String refby) {

        String urlJsonObj = "http://tradewatch.xyz/api/addUsersAdmin.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("name", name.getEditText().getText().toString());
            obj.put("mobile", mobile);
            obj.put("shop_name", company.getEditText().getText().toString());
            obj.put("district", "");
            obj.put("place", place.getEditText().getText().toString());
            obj.put("pincode", "");
            obj.put("password", password);
            obj.put("days", days);
            obj.put("ref_by", refby);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        Toasty.success(AddCustomer.this, "User Added", Toast.LENGTH_SHORT, true).show();
                        startActivity(new Intent(AddCustomer.this,NewMarketActivity.class));
                        finish();
                    }else{
                        Toasty.error(AddCustomer.this, "User Already Exists", Toast.LENGTH_SHORT, true).show();
                    }


                } catch (JSONException e) {
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                    // progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (hud.isShowing()){
                    hud.dismiss();
                }
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }
    private static NewUserDataAdded dataAdded;
    public void onNewUserDataAdded(NewUserDataAdded dataAdded){
        AddCustomer.dataAdded   = dataAdded;
    }

}
