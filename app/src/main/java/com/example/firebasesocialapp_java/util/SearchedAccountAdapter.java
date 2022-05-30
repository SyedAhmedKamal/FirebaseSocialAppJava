package com.example.firebasesocialapp_java.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.model.SearchAccountModel;

import java.util.ArrayList;

public class SearchedAccountAdapter extends RecyclerView.Adapter<SearchedAccountAdapter.MyViewHolderSearch> {

    ArrayList<SearchAccountModel> accountList;
    Context context;
    SearchedUserClickListener listener;

    public SearchedAccountAdapter(ArrayList<SearchAccountModel> accountList, SearchedUserClickListener listener) {
        this.accountList = accountList;
        this.listener = listener;
    }

    public static class MyViewHolderSearch extends RecyclerView.ViewHolder {

        TextView userName;
        ImageView userImage;
        LinearLayout searchLayout;

        public MyViewHolderSearch(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.user_name);
            userImage = itemView.findViewById(R.id.user_profile_image);
            searchLayout = itemView.findViewById(R.id.user_profile_layout);
        }
    }


    @NonNull
    @Override
    public MyViewHolderSearch onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolderSearch(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.searched_account_layout,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderSearch holder, int position) {

        holder.userName.setText(accountList.get(position).getUid());
        Glide.with(holder.itemView).load(accountList.get(position).getImageUrl()).into(holder.userImage);

        holder.searchLayout.setOnClickListener(view -> {

            listener.onUserProfileClicked(position);

        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }
}
