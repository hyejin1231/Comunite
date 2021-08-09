package com.example.practice210622;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class ComentAdapter extends RecyclerView.Adapter<ComentAdapter.CustomViewHolder> {

    private ArrayList<Coment> arrayList;
    private Context context;

     ArrayList<CoComent> arrayList1 ;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ArrayList<String> arrayList3;
    private ArrayList<String> arrayList4;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    private String key,key2,key3;
    String currentEmail ,idToken, currentProfile,currentNickName;
    String unique, title, contents,count,date,image,category;

    Random random = new Random();
    long now = System.currentTimeMillis();
    Date today = new Date(now);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm");
    SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("hh:mm:SS");
    String currentDate = simpleDateFormat.format(today);
    String currentTime = simpleDateFormat2.format(today);
    String currentTime3 = simpleDateFormat3.format(today);


    public ComentAdapter(ArrayList<Coment> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_coment,parent,false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  ComentAdapter.CustomViewHolder holder, int position) {
        FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
        StorageReference storageReference = storage.getReference();

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");

        holder.reComentCycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(holder.itemView.getContext());
        holder.reComentCycler.setLayoutManager(layoutManager);
        arrayList1 = new ArrayList<>();
        arrayList3 = new ArrayList<>();
        arrayList4 = new ArrayList<>();

        String path = arrayList.get(position).getProfile();

        Glide.with(holder.itemView)
                .load(path)
                .into(holder.iv_coment_profile);


        storageReference.child("profile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.itemView)
                        .load(uri)
                        .into(holder.iv_coment_profile);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull  Exception e) {

            }
        });

        holder.tv_coment_nickName.setText(arrayList.get(position).getNickName());
        holder.tv_coment_coment.setText(arrayList.get(position).getComent());
        holder.tv_coment_time.setText(arrayList.get(position).getTime());

        String image = arrayList.get(position).getImage();

        if (image.equals("x")) {
            holder.iv_coment_image.setVisibility(View.GONE);
        }else{
            holder.iv_coment_image.setVisibility(View.VISIBLE);

            storageReference.child("coment").child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(holder.itemView)
                            .load(uri)
                            .into(holder.iv_coment_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {

                }
            });
        }    arrayList1.clear();

        databaseReference.child("Coment").child(arrayList.get(position).getUnique()).child("댓글").child(arrayList.get(position).getComentUid()).child("답댓글").orderByChild("comentUid").equalTo(arrayList.get(position).getComentUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                arrayList1.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    CoComent coComent = child.getValue(CoComent.class);
                    arrayList1.add(0, coComent);
                    holder.adapter = new CoComentAdapter(arrayList1, context);
                    holder.reComentCycler.setAdapter(holder.adapter);
                    holder.adapter.notifyDataSetChanged();
                }
//                adapter = new CoComentAdapter(arrayList1, context);
//                holder.reComentCycler.setAdapter(adapter);
//                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });



//        databaseReference.child("Coment").child("CoComent").orderByChild("ComentUid").equalTo(arrayList.get(position).getComentUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
//                arrayList1.clear();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    CoComent coComent = child.getValue(CoComent.class);
//                    arrayList1.add(0,coComent);
//
//                    }
//                adapter = new CoComentAdapter(arrayList1, context);
//                holder.reComentCycler.setAdapter(adapter);
//
//
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError error) {
//
//            }
//        });
//        String isComent = arrayList.get(position).getIsComent();
//
//        if (isComent.equals("o")) {
//            holder.imageView.setVisibility(View.VISIBLE);
//        }


        //arrayList1.clear();
//        databaseReference.child("CoComent").orderByChild("ComentUid").equalTo(arrayList.get(position).getComentUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
////                    arrayList1.clear();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    CoComent coComent = child.getValue(CoComent.class);
//                    arrayList1.add(0,coComent);
//
//                    }
//                adapter = new CoComentAdapter(arrayList1, context);
//                holder.reComentCycler.setAdapter(adapter);
//
//
//                adapter.notifyDataSetChanged();
//
//
//            }
//
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError error) {
//
//            }
//        });
//        databaseReference.child("Coment").child(arrayList.get(position).getUnique()).child("댓글").child(arrayList.get(position).getComentUid()).child("답댓글").orderByChild("comentUid").equalTo(arrayList.get(position).getComentUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//
//            @Override
//            public void onDataChange(@NonNull  DataSnapshot snapshot) {
//                arrayList1.clear();
//                for (DataSnapshot child : snapshot.getChildren()) {
//                    CoComent coComent = child.getValue(CoComent.class);
//                    arrayList1.add(0,coComent);
//
//                    adapter = new CoComentAdapter(arrayList1, context);
//                    holder.reComentCycler.setAdapter(adapter);
//                    adapter.notifyDataSetChanged();
//                }
//
//
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull  DatabaseError error) {
//
//            }
//        });








        }




    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_coment_nickName;
        TextView tv_coment_time;
        TextView tv_coment_coment;
        ImageView iv_coment_image;
        ImageView iv_coment_profile;
        ImageView iv_btn_coment_add;
        ImageView imageView;

        RecyclerView reComentCycler;
        String currentEmail;

        private RecyclerView.Adapter adapter;
        private RecyclerView.LayoutManager layoutManager;
