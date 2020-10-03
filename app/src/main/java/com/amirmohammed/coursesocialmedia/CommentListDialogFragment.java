package com.amirmohammed.coursesocialmedia;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * <p>A fragment that shows a list of items as a modal bottom sheet.</p>
 * <p>You can show this modal bottom sheet from your activity like this:</p>
 * <pre>
 *     CommentListDialogFragment.newInstance(30).show(getSupportFragmentManager(), "dialog");
 * </pre>
 */

public class CommentListDialogFragment extends BottomSheetDialogFragment {

    // TODO: Customize parameter argument names
    private static final String ARG_POST_ID = "postId";
//    private Listener mListener;

    private List<Comment> comments = new ArrayList<>();
    private EditText editTextComment;
    private Button buttonComment;
    private SharedPreferences myShared;
    private CommentAdapter commentAdapter;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private FirebaseAuth auth = FirebaseAuth.getInstance();

    // TODO: Customize parameters
    static CommentListDialogFragment newInstance(String postId) {
        final CommentListDialogFragment fragment = new CommentListDialogFragment();
        final Bundle args = new Bundle();
        args.putString(ARG_POST_ID, postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_comment_list_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        RecyclerView recyclerView = view.findViewById(R.id.comment_dialog_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(true);
        commentAdapter = new CommentAdapter(comments);
        recyclerView.setAdapter(commentAdapter);

        myShared = requireContext().getSharedPreferences("userData", MODE_PRIVATE);

        editTextComment = view.findViewById(R.id.comment_dialog_et_comment);
        buttonComment = view.findViewById(R.id.comment_dialog_btn_comment);

        getCommentsFromCloud();
        getCommentFromUi();


    }

    private void getCommentFromUi() {
        buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String comment = editTextComment.getText().toString();

                if (comment.isEmpty()) {
                    Toast.makeText(requireContext(), "Please write comment", Toast.LENGTH_SHORT).show();
                    return;
                }

                pushCommentToPostDataOnCloudFirestore(comment);
            }
        });
    }

    private void pushCommentToPostDataOnCloudFirestore(String commentContent) {
        String commentId = String.valueOf(System.currentTimeMillis());
        String postId = getArguments().getString(ARG_POST_ID);

        Comment comment = new Comment();
        comment.setContent(commentContent);
        comment.setId(commentId);
        comment.setUid(auth.getCurrentUser().getUid());
        comment.setUsername(myShared.getString("username", "Guest"));
        comment.setProfileUrl(myShared.getString("userProfileLink", ""));

        firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .document(commentId)
                .set(comment)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(requireContext()
                                    , "Comment posted", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(requireContext(), errorMessage
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void getCommentsFromCloud() {
        String postId = getArguments().getString(ARG_POST_ID);

        firestore.collection("posts")
                .document(postId)
                .collection("comments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                        comments.clear();

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Comment comment = snapshot.toObject(Comment.class);
                            Log.i("AMIRMOHAMMED", "onEvent: " + comment.toString());
                            comments.add(comment);
                        }
                        Toast.makeText(requireContext(), "Size : "  +comments.size()
                                , Toast.LENGTH_SHORT).show();

                        commentAdapter.notifyDataSetChanged();
                    }
                });

    }
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        final Fragment parent = getParentFragment();
//        if (parent != null) {
//            mListener = (Listener) parent;
//        } else {
//            mListener = (Listener) context;
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        mListener = null;
//        super.onDetach();
//    }
//
//
//    public interface Listener {
//        void onCommentClicked(int position);
//    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUsername, textViewCommentContent;
        ImageView imageViewProfile;

        ViewHolder(View itemView) {
            // TODO: Customize the item layout
            super(itemView);
            textViewCommentContent = itemView.findViewById(R.id.item_comment_tv_content);
            textViewUsername = itemView.findViewById(R.id.item_comment_tv_username);
            imageViewProfile = itemView.findViewById(R.id.item_comment_iv_profile);
        }

    }

    private class CommentAdapter extends RecyclerView.Adapter<ViewHolder> {

        private List<Comment> comments;

        CommentAdapter(List<Comment> comments) {
            this.comments = comments;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_comment_list_dialog_item, parent, false));
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            Comment comment = comments.get(position);

            holder.textViewUsername.setText(comment.getUsername());
            holder.textViewCommentContent.setText(comment.getContent());

            Glide.with(requireContext()).load(comment.getProfileUrl())
                    .centerCrop().placeholder(R.drawable.ic_avatar)
                    .into(holder.imageViewProfile);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

    }

}
