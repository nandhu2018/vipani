package com.gigaappz.vipani.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.format.DateFormat;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.ConnectivityReceiver;
import com.gigaappz.vipani.PersonUtils;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.adapters.NewsRecyclerAdapter;
import com.gigaappz.vipani.adapters.Userinactiveadapter;
import com.gigaappz.vipani.adapters.Userpendingadapter;
import com.gigaappz.vipani.adapters.ViewPagerAdapter;
import com.gigaappz.vipani.fragments.AddUser;
import com.gigaappz.vipani.fragments.ContactUsTab;
import com.gigaappz.vipani.fragments.DomesticTab;
import com.gigaappz.vipani.fragments.ForeignTab;
import com.gigaappz.vipani.fragments.Foreignweb;
import com.gigaappz.vipani.fragments.Inactive;
import com.gigaappz.vipani.fragments.NewsTab;
import com.gigaappz.vipani.fragments.UsersTab;
import com.gigaappz.vipani.fragments.Userspending;
import com.gigaappz.vipani.interfaces.NewDomesticDataSaved;
import com.gigaappz.vipani.interfaces.NewUserDataAdded;
import com.gigaappz.vipani.interfaces.NewsItemSelected;
import com.gigaappz.vipani.interfaces.RefreshUsersList;
import com.gigaappz.vipani.models.BadgeView;
import com.gigaappz.vipani.models.Domestic;
import com.gigaappz.vipani.models.Domesticflash;
import com.gigaappz.vipani.models.NewsValueModel;
import com.gigaappz.vipani.models.UserModel;
import com.gigaappz.vipani.utils.AppConstants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import es.dmoral.toasty.Toasty;
import in.galaxyofandroid.spinerdialog.OnSpinerItemClick;
import in.galaxyofandroid.spinerdialog.SpinnerDialog;

import static com.gigaappz.vipani.utils.AppConstants.CHANNEL_ID;
import static com.gigaappz.vipani.utils.AppConstants.IS_ADMIN;
import static com.gigaappz.vipani.utils.AppConstants.SELECTED_TAB;
import static java.security.AccessController.getContext;

