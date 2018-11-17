package com.gigaappz.vipani;

/**
 * Created by DELL on 21-Sep-18.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gigaappz.vipani.adapters.UserRecyclerAdapter;
import com.gigaappz.vipani.interfaces.UserLongPressListener;
import com.gigaappz.vipani.utils.AppConstants;

import java.util.List;


import static com.gigaappz.vipani.fragments.UsersTab.userModels;


public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<PersonUtils> personUtils;

    public CustomRecyclerAdapter(Context context, List personUtils) {
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
       // holder.disablereason.setVisibility(View.GONE);
        holder.buttonlayout.setVisibility(View.GONE);
        holder.dor.setText(pu.getDor());
        holder.doe.setText(pu.getDoe());
        holder.dop.setText(pu.getDop());
        holder.remain.setText(pu.getRemain());

    }

    @Override
    public int getItemCount() {
        return personUtils.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener{

        public EditText pName;
        public TextView pJobProfile,disablereason;
        public EditText dor,dop,doe,remain;
        public LinearLayout buttonlayout;

        public ViewHolder(View itemView) {
            super(itemView);

            pName = (EditText) itemView.findViewById(R.id.mobile_text);
            //pJobProfile = (TextView) itemView.findViewById(R.id.mobile_text);
            disablereason = (TextView) itemView.findViewById(R.id.disable_reason);
            dor = (EditText) itemView.findViewById(R.id.date_registration);
            dop = (EditText) itemView.findViewById(R.id.date_purchase);
            doe = (EditText) itemView.findViewById(R.id.date_expiry);
            remain = (EditText) itemView.findViewById(R.id.date_remains);
            buttonlayout = (LinearLayout) itemView.findViewById(R.id.buttonlayout);
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

    private static UserLongPressListener listener;
    public void setOnLongPressListener(UserLongPressListener listener){
        CustomRecyclerAdapter.listener    = listener;
    }

    public void removeItem(int position){
        personUtils.remove(position);
        notifyItemRemoved(position);
    }

}