package com.gigaappz.vipani.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gigaappz.vipani.R;
import com.gigaappz.vipani.interfaces.CustomOnClick;
import com.gigaappz.vipani.interfaces.DomesticLongPressListener;
import com.gigaappz.vipani.models.DomesticValueModel;
import com.gigaappz.vipani.models.ForeignValueModel;
import com.gigaappz.vipani.utils.AppConstants;

import static com.gigaappz.vipani.fragments.ForeignTab.foreignValueModels;

public class ForeignRecyclerAdapter extends RecyclerView.Adapter<ForeignRecyclerAdapter.ViewHolder> {

    private Context context;
    private static CustomOnClick customOnClick;
    public ForeignRecyclerAdapter(Context context){
        this.context    = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view   = LayoutInflater.from(context).inflate(R.layout.foreign_recycler_single_row, viewGroup, false);
        return new ViewHolder(view);
    }

    public void setOnItemClickListener(CustomOnClick customOnClick){
        ForeignRecyclerAdapter.customOnClick=customOnClick;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ForeignValueModel domesticValueModel = foreignValueModels.get(i);
        viewHolder.headline.setText(domesticValueModel.getHeadText());
        viewHolder.subHead.setText(domesticValueModel.getSubHeadText());
        viewHolder.value.setText(domesticValueModel.getValueText());
        viewHolder.valueSub.setText(domesticValueModel.getValueSubText());
        viewHolder.timetext.setText("Last Updated On: "+domesticValueModel.getTimetext());
//        viewHolder.valueRate.setText(domesticValueModel.getValueRateText());
        viewHolder.valueDifference.setText(domesticValueModel.getValueDiffText());
//        viewHolder.valueSub.setText(domesticValueModel.getValueSubText());
        if (domesticValueModel.isProfit()) {
            viewHolder.indicatorLayout.setBackgroundColor(context.getResources().getColor(R.color.positive_indicator));
            viewHolder.valueDifference.setTextColor(context.getResources().getColor(R.color.positive_indicator));
            viewHolder.valueRate.setTextColor(context.getResources().getColor(R.color.positive_indicator));
        } else {
            viewHolder.indicatorLayout.setBackgroundColor(context.getResources().getColor(R.color.negative_indicator));
            viewHolder.valueDifference.setTextColor(context.getResources().getColor(R.color.negative_indicator));
            viewHolder.valueRate.setTextColor(context.getResources().getColor(R.color.negative_indicator));
        }
        viewHolder.time.setText(domesticValueModel.getTime());
    }

    @Override
    public int getItemCount() {
        return foreignValueModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener,View.OnClickListener {

        private RelativeLayout indicatorLayout;
        private TextView headline, subHead, value, valueSub, valueRate, valueDifference, time,timetext,remark;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            indicatorLayout = itemView.findViewById(R.id.indicator_layout);
            linearLayout = itemView.findViewById(R.id.linear1);
            headline        = itemView.findViewById(R.id.head_text_domestic);
            subHead         = itemView.findViewById(R.id.sub_head_text_domestic);
            value           = itemView.findViewById(R.id.value_text_domestic);
            valueSub        = itemView.findViewById(R.id.value_sub_text_domestic);
            valueRate       = itemView.findViewById(R.id.value_rate_text_domestic);
            valueDifference = itemView.findViewById(R.id.value_diff_text_domestic);
            time            = itemView.findViewById(R.id.news_time_text_domestic);
            timetext            = itemView.findViewById(R.id.timetext);
            Typeface regular = Typeface.createFromAsset(context.getAssets(), "AnjaliOldLipi.ttf");

            headline.setTypeface(regular);
            subHead.setTypeface(regular);
            value.setTypeface(regular);
            //valueSub.setTypeface(regular);
            valueRate.setTypeface(regular);
            valueDifference.setTypeface(regular);
            //timetext.setTypeface(regular);

            headline.setTextColor(context.getResources().getColor(R.color.textColorBlack));
            subHead.setTextColor(context.getResources().getColor(R.color.textColorBlack));
            value.setTextColor(context.getResources().getColor(R.color.textColorBlack));
            valueSub.setTextColor(context.getResources().getColor(R.color.textColorBlack));
            time.setTextColor(context.getResources().getColor(R.color.textColorBlack));
            timetext.setTextColor(context.getResources().getColor(R.color.textColorBlack));
            if (AppConstants.IS_ADMIN) {
                itemView.setOnLongClickListener(this);
            }
            linearLayout.setOnClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {
            domesticLongPressListener.onDomesticLongPressListener(foreignValueModels.get(getAdapterPosition()).getId());
            return true;
        }

        @Override
        public void onClick(View v) {
            customOnClick.onItemClick(v,getAdapterPosition());
        }
    }

    private static DomesticLongPressListener domesticLongPressListener;
    public void setLongPressListener(DomesticLongPressListener listener){
        ForeignRecyclerAdapter.domesticLongPressListener = listener;
    }

    public void removeItem(int position){
        foreignValueModels.remove(position);
        //notifyItemRemoved(position);
    }
}
