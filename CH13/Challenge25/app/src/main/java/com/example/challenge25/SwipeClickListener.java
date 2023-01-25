package com.example.challenge25;

import android.view.View;

import java.util.ArrayList;

public interface SwipeClickListener {
    void onEditClick(PictureAdapter.ViewHolder holder, View view, int itemPosition, int adapterPosition, ArrayList<PictureInfo> items);
    void onDeleteClick(PictureAdapter.ViewHolder holder, View view, int itemPosition,int adapterPosition, ArrayList<PictureInfo> items);
}
