package com.gigaappz.vipani.adapters;

/**
 * Created by DELL on 21-Sep-18.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.gigaappz.vipani.AppController;
import com.gigaappz.vipani.PersonUtils;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.activity.NewMarketActivity;
import com.gigaappz.vipani.fragments.Userspending;
import com.gigaappz.vipani.interfaces.RefreshUsersList;
import com.gigaappz.vipani.interfaces.UserLongPressListener;
import com.gigaappz.vipani.models.Domestic;
import com.gigaappz.vipani.utils.AppConstants;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shashank.sony.fancygifdialoglib.FancyGifDialog;
import com.shashank.sony.fancygifdialoglib.FancyGifDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;




public class Userpendingadapter extends RecyclerView.Adapter<Userpendingadapter.ViewHolder> {

    private Context context;
    private List<PersonUtils> personUtils;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    public Userpendingadapter(){}

    public Userpendingadapter(Context context, List personUtils) {
        this.context = context;
        this.personUtils = personUtils;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.itemView.setTag(personUtils.get(position));

        PersonUtils pu = personUtils.get(position);

        holder.pName.setText(pu.getPersonName());
        holder.name.setText(pu.getName());
        holder.place.setText(pu.getCompany()+","+pu.getPlace());
        holder.disablereason.setVisibility(View.GONE);
        holder.buttonlayout.setVisibility(View.VISIBLE);
        holder.dor.setText(pu.getDor());
        holder.doe.setText(pu.getDoe());
        holder.dop.setText(pu.getDop());
        holder.remain.setVisibility(View.GONE);
        holder.accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customalert("Days",personUtils.get(position).getId());
            }
        });
        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customalert1("Reject Reason",personUtils.get(position).getId());
            }
        });

        //holder.pJobProfile.setText(pu.getJobProfile());

    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        public EditText pName,name,place,company;
        public TextView pJobProfile,disablereason;
        public EditText dor,dop,doe,remain;
        public LinearLayout buttonlayout;
        public Button accept,reject;

        public ViewHolder(final View itemView) {
            super(itemView);

            pName = (EditText) itemView.findViewById(R.id.mobile_text);
            name = (EditText) itemView.findViewById(R.id.name_text);
            place = (EditText) itemView.findViewById(R.id.place_text);
            //pJobProfile = (TextView) itemView.findViewById(R.id.mobile_text);
            disablereason = (TextView) itemView.findViewById(R.id.disable_reason);
            dor = (EditText) itemView.findViewById(R.id.date_registration);
            dop = (EditText) itemView.findViewById(R.id.date_purchase);
            doe = (EditText) itemView.findViewById(R.id.date_expiry);
            remain = (EditText) itemView.findViewById(R.id.date_remains);
            buttonlayout = (LinearLayout) itemView.findViewById(R.id.buttonlayout);
            accept = (Button) itemView.findViewById(R.id.accept);
            reject = (Button) itemView.findViewById(R.id.reject);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PersonUtils cpu = (PersonUtils) view.getTag();

                    // Toast.makeText(view.getContext(), cpu.getPersonName()+" is "+ cpu.getJobProfile(), Toast.LENGTH_SHORT).show();

                }
            });
            if (AppConstants.IS_ADMIN) {
                itemView.setOnLongClickListener(this);
            }
        }

        @Override
        public boolean onLongClick(View v) {
            listener.onUserLongPressListener(getAdapterPosition());
            return true;
        }
    }
    public void remuser(final String token, final String userid, final String days) {

        String urlJsonObj = "http://tradewatch.xyz/api/extendValidity.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", userid);
            obj.put("days", days);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        mFirebaseInstance = FirebaseDatabase.getInstance();
                        mFirebaseDatabase = mFirebaseInstance.getReference("user");
                        Domestic name=new Domestic();
                        name.setName(userid+""+days);
                        mFirebaseDatabase.setValue(name);
                        AppConstants.SELECTED_TAB=2;
                        //// TODO: 10-Oct-18 interface
                        refreshUsersList.onUserListRefresh();

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
    public void remuser1(final String token, final String userid, final String comment) {

        String urlJsonObj = "http://tradewatch.xyz/api/blockUser.php";
        JSONObject obj = new JSONObject();
        try {
            obj.put("auth", token);
            obj.put("id", userid);
            obj.put("comment", comment);

        } catch (JSONException e) {
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                urlJsonObj, obj, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {


                try {

                    if (response.getString("responseStatus").equalsIgnoreCase("true")){
                        mFirebaseInstance = FirebaseDatabase.getInstance();
                        mFirebaseDatabase = mFirebaseInstance.getReference("user");
                        Domestic name=new Domestic();
                        name.setName(userid+""+comment);
                        mFirebaseDatabase.setValue(name);
                        AppConstants.SELECTED_TAB=2;
                        // TODO: 10-Oct-18 interface remove
                        refreshUsersList.onUserListRefresh();


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

    public void customalert(String hint, final String user){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);

        final EditText reasonText = dialog.findViewById(R.id.daysedit);

        inputLayout.getEditText().setHint(hint);

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
                String days   = inputLayout.getEditText().getText().toString();
                // TODO: 10/2/2018 upload reason to firebase
                remuser("g*Rg3I0",user,days);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void customalert1(String hint, final String user){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);

        final EditText reasonText = dialog.findViewById(R.id.daysedit);

        inputLayout.getEditText().setHint(hint);

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
                String days   = inputLayout.getEditText().getText().toString();
                // TODO: 10/2/2018 upload reason to firebase
                remuser1("g*Rg3I0",user,days);
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private static UserLongPressListener listener;
    public void setOnLongPressListener(UserLongPressListener listener){
        Userpendingadapter.listener    = listener;
    }

    public void removeItem(int position){
        personUtils.remove(position);
        notifyItemRemoved(position);
    }

    private static RefreshUsersList refreshUsersList;
    public void onRefreshUsersList(RefreshUsersList refreshUsersList){
        Userpendingadapter.refreshUsersList = refreshUsersList;
    }
}