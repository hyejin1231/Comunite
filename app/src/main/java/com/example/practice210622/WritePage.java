package com.example.practice210622;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.google.firebase.storage.UploadTask;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WritePage extends AppCompatActivity {

    private TextView tv_write_date;
    private EditText et_write_title, et_write_content;
    private ImageView iv_addPhoto;
    private Button btn_addPhoto,btn_write_complete;
    private Spinner spinner_write_category;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    String filename;

    Uri uri;
    Bitmap img;
    private Uri filePath;

    String idToken;
   private String email;
   String title, category, contents, image, nickName, profile;
    String key, key2;

    long now = System.currentTimeMillis();
    Date today = new Date(now);
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String date = simpleDateFormat.format(today);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_page);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");

        Intent intent = getIntent();
        email = intent.getStringExtra("email");

        Toast.makeText(this, email, Toast.LENGTH_SHORT).show();

        tv_write_date = findViewById(R.id.tv_write_date);
        et_write_title =findViewById(R.id.et_write_title);
        et_write_content = findViewById(R.id.et_write_content);
        iv_addPhoto = findViewById(R.id.iv_addPhoto);
        btn_write_complete = findViewById(R.id.btn_write_complete);
        btn_addPhoto = findViewById(R.id.btn_addPhoto);
        spinner_write_category = findViewById(R.id.spinner_write_category);

        tv_write_date.setText(date);

        // 스피너
        final String[] categorySpinner = {"카테고리", "자유게시판"};
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, categorySpinner);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_write_category.setAdapter(arrayAdapter);

        spinner_write_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = categorySpinner[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // 사진 추가
        btn_addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);
            }
        });


        btn_write_complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = et_write_title.getText().toString();
                contents = et_write_content.getText().toString();
                int count = 0;

                SimpleDateFormat formatter1 = new SimpleDateFormat("yyyyMMHHmmssSSS");
                Date now1 = new Date();
                filename = formatter1.format(now1);

                if (filePath != null) {

                    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
                    Date now = new Date();
                    image = formatter.format(now) + ".png";

                    final FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageReference = storage.getReferenceFromUrl("gs://practice210622.appspot.com").child("content").child(image);

                    storageReference.putFile(filePath)
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    Toast.makeText(WritePage.this, "업로드 성공", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull  Exception e) {
                                    Toast.makeText(WritePage.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                                }
                            });

                }else {
                    image = "x";
                }

                if (email == null) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final String currentUid = user.getUid();

                    databaseReference.child("UserAccount").orderByChild("idToken").equalTo(currentUid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()){
                                key = child.getKey();
                            }

                            nickName = snapshot.child(key).child("nickName").getValue().toString();
                            profile = snapshot.child(key).child("profile").getValue().toString();
                            //idToken = snapshot.child(key2).child("idToken").getValue().toString();

                            Content content = new Content();
                            content.setTitle(title);
                            content.setContents(contents);
                            content.setCategory(category);
                            content.setCount(count);
                            content.setDate(date);
                            content.setNickName(nickName);
                            content.setProfile(profile);
                            content.setImage(image);
                            content.setIdToken(currentUid);

                            databaseReference.child("Content").child(filename).setValue(content);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(WritePage.this, "등록 성공", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(WritePage.this, MainBoard.class);
                    startActivity(intent1);
                    finish();


                }else{
                    databaseReference.child("UserAccount").orderByChild("et_email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot child : snapshot.getChildren()){
                                key2 = child.getKey();
                            }

                            nickName = snapshot.child(key2).child("nickName").getValue().toString();
                            profile = snapshot.child(key2).child("profile").getValue().toString();
                            idToken = snapshot.child(key2).child("idToken").getValue().toString();

                            Content content = new Content();
                            content.setTitle(title);
                            content.setContents(contents);
                            content.setCategory(category);
                            content.setCount(count);
                            content.setDate(date);
                            content.setNickName(nickName);
                            content.setProfile(profile);
                            content.setImage(image);
                            content.setUnique(filename);
                            content.setIdToken(idToken);

                            databaseReference.child("Content").child(filename).setValue(content);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(WritePage.this, "등록 성공", Toast.LENGTH_SHORT).show();
                    Intent intent1 = new Intent(WritePage.this, MainBoard.class);
                    intent1.putExtra("email", email);
                    intent1.putExtra("name", nickName);
                    intent1.putExtra("profileImg", profile);
                    startActivity(intent1);
                    finish();

                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                try{
                    filePath = data.getData();
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    img = BitmapFactory.decodeStream(in);
                    in.close();
                    iv_addPhoto.setImageBitmap(img);
                    iv_addPhoto.setVisibility(View.VISIBLE);

                    uri = data.getData();

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
















