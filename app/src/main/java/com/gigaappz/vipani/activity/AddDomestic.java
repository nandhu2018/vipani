package com.gigaappz.vipani.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.models.Domestic;
import com.gigaappz.vipani.utils.AppConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class AddDomestic extends AppCompatActivity {
    TextInputLayout type,subhead,value,remarks;
    Button save;
    SharedPreferences sharedPreferences;
    ArrayAdapter<String> arrayAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_domestic_values);
        type=(TextInputLayout) findViewById(R.id.mainhead);
        subhead=(TextInputLayout) findViewById(R.id.subhead);
        value=(TextInputLayout) findViewById(R.id.currentrate);
        remarks=(TextInputLayout) findViewById(R.id.remark);
        save=(Button) findViewById(R.id.save);
        arrayAdapter = new ArrayAdapter<String>(AddDomestic.this, android.R.layout.select_dialog_singlechoice);
        sharedPreferences=getSharedPreferences("token", Context.MODE_PRIVATE);
        getdomesticcategorylist("g*Rg3I0");
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type.getEditText().getText().toString().equalsIgnoreCase("")){
                    type.getEditText().setError("Select title");
                }else {
                    addDomestic("g*Rg3I0", type.getEditText().getText().toString(), subhead.getEditText().getText().toString(), value.getEditText().getText().toString(),remarks.getEditText().getText().toString());
                }
            }
        });
        type.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddDomestic.this);
                builderSingle.setIcon(R.drawable.user_icon);
                builderSingle.setTitle("Select One Name:-");


                //listalert();
                builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String strName = arrayAdapter.getItem(which);
                        type.getEditText().setText(strName);

                    }
                });
                builderSingle.show();

            }
        });
    }

    public void listalert(){
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddDomestic.this);
        builderSingle.setIcon(R.drawable.user_icon);
        builderSingle.setTitle("Select One Name:-");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddDomestic.this, android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Hardik");
        arrayAdapter.add("Archit");
        arrayAdapter.add("Jignesh");
        arrayAdapter.add("Umang");
        arrayAdapter.add("Gatti");

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String strName = arrayAdapter.getItem(which);
                type.getEditText().setText(strName);
                /*String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(AddDomestic.this);
                builderInner.setMessage(strName);
                type.getEditText().setText(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();*/
            }
        });
        builderSingle.show();
    }
    public void getdomesticcategorylist(final String token) {

        String urlJsonObj = "http://tradewatch.xyz/api/getDomesticCategories.php";
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



                    JSONArray cast=response.getJSONArray("data");
                    for (int i=0; i<cast.length(); i++) {
                        JSONObject domcategory = cast.getJSONObject(i);
                        arrayAdapter.add(domcategory.getString("name"));
                    }



                /*String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(AddDomestic.this);
                builderInner.setMessage(strName);
                type.getEditText().setText(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();*/



                    //txtResponse.setText(jsonResponse);

                } catch (JSONException e) {
                   // progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddDomestic.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
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
    public void addDomestic(final String token, final String title, final String sub, final String price,final String remarks) {

        String urlJsonObj = "http://tradewatch.xyz/api/updateDomesticPrice.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("main_title", title);
            obj.put("sub_title", sub);
            obj.put("price", price);
            obj.put("udf1", remarks);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        mFirebaseInstance = FirebaseDatabase.getInstance();
                        mFirebaseDatabase = mFirebaseInstance.getReference("domestic");
                        Domestic name=new Domestic();
                        name.setName(title+""+price+""+sub);
                        mFirebaseDatabase.setValue(name);
                        Toasty.success(AddDomestic.this, "Domestic value updated", Toast.LENGTH_SHORT, true).show();
                        startActivity(new Intent(AddDomestic.this,NewMarketActivity.class));
                        finish();
                    }else {
                        Toast.makeText(AddDomestic.this, ""+response.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toasty.error(AddDomestic.this, "Domestic value updation failed", Toast.LENGTH_SHORT, true).show();
                    // progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(AddDomestic.this, "Domestic value updation failed", Toast.LENGTH_SHORT, true).show();
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    @Override
    public void onBackPressed() {
        AppConstants.SELECTED_TAB =0;
        startActivity(new Intent(AddDomestic.this, NewMarketActivity.class));
        finish();
    }
}
