package org.techtown.challenge30;

import android.view.View;

public interface OnFriendItemClickListener {
    public void onItemClick(FriendAdapter.ViewHolder holder, View view, int position);
}