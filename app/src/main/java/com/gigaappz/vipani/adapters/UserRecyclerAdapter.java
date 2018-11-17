package com.gigaappz.vipani.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gigaappz.vipani.R;
import com.gigaappz.vipani.interfaces.UserLongPressListener;
import com.gigaappz.vipani.models.UserModel;
import com.gigaappz.vipani.utils.AppConstants;

import java.util.List;


import static com.gigaappz.vipani.activity.MarketActivity.regular;
import static com.gigaappz.vipani.fragments.UsersTab.userModels;


public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.ViewHolder>{
    private List<UserModel> usersList;
    private Context context;
/*
    public UserRecyclerAdapter(Context context) {
        this.context = context;
    }*/

    public UserRecyclerAdapter(Context context,List<UserModel> usersList) {
        this.usersList = usersList;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view   = LayoutInflater.from(context).inflate(R.layout.user_card_view, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        UserModel model = userModels.get(position);
        //viewHolder.nameText.setText(model.getUserName());
        viewHolder.mobileText.setText(model.getUserMobile());
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView nameText, mobileText;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //nameText    = itemView.findViewById(R.id.name_text);
            mobileText  = itemView.findViewById(R.id.mobile_text);

            //nameText.setTypeface(regular);
            mobileText.setTypeface(regular);

           // nameText.setTextColor(context.getResources().getColor(R.color.textColorBlack));
            mobileText.setTextColor(context.getResources().getColor(R.color.textColorGray));

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

    private static UserLongPressListener listener;
    public void setOnLongPressListener(UserLongPressListener listener){
        UserRecyclerAdapter.listener    = listener;
    }

    public void removeItem(int position){
        userModels.remove(position);
        notifyItemRemoved(position);
    }
}
