package com.gigaappz.vipani;

import com.gigaappz.vipani.models.DomesticValueModel;
import com.gigaappz.vipani.models.NewsValueModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import static com.gigaappz.vipani.utils.AppConstants.LAST_UPDATE_KEY;
import static com.gigaappz.vipani.utils.AppConstants.NEWS_KEY;
import static com.gigaappz.vipani.utils.AppConstants.NOTIFICATION_KEY;

/**
 * Created by DELL on 26-Sep-18.
 */

public class FirebaseManupulator {

    private DatabaseReference notificationNode;
    private DatabaseReference lastUpdate;
    private DatabaseReference newsNode;

    public FirebaseManupulator(String node){
        if (node.equals(NOTIFICATION_KEY)) {
            notificationNode = FirebaseDatabase.getInstance().getReference().child(NOTIFICATION_KEY);
            lastUpdate  = FirebaseDatabase.getInstance().getReference().child(LAST_UPDATE_KEY);
        } else if (node.equals(NEWS_KEY)){
            newsNode    = FirebaseDatabase.getInstance().getReference().child(NEWS_KEY);
        }
    }

    /**
     * method to upload news to firebase
     * @param model news model class
     */
    public void uploadNews(NewsValueModel model){
        /*Map<String, String> map = new HashMap<>();
        map.put("pic_url", model.getPicURL());
        map.put("news_head", model.getNewsHead());
        map.put("news_content", model.getNewsContent());*/
        newsNode.push().setValue(model);
    }

    public void sendDomesticNotification(DomesticValueModel model){
        Map<String, String> map = new HashMap<>();
        map.put("user", "user");
        map.put("head_value", model.getHeadText());
        map.put("sub_head_value", model.getSubHeadText());
        map.put("value_text", model.getValueText());
        map.put("value_sub_text", model.getValueSubText());
        map.put("value_rate_text", model.getValueRateText());
        map.put("value_diff_text", model.getValueDiffText());
        map.put("time", model.getTime());

        notificationNode.push().setValue(map);
        lastUpdate.child("time").setValue(model.getTime());
    }

}
