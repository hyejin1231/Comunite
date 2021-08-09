package com.example.practice210622;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyContentEdit extends AppCompatActivity {

    private RecyclerView EditContentCyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Content> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_content_edit);

        EditContentCyclerView = findViewById(R.id.EditContentCyclerView);
        EditContentCyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        EditContentCyclerView.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = user.getUid();

        databaseReference.child("Content").orderByChild("idToken").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot child : snapshot.getChildren()){
                    Content content = child.getValue(Content.class);
                    arrayList.add(content);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                Log.e("MyContentEdit",String.valueOf(error.toException())); // 에러문 출력
            }
        });

        adapter = new ContentEditAdapter(arrayList, this);
        EditContentCyclerView.setAdapter(adapter);
    }
}