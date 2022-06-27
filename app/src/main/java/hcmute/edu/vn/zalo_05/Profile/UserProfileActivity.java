package hcmute.edu.vn.zalo_05.Profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.LoadingDialogService;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
import hcmute.edu.vn.zalo_05.Utilities.PreferenceManager;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = UserProfileActivity.class.getName();

//    private DocumentReference documentReference;
//    private String storagePath;
//    private LoadingDialogService loadingDialogService;

    private CircleImageView userImage;
    private TextView textZaloNameCover;
    private TextView textZaloName;
    private TextView textUserName;
    private TextView textGender;
    private TextView textBirthDate;
    private TextView textNumberPhone;
    private TextView backTextView;

    private User currentUser;

    private StorageReference storageReference;
    private Uri imageUri;

    private TextView pickImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

//        this.loadingDialogService = LoadingDialogService.getInstance();
        map();
        init();

//        this.documentReference = FirebaseFirestore.getInstance().collection(Constants.KEY_COLLECTION_USERS).document(currentUser.getNumberPhone());
//        this.storageReference = FirebaseStorage.getInstance().getReference();
//        this.storagePath = currentUser.getNumberPhone() + "/Media/Profile_Image/profile";
//
//        this.userImage = findViewById(R.id.activity_user_profile_user_image);
//        this.pickImage = findViewById(R.id.pick_image);
//        this.pickImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(checkStoragePermission()) {
//                    pickImage();
//                }
//            }
//        });
    }
//    load data current user to element of view when create view
    private void init() {
        currentUser = UserService.getInstance(this).getCurrentUser();

        if(currentUser.getAvatarImageUrl() != null && currentUser.getAvatarImageUrl() != "") {
            Picasso.get().load(currentUser.getAvatarImageUrl()).into(userImage);
        }
        textZaloNameCover.setText(currentUser.getZaloName());
        if(currentUser.getUserName() != null && currentUser.getUserName() != "") {
            textUserName.setText(currentUser.getUserName());
        } else {
            textUserName.setText("Tạo user name");
        }

        textZaloName.setText(currentUser.getZaloName());
        textGender.setText(currentUser.getGender());
        textBirthDate.setText(currentUser.getBirthdate());
        textNumberPhone.setText(currentUser.getNumberPhone());
        backTextView.setOnClickListener(v -> onBackPressed());
    }

//    map each element in the view to objects
    private void map() {
        userImage = findViewById(R.id.activity_user_profile_user_image);
        textZaloNameCover = findViewById(R.id.activity_user_profile_text_name);
        textUserName = findViewById(R.id.activity_user_profile_text_user_name);
        textZaloName = findViewById(R.id.activity_user_profile_text_zalo_name);
        textGender = findViewById(R.id.activity_user_profile_text_gender);
        textBirthDate = findViewById(R.id.activity_user_profile_text_birthdate);
        textNumberPhone = findViewById(R.id.activity_user_profile_text_number_phone);
        backTextView = findViewById(R.id.activity_user_profile_back_text_view);
    }

//    private boolean checkStoragePermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//                return true;
//            }
//            else {
//                requestStoragePermission();
//                return false;
//            }
//        }
//        return false;
//    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void requestStoragePermission() {
//        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//        requestPermissions(permissions, 100);
//
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case 100:
//                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    pickImage();
//                } else {
//                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
//                }
//                break;
//        }
//    }
//
//    private void uploadData() {
//        loadingDialogService.show(UserProfileActivity.this);
//
//        storageReference.child(storagePath).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//            @Override
//            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                Task<Uri> task = taskSnapshot.getStorage().getDownloadUrl();
//                task.addOnCompleteListener(new OnCompleteListener<Uri>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Uri> task) {
//                        String url = task.getResult().toString();
//                        String oldUrl = currentUser.getAvatarImageUrl();
//                        currentUser.setAvatarImageUrl(url);
//                        documentReference.set(currentUser).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if(task.isSuccessful()) {
//                                    // After upload avatar, update uploaded-url to currentUser object in preferenceManager
//                                    Gson gson = new Gson();
//                                    String objecToJson = gson.toJson(currentUser);
//                                    preferenceManager.putString(Constants.KEY_CURRENT_USER, objecToJson);
//
////                                    if(!oldUrl.isEmpty()) {
////                                        storageReference.child(storagePath).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
////                                            @Override
////                                            public void onSuccess(Void unused) {
////                                                Log.d(TAG, "Deleted old avatar image");
////                                            }
////                                        }).addOnFailureListener(new OnFailureListener() {
////                                            @Override
////                                            public void onFailure(@NonNull Exception e) {
////                                                Log.d(TAG, "Failed to delete old avatar image");
////                                            }
////                                        });
////                                    }
//
//                                    loadingDialogService.dismiss();
//                                    Toast.makeText(UserProfileActivity.this, "Uploaded avatar successfully", Toast.LENGTH_SHORT).show();
//                                }
//                                else {
//                                    loadingDialogService.dismiss();
//                                    Toast.makeText(UserProfileActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
//                                    Log.d(TAG, "Failed upload: ", task.getException());
//                                }
//                            }
//                        });
//                    };
//                });
//            }
//        });
//    }
//
//    private void pickImage() {
//        CropImage.activity(imageUri)
//                .setCropShape(CropImageView.CropShape.OVAL)
//                .start(this);
//    }
//
//    @SuppressLint("MissingSuperCall")
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                imageUri = result.getUri();
//                userImage.setImageURI(imageUri);
//                uploadData();
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//            }
//        }
//    }
}