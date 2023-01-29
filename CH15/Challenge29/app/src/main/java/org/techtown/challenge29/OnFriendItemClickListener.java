package org.techtown.challenge29;

import android.view.View;

public interface OnFriendItemClickListener {
    public void onItemClick(FriendAdapter.ViewHolder holder, View view, int position);
}