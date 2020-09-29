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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostHolder> {

    private List<Post> posts;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

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
    public void onBindViewHolder(@NonNull final PostHolder holder, int position) {
        final Post post = posts.get(position);

        holder.textViewUsername.setText(post.getUsername());
        holder.textViewContent.setText(post.getContent());

        Glide.with(holder.itemView.getContext())
                .load(post.getUserProfile())
                .placeholder(R.drawable.ic_avatar)
                .centerCrop()
                .into(holder.imageViewProfile);

        holder.linearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firestore.collection("posts")
                        .document(post.getPostId())
                        .collection("likes")
                        .document(auth.getUid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                Likes likes = task.getResult().toObject(Likes.class);

                                if (likes == null) {

                                    firestore.collection("posts")
                                            .document(post.getPostId())
                                            .collection("likes")
                                            .document(auth.getUid())
                                            .set(new Likes(auth.getUid()));
                                    holder.imageViewLike.setImageResource(R.drawable.ic_like_pressed);

                                } else {

                                    firestore.collection("posts")
                                            .document(post.getPostId())
                                            .collection("likes")
                                            .document(auth.getUid())
                                            .delete();

                                    holder.imageViewLike.setImageResource(R.drawable.ic_like);
                                }

                            }
                        });

//                firestore.collection("posts")
//                        .document(post.getPostId())
//                        .collection("likes")
//                        .whereEqualTo("name", auth.getUid())
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//
//                            }
//                        });


            }
        });


        firestore.collection("posts")
                .document(post.getPostId())
                .collection("likes")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int likes = Objects.requireNonNull(task.getResult()).size();
                            String message = "Like (" + likes + ")";
                            holder.textViewLike.setText(message);
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    class PostHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProfile, imageViewLike;
        TextView textViewContent, textViewUsername, textViewLike;
        LinearLayout linearLayoutLike, linearLayoutComment;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageViewLike = itemView.findViewById(R.id.post_iv_like);
            imageViewProfile = itemView.findViewById(R.id.post_iv_user_profile);

            textViewContent = itemView.findViewById(R.id.post_tv_content);
            textViewUsername = itemView.findViewById(R.id.post_tv_username);
            textViewLike = itemView.findViewById(R.id.post_tv_like);

            linearLayoutLike = itemView.findViewById(R.id.post_like_layout);
            linearLayoutComment = itemView.findViewById(R.id.post_comment_layout);


        }
    }
}
