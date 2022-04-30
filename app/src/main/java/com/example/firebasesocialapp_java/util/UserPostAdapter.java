package com.example.firebasesocialapp_java.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.databinding.ActivityUserPostBinding;
import com.example.firebasesocialapp_java.databinding.UserPostLayoutBinding;
import com.example.firebasesocialapp_java.model.Post;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.MyViewHolder> {

    ArrayList<Post> postList;
    Context context;

    public UserPostAdapter(Context context, ArrayList<Post> postList) {
        this.postList = postList;
        this.context = context;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_author, tv_likes;
        ImageView postImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_author = itemView.findViewById(R.id.author_name);
            tv_likes = itemView.findViewById(R.id.likes_count);
            postImage = itemView.findViewById(R.id.post_image);

        }

        /*UserPostLayoutBinding binding;
        public MyViewHolder(UserPostLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }*/

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        /*return new MyViewHolder(UserPostLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));*/
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.user_post_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tv_author.setText(postList.get(position).getAuthor());
        Glide.with(holder.itemView).load(postList.get(position).getImageUrl()).into(holder.postImage);
        holder.tv_likes.setText(String.valueOf(postList.get(position).getLikes()));
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

}
