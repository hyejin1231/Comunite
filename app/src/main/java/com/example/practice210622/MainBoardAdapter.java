package com.example.practice210622;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MainBoardAdapter extends RecyclerView.Adapter<MainBoardAdapter.CustomViewHolder> {

    private ArrayList<Content> arrayList;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    int viewCount;
    String getUnique;
    String key;

    public MainBoardAdapter(ArrayList<Content> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_content,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  MainBoardAdapter.CustomViewHolder holder, int position) {

        FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
        StorageReference storageReference = storage.getReference();

        String path = arrayList.get(position).getProfile();

        Glide.with(holder.itemView)
                .load(path)
                .into(holder.iv_content_profile);

        storageReference.child("profile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView)
                        .load(uri)
                        .into(holder.iv_content_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {

            }
        });

        // 실제 아이템들에 대한 매칭하는 곳
//        Glide.with(holder.itemView)
//                .load(arrayList.get(position).getProfile())
//                .into(holder.iv_content_profile);


        holder.tv_content_nickName.setText(arrayList.get(position).getNickName());
        holder.tv_content_title.setText(arrayList.get(position).getTitle());
        holder.tv_content_date.setText(arrayList.get(position).getDate());
        holder.tv_content_count.setText(String.valueOf(arrayList.get(position).getCount()));
        holder.tv_content_category.setText(arrayList.get(position).getCategory());
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_content_profile;
        TextView tv_content_nickName;
        TextView tv_content_count;
        TextView tv_content_category;
        TextView tv_content_title;
        TextView tv_content_date;

        public CustomViewHolder(@NonNull  View itemView) {
            super(itemView);

            this.iv_content_profile = itemView.findViewById(R.id.iv_content_profile);
            this.tv_content_title = itemView.findViewById(R.id.tv_content_title);
            this.tv_content_category = itemView.findViewById(R.id.tv_content_category);
            this.tv_content_count = itemView.findViewById(R.id.tv_content_count);
            this.tv_content_date = itemView.findViewById(R.id.tv_content_date);
            this.tv_content_nickName = itemView.findViewById(R.id.tv_content_nickName);

            database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
            databaseReference = database.getReference("practice210622");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition(); // 현재 itemview의 position
                    getUnique = arrayList.get(position).getUnique();
                    viewCount = arrayList.get(position).getCount() + 1;

                    // 조회수를 1증가 시키기
                    databaseReference.child("Content").orderByChild("unique").equalTo(getUnique).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()) {
                                key = child.getKey();
                            }

                            snapshot.getRef().child(key).child("count").setValue(viewCount);
                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });

                    Intent intent = new Intent(v.getContext(),ViewPage.class);
                    intent.putExtra("unique", arrayList.get(position).getUnique());
                    intent.putExtra("title", arrayList.get(position).getTitle());
                    intent.putExtra("contents",arrayList.get(position).getContents());
                    intent.putExtra("count", String.valueOf(arrayList.get(position).getCount()));
                    intent.putExtra("date",arrayList.get(position).getDate());
                    intent.putExtra("image", arrayList.get(position).getImage());
                    intent.putExtra("category", arrayList.get(position).getCategory());

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    itemView.getContext().startActivity(intent);

                }
            });


        }
    }
}






