//        private ArrayList<Coment> arrayList1;

        public CustomViewHolder(@NonNull  View itemView) {
            super(itemView);

//            arrayList1 = new ArrayList<>();

            this.tv_coment_nickName = itemView.findViewById(R.id.tv_coment_nickName);
            this.tv_coment_time = itemView.findViewById(R.id.tv_coment_time);
            this.tv_coment_coment = itemView.findViewById(R.id.tv_coment_coment);
            this.iv_btn_coment_add = itemView.findViewById(R.id.iv_btn_coment_add);
            this.iv_coment_image = itemView.findViewById(R.id.iv_coment_image);
            this.iv_coment_profile = itemView.findViewById(R.id.iv_coment_profile);
            this.reComentCycler = itemView.findViewById(R.id.reComentCycler);
            this.imageView = itemView.findViewById(R.id.imageView);

            this.reComentCycler.setHasFixedSize(true);
            this.layoutManager = new LinearLayoutManager(itemView.getContext());
            this.reComentCycler.setLayoutManager(layoutManager);



            database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
            databaseReference = database.getReference("practice210622");

            List<String> keys = new ArrayList<>();
            keys.add("properties.nickname");
            keys.add("properties.profile_image");
            keys.add("kakao_account.email");
            UserManagement.getInstance().me(keys, new MeV2ResponseCallback() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    String message = "failed to get user info. msg=" + errorResult;
                    Logger.d(message);
                }

                @Override
                public void onSessionClosed(ErrorResult errorResult) {

                }

                @Override
                public void onSuccess(MeV2Response response) {
                    //Toast.makeText(ViewPage.this,response.getNickname() , Toast.LENGTH_SHORT).show();
                    Log.e(TAG,"nickname : " + response.getNickname());
                    Log.e(TAG,"email: " + response.getKakaoAccount().getEmail());
                    currentEmail = response.getKakaoAccount().getEmail();
                    //Logger.d("profile image: " + response.getKakaoAccount().getProfileImagePath());
                    //redirectMainActivity();
                }

            });

            iv_btn_coment_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    final List<String> addItems = new ArrayList<>();
                    addItems.add("답댓글");
                    final CharSequence[] items = addItems.toArray(new String[addItems.size()]);
                    final List SelectedItems = new ArrayList();
                    int defaultItem = 0;
                    SelectedItems.add(defaultItem);
                    final EditText et=  new EditText(itemView.getContext());

                    AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                    builder.setTitle("Choose");
                    builder.setSingleChoiceItems(items, defaultItem,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SelectedItems.clear();
                                    SelectedItems.add(which);
                                }
                            });
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int position = getAdapterPosition();
                            String selectFuctioin ="";
                            if (!SelectedItems.isEmpty()) {
                                int index = (int)SelectedItems.get(0);
                                selectFuctioin = addItems.get(index);
                            }

                            if (selectFuctioin.equals("답댓글")) {
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(itemView.getContext());
                                builder1.setTitle("댓글쓰기");
                                builder1.setMessage("댓글을 입력하세요.");
                                builder1.setView(et);
                                if (et.getParent() != null ){
                                    ((ViewGroup)et.getParent()).removeView(et);
                                }
                                builder1.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(currentEmail != null) {

                                            databaseReference.child("UserAccount").orderByChild("et_email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                                    for (DataSnapshot child: snapshot.getChildren()) {
                                                        key = child.getKey();
                                                    }

                                                    idToken = snapshot.child(key).child("idToken").getValue().toString();
                                                    currentProfile = snapshot.child(key).child("profile").getValue().toString();
                                                    currentNickName =snapshot.child(key).child("nickName").getValue().toString();

                                                    String unique1 = "";
                                                    for (int i = 0; i < 28; i++) {
                                                        unique1 += String.valueOf((char) ((int) (random.nextInt(26)) + 97));
                                                    }

                                                    Coment coment = new Coment();
                                                    coment.setUnique(arrayList.get(position).getUnique());
                                                    coment.setComent(et.getText().toString());
                                                    coment.setImage("x");
                                                    coment.setIdToken(idToken);
                                                    coment.setProfile(currentProfile);
                                                    coment.setDate(currentDate);
                                                    coment.setTime(currentTime);
                                                    coment.setNickName(currentNickName);
                                                    coment.setComentUid(arrayList.get(position).getComentUid());
                                                    coment.setCocomentUid(unique1);


                                                    databaseReference.child("Coment").child(arrayList.get(position).getUnique()).child("댓글").child(arrayList.get(position).getComentUid()).child("답댓글").child(unique1).setValue(coment);

                                                    databaseReference.child("Content").orderByChild("unique").equalTo(arrayList.get(position).getUnique()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                                key3 = child.getKey();
                                                            }

                                                            count = (String) snapshot.child(key3).child("count").getValue().toString();
                                                            unique = (String) snapshot.child(key3).child("unique").getValue();
                                                            title = (String) snapshot.child(key3).child("title").getValue();
                                                            contents = (String) snapshot.child(key3).child("contents").getValue();
                                                            date =  (String) snapshot.child(key3).child("date").getValue();
                                                            image = (String) snapshot.child(key3).child("image").getValue();
                                                            category = (String) snapshot.child(key3).child("category").getValue();

                                                            Intent intent1 = new Intent(itemView.getContext(), ViewPage.class);
                                                            intent1.putExtra("unique", unique );
                                                            intent1.putExtra("title", title);
                                                            intent1.putExtra("contents",contents);
                                                            intent1.putExtra("count", count);
                                                            intent1.putExtra("date", date);
                                                            intent1.putExtra("image", image);
                                                            intent1.putExtra("category", category);

                                                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addFlag

                                                            itemView.getContext().startActivity(intent1);


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onCancelled(@NonNull  DatabaseError error) {

                                                }
                                            });
                                        }else {
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            String currentUid = user.getUid();

                                            databaseReference.child("UserAccount").orderByChild("idToken").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                                    for (DataSnapshot child : snapshot.getChildren()){
                                                        key2 = child.getKey();
                                                    }

                                                    String unique1 = "";
                                                    for (int i = 0; i < 28; i++) {
                                                        unique1 += String.valueOf((char) ((int) (random.nextInt(26)) + 97));
                                                    }

                                                    idToken = snapshot.child(key2).child("idToken").getValue().toString();
                                                    currentProfile = snapshot.child(key2).child("profile").getValue().toString();
                                                    currentNickName =snapshot.child(key2).child("nickName").getValue().toString();

                                                    Coment coment = new Coment();
                                                    coment.setUnique(arrayList.get(position).getUnique());
                                                    coment.setComent(et.getText().toString());
                                                    coment.setImage("x");
                                                    coment.setIdToken(idToken);
                                                    coment.setProfile(currentProfile);
                                                    coment.setDate(currentDate);
                                                    coment.setTime(currentTime);
                                                    coment.setNickName(currentNickName);
                                                    coment.setComentUid(arrayList.get(position).getComentUid());
                                                    coment.setCocomentUid(unique1);

                                                    databaseReference.child("Coment").child(arrayList.get(position).getUnique()).child("댓글").child(arrayList.get(position).getComentUid()).child("답댓글").child(unique1).setValue(coment);

                                                    databaseReference.child("Content").orderByChild("unique").equalTo(arrayList.get(position).getUnique()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                                key3 = child.getKey();
                                                            }

                                                            count = (String) snapshot.child(key3).child("count").getValue().toString();
                                                            unique = (String) snapshot.child(key3).child("unique").getValue();
                                                            title = (String) snapshot.child(key3).child("title").getValue();
                                                            contents = (String) snapshot.child(key3).child("contents").getValue();
                                                            date =  (String) snapshot.child(key3).child("date").getValue();
                                                            image = (String) snapshot.child(key3).child("image").getValue();
                                                            category = (String) snapshot.child(key3).child("category").getValue();

                                                            Intent intent1 = new Intent(itemView.getContext(), ViewPage.class);
                                                            intent1.putExtra("unique", unique );
                                                            intent1.putExtra("title", title);
                                                            intent1.putExtra("contents",contents);
                                                            intent1.putExtra("count", String.valueOf(count));
                                                            intent1.putExtra("date", date);
                                                            intent1.putExtra("image", image);
                                                            intent1.putExtra("category", category);

                                                            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    intent.addFlag

                                                            itemView.getContext().startActivity(intent1);


                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });

                                                }

                                                @Override
                                                public void onCancelled(@NonNull  DatabaseError error) {

                                                }
                                            });

                                        }
                                    }
                                });
                                builder1.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(itemView.getContext(), "등록 취소", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                builder1.show();
                            }


                        }
                    });

                    builder.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(itemView.getContext(), "취소누름", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.show();
                }
            });
        }
    }
}



























