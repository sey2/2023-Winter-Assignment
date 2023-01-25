package com.example.challenge25;

import android.view.View;

public interface OnPictureItemClickListener {
    void onItemClick(PictureAdapter.ViewHolder holder, View view, int position);
}