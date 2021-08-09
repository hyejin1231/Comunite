package com.example.practice210622;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainBoard extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Content> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private ImageView ivBtn_mypage,ivBtn_write;
    private String nickName, email, profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_board);

        Intent intent = getIntent();
        nickName = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        profile  = intent.getStringExtra("profileImg");

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 리사이클러뷰 기존성능 강화
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>(); // Content 객체를 담을 Arraylist

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");
//        databaseReference = database.getReference("practice210622").child("Content"); // DB테이블 연결

        ivBtn_mypage = findViewById(R.id.ivBtn_mypage);
        ivBtn_write = findViewById(R.id.ivBtn_write);

        databaseReference.child("Content").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                // 파이어베이스 데이터베이스의 데이터 받아오는 곳
                arrayList.clear(); // 기존 배열리스트가 존재하지 않도록 초기화 (혹시 몰라 하는 것임)
                for (DataSnapshot child : snapshot.getChildren()) {
                    Content content = child.getValue(Content.class); // 만들어뒀던 Content 객체에 데이터를 담음.
                    arrayList.add(content); // 담은 데이터들을 배열리스트에 넣고 리사이클러뷰로 보낼 준비
                }

                adapter.notifyDataSetChanged(); // 리스트 저장 및 새로고침

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // DB를 가져오던 중 에러 발생 시
                Log.e("MainBoard",String.valueOf(error.toException())); // 에러문 출력
            }
        });

        adapter = new MainBoardAdapter(arrayList,this);
        recyclerView.setAdapter(adapter); // 리사이클러뷰에 어뎁터 연결


        // mypage
        ivBtn_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email == null) {
                    Intent intent = new Intent(MainBoard.this, MainActivity.class);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(MainBoard.this, MainActivity2.class);
                    intent.putExtra("name", nickName);
                    intent.putExtra("profileImg", profile);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }

            }
        });

        // 글 작성 페이지
        ivBtn_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email == null) {
                    Intent intent = new Intent(MainBoard.this, WritePage.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(MainBoard.this, WritePage.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }
}