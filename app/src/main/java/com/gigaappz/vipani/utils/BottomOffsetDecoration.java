package com.gigaappz.vipani.utils;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class BottomOffsetDecoration extends RecyclerView.ItemDecoration {

    private int bottomOffset;

    public BottomOffsetDecoration(int bottomOffset) {
        this.bottomOffset = bottomOffset;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int dataSize    = state.getItemCount();
        int position    = parent.getChildAdapterPosition(view);
        if (dataSize > 0 && position == dataSize-1){
            outRect.set(0,0,0,bottomOffset);
        } else {
            outRect.set(0,0,0,0);
        }
    }
}
