package com.example.practice210622;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

public class CoComentAdapter extends RecyclerView.Adapter<CoComentAdapter.CustomViewHolder> {

//    private ArrayList<Coment> arrayList;
//    private Context context;
//
//    public CoComentAdapter(ArrayList<Coment> arrayList, Context context) {
//        this.arrayList = arrayList;
//        this.context = context;
//    }

    private  ArrayList<CoComent> arrayList;
    private Context context;

    public CoComentAdapter(ArrayList<CoComent> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String key,key2,key3;
    String currentEmail ,idToken, currentProfile,currentNickName;
    String unique, title, contents,count,date,image,category;

    ArrayList<CoCoComent> arrayList1 ;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    Random random = new Random();
    long now = System.currentTimeMillis();
    Date today = new Date(now);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm");
    SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("hh:mm:SS");
    String currentDate = simpleDateFormat.format(today);
    String currentTime = simpleDateFormat2.format(today);
    String currentTime3 = simpleDateFormat3.format(today);

//    private ArrayList<String> arrayList;
//    private Context context;
//
//    public CoComentAdapter(ArrayList<String> arrayList, Context context) {
//        this.arrayList = arrayList;
//        this.context = context;
//    }

//    private String x;
//    private Context context;
//
//    public CoComentAdapter(String x, Context context) {
//        this.x = x;
//        this.context = context;
//    }



    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cocoment,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CoComentAdapter.CustomViewHolder holder, int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
        StorageReference storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");

//        databaseReference.child("CoComent").orderByChild("ComentUid").equalTo(x).addListenerForSingleValueEvent(new ValueEventListener() {
//        databaseReference.child("Coment").child(arrayList.get(position).getUnique()).child("댓글").child(arrayList.get(position).getComentUid()).child("답댓글").orderByChild("comentUid").equalTo(arrayList.get(position).getComentUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot child: snapshot.getChildren()) {
//                    key = child.getKey();
//
//                    String path = (String) snapshot.child(key).child("profile").getValue().toString();
//
//                    Glide.with(holder.itemView)
//                            .load(path)
//                            .into(holder.iv_cocoment_profile);
//
//                    storageReference.child("profile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            Glide.with(holder.itemView)
//                                    .load(uri)
//                                    .into(holder.iv_cocoment_profile);
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull  Exception e) {
//
//                        }
//                    });
//
//                    String nickName = (String) snapshot.child(key).child("nickName").getValue().toString();
//                    String coment = (String) snapshot.child(key).child("coment").getValue().toString();
//                    String time = (String) snapshot.child(key).child("time").getValue().toString();
//                    String image =(String) snapshot.child(key).child("image").getValue().toString();
//                    holder.tv_cocoment_nickName.setText(nickName);
//                    holder.tv_cocoment_coment.setText(coment);
//                    holder.tv_cocoment_time.setText(time);
//
//                    if (image.equals("x")) {
//                        holder.iv_cocoment_image.setVisibility(View.GONE);
//                    }else{
//                        holder.iv_cocoment_image.setVisibility(View.VISIBLE);
//
//                        storageReference.child("coment").child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                            @Override
//                            public void onSuccess(Uri uri) {
//                                Glide.with(holder.itemView)
//                                        .load(uri)
//                                        .into(holder.iv_cocoment_image);
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull  Exception e) {
//
//                            }
//                        });
//                    }
//
//
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError error) {
//
//            }
//        });
//

//        holder.reCoComentCycler.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(holder.itemView.getContext());
//        holder.reCoComentCycler.setLayoutManager(layoutManager);

        arrayList1 = new ArrayList<>();




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
//        holder.reCoComentCycler.setVisibility(View.VISIBLE);
//        Toast.makeText(context, "x", Toast.LENGTH_SHORT).show();
//        arrayList1.clear();
//        databaseReference.child("Coment").child(arrayList.get(position).getUnique()).child("댓글").child(arrayList.get(position).getComentUid()).child("대댓글").orderByChild("cocomentUid").equalTo(arrayList.get(position).getCocomentUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
//                arrayList1.clear();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    CoCoComent coCoComent = child.getValue(CoCoComent.class);
//                    arrayList1.add(0, coCoComent);
//                    holder.adapter = new CoCocomentAdpater(arrayList1, context);
//                    holder.reCoComentCycler.setAdapter(holder.adapter);
//                    holder.adapter.notifyDataSetChanged();
//                }
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError error) {
//
//            }
//        });
////
//        arrayList1.clear();
//        holder.adapter = new CoCocomentAdpater(arrayList1, context);
//        holder.reCoComentCycler.setAdapter(holder.adapter);
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
//        return 0;

    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_cocoment_nickName;
        TextView tv_cocoment_time;
        TextView tv_cocoment_coment;
        ImageView iv_cocoment_image;
        ImageView iv_cocoment_profile;
        ImageView iv_btn_cocoment_add;
        RecyclerView reCoComentCycler;

        String currentEmail;

        private RecyclerView.Adapter adapter;
        private RecyclerView.LayoutManager layoutManager;

        public CustomViewHolder(@NonNull  View itemView) {
            super(itemView);

            this.tv_cocoment_nickName = itemView.findViewById(R.id.tv_cocoment_nickName);
            this.tv_cocoment_time = itemView.findViewById(R.id.tv_cocoment_time);
            this.tv_cocoment_coment = itemView.findViewById(R.id.tv_cocoment_coment);
            this.iv_btn_cocoment_add = itemView.findViewById(R.id.iv_btn_cocoment_add);
            this.iv_cocoment_image = itemView.findViewById(R.id.iv_cocoment_image);
            this.iv_cocoment_profile = itemView.findViewById(R.id.iv_cocoment_profile);
            this.reCoComentCycler = itemView.findViewById(R.id.reCoComentCycler);

            this.reCoComentCycler.setHasFixedSize(true);
            this.layoutManager = new LinearLayoutManager(itemView.getContext());
            this.reCoComentCycler.setLayoutManager(layoutManager);

            database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
            databaseReference = database.getReference("practice210622");

        }
    }
}
