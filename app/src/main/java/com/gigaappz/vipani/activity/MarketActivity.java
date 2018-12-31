package com.gigaappz.vipani.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigaappz.vipani.R;
import com.gigaappz.vipani.adapters.NewsRecyclerAdapter;
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
import com.gigaappz.vipani.models.NewsValueModel;

import static com.gigaappz.vipani.utils.AppConstants.IS_ADMIN;


public class MarketActivity extends AppCompatActivity implements View.OnClickListener,
        NewDomesticDataSaved, NewUserDataAdded, NewsItemSelected     {

    private ViewPager viewPager;
    private final int FIRST_BUTTON = 0, SECOND_BUTTON = 1, THIRD_BUTTON = 2, FOURTH_BUTTON = 3, FAB = 4;
    private Context context;

    public static Typeface regular, bold, italics, boldItalics;
    public static int SELECTED_TAB = 0;
    private RelativeLayout firstButton;
    private RelativeLayout secondButton;
    private RelativeLayout thirdButton;
    private RelativeLayout fourthButton;
    private RelativeLayout iconGroup;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private TextView contactText;
//    private View adminPanel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_market);

        context     = getApplicationContext();

//        checkPermissions();
// TODO: 9/2/2018

        IS_ADMIN = true;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager   = findViewById(R.id.view_pager);

        tabLayout   = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        firstButton     = findViewById(R.id.first_button);
        secondButton    = findViewById(R.id.second_button);
        thirdButton     = findViewById(R.id.third_button);
        fourthButton    = findViewById(R.id.fourth_button);
        iconGroup       = findViewById(R.id.icon_group);
        TextView marketText = findViewById(R.id.market_text);
        TextView newsText   = findViewById(R.id.news_text);
        contactText= findViewById(R.id.contact_text);
        TextView userText   = findViewById(R.id.user_text);
        TextView appTitleText   = findViewById(R.id.app_title_text);
        fab             = findViewById(R.id.fab2);
//        adminPanel      = findViewById(R.id.admin_panel);

//        initViewAdminPanel();

        firstButton.setOnClickListener(this);
        secondButton.setOnClickListener(this);
        thirdButton.setOnClickListener(this);
        fourthButton.setOnClickListener(this);
        fab.setOnClickListener(this);
        setUpViewPager(SELECTED_TAB);

        regular     = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams.ttf");
        bold        = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold.ttf");
        italics     = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_italic.ttf");
        boldItalics = Typeface.createFromAsset(getAssets(), "fonts/caviar_dreams_bold_italic.ttf");

        marketText.setTypeface(bold);
        newsText.setTypeface(bold);
        contactText.setTypeface(bold);
        userText.setTypeface(bold);
        appTitleText.setTypeface(bold);

        if (IS_ADMIN){
            fab.setVisibility(View.VISIBLE);
            thirdButton.setVisibility(View.VISIBLE);
            contactText.setText("Admin Panel");
        } else {
            fab.setVisibility(View.GONE);
            thirdButton.setVisibility(View.GONE);
            contactText.setText("Contact Us");
        }
//        adminPanel.setVisibility(View.GONE);
       // new AddDomesticValues().setOnNewDomesticDataSaved(this);
        new AddUser().onNewUserDataAdded(this);

        new NewsRecyclerAdapter().setOnNewsItemSelected(this);
    }







    /*private void setUpViewPager(int button) {
        ViewPagerAdapter adapter    = new ViewPagerAdapter(getSupportFragmentManager());

        switch (button){
            case FIRST_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                secondButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                adapter.addFragment(new DomesticTab(), "Domestic");
                adapter.addFragment(new ForeignTab(), "Foreign");
                tabLayout.setVisibility(View.VISIBLE);
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                }
                iconGroup.setVisibility(View.VISIBLE);
                SELECTED_TAB = FIRST_BUTTON;
                break;
            case SECOND_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                adapter.addFragment(new NewsTab(), "NEWS");
                tabLayout.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                iconGroup.setVisibility(View.VISIBLE);
                SELECTED_TAB = SECOND_BUTTON;
                break;
            case THIRD_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                secondButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                adapter.addFragment(new UsersTab(), "Users");
                tabLayout.setVisibility(View.GONE);
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                }
                iconGroup.setVisibility(View.VISIBLE);
                SELECTED_TAB = THIRD_BUTTON;
                break;
            case FOURTH_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                secondButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));

                adapter.addFragment(new ContactUsTab(), "Contact Us");
                tabLayout.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                iconGroup.setVisibility(View.VISIBLE);
                SELECTED_TAB = FOURTH_BUTTON;
                break;
            case FAB:
                if (SELECTED_TAB == FIRST_BUTTON) {
                    adapter.addFragment(new AddDomesticValues(), "Add new notification");
                } else {
                    adapter.addFragment(new AddCustomer(), "Add new user");
                }
                tabLayout.setVisibility(View.VISIBLE);
                fab.setVisibility(View.GONE);
                iconGroup.setVisibility(View.GONE);
                break;

        }

        viewPager.setAdapter(adapter);
    }*/
    private void setUpViewPager(int button) {
        ViewPagerAdapter adapter    = new ViewPagerAdapter(getSupportFragmentManager());
        firstButton.setEnabled(true);
        secondButton.setEnabled(true);
        thirdButton.setEnabled(true);
        fourthButton.setEnabled(true);
        switch (button){
            case FIRST_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));

                adapter.addFragment(new DomesticTab(), "Domestic");
                adapter.addFragment(new Foreignweb(), "Foreign");
                tabLayout.setVisibility(View.VISIBLE);
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                }
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);
                thirdButton.setVisibility(View.VISIBLE);
                fourthButton.setVisibility(View.VISIBLE);
                SELECTED_TAB = FIRST_BUTTON;
                break;
            case SECOND_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));

                adapter.addFragment(new NewsTab(), "NEWS");
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                }
                tabLayout.setVisibility(View.GONE);
                //fab.setVisibility(View.GONE);
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);
                thirdButton.setVisibility(View.VISIBLE);
                fourthButton.setVisibility(View.VISIBLE);
                SELECTED_TAB = SECOND_BUTTON;
                break;
            case THIRD_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));

                adapter.addFragment(new UsersTab(), "Active Users");
                adapter.addFragment(new Inactive(), "Inactive Users");
                adapter.addFragment(new Userspending(), "Pending Request");
                tabLayout.setVisibility(View.VISIBLE);
                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                }
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);
                thirdButton.setVisibility(View.VISIBLE);
                fourthButton.setVisibility(View.VISIBLE);
                SELECTED_TAB = THIRD_BUTTON;
                break;
            case FOURTH_BUTTON:
                firstButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                secondButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                thirdButton.setBackgroundColor(getResources().getColor(R.color.textColorWhite));
                fourthButton.setBackgroundColor(getResources().getColor(R.color.textColorGray));

                if (IS_ADMIN) {
                    fab.setVisibility(View.VISIBLE);
                }

                adapter.addFragment(new ContactUsTab(), "Contact Us");
                tabLayout.setVisibility(View.GONE);
               // fab.setVisibility(View.GONE);
