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

import com.gigaappz.vipani.PersonUtils;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.interfaces.RefreshUsersList;
import com.gigaappz.vipani.interfaces.UserLongPressListener;
import com.gigaappz.vipani.utils.AppConstants;

import java.util.List;

//import static com.gigaappz.vipani.activity.MarketActivity.isAdmin;


public class Userinactiveadapter extends RecyclerView.Adapter<Userinactiveadapter.ViewHolder> {

    private Context context;
    private List<PersonUtils> personUtils;

    public Userinactiveadapter(Context context, List personUtils) {
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
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(personUtils.get(position));

        PersonUtils pu = personUtils.get(position);

        holder.pName.setText(pu.getPersonName());
        holder.name.setText(pu.getName());
        holder.place.setText(pu.getCompany()+","+pu.getPlace());
        holder.disablereason.setVisibility(View.VISIBLE);
        holder.buttonlayout.setVisibility(View.GONE);
        holder.dor.setText(pu.getDor());
        holder.doe.setText(pu.getDoe());
        holder.dop.setText(pu.getDop());
        holder.disablereason.setText("Reason : "+pu.getDisablereason());
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

        public ViewHolder(View itemView) {
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

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customalert("Days");
                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    customalert("Reject Reason");
                }
            });
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
    public void customalert(String hint){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.customalert);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final TextInputLayout inputLayout = dialog.findViewById(R.id.days);

        final EditText reasonText = dialog.findViewById(R.id.daysedit);

        reasonText.setHint(hint);

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
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    private static UserLongPressListener listener;
    public void setOnLongPressListener(UserLongPressListener listener){
        Userinactiveadapter.listener    = listener;
    }

    public void removeItem(int position){
        personUtils.remove(position);
        notifyItemRemoved(position);
    }

}