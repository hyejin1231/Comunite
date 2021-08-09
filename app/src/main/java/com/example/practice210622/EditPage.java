package com.example.practice210622;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class EditPage extends AppCompatActivity {

    private Button btn_editC_edit;
    private EditText tv_editC_title;
    private  EditText tv_editC_contents;
    private  TextView tv_editC_category;
    private  TextView tv_editC_date;
    private  TextView tv_editC_nickName;
    private  TextView tv_editC_count;
    private ImageView iv_btn_editC_back;
    private ImageView iv_editC_profile;
    private ImageView iv_editC_image;
    private  Button btn_editC_Delete;

    String unique, title, contents,count,date,image,category;
    String nickName, profile, idToken;
    String key, key2, key3;
    String kakao, email;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    FirebaseStorage storage = FirebaseStorage.getInstance("gs://practice210622.appspot.com");
    StorageReference storageReference = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        btn_editC_edit = findViewById(R.id.btn_editC_edit);
        btn_editC_Delete = findViewById(R.id.btn_editC_Delete);
        iv_btn_editC_back = findViewById(R.id.iv_btn_editC_back);
        iv_editC_image = findViewById(R.id.iv_editC_image);
        iv_editC_profile = findViewById(R.id.iv_editC_profile);
        tv_editC_title = findViewById(R.id.tv_editC_title);
        tv_editC_contents = findViewById(R.id.tv_editC_contents);
        tv_editC_category = findViewById(R.id.tv_editC_category);
        tv_editC_date = findViewById(R.id.tv_editC_date);
        tv_editC_count = findViewById(R.id.tv_editC_count);
        tv_editC_nickName = findViewById(R.id.tv_editC_nickName);

        database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
        databaseReference = database.getReference("practice210622");

        Intent intent = getIntent();
        unique = intent.getExtras().getString("unique");
        title = intent.getExtras().getString("title");
        contents = intent.getExtras().getString("contents");
        count = intent.getExtras().getString("count");
        date = intent.getExtras().getString("date");
        image = intent.getExtras().getString("image");
        category= intent.getExtras().getString("category");
        idToken =  intent.getExtras().getString("idToken");

        tv_editC_title.setText(title);
        tv_editC_contents.setText(contents);
        tv_editC_date.setText(date);
        tv_editC_category.setText(category);
        tv_editC_count.setText(count);

        if (image.equals("x")) {
            iv_editC_image.setVisibility(View.GONE);
        }else{
            iv_editC_image.setVisibility(View.VISIBLE);

            storageReference.child("content").child(image).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(EditPage.this)
                            .load(uri)
                            .into(iv_editC_image);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

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

                tv_editC_nickName.setText(nickName);

                Glide.with(EditPage.this)
                        .load(profile)
                        .into(iv_editC_profile);

                storageReference.child("profile").child(profile).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(EditPage.this)
                                .load(uri)
                                .into(iv_editC_profile);
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


        databaseReference.child("UserAccount").orderByChild("idToken").equalTo(idToken).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    key = child.getKey();
                }

                kakao = (String) snapshot.child(key).child("kakao").getValue();
                email = (String) snapshot.child(key).child("et_email").getValue();

                iv_btn_editC_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (kakao.equals("x")) {
                            Intent intent1 = new Intent(EditPage.this, MyContentEdit.class);
                            startActivity(intent1);
                            finish();
                        }else {
                            Intent intent1 = new Intent(EditPage.this, MyContentEdit2.class);
                            intent1.putExtra("name", nickName);
                            intent1.putExtra("email", email);
                            intent1.putExtra("profileImg", profile);
                            startActivity(intent1);
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // 수정 버튼
        btn_editC_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String edit_title = tv_editC_title.getText().toString();
                final String edit_contents = tv_editC_contents.getText().toString();

                databaseReference.child("Content").orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            key3 = child.getKey();
                        }

                        snapshot.getRef().child(key3).child("title").setValue(edit_title);
                        snapshot.getRef().child(key3).child("contents").setValue(edit_contents);


                        databaseReference.child("UserAccount").orderByChild("idToken").equalTo(idToken).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    key = child.getKey();
                                }

                                kakao = (String) snapshot.child(key).child("kakao").getValue();
                                email = (String) snapshot.child(key).child("et_email").getValue();

                                        if (kakao.equals("x")) {
                                            Toast.makeText(EditPage.this, "수정 완료", Toast.LENGTH_SHORT).show();
                                            Intent intent1 = new Intent(getApplicationContext(), MyContentEdit.class);
                                            startActivity(intent1);
                                            finish();
                                        }else {
                                            Toast.makeText(EditPage.this, "수정 완료", Toast.LENGTH_SHORT).show();
                                            Intent intent1 = new Intent(EditPage.this, MyContentEdit2.class);
                                            intent1.putExtra("name", nickName);
                                            intent1.putExtra("email", email);
                                            intent1.putExtra("profileImg", profile);
                                            startActivity(intent1);
                                            finish();
                                        }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditPage.this, "수정 실패", Toast.LENGTH_SHORT).show();
                    }
                });


            }
        }); // 수정버튼 end

        // 삭제버튼
        btn_editC_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditPage.this);
                builder.setTitle("삭제하시겠습니까?");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseReference.child("Content").orderByChild("unique").equalTo(unique).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot child : snapshot.getChildren()) {
                                    key2 = child.getKey();
                                }

                                snapshot.getRef().child(key2).removeValue();

                                databaseReference.child("UserAccount").orderByChild("idToken").equalTo(idToken).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull  DataSnapshot snapshot) {
                                        for (DataSnapshot child : snapshot.getChildren()) {
                                            key = child.getKey();
                                        }

                                        kakao = (String) snapshot.child(key).child("kakao").getValue();
                                        email = (String) snapshot.child(key).child("et_email").getValue();

                                                if (kakao.equals("x")) {
                                                    Toast.makeText(getApplicationContext(), "해당 글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Intent intent1 = new Intent(getApplicationContext(), MyContentEdit.class);
                                                    startActivity(intent1);
                                                    finish();
                                                }else {
                                                    Toast.makeText(getApplicationContext(), "해당 글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                                    Intent intent1 = new Intent(EditPage.this, MyContentEdit2.class);
                                                    intent1.putExtra("name", nickName);
                                                    intent1.putExtra("email", email);
                                                    intent1.putExtra("profileImg", profile);
                                                    startActivity(intent1);
                                                    finish();
                                                }

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
                });
                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(EditPage.this, "Cancel", Toast.LENGTH_SHORT).show(); // 실행할 코드
                            }
                        });
                builder.show();
            }
        }); // 삭제버튼 end




    }
}