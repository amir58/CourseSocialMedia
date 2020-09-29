package com.amirmohammed.coursesocialmedia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<Post> posts;

    public PostAdapter(List<Post> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new PostHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {
        Post post = posts.get(position);

        holder.textViewUsername.setText(post.getUsername());
        holder.textViewContent.setText(post.getContent());

        Glide.with(holder.itemView.getContext())
                .load(post.getUserProfile())
                .placeholder(R.drawable.ic_avatar)
                .centerCrop()
                .into(holder.imageViewProfile);

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }


    class PostHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile, imageViewLike;
        TextView textViewContent, textViewUsername;
        LinearLayout linearLayoutLike, linearLayoutComment;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLike = itemView.findViewById(R.id.post_iv_like);
            imageViewProfile = itemView.findViewById(R.id.post_iv_user_profile);

            textViewContent = itemView.findViewById(R.id.post_tv_content);
            textViewUsername = itemView.findViewById(R.id.post_tv_username);

            linearLayoutLike = itemView.findViewById(R.id.post_like_layout);
            linearLayoutComment = itemView.findViewById(R.id.post_comment_layout);

        }
    }
}
