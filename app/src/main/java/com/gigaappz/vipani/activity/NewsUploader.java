package com.gigaappz.vipani.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
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
import com.gigaappz.vipani.ConnectivityReceiver;
import com.gigaappz.vipani.FirebaseManupulator;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.models.DomesticValueModel;
import com.gigaappz.vipani.models.NewsValueModel;
import com.gigaappz.vipani.utils.AppConstants;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.mvc.imagepicker.ImagePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

import static com.gigaappz.vipani.utils.AppConstants.NEWS_FROM_DEVICE;
import static com.gigaappz.vipani.utils.AppConstants.NEWS_FROM_URL;
import static com.gigaappz.vipani.utils.AppConstants.NEWS_KEY;


public class NewsUploader extends AppCompatActivity implements View.OnClickListener,
        ConnectivityReceiver.ConnectivityReceiverListener {

    private EditText firstValue;
    private EditText secondValue;
    private EditText thirdValue;
    private ImageView newsPhoto;
    KProgressHUD hud;
    String encoded;
    private TextView fromDevice, fromURL;
    String newsTitle;
    String description ;
    String newsURL;
    String titlefromurl,imgfromurl;
    private int GALLERY_ACTIVITY_CODE = 200;
    private final int RESULT_CROP = 400;
    //FirebaseStorage storage;
    //StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_uploader);

        firstValue  = findViewById(R.id.first_et_news);
        secondValue = findViewById(R.id.second_et_news);
        thirdValue  = findViewById(R.id.third_et_news);
        newsPhoto   = findViewById(R.id.news_image);
        fromDevice  = findViewById(R.id.news_from_device);
        fromURL     = findViewById(R.id.news_from_url);
        Button addButton    = findViewById(R.id.add_button);
        // storage = FirebaseStorage.getInstance();
        // storageReference = storage.getReference();
//        Button cancelButton = findViewById(R.id.cancel_button);

        addButton.setOnClickListener(this);
//        cancelButton.setOnClickListener(this);
        newsPhoto.setOnClickListener(this);
        ImagePicker.setMinQuality(600, 600);
        //storageReference=FirebaseStorage.getInstance().getReference();
        fromDevice.setOnClickListener(this);
        fromURL.setOnClickListener(this);

        newsPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }


    private Uri imageUri;
    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            try {
                imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                //encode image to base64 string
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] byteArrayImage = baos.toByteArray();
                encoded = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                newsPhoto.setImageBitmap(selectedImage);


            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(NewsUploader.this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(NewsUploader.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    private String newsFrom = NEWS_FROM_DEVICE;
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.news_from_device:
                newsPhoto.setVisibility(View.VISIBLE);
                firstValue.setVisibility(View.VISIBLE);
                secondValue.setVisibility(View.VISIBLE);
                thirdValue.setVisibility(View.VISIBLE);

                newsFrom    = NEWS_FROM_DEVICE;

                fromURL.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                fromDevice.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
                break;
            case R.id.news_from_url:
                newsPhoto.setVisibility(View.GONE);
                firstValue.setVisibility(View.VISIBLE);
                secondValue.setVisibility(View.GONE);
                thirdValue.setVisibility(View.GONE);

                newsFrom    = NEWS_FROM_URL;

                fromURL.setBackgroundColor(getResources().getColor(R.color.cardview_dark_background));
                fromDevice.setBackgroundColor(getResources().getColor(R.color.cardview_light_background));
                break;
            case R.id.add_button:
                newsTitle    = firstValue.getText().toString();
                description  = secondValue.getText().toString();
                newsURL = thirdValue.getText().toString();

                if (newsFrom.equalsIgnoreCase(NEWS_FROM_DEVICE)){
                    addNews("g*Rg3I0",newsFrom, newsTitle, "author", description,encoded, newsURL);
                } else {
                    NewsUploader.GetWebDetails details = new GetWebDetails();
                    details.execute(newsTitle);

                }

                // TODO: 12-Nov-18 uncomment if want to save url instead of image in server
                /*if (newsFrom == NEWS_FROM_DEVICE){
                    addNews("g*Rg3I0",newsFrom, newsTitle, "author", description, encoded, newsURL);
                } else {
                    NewsUploader.GetWebDetails details = new GetWebDetails();
                    details.execute(newsTitle);
                }*/


//                NewsValueModel model= new NewsValueModel();
                // TODO: 12-Nov-18 uncomment if errror
                /*newsTitle    = firstValue.getText().toString();
                description  = secondValue.getText().toString();
                newsURL = thirdValue.getText().toString();

                if (newsPhoto.getVisibility() == View.VISIBLE){
                    // TODO: 20-Oct-18 its from device
                    addNews("g*Rg3I0","1",newsTitle,newsURL,description,encoded,"");

                } else {

                    NewsUploader.GetWebDetails details = new GetWebDetails();
                    details.execute(newsTitle);
                    // TODO: 20-Oct-18 its from url
                }
                if (newsTitle.length() > 3) {
                    if (description.isEmpty()){
                        secondValue.setError("Cannot be empty");
                    } else {
                        // TODO: 9/22/2018 upload title, description, photo to firebase
                        *//*NewsValueModel model = new NewsValueModel();
                        model.setIsURL(0);
                        model.setPicURL(encodedImage);
                        model.setNewsHead(newsTitle);
                        model.setNewsContent(description);

                        new FirebaseManupulator(NEWS_KEY).uploadNews(model);
                        toMarketActivity();*//*
                        //uploadImage();
                        //uploadFile();
                    }
                } else {
                    if (!newsURL.isEmpty()){
                        *//*NewsUploader.GetWebDetails details = new GetWebDetails();
                        details.execute(newsURL);*//*
//                        newsUploaded.onNewsUploaded();
                    }
                    else {
                        Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show();
                    }
                }*/
                break;
            /*case R.id.cancel_button:
                toMarketActivity();
                break;*/

            /*case R.id.news_image:
                *//*ActivityCompat.requestPermissions(NewsUploader.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);*//*
                // TODO: 9/22/2018 get image from gallery and crop
                break;*/
        }
    }


    // TODO: 12-Nov-18 uncomment to extract data from url
    public class GetWebDetails extends AsyncTask<String, Void, Void> {

        private NewsValueModel model = new NewsValueModel();
        @Override
        protected Void doInBackground(String... strings) {

            String url = strings[0];

            Document document   = getDocumet(url);

            Elements title  = document.select("meta[property=og:title");
            if (title!=null && title.size() > 0) {
                model.setNewsHead(title.attr("content"));
            } else {
                model.setNewsHead(document.title());
            }

            Elements imageUrl   = document.select("meta[property=og:image");
            if (imageUrl!=null && imageUrl.size()>0){
                model.setPicURL(imageUrl.attr("content"));
            } else {
                String stringDocument = document.toString();
                Pattern pattern = Pattern.compile("<.*?apple-touch-icon.*?>");
                Matcher matcher = pattern.matcher(stringDocument);
                if (matcher.find()) {
                    String data = stringDocument.substring(matcher.start() + 1, matcher.end() - 2);
                    data = data.substring(data.lastIndexOf("\"") + 1, data.length());
                    model.setPicURL(data);
                } else {
                    pattern = Pattern.compile("\".*?favicon.ico.*?\"");
                    matcher = pattern.matcher(stringDocument);
                    if (matcher.find()) {
                        String data = stringDocument.substring(matcher.start() + 1, matcher.end() - 1);
                        data = data.substring(data.lastIndexOf("\"") + 1, data.length());
                        model.setPicURL(data);
                    }
                }
            }

            model.setIsURL(""+newsFrom);
            model.setNewsURL(baseUrl);

            return null;
        }

        private String baseUrl;
        private Document getDocumet(String url) {

            Document document   = null;
            try {
                document = Jsoup.connect(url).get();
                baseUrl = url;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                if (!url.contains("http://") && !url.contains("https://")){
                    document = getDocumet("https://"+url);
                }
            }
            return document;
        }

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            if (model.getNewsHead()!=null) {
                if (!model.getNewsHead().isEmpty()) {
                    //new FirebaseManupulator(NEWS_KEY).uploadNews(model);
                    // TODO: 12-Nov-18 uncomment if error
//                    addNews("g*Rg3I0","0",model.getNewsHead(),model.getNewsURL(),"",model.getPicURL(),"");
                    addNews("g*Rg3I0", newsFrom, model.getNewsHead(), model.getPicURL(), "", "", model.getNewsURL());
                }
            } else {
                Toast.makeText(NewsUploader.this, "Invalid URL", Toast.LENGTH_SHORT).show();
            }
        }
    }



    public void toMarketActivity(){
        AppConstants.SELECTED_TAB =1;
        Intent intent = new Intent(NewsUploader.this, NewMarketActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        toMarketActivity();
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

    public void addNews(final String token,String type,String title,String author,String body,String image,String url) {
        String  urlJsonObj = "http://tradewatch.xyz/addNews.php";
        hud = KProgressHUD.create(NewsUploader.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(false)
                .setLabel("Uploading")
                .show();

        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("type", type);
            obj.put("title", title);
            obj.put("author", author);
            obj.put("body", body);
            obj.put("image", image);
            obj.put("url", url);


        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {
                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        Toasty.success(NewsUploader.this,"News Added", Toast.LENGTH_SHORT, true).show();
                        //todo check if error
                        toMarketActivity();
                    }else {
                        Toasty.error(NewsUploader.this,"Failed to add news", Toast.LENGTH_SHORT, true).show();
                    }
                    if (hud.isShowing()){
                        hud.dismiss();
                    }


                } catch (JSONException e) {

                    // progressBar.setVisibility(View.GONE);
                    if (hud.isShowing()){
                        hud.dismiss();
                    }
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
                if (hud.isShowing()){
                    hud.dismiss();
                }
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


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

/*private void uploadFile() {
        //checking if file is available
        if (imageUri != null) {
            //displaying progress dialog while image is uploading
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading");
            progressDialog.show();

            //getting the storage reference
            StorageReference sRef = storageReference.child("images/"+ UUID.randomUUID().toString());

            //adding the file to reference
            sRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //dismissing the progress dialog
                            progressDialog.dismiss();

                            //displaying success toast
                            Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                            //creating the upload object to store uploaded image details
                            //Upload upload = new Upload(editTextName.getText().toString().trim(), taskSnapshot.getDownloadUrl().toString());

                            //adding an upload to firebase database
                            *//*String uploadId = mDatabase.push().getKey();
                            mDatabase.child(uploadId).setValue(upload);*//*

                            NewsValueModel model = new NewsValueModel();
                            model.setIsURL(0);
                            model.setAuthor("Self");
                            model.setPicURL(taskSnapshot.getDownloadUrl().toString());
                            model.setNewsHead("head");
                            model.setNewsContent("content");
                            model.setNewsURL("");
                            new FirebaseManupulator(NEWS_KEY).uploadNews(model);
                            // TODO: 10-Oct-18 add image url to firebase
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                        }
                    });
        } else {
            Toast.makeText(this, "Error on image upload", Toast.LENGTH_SHORT).show();
            //display an error if no file is selected
        }
    }*/