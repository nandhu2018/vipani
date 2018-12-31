package com.gigaappz.vipani.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.models.Domestic;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class Newsedit extends AppCompatActivity {
    ImageView back;
    EditText head,body;
    Button update;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsedit);
        back=(ImageView)findViewById(R.id.back);
        head=(EditText) findViewById(R.id.heading);
        body=(EditText)findViewById(R.id.body);
        update=(Button) findViewById(R.id.ok_button);

        head.setText(getIntent().getStringExtra("head"));
        body.setText(getIntent().getStringExtra("body"));

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatenews();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void updatenews() {

        String urlJsonObj = "http://tradewatch.xyz/api/editNews.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", "g*Rg3I0");
            obj.put("id", getIntent().getStringExtra("id"));
            obj.put("title", head.getText().toString());
            obj.put("body", body.getText().toString());

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        mFirebaseInstance = FirebaseDatabase.getInstance();
                        mFirebaseDatabase = mFirebaseInstance.getReference("news1");
                        Domestic name=new Domestic();
                        name.setName(head.getText().toString());
                        mFirebaseDatabase.setValue(name);
                        Toast.makeText(Newsedit.this, "News updated", Toast.LENGTH_SHORT).show();
                        finish();

                    }else {
                        Toast.makeText(Newsedit.this, ""+response.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(Newsedit.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    // progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Newsedit.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }
}
