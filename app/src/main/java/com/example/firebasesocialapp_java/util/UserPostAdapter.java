package com.example.firebasesocialapp_java.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.firebasesocialapp_java.R;
import com.example.firebasesocialapp_java.model.Post;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.MyViewHolder> {

    ArrayList<Post> postList;
    Context context;
    ItemClickInterface listener;

    public UserPostAdapter(Context context, ArrayList<Post> postList, ItemClickInterface listener) {
        this.postList = postList;
        this.context = context;
        this.listener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_author, tv_likes;
        ImageView postImage;
        //ExoPlayer exoPlayer;
        VideoView videoView;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_author = itemView.findViewById(R.id.author_name);
            tv_likes = itemView.findViewById(R.id.likes_count);
            postImage = itemView.findViewById(R.id.post_image);
            //exoPlayer = itemView.findViewById(R.id.post_video);
            videoView = itemView.findViewById(R.id.post_video);
            cardView = itemView.findViewById(R.id.card_layout);

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
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_author.setText(postList.get(position).getAuthor());
        String url = postList.get(position).getImageUrl();
        if (url.matches("(.*)mp4(.*)")) {
            holder.videoView.setVisibility(View.VISIBLE);
            // url contain video
            Uri uri = Uri.parse(url);

            //TODO("Error persists!")
            holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    holder.videoView.requestFocus();
                    MediaController mediaController = new MediaController(context);
                    mediaController.setAnchorView(holder.videoView);

                    holder.videoView.setMediaController(mediaController);
                    holder.videoView.start();


                }
            });
            holder.videoView.setVideoURI(uri);

        } else {
            // url contain image only
            holder.postImage.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView).load(postList.get(position).getImageUrl()).into(holder.postImage);
        }
        holder.tv_likes.setText(String.valueOf(postList.get(position).getLikes()));

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onItemDelete(position);
                return false;
            }
        });

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemUpdate(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }


}
