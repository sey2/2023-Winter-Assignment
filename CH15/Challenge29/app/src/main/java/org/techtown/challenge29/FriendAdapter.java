package org.techtown.challenge29;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

    /*
    어댑터 클래스 는 어댑터 뷰와 해당 뷰의 기본 데이터 사이의 가교 역할을 합니다.
    어댑터 클래스를 사용하여 데이터 항목에 액세스할 수 있습니다.
    또한 어댑터는 데이터셋 각 항목에 대한 뷰를 생성합니다.
    */


public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder>
        implements OnFriendItemClickListener {
    ArrayList<FriendItem> items = new ArrayList<FriendItem>();

    OnFriendItemClickListener listener;


    /* friend_item.xml 파일과 자바 코드와 연결 해주는 메소드 입니다. (인플레이션) */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemView = inflater.inflate(R.layout.friend_item, viewGroup, false);

        return new ViewHolder(itemView, this);
    }

    /* 생성된 뷰홀더에 데이터를 바인딩 해주는 함수입니다.
         데이터가 스크롤 되어서 맨 위에있던 뷰 홀더(레이아웃) 객체가 맨 아래로 이동한다면,
         그 레이아웃은 재사용 하되 데이터는 새롭게 바뀌게 됩니다.
   */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        FriendItem item = items.get(position);
        viewHolder.setItem(item);
    }

    // ArrayList에 들어있는 아이템의 수를 리턴 합니다.
    @Override
    public int getItemCount() {
        return items.size();
    }

    // ArrayList에 아이템을 추가합니다.
    public void addItem(FriendItem item) {
        items.add(item);
    }

    // ArrayList를 통째로 넘겨줘서 아이템 정보를 초기화합니다.
    public void setItems(ArrayList<FriendItem> items) {
        this.items = items;
    }

    // ArrayList에서 친구 정보를 가져옵니다.
    public FriendItem getItem(int position) {
        return items.get(position);
    }

    // ArrayList에 아이템을 원하는 위치에 추가합니다.
    public void setItem(int position, FriendItem item) {
        items.set(position, item);
    }

    // ArrayList 초기화 (아이템 전부 삭제)
    public void removeAllItem(){items = new ArrayList<>();}

    // listener 초기화 메서드
    public void setOnItemClickListener(OnFriendItemClickListener listener) {
        this.listener = listener;
    }

    // 해당 메소드를 이용해 아이템 클릭시 콜백 메서드가 동작하게 합니다.
    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null) {
            listener.onItemClick(holder, view, position);
        }
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
        ImageView imageView;

        public ViewHolder(View itemView, final OnFriendItemClickListener listener) {
            super(itemView);

            textView = itemView.findViewById(R.id.textView);
            textView2 = itemView.findViewById(R.id.textView2);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null) {
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(FriendItem item) {
            textView.setText(item.getName());
            textView2.setText(item.getMobile());
        }

    }

}
