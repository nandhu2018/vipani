package com.gigaappz.vipani.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.models.NewsValueModel;

public class NewsContentFromDevice extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content_from_device);
        TextView headText, contentText;

        ImageView newsImage,back;

        headText    = findViewById(R.id.head_text_news_content_from_device);
        contentText = findViewById(R.id.content_text_news_content_from_device);
        newsImage   = findViewById(R.id.image_news_content_from_device);
        back   = findViewById(R.id.back);
        Typeface regular = Typeface.createFromAsset(getAssets(), "AnjaliOldLipi.ttf");

        //headText.setTypeface(regular,Typeface.BOLD);
        //contentText.setTypeface(regular);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getNewsDetailsFromServer();
        headText.setText(model.getNewsHead());
        contentText.setText(model.getNewsContent());
        Glide.with(this)
                .load(model.getPicURL())
                .thumbnail(0.5f)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(newsImage);
    }

    NewsValueModel model;
    private void getNewsDetailsFromServer() {
        model = new NewsValueModel();
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        model = (NewsValueModel) bundle.getSerializable("NEWS_VALUE");
    }
}
