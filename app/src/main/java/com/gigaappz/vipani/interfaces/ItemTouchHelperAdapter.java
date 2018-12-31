package com.gigaappz.vipani.interfaces;

/**
 * Created by DELL on 19-Dec-18.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
