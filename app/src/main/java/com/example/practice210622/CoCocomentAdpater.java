package com.example.practice210622;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CoCocomentAdpater extends RecyclerView.Adapter<CoCocomentAdpater.CustomViewHolder>{

    private ArrayList<CoCoComent> arrayList;
    private Context context;

    public CoCocomentAdpater(ArrayList<CoCoComent> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cococoment,parent,false);
        CustomViewHolder customViewHolder = new CustomViewHolder(view);
        return customViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull  CoCocomentAdpater.CustomViewHolder holder, int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
        StorageReference storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");

        String path = arrayList.get(position).getProfile();

        Glide.with(holder.itemView)
                .load(path)
                .into(holder.iv_cocoment_profile);

        storageReference.child("profile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView)
                        .load(uri)
                        .into(holder.iv_cocoment_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {

            }
        });

        holder.tv_cocoment_nickName.setText(arrayList.get(position).getNickName());
        holder.tv_cocoment_coment.setText(arrayList.get(position).getComent());
        holder.tv_cocoment_time.setText(arrayList.get(position).getTime());

        String image = arrayList.get(position).getImage();

        if (image.equals("x")) {
            holder.iv_cocoment_image.setVisibility(View.GONE);
        }else{
            holder.iv_cocoment_image.setVisibility(View.VISIBLE);

            storageReference.child("coment").child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(holder.itemView)
                            .load(uri)
                            .into(holder.iv_cocoment_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_cocoment_nickName;
        TextView tv_cocoment_time;
        TextView tv_cocoment_coment;
        ImageView iv_cocoment_image;
        ImageView iv_cocoment_profile;
        ImageView iv_btn_cocoment_add;
//        RecyclerView reCoCoComentCycler;

        public CustomViewHolder(@NonNull  View itemView) {
            super(itemView);

            this.tv_cocoment_nickName = itemView.findViewById(R.id.tv_cocoment_nickName);
            this.tv_cocoment_time = itemView.findViewById(R.id.tv_cocoment_time);
            this.tv_cocoment_coment = itemView.findViewById(R.id.tv_cocoment_coment);
            this.iv_btn_cocoment_add = itemView.findViewById(R.id.iv_btn_cocoment_add);
            this.iv_cocoment_image = itemView.findViewById(R.id.iv_cocoment_image);
            this.iv_cocoment_profile = itemView.findViewById(R.id.iv_cocoment_profile);
//            this.reCoCoComentCycler = itemView.findViewById(R.id.reCoCoComentCycler);
        }
    }
}
