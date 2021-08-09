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
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.helper.log.Logger;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyContentEdit2 extends AppCompatActivity {

    private RecyclerView EditContentCyclerView2;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Content> arrayList;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String nickName, email, profile;
    ImageView iv_eidt2_back;
    String key2;
    String idToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_content_edit2);

        EditContentCyclerView2 = findViewById(R.id.EditContentCyclerView2);
        EditContentCyclerView2.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        EditContentCyclerView2.setLayoutManager(layoutManager);
        arrayList = new ArrayList<>();

        iv_eidt2_back = findViewById(R.id.iv_eidt2_back);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");

        Intent intent = getIntent();
        nickName = intent.getStringExtra("name");
        email = intent.getStringExtra("email");
        profile  = intent.getStringExtra("profileImg");

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
                email = response.getKakaoAccount().getEmail();
                //Logger.d("profile image: " + response.getKakaoAccount().getProfileImagePath());
                //redirectMainActivity();
            }

        });



        databaseReference.child("UserAccount").orderByChild("et_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()){
                    key2 = child.getKey();
                }
                idToken = snapshot.child(key2).child("idToken").getValue().toString();

                databaseReference.child("Content").orderByChild("idToken").equalTo(idToken).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyContentEdit2.this, "!", Toast.LENGTH_SHORT).show();
                        Log.e("MyContentEdit2",String.valueOf(error.toException())); // 에러문 출력
                    }
                });


                adapter = new ContentEditAdapter(arrayList, MyContentEdit2.this);
                EditContentCyclerView2.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




        iv_eidt2_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MyContentEdit2.this, MainActivity2.class);
                intent1.putExtra("name", nickName);
                intent1.putExtra("email", email);
                intent1.putExtra("profileImg", profile);
                startActivity(intent1);
                finish();
            }
        });
    }
}