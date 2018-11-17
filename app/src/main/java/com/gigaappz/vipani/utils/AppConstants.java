package com.gigaappz.vipani.utils;


import com.gigaappz.vipani.models.DomesticValueModel;
import com.gigaappz.vipani.models.ForeignValueModel;
import com.gigaappz.vipani.models.NewsValueModel;

import java.util.ArrayList;
import java.util.List;

public class AppConstants {
    public static String NEWS_API_URL           = "https://newsapi.org/v2/top-headlines?sources=techcrunch&apiKey=d3003e5967404a558600ee7e7d90734c";
    public static String FIREBASE_DATABASE_URL  = "https://vipani-40e68.firebaseio.com/";
    public static String NOTIFICATION_KEY       = "notification";
    public static String NEWS_KEY               = "news";
    public static String LAST_UPDATE_KEY        = "last_update";
    public static String CHANNEL_ID             = "vipani_notification";
    public static NewsValueModel NEWS_CONTENT_URL       = new NewsValueModel();
    public static int NOTIFICATION_ID           = 34;
    public static int SELECTED_TAB = 0;
    public static Boolean IS_ADMIN = false;

    public static String NEWS_FROM_URL    = "1";
    public static String NEWS_FROM_DEVICE    = "0";

    public static int SELECTED_NEWS_POSITION    = 0;
    public static boolean APP_IS_OPEN           = false;
    public static List<DomesticValueModel> domesticValueModels  = new ArrayList<>();
    public static List<ForeignValueModel> foreignValueModels    = new ArrayList<>();
    public static List<NewsValueModel> newsValueModels          = new ArrayList<>();

}
