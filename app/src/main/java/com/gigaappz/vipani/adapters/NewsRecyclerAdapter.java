package com.gigaappz.vipani.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gigaappz.vipani.R;
import com.gigaappz.vipani.interfaces.NewsItemSelected;
import com.gigaappz.vipani.interfaces.NewsLongPressListener;
import com.gigaappz.vipani.models.NewsValueModel;
import com.gigaappz.vipani.utils.AppConstants;

import java.util.List;

import static com.gigaappz.vipani.utils.AppConstants.NEWS_CONTENT_URL;
import static com.gigaappz.vipani.utils.AppConstants.NEWS_FROM_URL;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.ViewHolder>{

    private Context context;
    private List<NewsValueModel> newsValueModels;

    public NewsRecyclerAdapter(){}

    public NewsRecyclerAdapter(Context context, List<NewsValueModel> newsValueModels) {
        this.context = context;
        this.newsValueModels = newsValueModels;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view   = LayoutInflater.from(context).inflate(R.layout.news_cardview, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        NewsValueModel newsValueModel   = newsValueModels.get(i);

        if (!newsValueModel.getPicURL().isEmpty()) {
            Glide.with(context)
                    .load(newsValueModel.getPicURL())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.newsPic);
        } else {
            viewHolder.newsPic.setImageDrawable(context.getResources().getDrawable(R.drawable.no_thumbnail));
        }
        viewHolder.newsHead.setText(newsValueModel.getNewsHead());
        viewHolder.author.setText(newsValueModel.getAuthor());

        /*viewHolder.newsHead.setText(newsValueModel.getNewsHead());
        if (newsValueModel.getPicURL() != null && newsValueModel.getPicURL().length() > 0) {
            //if url, load image directly
            if (newsValueModel.getIsURL() == 1){
                Glide.with(context)
                        .load(newsValueModel.getPicURL())
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.newsPic);
            } else if (newsValueModel.getIsURL() == 0){
                //if not url, decode image from string and load
                byte[] imageByteArray = Base64.decode(newsValueModel.getPicURL(), Base64.DEFAULT);
                Glide.with(context)
                        .load(imageByteArray)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.newsPic);
            }
        } else {
            viewHolder.newsPic.setImageDrawable(context.getResources().getDrawable(R.drawable.no_thumbnail));
        }*/
    }

    @Override
    public int getItemCount() {
        return newsValueModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        private ImageView newsPic;
        private TextView newsHead;
        private TextView author;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            newsPic = itemView.findViewById(R.id.news_image_card_view_news);
            newsHead= itemView.findViewById(R.id.news_head_card_view_news);
            author  = itemView.findViewById(R.id.author_text);
            Typeface regular = Typeface.createFromAsset(context.getAssets(), "AnjaliOldLipi.ttf");
            //newsHead.setTypeface(regular,Typeface.BOLD);
            author.setTypeface(regular);
            if (AppConstants.IS_ADMIN) {
                itemView.setOnLongClickListener(this);
            }
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            newsItemSelected.onNewsItemSelected(newsValueModels.get(getAdapterPosition()));
            /*if (newsValueModels.get(getAdapterPosition()).getIsURL() == 0) {
                //it is a url
                NEWS_CONTENT_URL = newsValueModels.get(getAdapterPosition());
                newsItemSelected.onNewsItemSelected(getAdapterPosition());
            } else {
                // TODO: 04-Oct-18 show news if not url data
            }*/
        }

        @Override
        public boolean onLongClick(View v) {
            newsLongPressListener.onNewsLongPressListener(getAdapterPosition());
            return true;
        }
    }

    private static NewsItemSelected newsItemSelected;
    public void setOnNewsItemSelected(NewsItemSelected newsItemSelected){
        NewsRecyclerAdapter.newsItemSelected = newsItemSelected;
    }

    private static NewsLongPressListener newsLongPressListener;
    public void setOnNewsLongPressListener(NewsLongPressListener newsLongPressListener){
        NewsRecyclerAdapter.newsLongPressListener   = newsLongPressListener;
    }

    public void removeItem(int position){
        newsValueModels.remove(position);
        notifyItemRemoved(position);
    }
}
/*if (!newsValueModel.getPicURL().equals("null")) {
            viewHolder.picTextLayout.setVisibility(View.VISIBLE);
            viewHolder.newsHeadDummy.setVisibility(View.GONE);
            viewHolder.newsHead.setText(newsValueModel.getNewsHead());
            Glide.with(context)
                    .load(newsValueModel.getPicURL())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.photo);
        } else {
            viewHolder.picTextLayout.setVisibility(View.GONE);
            viewHolder.newsHeadDummy.setVisibility(View.VISIBLE);
            viewHolder.newsHeadDummy.setText(newsValueModel.getNewsHead());
        }
        viewHolder.newsContent.setText(newsValueModel.getNewsContent());*/
        /*if (!newsValueModel.getPicURL().equals("null")) {
            viewHolder.newsHead.setText(newsValueModel.getNewsHead());
            Glide.with(context)
                    .load(newsValueModel.getPicURL())
                    .thumbnail(0.5f)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(viewHolder.newsPic);
        }*/
        /*newsHead    = itemView.findViewById(R.id.news_head_news_recycler);
            newsContent = itemView.findViewById(R.id.news_descript_news_recycler);
            photo       = itemView.findViewById(R.id.news_image_news_recycler);
            picTextLayout   = itemView.findViewById(R.id.image_text_layout_news_recycler);
            newsHeadDummy   = itemView.findViewById(R.id.news_head_dummy_news_recycler);

            newsHead.setTypeface(bold);
            newsHeadDummy.setTypeface(bold);
            newsContent.setTypeface(regular);*/