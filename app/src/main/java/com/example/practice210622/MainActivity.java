package com.example.practice210622;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

// 그냥 회원가입 후 로그인했을 때
public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
     String key;
     TextView tv_nickName,tv_email;
     Button btn_edit;
     ImageView iv_myContent;
     private ImageView iv_btn_mypage_back;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연동
        databaseReference = database.getReference("practice210622"); // DB 테이블 연동

         tv_nickName = findViewById(R.id.tv_nickName);
         tv_email = findViewById(R.id.tv_email);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setBackground(new ShapeDrawable(new OvalShape())); // 이미지 둥글게 만들기
        iv_profile.setClipToOutline(true);// 이미지 둥글게 만들기
        Button btn_logout = findViewById(R.id.btn_logout);
        btn_edit = findViewById(R.id.btn_edit);
        iv_myContent = findViewById(R.id.iv_myContent);
        iv_btn_mypage_back = findViewById(R.id.iv_btn_mypage_back);


//gs://practice210622.appspot.com/

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();
       // tv_email.setText(currentUid);

        databaseReference.child("UserAccount").orderByChild("idToken").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()){
                    key = child.getKey();
                }

                FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
                StorageReference storageReference = storage.getReference();

                String path = (String) snapshot.child(key).child("profile").getValue();

                if (path.equals("default")) {
                    Glide.with(MainActivity.this)
                            .load(R.drawable.user)
                            .into(iv_profile);
                }
                else if(path.substring(0,5).equals("https")) {

                    Glide.with(MainActivity.this)
                            .load(user.getPhotoUrl())
                            .into(iv_profile);
                }

                else {
                    storageReference.child("profile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(MainActivity.this)
                                    .load(uri)
                                    .into(iv_profile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(MainActivity.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }




                tv_nickName.setText(snapshot.child(key).child("nickName").getValue().toString());
                tv_email.setText(snapshot.child(key).child("et_email").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });

        // 자신이 작성한 글 목록 보기
        iv_myContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyContentEdit.class);
                startActivity(intent);
            }
        });



        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃
                mFirebaseAuth.signOut();

                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Edit_Mypage.class);
                startActivity(intent);
            }
        });

        iv_btn_mypage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainBoard.class);
                startActivity(intent);
                finish();
            }
        });



        // 탈퇴처리
        //mFirebaseAuth.getCurrentUser().delete();
    }
}