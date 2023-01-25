package com.example.challenge25;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>
        implements OnPictureItemClickListener, SwipeClickListener{
    ArrayList<PictureInfo> items = new ArrayList<PictureInfo>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    OnPictureItemClickListener listener;
    SwipeClickListener swipeListener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.picture_item, viewGroup, false);

        return new ViewHolder(itemView, this, swipeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PictureInfo item = items.get(position);
        binderHelper.setOpenOnlyOne(true);
        binderHelper.bind(viewHolder.swipelayout,item.displayName);
        viewHolder.bind(item, items);
        viewHolder.setItem(item);
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public void addItem(PictureInfo item) {
        items.add(item);
    }

    public void setItems(ArrayList<PictureInfo> items) {
        this.items = items;
    }

    public PictureInfo getItem(int position) {
        return items.get(position);
    }

    public void setItem(int position, PictureInfo item) {
        items.set(position, item);
    }

    public void setOnItemClickListener(OnPictureItemClickListener listener) {
        this.listener = listener;
    }

    public void setSwipeListener(SwipeClickListener listener){
        this.swipeListener = listener;
    }

    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

    @Override
    public void onEditClick(ViewHolder holder, View view, int itemPosition, int adapterPosition, ArrayList<PictureInfo> items) {
        if (swipeListener!= null) {
            swipeListener.onDeleteClick(holder, view, itemPosition, adapterPosition,items);
        }
    }

    @Override
    public void onDeleteClick(ViewHolder holder, View view, int itemPosition, int adapterPosition, ArrayList<PictureInfo> items) {
        if(swipeListener != null)
            swipeListener.onDeleteClick(holder, view, itemPosition, adapterPosition, items);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView2;

        SwipeRevealLayout swipelayout;
        private View deleteLayout;
        private View editLayout;

        SwipeClickListener swipeClickListener;


        ImageView imageView;

        BitmapFactory.Options options = new BitmapFactory.Options();

        public ViewHolder(View itemView, final OnPictureItemClickListener listener, final SwipeClickListener swipeClickListener) {
            super(itemView);

            swipelayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            editLayout = itemView.findViewById(R.id.txtEdit);
            deleteLayout = itemView.findViewById(R.id.txtDelete);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);

            options.inSampleSize = 12;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

            this.swipeClickListener = swipeClickListener;
        }


        // Swipe Layout (삭제, 수정) 리스너 설정
        public void bind(final PictureInfo item, final ArrayList<PictureInfo> items){
            deleteLayout.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int position = item.id;

                    if(swipeClickListener != null){
                        swipeClickListener.onDeleteClick(ViewHolder.this, view, position,getAdapterPosition(), items);
                    }

                }
            });


            editLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = item.id;

                    if(swipeClickListener != null)
                        swipeClickListener.onEditClick(ViewHolder.this, view, position,getAdapterPosition(),items);
                }
            });
        }

        public void setItem(PictureInfo item) {
            textView.setText(item.getDisplayName());
            textView2.setText(item.getDateAdded());

            Bitmap bitmap = BitmapFactory.decodeFile(item.getPath(), options);
            imageView.setImageBitmap(bitmap);
        }

    }

}