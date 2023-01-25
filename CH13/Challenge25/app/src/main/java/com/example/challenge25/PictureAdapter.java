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

/*
    어댑터 클래스 는 어댑터 뷰와 해당 뷰의 기본 데이터 사이의 가교 역할을 합니다.
    어댑터 클래스를 사용하여 데이터 항목에 액세스할 수 있습니다.
    또한 어댑터는 데이터셋 각 항목에 대한 뷰를 생성합니다.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>
        implements OnPictureItemClickListener, SwipeClickListener{
    ArrayList<PictureInfo> items = new ArrayList<PictureInfo>();
    private final ViewBinderHelper binderHelper = new ViewBinderHelper();

    OnPictureItemClickListener listener;
    SwipeClickListener swipeListener;


    /* picture_item.xml 파일과 자바 코드와 연결 해주는 메소드 입니다. (인플레이션) */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.picture_item, viewGroup, false);

        return new ViewHolder(itemView, this, swipeListener);
    }

    /* 생성된 뷰홀더에 데이터를 바인딩 해주는 함수입니다.
    데이터가 스크롤 되어서 맨 위에있던 뷰 홀더(레이아웃) 객체가 맨 아래로 이동한다면,
    그 레이아웃은 재사용 하되 데이터는 새롭게 바뀌게 됩니다.
    */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        PictureInfo item = items.get(position);

        //swipeLayout Setting and binding
        binderHelper.setOpenOnlyOne(true);
        binderHelper.bind(viewHolder.swipelayout,item.displayName);

        // swipeLayout 리스너 설정
        viewHolder.bind(item, items);
        viewHolder.setItem(item);
    }


    // ArrayList에 들어있는 아이템의 수를 리턴 합니다.
    @Override
    public int getItemCount() {
        return items.size();
    }

    // ArrayList에 아이템을 추가합니다.
    public void addItem(PictureInfo item) {
        items.add(item);
    }

    // ArrayList를 통째로 넘겨줘서 아이템 정보를 초기화합니다.
    public void setItems(ArrayList<PictureInfo> items) {
        this.items = items;
    }

    // ArrayList에서 사진 정보를 가져옵니다.
    public PictureInfo getItem(int position) {
        return items.get(position);
    }

    // ArrayList에 아이템을 원하는 위치에 추가합니다.
    public void setItem(int position, PictureInfo item) {
        items.set(position, item);
    }

    // listener 초기화 메서드
    public void setOnItemClickListener(OnPictureItemClickListener listener) {
        this.listener = listener;
    }

    public void setSwipeListener(SwipeClickListener listener){
        this.swipeListener = listener;
    }

    // 해당 메소드를 이용해 아이템 클릭시 콜백 메서드가 동작하게 합니다.
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
    }

  // 해당 메소드를 이용해 SiwpeLayout의 수정 버튼 클릭시 콜백 메서드가 동작하게 합니다.
    @Override
    public void onEditClick(ViewHolder holder, View view, int itemPosition, int adapterPosition, ArrayList<PictureInfo> items) {
        if (swipeListener!= null) {
            swipeListener.onDeleteClick(holder, view, itemPosition, adapterPosition,items);
        }
    }

    // 해당 메소드를 이용해 SiwpeLayout의 삭제 버튼 클릭시 콜백 메서드가 동작하게 합니다.
    @Override
    public void onDeleteClick(ViewHolder holder, View view, int itemPosition, int adapterPosition, ArrayList<PictureInfo> items) {
        if(swipeListener != null)
            swipeListener.onDeleteClick(holder, view, itemPosition, adapterPosition, items);
    }

    /*
     뷰홀더는 말 그대로 뷰를 보관하는 Holder 객체입니다.
     본래 리사이클러뷰의 뷰 업데이트 시에는 findViewById()가 호출되는데 findViewById()는 고비용 작업이라 매번 호출 될 시 성능 저하로 이어집니다.
     이를 해결하기 위해 나온 것이 뷰홀더라는 개념입니다.
     간단히 서술하자면, 뷰 업데이트 시 불필요한 findViewById()의 호출 피하기 위해 findViewById()를 통해 뷰를 불러오지 않고
     저장 된 객체로 액세스하기 위해 사용합니다.
     */

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

            /* findViewById는 BookItem.xml 파일에 엤는 View들을 자바 코드의 오브젝트와 연결해주는 역할을 해줍니다.*/
            swipelayout = (SwipeRevealLayout) itemView.findViewById(R.id.swipe_layout);
            editLayout = itemView.findViewById(R.id.txtEdit);
            deleteLayout = itemView.findViewById(R.id.txtDelete);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);

            options.inSampleSize = 12;

              /* 사진 정보 목록 아이템을 선택시 처리할 기능부를 구현해주는 부분입니다.
               setOnClickListener는 객체의 상태 변화가 있을 떄 마다 메서드등을 통해 감지하는
               옵저버 패턴을 이용한 디자인 패턴입니다.
               리싸이클러뷰의 Book Item을 클릭할 때 마다 아래의 메서드가 호출 되므로
               아이템 클릭시 원하는 기능을 동작하도록 하려면 아래 메서드에 구현하면 됩니다.
               */
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

        // textView, imageView의 텍스트, 이미지를 지정하는 메소드
        public void setItem(PictureInfo item) {
            textView.setText(item.getDisplayName());
            textView2.setText(item.getDateAdded());

            Bitmap bitmap = BitmapFactory.decodeFile(item.getPath(), options);
            imageView.setImageBitmap(bitmap);
        }

    }

}