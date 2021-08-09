package com.example.practice210622;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Edit_Mypage extends AppCompatActivity {

    ImageView iv_editProfile;
    Button btn_setting, btn_default, btn_editOk;
    TextView tv_editEmail;
    EditText et_editNickName;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String currentEmail = user.getEmail();

    private Uri ImagePath;
    Uri uri;
    Bitmap bitmap;
    String ImageName;
    String key,key2;

    private ArrayList<String> arrayList;
    boolean defaultornot = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_mypage);

        iv_editProfile = findViewById(R.id.iv_editProfile);
        btn_default = findViewById(R.id.btn_default);
        btn_editOk = findViewById(R.id.btn_editOk);
        btn_setting = findViewById(R.id.btn_setting);
        tv_editEmail = findViewById(R.id.tv_editEmail);
        et_editNickName = findViewById(R.id.et_editNickName);

        arrayList = new ArrayList<String>();

        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference("practice210622");

        databaseReference.child("UserAccount").orderByChild("et_email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }
                FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
                StorageReference storageReference = storage.getReference();

                String path = (String) snapshot.child(key).child("profile").getValue();

                tv_editEmail.setText(snapshot.child(key).child("et_email").getValue().toString());
                et_editNickName.setText(snapshot.child(key).child("nickName").getValue().toString());

                if (path.equals("default")) {
                    Glide.with(Edit_Mypage.this)
                            .load(R.drawable.user)
                            .into(iv_editProfile);
                } else if (path.substring(0, 5).equals("https")) {

                    Glide.with(Edit_Mypage.this)
                            .load(user.getPhotoUrl())
                            .into(iv_editProfile);
                } else {
                    storageReference.child("profile").child(path).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(Edit_Mypage.this)
                                    .load(uri)
                                    .into(iv_editProfile);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(Edit_Mypage.this, "실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });


        // 프로필 사진 설정
        btn_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent,1);

                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMHH_mmss");
                Date now = new Date();
                ImageName = user.getEmail() + formatter.format(now) + ".png";
            }
        });

        // 프로필 사진 기본으로 설정
        btn_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_editProfile.setImageResource(R.drawable.user);

                ImageName = "default";
                defaultornot = true;

            }
        });

        databaseReference.child("UserAccount").equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                arrayList.clear();
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    UserAccount userAccount = snapshot1.getValue(UserAccount.class);
                    arrayList.add(userAccount.getNickName());
                }
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {

            }
        });



        // 수정 완료 버튼
        btn_editOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nickName = et_editNickName.getText().toString();

                if (arrayList.contains(nickName) == true) {
                    Toast.makeText(Edit_Mypage.this, "중복된 닉네임입니다.", Toast.LENGTH_SHORT).show();
                }else{

                    databaseReference.child("UserAccount").orderByChild("et_email").equalTo(currentEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull  DataSnapshot snapshot) {
                            for (DataSnapshot child: snapshot.getChildren()) {
                                key2 = child.getKey();
                            }
                            if (ImagePath != null){
                                final FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference storageReference = storage.getReferenceFromUrl("gs://practice210622.appspot.com").child("profile").child(ImageName);
                                storageReference.putFile(ImagePath)
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                if (defaultornot == true) {
                                                    Toast.makeText(Edit_Mypage.this, "수정완료", Toast.LENGTH_SHORT).show();
                                                    databaseReference.child("UserAccount").child(key2).child("nickName").setValue(nickName);
                                                    databaseReference.child("UserAccount").child(key2).child("profile").setValue("default");

                                                    String pwd = (String) snapshot.child(key2).child("et_pwd").getValue();
                                                    if (pwd.isEmpty()) {
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                                    } else {
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                    }

                                                }else {
                                                    Toast.makeText(Edit_Mypage.this, "수정완료", Toast.LENGTH_SHORT).show();
                                                    databaseReference.child("UserAccount").child(key2).child("nickName").setValue(nickName);
                                                    databaseReference.child("UserAccount").child(key2).child("profile").setValue(ImageName);

                                                    String pwd = (String) snapshot.child(key2).child("et_pwd").getValue();
                                                    if (pwd.isEmpty()) {
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                                        startActivity(intent);
                                                    } else {
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull  Exception e) {
                                        Toast.makeText(Edit_Mypage.this, "업로드 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }else{
                                if (defaultornot == true) {
                                    Toast.makeText(Edit_Mypage.this, "수정완료", Toast.LENGTH_SHORT).show();
                                    databaseReference.child("UserAccount").child(key2).child("nickName").setValue(nickName);
                                    databaseReference.child("UserAccount").child(key2).child("profile").setValue("default");

                                    String pwd = (String) snapshot.child(key2).child("et_pwd").getValue();
                                    if (pwd.isEmpty()) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    }
                                }else{
                                    Toast.makeText(Edit_Mypage.this, "수정완료", Toast.LENGTH_SHORT).show();
                                    databaseReference.child("UserAccount").child(key2).child("nickName").setValue(nickName);

                                    String pwd = (String) snapshot.child(key2).child("et_pwd").getValue();
                                    if (pwd.isEmpty()) {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                                    } else {
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    }
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull  DatabaseError error) {

                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        ImagePath = null;
        if (requestCode == 1 ) {
            if(resultCode == RESULT_OK){
                try {
                    ImagePath = data.getData();

                    InputStream in2 = getContentResolver().openInputStream(ImagePath);
                    bitmap = BitmapFactory.decodeStream(in2);
                    in2.close();
                    iv_editProfile.setImageBitmap(bitmap);
                    uri = data.getData();


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }
}