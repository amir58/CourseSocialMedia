package com.amirmohammed.coursesocialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    EditText editTextPost;
    Button buttonAddPost;
    RecyclerView recyclerView;
    SharedPreferences myShared;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    List<Post> posts = new ArrayList<>();
    PostAdapter postAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPost = findViewById(R.id.main_et_add_new_post);
        buttonAddPost = findViewById(R.id.main_btn_add_post);
        recyclerView = findViewById(R.id.main_rv_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        myShared = getSharedPreferences("userData", MODE_PRIVATE);

        buttonAddPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromUi();
            }
        });

        postAdapter = new PostAdapter(posts, commentI);
        recyclerView.setAdapter(postAdapter);

        getPostsFromCloud();

//        getUserData();
    }

    CommentI commentI = new CommentI() {
        @Override
        public void onCommentLayoutClick(String postId) {
            CommentListDialogFragment.newInstance(postId)
                    .show(getSupportFragmentManager(), "CommentDialog");
        }
    };

    private void getDataFromUi() {
        String content = editTextPost.getText().toString();

        if (content.isEmpty()) {
            Toast.makeText(this, "Please write post content", Toast.LENGTH_SHORT).show();
            return;
        }

        Post post = new Post();
        post.setContent(content);
        post.setUsername(myShared.getString("username", "Guest"));
        post.setUserProfile(myShared.getString("userProfileLink", ""));
        post.setUserId(FirebaseAuth.getInstance().getUid());
        post.setPostId(String.valueOf(System.currentTimeMillis()));

        pushPostToDatabase(post);
    }

    private void pushPostToDatabase(Post post) {
        firestore.collection("posts")
                .document(post.getPostId())
                .set(post)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                            editTextPost.setText("");

                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(MainActivity.this, "Post failed : " + errorMessage, Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_menu_profile:
                startActivity(new Intent(MainActivity.this,
                        ProfileActivity.class));
                break;

            case R.id.main_menu_logout:
                FirebaseAuth.getInstance().signOut();
                finish();
                startActivity(new Intent(MainActivity.this,
                        SignInActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    private void getPostsFromCloud() {
        firestore.collection("posts")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        posts.clear();

                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                            Post post = snapshot.toObject(Post.class);
                            posts.add(post);
                        }

                        if (!posts.isEmpty()) postAdapter.notifyDataSetChanged();

                    }
                });
    }

    private void getUserData() {
        String uid = FirebaseAuth.getInstance().getUid();

        if (uid == null) {
            return;
        }

        firestore.collection("users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = Objects.requireNonNull(task.getResult()).toObject(User.class);

                            if (user == null) {
                                return;
                            }

                            myShared.edit().putString("username", user.getUsername()).apply();
                            myShared.edit().putString("userProfileLink", user.getProfileUrl()).apply();
                        }
                    }
                });

    }


}
