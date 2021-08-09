package com.example.practice210622;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.LogoutResponseCallback;

// 카카오로 로그인 했을 때
public class MainActivity2 extends AppCompatActivity {

    private String nickName, email, profile;
//    Button btn_edit;

    ImageView iv_myContent;
    private ImageView iv_btn_mypage2_back;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        Intent intent = getIntent();
        nickName = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        profile  = intent.getStringExtra("profileImg");

        TextView tv_nickName = findViewById(R.id.tv_nickName);
        TextView tv_email = findViewById(R.id.tv_email);
        ImageView iv_profile = findViewById(R.id.iv_profile);
        iv_profile.setBackground(new ShapeDrawable(new OvalShape())); // 이미지 둥글게 만들기
        iv_profile.setClipToOutline(true);// 이미지 둥글게 만들기
        iv_myContent = findViewById(R.id.iv_myContent);
        iv_btn_mypage2_back= findViewById(R.id.iv_btn_mypage2_back);

//        btn_edit = findViewById(R.id.btn_edit);

        tv_nickName.setText(nickName);
        tv_email.setText(email);

        //프로필 이미지 set
        Glide.with(this).load(profile).into(iv_profile);

        //카카오 로그아웃
        findViewById(R.id.btn_kakaoLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserManagement.getInstance().requestLogout(new LogoutResponseCallback() {
                    @Override
                    public void onCompleteLogout() {
                        //로그아웃 성공시 수행하는 지점
                        Intent intent = new Intent(MainActivity2.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); //현재 액티비티 종료
                    }
                });
            }
        });


        iv_myContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MyContentEdit2.class);
                intent.putExtra("name", nickName);
                intent.putExtra("email", email);
                intent.putExtra("profileImg", profile);

                startActivity(intent);
                finish();
            }
        });

        iv_btn_mypage2_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainBoard.class);
                intent.putExtra("name", nickName);
                intent.putExtra("email", email);
                intent.putExtra("profileImg", profile);

                startActivity(intent);
                finish();
            }
        });

//        // 수정 버튼
//        btn_edit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent1 = new Intent(MainActivity2.this, Edit_Mypage2.class);
//                intent1.putExtra("email",email);
//                startActivity(intent1);
//            }
//        });
    }
}