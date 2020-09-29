package com.amirmohammed.coursesocialmedia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    ImageView imageViewProfile;
    EditText editTextEmail, editTextUsername, editTextPhoneNumber;
    Button buttonSave;

    Uri profilePath = null;
    String profileUrl = null;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        imageViewProfile = findViewById(R.id.profile_iv);
        editTextEmail = findViewById(R.id.profile_et_email);
        editTextUsername = findViewById(R.id.profile_et_username);
        editTextPhoneNumber = findViewById(R.id.profile_et_phone_number);
        buttonSave = findViewById(R.id.profile_btn_save);

        sharedPreferences = getSharedPreferences("userData", MODE_PRIVATE);

        getUserDataFromFirestore();

        imageViewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(ProfileActivity.this);
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDataFromUi();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profilePath = result.getUri();
                uploadProfileImage(profilePath);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private void uploadProfileImage(Uri profilePath) {
        storageReference.child("ProfileImages").child(auth.getUid()).putFile(profilePath)
                .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Profile image updated", Toast.LENGTH_SHORT).show();
                            getProfileUrl();
                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getProfileUrl() {
        storageReference.child("ProfileImages").child(auth.getUid()).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            profileUrl = task.getResult().toString();
                            Glide.with(ProfileActivity.this)
                                    .load(profileUrl).centerCrop().into(imageViewProfile);

                            Map<String, Object> map = new HashMap<>();
                            map.put("profileUrl", profileUrl);

                            firestore.collection("Users")
                                    .document(auth.getUid()).update(map);

                            sharedPreferences.edit().putString("userProfileLink", profileUrl).apply();

                        } else {
                            String errorMessage = task.getException().getMessage();
                            Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void getDataFromUi() {
        String username = editTextUsername.getText().toString();
        String phoneNumber = editTextPhoneNumber.getText().toString();

        if (username.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "Please fill all data", Toast.LENGTH_SHORT).show();
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPhoneNumber(phoneNumber);

        if (profileUrl != null) {
            user.setProfileUrl(profileUrl);
        }

        uploadUserDataToFirestore(user);
    }

    private void uploadUserDataToFirestore(final User user) {
        firestore.collection("Users").document(auth.getUid())
                .set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "User data updated",
                            Toast.LENGTH_SHORT).show();

                    sharedPreferences.edit().putString("username", user.getUsername()).apply();

                } else {
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(ProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getUserDataFromFirestore() {
        firestore.collection("Users").document(auth.getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User user = task.getResult().toObject(User.class);

                    editTextEmail.setText(auth.getCurrentUser().getEmail());

                    if (user == null) return;

                    editTextPhoneNumber.setText(user.getPhoneNumber());
                    editTextUsername.setText(user.getUsername());

                    Glide.with(ProfileActivity.this)
                            .load(user.getProfileUrl())
                            .centerCrop()
                            .placeholder(R.mipmap.ic_launcher)
                            .into(imageViewProfile);
                }
            }
        });
    }

}