public class NewMarketActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener,
        NewDomesticDataSaved, NewUserDataAdded, NewsItemSelected, RefreshUsersList, ConnectivityReceiver.ConnectivityReceiverListener {

    private ViewPager viewPager;
    private final int FIRST_BUTTON = 0, SECOND_BUTTON = 1, THIRD_BUTTON = 2, FOURTH_BUTTON = 3, FAB = 4;
    private Context context;
    int position = 0;
    ArrayList<String> items = new ArrayList<>();
    public static Typeface regular, bold, italics, boldItalics;
    //    public static int selectedTab = 0;
    private RelativeLayout firstButton;
    private RelativeLayout secondButton;
    private RelativeLayout thirdButton;
    private RelativeLayout fourthButton;
    SharedPreferences sharedPreferences, sharednews;
    private RelativeLayout iconGroup, drawer1;
    private TabLayout tabLayout;
    private FloatingActionButton fab, fab1, fab2;
    private TextView contactText, profilename;
    SpinnerDialog spinnerDialog;
    TextView marketText, newsText, userText, appTitleText;
    private DatabaseReference mFirebaseDatabase, mFirebaseDatabase1;
    private FirebaseDatabase mFirebaseInstance;
    String strName = "";
    TextView badge;

    //    private View adminPanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_market);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        badge = (TextView) findViewById(R.id.badge);
        sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        sharednews = getSharedPreferences("news", Context.MODE_PRIVATE);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        if (IS_ADMIN) {
            toolbar1.setVisibility(View.GONE);
            setSupportActionBar(toolbar);



        } else {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toolbar.setVisibility(View.GONE);
            setSupportActionBar(toolbar1);

            mFirebaseDatabase1 = mFirebaseInstance.getReference("domesticupdate");
            mFirebaseDatabase1.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    Domesticflash domestic = dataSnapshot.getValue(Domesticflash.class);
                    //Toast.makeText(NewMarketActivity.this, ""+domestic.getId(), Toast.LENGTH_SHORT).show();
                    domesticflash("g*Rg3I0", domestic.getId());


                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        // requestpermission();
        //getallcontacts();

        mFirebaseDatabase = mFirebaseInstance.getReference("news1");

        mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Domestic domestic = dataSnapshot.getValue(Domestic.class);
                if (sharednews.contains("news")) {
                    if (sharednews.getString("news", "").equalsIgnoreCase(domestic.getName())) {
                        badge.setVisibility(View.GONE);
                    } else {
                        badge.setVisibility(View.VISIBLE);
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(NewMarketActivity.this, CHANNEL_ID)
                                .setSmallIcon(R.drawable.news_icon_15x15)
                                .setContentTitle("Vipani- News Updated")
                                .setContentText(domestic.getName())
                                .setColor(Color.parseColor("#009add"))
                                .setAutoCancel(true);

                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                        AppConstants.SELECTED_TAB = 1;
                        PendingIntent contentIntent =
                                PendingIntent.getActivity(NewMarketActivity.this, 0, new Intent(NewMarketActivity.this, NewMarketActivity.class), 0);
                        mBuilder.setContentIntent(contentIntent);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "vipani", NotificationManager.IMPORTANCE_HIGH);

                            notificationManager.createNotificationChannel(mChannel);
                        }

                        notificationManager.notify(0, mBuilder.build());
                        SharedPreferences.Editor editor1 = getSharedPreferences("news", MODE_PRIVATE).edit();
                        editor1.putString("news", domestic.getName());
                        editor1.apply();
                    }
                } else {
                    SharedPreferences.Editor editor1 = getSharedPreferences("news", MODE_PRIVATE).edit();
                    editor1.putString("news", domestic.getName());
                    editor1.apply();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        new Userpendingadapter().onRefreshUsersList(this);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        context = getApplicationContext();

//        checkPermissions();
// TODO: 9/2/2018


        // IS_ADMIN = true;

        viewPager = findViewById(R.id.view_pager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        firstButton = findViewById(R.id.first_button);
        secondButton = findViewById(R.id.second_button);
        thirdButton = findViewById(R.id.third_button);
        fourthButton = findViewById(R.id.fourth_button);
        iconGroup = findViewById(R.id.icon_group);
        drawer1 = findViewById(R.id.relative);
        marketText = findViewById(R.id.market_text);
        newsText = findViewById(R.id.news_text);
        contactText = findViewById(R.id.contact_text);
        //profilename= (TextView) findViewById(R.id.profile_name);
        userText = findViewById(R.id.user_text);
        appTitleText = findViewById(R.id.app_title_text);
        fab = findViewById(R.id.fab2);
   /*     fab1 = findViewById(R.id.fab2);
        fab2 = findViewById(R.id.fab3);
        final FloatingLayout floatingLayout = findViewById(R.id.floating_layout);
       */
//        adminPanel      = findViewById(R.id.admin_panel);

//        initViewAdminPanel();
        /*if (sharedPreferences.contains("mobile")) {
            profilename.setText(sharedPreferences.getString("mobile", ""));
        }*/
        firstButton.setOnClickListener(this);
        secondButton.setOnClickListener(this);
        thirdButton.setOnClickListener(this);
        fourthButton.setOnClickListener(this);
        fab.setOnClickListener(this);
        setUpViewPager(AppConstants.SELECTED_TAB);

        regular = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf");
        bold = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf");
        italics = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_italic.ttf");
        boldItalics = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold_italic.ttf");

        marketText.setTypeface(bold);
        newsText.setTypeface(bold);
        contactText.setTypeface(bold);
        userText.setTypeface(bold);
        appTitleText.setTypeface(bold);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                position = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        if (IS_ADMIN) {
            fab.setVisibility(View.VISIBLE);
            thirdButton.setVisibility(View.VISIBLE);
            fourthButton.setVisibility(View.GONE);
            contactText.setText("Admin Panel");


        } else {
            fab.setVisibility(View.GONE);
            firstButton.setVisibility(View.VISIBLE);
            secondButton.setVisibility(View.VISIBLE);
            thirdButton.setVisibility(View.GONE);
            fourthButton.setVisibility(View.VISIBLE);
            contactText.setText("Contact Us");

            // navigationView.setVisibility(View.GONE);
        }
//        adminPanel.setVisibility(View.GONE);
        // new AddDomesticValues().setOnNewDomesticDataSaved(this);
        new AddUser().onNewUserDataAdded(this);

        new NewsRecyclerAdapter().setOnNewsItemSelected(this);
        /*final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @SuppressWarnings("unchecked")
                    public void run() {
                        try {

                        }
                        catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 10000);*/


    }


    public void requestpermission() {
        ActivityCompat.requestPermissions(NewMarketActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(NewMarketActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2);
    }

    private void setUpViewPager(int button) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (IS_ADMIN) {
            firstButton.setEnabled(true);
            secondButton.setEnabled(true);
            thirdButton.setEnabled(true);
            fourthButton.setEnabled(false);
        } else {
            firstButton.setEnabled(true);
            secondButton.setEnabled(true);
            thirdButton.setEnabled(false);
            fourthButton.setEnabled(true);
        }

        switch (button) {
            case FIRST_BUTTON:
                thirdButton.setVisibility(View.GONE);

                fourthButton.setVisibility(View.VISIBLE);
                /*firstButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));*/
                marketText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.barwhite, 0, 0);
                contactText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contactblack, 0, 0);
                userText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.userblack, 0, 0);
                newsText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newsblack, 0, 0);
                adapter.addFragment(new DomesticTab(), "Domestic");
                adapter.addFragment(new Foreignweb(), "International");
                tabLayout.setVisibility(View.VISIBLE);
                if (IS_ADMIN) {

                    fab.setVisibility(View.VISIBLE);

                    thirdButton.setVisibility(View.VISIBLE);
                    fourthButton.setVisibility(View.GONE);
                }
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);

                AppConstants.SELECTED_TAB = FIRST_BUTTON;

                //View v = getActionBar().getTabAt(0).getCustomView();
               /* BadgeView badge1 = new BadgeView(NewMarketActivity.this, adapter.getItem(0).getView());
                badge1.setText("1");
                badge1.show();*/
                break;
            case SECOND_BUTTON:
                thirdButton.setVisibility(View.GONE);
                badge.setVisibility(View.GONE);
                fourthButton.setVisibility(View.VISIBLE);
                /*firstButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));*/
                newsText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newswhite, 0, 0);
                contactText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contactblack, 0, 0);
                userText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.userblack, 0, 0);
                marketText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.barblack, 0, 0);
                adapter.addFragment(new NewsTab(), "NEWS");
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                    thirdButton.setVisibility(View.VISIBLE);
                    fourthButton.setVisibility(View.GONE);
                }
                tabLayout.setVisibility(View.GONE);
                //fab.setVisibility(View.GONE);
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);
                AppConstants.SELECTED_TAB = SECOND_BUTTON;
                break;
            case THIRD_BUTTON:
                thirdButton.setVisibility(View.GONE);

                fourthButton.setVisibility(View.VISIBLE);
                /*firstButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));*/
                userText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.userwhite, 0, 0);
                contactText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contactblack, 0, 0);
                newsText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newsblack, 0, 0);
                marketText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.barblack, 0, 0);
                adapter.addFragment(new UsersTab(), "Active\nUsers");
                adapter.addFragment(new Inactive(), "Inactive\nUsers");
                adapter.addFragment(new Userspending(), "Pending\nRequest");
                tabLayout.setVisibility(View.VISIBLE);
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                    thirdButton.setVisibility(View.VISIBLE);
                    fourthButton.setVisibility(View.GONE);
                }
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);
                AppConstants.SELECTED_TAB = THIRD_BUTTON;
                break;
            case FOURTH_BUTTON:
                thirdButton.setVisibility(View.GONE);

                fourthButton.setVisibility(View.VISIBLE);
               /* firstButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));*/
                contactText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contactwhite, 0, 0);
                userText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.userblack, 0, 0);
                newsText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newsblack, 0, 0);
                marketText.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.barblack, 0, 0);
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                    thirdButton.setVisibility(View.VISIBLE);
                    fourthButton.setVisibility(View.GONE);
                }

                adapter.addFragment(new ContactUsTab(), "Contact Us");
                tabLayout.setVisibility(View.GONE);
                // fab.setVisibility(View.GONE);
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);
                AppConstants.SELECTED_TAB = FOURTH_BUTTON;
                break;
            case FAB:
                if (AppConstants.SELECTED_TAB == FIRST_BUTTON) {


                    /*if (position == 0) {*/
                        startActivity(new Intent(NewMarketActivity.this, AddDomestic.class));
                        finish();
                   /* } else {
                        startActivity(new Intent(NewMarketActivity.this, AddForeign.class));
                        finish();
                    }*/

                    //adapter.addFragment(new AddDomesticValues(), "Add new notification");
                } else if (AppConstants.SELECTED_TAB == SECOND_BUTTON) {
                    startActivity(new Intent(NewMarketActivity.this, NewsUploader.class));
                    finish();
                    //adapter.addFragment(new AddDomesticValues(), "Add new notification");
                } else if (AppConstants.SELECTED_TAB == THIRD_BUTTON) {
                    //adapter.addFragment(new AddCustomer(), "Add new user");
                    startActivity(new Intent(NewMarketActivity.this, AddCustomer.class));
                    finish();
                }
                /*tabLayout.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
//               iconGroup.setVisibility(View.GONE);
                firstButton.setVisibility(View.INVISIBLE);
                secondButton.setVisibility(View.INVISIBLE);
                thirdButton.setVisibility(View.INVISIBLE);
                fourthButton.setVisibility(View.INVISIBLE);*/
              /*  firstButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));*/
                break;

        }

        viewPager.setAdapter(adapter);
    }


    public void domesticflash(final String token, final String id) {

        String urlJsonObj = "http://tradewatch.xyz/api/getDomesticFlash.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", id);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        JSONArray cast = response.getJSONArray("data");
                        String price1 = "", price2 = "";
                        for (int i = 0; i < cast.length(); i++) {
                            JSONObject domcategory = cast.getJSONObject(i);
                            if (i == 0) {
                                price1 = domcategory.getString("price");
                            } else {
                                price2 = domcategory.getString("price");

                                SharedPreferences sharedPreferences= getSharedPreferences("flashdetails", Context.MODE_PRIVATE);
                                if (sharedPreferences.contains("flash")){
                                    if (!sharedPreferences.getString("flash","").equalsIgnoreCase(id+price2+"")){
                                        SharedPreferences.Editor editor = getSharedPreferences("flashdetails", MODE_PRIVATE).edit();
                                        editor.putString("flash",id+price2+"");
                                        editor.apply();
                                        flashupdate(response.getString("main_title"), response.getString("sub_title"), price2, price1);
                                    }
                                }else {
                                    SharedPreferences.Editor editor = getSharedPreferences("flashdetails", MODE_PRIVATE).edit();
                                    editor.putString("flash",id+price2+"");
                                    editor.apply();
                                    flashupdate(response.getString("main_title"), response.getString("sub_title"), price2, price1);
                                }

                            }

                        }
                    } else {
                        Toast.makeText(context, "" + response.getString("responseMessage"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    // progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                //progressBar.setVisibility(View.GONE);
                // hide the progress dialog
            }

        });
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq);


    }

    public void flashupdate(String head, String sub, String priceold, String pricenew) {
        final Dialog dialog = new Dialog(NewMarketActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.domesticflashalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextView heading = (TextView) dialog.findViewById(R.id.heading);
        final TextView price = (TextView) dialog.findViewById(R.id.price);
        Typeface regular = Typeface.createFromAsset(getAssets(), "AnjaliOldLipi.ttf");

        heading.setTypeface(regular);
        price.setTypeface(regular);
        heading.setText(head + "\n" + sub);
        price.setText("Price changed from " + priceold + " to " + pricenew);
        Button okButton = (Button) dialog.findViewById(R.id.ok_button);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        if (!isFinishing()){
            dialog.show();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.first_button:

                setUpViewPager(FIRST_BUTTON);
                break;
            case R.id.second_button:

                setUpViewPager(SECOND_BUTTON);
                break;
            case R.id.third_button:

                setUpViewPager(THIRD_BUTTON);
                break;
            case R.id.fourth_button:

                setUpViewPager(FOURTH_BUTTON);
                break;
            case R.id.fab2:

                setUpViewPager(FAB);
                break;
            /*case R.id.add_user:
                // TODO: 9/30/2018 add user activity intent
                break;
            case R.id.add_domestic:
                // TODO: 9/30/2018 add domestic intent
                break;
            case R.id.add_type:
                addUserButton.setVisibility(View.GONE);
                addDomesticButton.setVisibility(View.GONE);
                addNewsButton.setVisibility(View.GONE);
                addMarketTypeButton.setVisibility(View.VISIBLE);
                extendValidityButton.setVisibility(View.GONE);
                backArrow.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                addMarketType();
                break;
            case R.id.extend_validity:
                addUserButton.setVisibility(View.GONE);
                addDomesticButton.setVisibility(View.GONE);
                addNewsButton.setVisibility(View.GONE);
                addMarketTypeButton.setVisibility(View.GONE);
                extendValidityButton.setVisibility(View.VISIBLE);
                backArrow.setVisibility(View.VISIBLE);
                addButton.setVisibility(View.VISIBLE);
                editText.setVisibility(View.VISIBLE);
                extendValidity();
                break;
            case R.id.add_news:
                // TODO: 9/30/2018 intent to add news
                break;
            case R.id.back_button_admin_panel:
                initialView();
                break;
            case R.id.ok_button_admin_panel:
                // TODO: 9/30/2018
                break;*/
        }
    }

    /*private View addUserButton, addDomesticButton, addNewsButton, addMarketTypeButton, extendValidityButton;
    private Button backArrow, addButton;
    private EditText editText;
    private void initViewAdminPanel(){

        addUserButton   = findViewById(R.id.add_user);
        addNewsButton   = findViewById(R.id.add_news);
        addDomesticButton   = findViewById(R.id.add_domestic);
        addMarketTypeButton = findViewById(R.id.add_type);
        extendValidityButton= findViewById(R.id.extend_validity);
        backArrow       = findViewById(R.id.back_button_admin_panel);
        addButton       = findViewById(R.id.ok_button_admin_panel);
        editText        =findViewById(R.id.edit_text_admin_panel);

        initialView();

        addUserButton.setOnClickListener(this);
        addDomesticButton.setOnClickListener(this);
        addNewsButton.setOnClickListener(this);
        addMarketTypeButton.setOnClickListener(this);
        extendValidityButton.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        addButton.setOnClickListener(this);
    }

    private void initialView() {
        addUserButton.setVisibility(View.VISIBLE);
        addDomesticButton.setVisibility(View.VISIBLE);
        addNewsButton.setVisibility(View.VISIBLE);
        addMarketTypeButton.setVisibility(View.VISIBLE);
        extendValidityButton.setVisibility(View.VISIBLE);
        backArrow.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        editText.setVisibility(View.GONE);
    }*/

    @Override
    public void onNewDomesticDataSave() {
        setUpViewPager(FIRST_BUTTON);
    }

    @Override
    public void onNewUserDataAdded() {
        setUpViewPager(THIRD_BUTTON);
    }

    @Override
    public void onNewsItemSelected(NewsValueModel model) {
        Intent intent;
        if (model.getIsURL().equalsIgnoreCase("1")) {
            intent = new Intent(NewMarketActivity.this, NewsContent.class);
        } else {
            intent = new Intent(NewMarketActivity.this, NewsContentFromDevice.class);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("NEWS_VALUE", model);
        intent.putExtras(bundle);
        /*intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        finish();*/
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_market, menu);
        if (IS_ADMIN) {
            menu.findItem(R.id.search).setVisible(true);
            menu.findItem(R.id.action_settings).setVisible(false);
        } else {
            menu.findItem(R.id.search).setVisible(false);
            menu.findItem(R.id.action_settings).setVisible(true);
        }


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.search) {

            searchalert();
           /* spinnerDialog = new SpinnerDialog(NewMarketActivity.this, items, "Select or Search User", R.style.DialogAnimations_SmileWindow, "Close");// With 	Animation
            spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {
                    getDetailsFromID(item);
//                    showPopupUserCard(items.get(position));

                    //Toast.makeText(AddCustomer.this, item + "  " + position+"", Toast.LENGTH_SHORT).show();
                    //refered.getEditText().setText(item);
                    //selectedItems.setText(item + " Position: " + position);
                }
            });
            spinnerDialog.showSpinerDialog();*/
            return true;
        }
        if (id == R.id.action_settings) {

            startActivity(new Intent(NewMarketActivity.this,Settings.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void searchalert() {
        final Dialog dialog = new Dialog(NewMarketActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);
        final TextInputLayout remark = dialog.findViewById(R.id.remarks);
        final EditText reasonText = dialog.findViewById(R.id.daysedit);
        // remark.setVisibility(View.VISIBLE);
        reasonText.setHint("Search Here");
        reasonText.setKeyListener(DigitsKeyListener.getInstance("asdfghjklqwertyuiopzxcvbnmZXCVBNMASDFGHJKLQWERTYUIOP., 1234567890"));
        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

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
                Intent intent = new Intent(NewMarketActivity.this, SearchResult.class);
                intent.putExtra("query", term);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void getDetailsFromID(String userid) {
        String urlJsonObj = "http://tradewatch.xyz/api/searchUser.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", "g*Rg3I0");
            obj.put("user_id", userid);
        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                    Calendar start = Calendar.getInstance();
                    Calendar start1 = Calendar.getInstance();
                    Calendar start2 = Calendar.getInstance();
                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        JSONArray dataArray = response.getJSONArray("data");
                        JSONObject data = dataArray.getJSONObject(0);
                        UserModel model = new UserModel();
                        model.setUserMobile(data.getString("user_id"));
                        model.setUserStatus(data.getInt("user_status"));
                        //String joined = formatter.format(new Date(data.getInt("joined_on")));
                        //String paymentdate = formatter.format(new Date(data.getInt("payment_date")));
                        //String expiry = formatter.format(new Date(data.getInt("payment_expiry")));
                        start.setTimeInMillis(data.getInt("joined_on") * 1000L);
                        start1.setTimeInMillis(data.getInt("payment_date") * 1000L);
                        start2.setTimeInMillis(data.getInt("payment_expiry") * 1000L);
                        model.setRegisteredOn(DateFormat.format("dd-MM-yyyy hh:mm:ss", start).toString());
                        model.setPurchasedOn(DateFormat.format("dd-MM-yyyy hh:mm:ss", start1).toString());
                        model.setExpiredon(DateFormat.format("dd-MM-yyyy hh:mm:ss", start2).toString());
                      /*  model.setRegisteredOn(data.getString("joined_on"));
                        model.setPurchasedOn(data.getString("payment_date"));
                        model.setExpiredon(data.getString("payment_expiry"));*/
                        showPopupUserCard(model);
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

    private void showPopupUserCard(final UserModel model) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.user_card_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText mobileText = dialog.findViewById(R.id.mobile_text);
        TextView dateRemains = dialog.findViewById(R.id.date_remains);
        EditText registeredOn = dialog.findViewById(R.id.date_registration);
        EditText purchasedOn = dialog.findViewById(R.id.date_purchase);
        EditText expiredOn = dialog.findViewById(R.id.date_expiry);
        TextView userStatus = dialog.findViewById(R.id.disable_reason);
        Button makeAdminButton = dialog.findViewById(R.id.accept);
        Button deleteUserButton = dialog.findViewById(R.id.reject);

        mobileText.setText(model.getUserMobile());
        dateRemains.setText(model.getDateRemains());
        registeredOn.setText(model.getRegisteredOn());
        purchasedOn.setText(model.getPurchasedOn());
        expiredOn.setText(model.getExpiredon());

        String status = "User status : ";
        if (model.getUserStatus() == 0) {
            status = status + "active";
        } else if (model.getUserStatus() == 1) {
            status = status + "inactive";
        } else {
            status = status + "pending";
        }
        userStatus.setText(status);
        userStatus.setVisibility(View.VISIBLE);

        makeAdminButton.setText("Make As Admin");
        deleteUserButton.setText("Remove User");

        makeAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 09-Oct-18
                dialog.dismiss();
                makeadmin("g*Rg3I0", model.getUserMobile());
            }
        });

        deleteUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 09-Oct-18
                dialog.dismiss();
                customalert(model.getUserMobile());
            }
        });

        dialog.show();
    }

    public void customalert(final String user) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);

        final EditText reasonText = dialog.findViewById(R.id.daysedit);

        reasonText.setHint("Reason");

        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = inputLayout.getEditText().getText().toString();
                remuser("g*Rg3I0", user, reason);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void makeadmin(final String token, String userid) {

        String urlJsonObj = "http://tradewatch.xyz/api/makeAsAdmin.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("user_id", userid);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        SELECTED_TAB = THIRD_BUTTON;
                        setUpViewPager(THIRD_BUTTON);
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

    public void remuser(final String token, String userid, String reason) {

        String urlJsonObj = "http://tradewatch.xyz/blockUser.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("user_id", userid);
            obj.put("comment", reason);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        SELECTED_TAB = THIRD_BUTTON;
                        setUpViewPager(THIRD_BUTTON);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_user) {
            startActivity(new Intent(NewMarketActivity.this, AddCustomer.class));
            finish();
            // // TODO: 04-Oct-18
        } else if (id == R.id.add_domestic) {
            startActivity(new Intent(NewMarketActivity.this, AddDomestic.class));
            finish();
// TODO: 04-Oct-18
        } else if (id == R.id.add_news) {
            startActivity(new Intent(NewMarketActivity.this, NewsUploader.class));
            finish();
// TODO: 04-Oct-18
        } else if (id == R.id.add_category) {
            showDialog("Add Category");
            // getdomesticcategorylist("g*Rg3I0");
            /*spinnerDialog=new SpinnerDialog(NewMarketActivity.this,items,"Select or Search User",R.style.DialogAnimations_SmileWindow,"Close");// With 	Animation
            spinnerDialog.bindOnSpinerListener(new OnSpinerItemClick() {
                @Override
                public void onClick(String item, int position) {

                    strName=item;
                    //selectedItems.setText(item + " Position: " + position);
                }
            });*/


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showDialog(String hint) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.text_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText reasonText = dialog.findViewById(R.id.reason_text);
        TextInputLayout inputLayout = dialog.findViewById(R.id.reason_til);

        reasonText.setHint(hint);
        inputLayout.setHint(hint);

        Button okButton = (Button) dialog.findViewById(R.id.ok_button);
        Button cancelButton = (Button) dialog.findViewById(R.id.cancel_button);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reason = reasonText.getText().toString();
                // TODO: 10/2/2018 upload reason to firebase adddomesticcategories
                if (reasonText.getText().toString().equalsIgnoreCase("")) {
                    reasonText.setError("Enter Category Name");
                } else {
                    addcategory(reason);
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }


    private void getallcontacts() {
        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);

        String url = "http://tradewatch.xyz/allMobileNumbers.php";
        JSONObject object = new JSONObject();
        try {
            //object.put("auth",sharedPreferences.getString("token",""));
            object.put("auth", "g*Rg3I0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray cast = response.getJSONArray("data");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject mobile = cast.getJSONObject(i);
                        items.add(mobile.getString("mobile_no"));
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

    private void addcategory(String name) {
        SharedPreferences sharedPreferences = getSharedPreferences("token", Context.MODE_PRIVATE);

        String url = "http://tradewatch.xyz/api/addDomesticCategories.php";
        JSONObject object = new JSONObject();
        try {
            //object.put("auth",sharedPreferences.getString("token",""));
            object.put("auth", "g*Rg3I0");
            object.put("name", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getString("responseStatus").equalsIgnoreCase("true")) {
                        Toasty.success(NewMarketActivity.this, "Category added", Toast.LENGTH_SHORT, true).show();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            case 2: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onUserListRefresh() {
//        setUpViewPager(SELECTED_TAB);
        finish();
        startActivity(getIntent());
    }


    private void noInternet(boolean isConnected) {
        /*FancyGifDialog.Builder builder = new FancyGifDialog.Builder(NewMarketActivity.this);
                builder.setTitle("Internet Error")
                .setMessage("No connectivity")
                .setPositiveBtnBackground("#3fb5if (isConnected) {\n" +
                        "        } else {51")
                .setPositiveBtnText("Try Again")
                .setNegativeBtnText("Exit")
                .setNegativeBtnBackground("#8b0101")
                .setGifResource(R.drawable.nointernet)   //Pass your Gif here
                .isCancellable(true)
                .OnPositiveClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                        startActivity(intent);
                    }
                })
                .OnNegativeClicked(new FancyGifDialogListener() {
                    @Override
                    public void OnClick() {
                        finish();
                    }
                })
                .build();*/
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet Connection");
        builder.setMessage("Please turn on internet connection to continue");
        builder.setNegativeButton("Go to settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                startActivity(intent);
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        if (isConnected) {
            alertDialog.show();
        } else {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
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
        //noInternet(isConnected);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
 /*   @Override
    protected void onNewIntent(Intent intent) {

        processIntent(intent);
    };

    private void processIntent(Intent intent){

    }*/
}
