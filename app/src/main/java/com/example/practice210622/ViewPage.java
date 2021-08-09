package com.example.practice210622;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.content.ContentValues.TAG;

public class ViewPage extends AppCompatActivity {

    private TextView tv_view_title;
    private TextView tv_view_contents;
    private TextView tv_view_category;
    private TextView tv_view_date;
    private TextView tv_view_nickName,tv_view_count;
    Random random = new Random();

    private ImageView iv_view_profile;
    private ImageView iv_view_image;
    private ImageView iv_btn_view_back;
    private Button btn_view_addComent;

    RecyclerView ComentReCycler;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Coment> arrayList;

    String unique, title, contents,count,date,image,category;
    String nickName, profile;
    String key;
    String key2;
    String key3;
    String filename;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    String currentEmail ,idToken, currentProfile,currentNickName;

    long now = System.currentTimeMillis();
    Date today = new Date(now);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("hh:mm");
    SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("hh:mm:SS");
    String currentDate = simpleDateFormat.format(today);
    String currentTime = simpleDateFormat2.format(today);
    String currentTime3 = simpleDateFormat3.format(today);

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
    StorageReference storageReference = storage.getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);

        ComentReCycler = findViewById(R.id.ComentReCycler);
        ComentReCycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ComentReCycler.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        tv_view_title = findViewById(R.id.tv_view_title);
        tv_view_contents = findViewById(R.id.tv_view_contents);
        tv_view_category = findViewById(R.id.tv_view_category);
        tv_view_date = findViewById(R.id.tv_view_date);
        tv_view_nickName = findViewById(R.id.tv_view_nickName);
        iv_view_image = findViewById(R.id.iv_view_image);
        iv_view_profile = findViewById(R.id.iv_view_profile);
        tv_view_count = findViewById(R.id.tv_view_count);
        iv_btn_view_back = findViewById(R.id.iv_btn_view_back);
        btn_view_addComent = findViewById(R.id.btn_view_addComent);

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



        Intent intent = getIntent();
        unique = intent.getExtras().getString("unique");
        title = intent.getExtras().getString("title");
        contents = intent.getExtras().getString("contents");
        count = intent.getExtras().getString("count");
        date = intent.getExtras().getString("date");
        image = intent.getExtras().getString("image");
        category= intent.getExtras().getString("category");

        tv_view_title.setText(title);
        tv_view_contents.setText(contents);
        tv_view_date.setText(date);
        tv_view_category.setText(category);
        tv_view_count.setText(count);


        if (image.equals("x")) {
            iv_view_image.setVisibility(View.GONE);
        }else{
            iv_view_image.setVisibility(View.VISIBLE);

            storageReference.child("content").child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(ViewPage.this)
                            .load(uri)
                            .into(iv_view_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull  Exception e) {

                }
            });
        }

        databaseReference.child("Content").orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }

                nickName = (String) snapshot.child(key).child("nickName").getValue();
                profile = (String) snapshot.child(key).child("profile").getValue();

                tv_view_nickName.setText(nickName);

                Glide.with(ViewPage.this)
                        .load(profile)
                        .into(iv_view_profile);

                storageReference.child("profile").child(profile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(ViewPage.this)
                                .load(uri)
                                .into(iv_view_profile);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("Coment").child(unique).child("댓글").orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Coment coment = child.getValue(Coment.class);
                    arrayList.add(coment);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.e("ViewPage",String.valueOf(error.toException())); // 에러문 출력
            }
        });



        adapter = new ComentAdapter(arrayList,this);
        ComentReCycler.setAdapter(adapter);


       iv_btn_view_back.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent1 = new Intent(ViewPage.this, MainBoard.class);
               startActivity(intent1);
               finish();
           }
       });


        final EditText et=  new EditText(ViewPage.this);

        btn_view_addComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ViewPage.this);
                builder.setTitle("댓글쓰기");
                builder.setMessage("댓글을 입력하세요.");
                builder.setView(et);
                if (et.getParent() != null ){
                    ((ViewGroup)et.getParent()).removeView(et);
                }

                builder.setPositiveButton("등록", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (currentEmail != null) {
                            databaseReference.child("UserAccount").orderByChild("et_email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    for (DataSnapshot child : snapshot.getChildren()){
                                        key2 = child.getKey();
                                    }

                                    idToken = snapshot.child(key2).child("idToken").getValue().toString();
                                    currentProfile = snapshot.child(key2).child("profile").getValue().toString();
                                    currentNickName =snapshot.child(key2).child("nickName").getValue().toString();

                                    String unique1 = "";
                                    for (int i = 0; i < 28; i++) {
                                        unique1 += String.valueOf((char) ((int) (random.nextInt(26)) + 97));
                                    }

                                    filename = currentNickName + title + unique+ currentTime3;
                                    Coment coment = new Coment();
                                    coment.setUnique(unique);
                                    coment.setComent(et.getText().toString());
                                    coment.setImage("x");
                                    coment.setIdToken(idToken);
                                    coment.setProfile(currentProfile);
                                    coment.setDate(currentDate);
                                    coment.setTime(currentTime);
                                    coment.setNickName(currentNickName);
                                    coment.setComentUid(filename);

//                                    databaseReference.child("Coment").child(filename).child("aaa").child(unique1).setValue(coment);
//                                    databaseReference.child("Coment").child(filename).setValue(coment);
                                    databaseReference.child("Coment").child(unique).child("댓글").child(filename).setValue(coment);

                                    databaseReference.child("Content").orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                key3 = child.getKey();
                                            }

                                            count = (String) snapshot.child(key3).child("count").getValue();

                                            Intent intent1 = new Intent(getApplicationContext(), ViewPage.class);
                                            intent1.putExtra("unique", unique);
                                            intent1.putExtra("title", title);
                                            intent1.putExtra("contents",contents);
                                            intent1.putExtra("count", String.valueOf(count));
                                            intent1.putExtra("date", date);
                                            intent1.putExtra("image", image);
                                            intent1.putExtra("category", category);
                                            startActivity(intent1);
                                            finish();


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });


                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else{
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String currentUid = user.getUid();

                            databaseReference.child("UserAccount").orderByChild("idToken").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                                    filename = currentNickName + title + unique+ currentTime3;
                                    Coment coment = new Coment();
                                    coment.setUnique(unique);
                                    coment.setComent(et.getText().toString());
                                    coment.setImage("x");
                                    coment.setIdToken(idToken);
                                    coment.setProfile(currentProfile);
                                    coment.setDate(currentDate);
                                    coment.setTime(currentTime);
                                    coment.setNickName(currentNickName);
                                    coment.setComentUid(filename);


//                                    databaseReference.child("Coment").child(filename).setValue(coment);
                                    databaseReference.child("Coment").child(unique).child("댓글").child(filename).setValue(coment);
//                                    databaseReference.child("Coment").child(filename).child("aaa").child(unique1).setValue(coment);
                                    adapter.notifyDataSetChanged();

                                    databaseReference.child("Content").orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot child : snapshot.getChildren()) {
                                                key3 = child.getKey();
                                            }

                                           count =  (String)snapshot.child(key3).child("count").getValue().toString();

                                            Intent intent1 = new Intent(getApplicationContext(), ViewPage.class);
                                            Toast.makeText(ViewPage.this, title, Toast.LENGTH_SHORT).show();
                                            intent1.putExtra("unique", unique);
                                            intent1.putExtra("title", title);
                                            intent1.putExtra("contents",contents);
                                            intent1.putExtra("count", String.valueOf(count));
                                            intent1.putExtra("date", date);
                                            intent1.putExtra("image", image);
                                            intent1.putExtra("category", category);
                                            startActivity(intent1);
                                            finish();


                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }




                    }
                });

                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ViewPage.this, "등록 취소", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }
        });

        adapter.notifyDataSetChanged();


    }
}