//                iconGroup.setVisibility(View.VISIBLE);
                firstButton.setVisibility(View.VISIBLE);
                secondButton.setVisibility(View.VISIBLE);
                thirdButton.setVisibility(View.VISIBLE);
                fourthButton.setVisibility(View.VISIBLE);
                SELECTED_TAB = FOURTH_BUTTON;
                break;
            case FAB:
                if (SELECTED_TAB == FIRST_BUTTON) {
                    startActivity(new Intent(MarketActivity.this, AddDomestic.class));
                    //adapter.addFragment(new AddDomesticValues(), "Add new notification");
                } else if (SELECTED_TAB == SECOND_BUTTON) {
                    startActivity(new Intent(MarketActivity.this, NewsUploader.class));
                    //adapter.addFragment(new AddDomesticValues(), "Add new notification");
                }else if (SELECTED_TAB == THIRD_BUTTON){
                    //adapter.addFragment(new AddCustomer(), "Add new user");
                    startActivity(new Intent(MarketActivity.this, AddCustomer.class));
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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
            case R.id.add_user:
                // TODO: 9/30/2018 add user activity intent
                break;
            case R.id.add_domestic:
                // TODO: 9/30/2018 add domestic intent
                break;
            /*case R.id.add_type:
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

   /* private void extendValidity() {
        // TODO: 9/30/2018
    }

    private void addMarketType() {
        // TODO: 9/30/2018
    }
*/
   /* private View addUserButton, addDomesticButton, addNewsButton, addMarketTypeButton, extendValidityButton;
    private Button backArrow, addButton;
    private EditText editText;*/
    /*private void initViewAdminPanel(){

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
    }*/

    /*private void initialView() {
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
        if (model.getIsURL().equalsIgnoreCase("0")) {
            intent = new Intent(MarketActivity.this, NewsContent.class);
        } else {
            intent = new Intent(MarketActivity.this, NewsContentFromDevice.class);
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("NEWS_VALUE", model);
        intent.putExtras(bundle);
       /* intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        finish();*/
        startActivity(intent);
    }





    /*private static final int VIEW_PROFILE = 0, VIEW_USERS = 1, ADD_NOTIFICATION = 2;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, VIEW_PROFILE, Menu.NONE, "View Profile");
        menu.add(Menu.NONE, VIEW_USERS, Menu.NONE, "View Users");
        menu.add(Menu.NONE, ADD_NOTIFICATION, Menu.NONE, "New Notification");
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case VIEW_PROFILE:
                Toast.makeText(context, "View Profile", Toast.LENGTH_SHORT).show();
                break;
            case VIEW_USERS:
                Toast.makeText(context, "View Users", Toast.LENGTH_SHORT).show();
                break;
            case ADD_NOTIFICATION:
                Toast.makeText(context, "New notification", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }*/

    /*private static final int MY_PERMISSION_REQUEST_INTERNET = 0;
    private static final int MY_PERMISSION_REQUEST_CHECK_NETWORK_STATUS = 1;
    *//**
     * method to check whether app have all permissions needed
     *//*
    private void checkPermissions() {
        //checking whether have internet permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {

            //checking already user denied the previous request for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(MarketActivity.this,
                    Manifest.permission.INTERNET)) {
                Toast.makeText(context, "Please grand permission for normal flow of app", Toast.LENGTH_SHORT).show();
            }

            //requesting permission for internet
            ActivityCompat.requestPermissions(MarketActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    MY_PERMISSION_REQUEST_INTERNET);

        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {

            //checking already user denied the previous request for permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(MarketActivity.this,
                    Manifest.permission.ACCESS_NETWORK_STATE)) {
                Toast.makeText(context, "Please grand permission for normal flow of app", Toast.LENGTH_SHORT).show();
            }

            //requesting permission for internet
            ActivityCompat.requestPermissions(MarketActivity.this,
                    new String[]{Manifest.permission.ACCESS_NETWORK_STATE},
                    MY_PERMISSION_REQUEST_CHECK_NETWORK_STATUS);

        }
    }

    *//**
     * the permission request result will be getting here
     * @param requestCode request code for select the request
     * @param permissions permissions
     * @param grantResults user reply for request
     *//*
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MY_PERMISSION_REQUEST_INTERNET:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted

                } else {
                    //permission denied
                    Toast.makeText(context, "Please grand permission for normal flow of app", Toast.LENGTH_SHORT).show();

                }
                break;

            case MY_PERMISSION_REQUEST_CHECK_NETWORK_STATUS:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //permission granted

                } else {
                    //permission denied
                    Toast.makeText(context, "Please grand permission for normal flow of app", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }*/

}
