package com.example.practice210622;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContentEditAdapter extends RecyclerView.Adapter<ContentEditAdapter.CustomViewHolder> {

    private ArrayList<Content> arrayList;
    private Context context;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;

    public ContentEditAdapter(ArrayList<Content> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_edit_content,parent,false);
       CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull  ContentEditAdapter.CustomViewHolder holder, int position) {

        holder.tv_edit_title.setText(arrayList.get(position).getTitle());
        holder.tv_edit_count.setText(String.valueOf(arrayList.get(position).getCount()));
        holder.tv_edit_date.setText(arrayList.get(position).getDate());
        holder.tv_edit_nickName.setText(arrayList.get(position).getNickName());
        holder.tv_edit_category.setText(arrayList.get(position).getCategory());

    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView tv_edit_title;
        TextView tv_edit_category;
        TextView tv_edit_count;
        TextView tv_edit_date;
        TextView tv_edit_nickName;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);

            this.tv_edit_title = itemView.findViewById(R.id.tv_edit_title);
            this.tv_edit_count = itemView.findViewById(R.id.tv_edit_count);
            this.tv_edit_category = itemView.findViewById(R.id.tv_edit_category);
            this.tv_edit_nickName = itemView.findViewById(R.id.tv_edit_nickName);
            this.tv_edit_date = itemView.findViewById(R.id.tv_edit_date);

            database = FirebaseDatabase.getInstance(); // 파이어베이스 데이터베이스 연결
            databaseReference = database.getReference("practice210622");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();


                    Intent intent = new Intent(v.getContext(),EditPage.class);
                    intent.putExtra("unique", arrayList.get(position).getUnique());
                    intent.putExtra("title", arrayList.get(position).getTitle());
                    intent.putExtra("contents",arrayList.get(position).getContents());
                    intent.putExtra("count", String.valueOf(arrayList.get(position).getCount()));
                    intent.putExtra("date",arrayList.get(position).getDate());
                    intent.putExtra("category", arrayList.get(position).getCategory());
                    intent.putExtra("image", arrayList.get(position).getImage());
                    intent.putExtra("idToken",arrayList.get(position).getIdToken());

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }


}



















