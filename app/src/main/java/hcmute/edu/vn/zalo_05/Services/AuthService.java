package hcmute.edu.vn.zalo_05.Services;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.zalo_05.Auth.EnterPasswordActivity;
import hcmute.edu.vn.zalo_05.Auth.LoginActivity;
import hcmute.edu.vn.zalo_05.MainActivity;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
import hcmute.edu.vn.zalo_05.Utilities.PreferenceManager;

public class AuthService {
    private static AuthService instance;
    private FirebaseAuth auth;
    private FirebaseFirestore database;
    private LoadingDialogService loadingDialogService;

    private static Context context;

    private AuthService() {
        this.auth = FirebaseAuth.getInstance();
        this.database = FirebaseFirestore.getInstance();
        this.loadingDialogService = LoadingDialogService.getInstance();
    }

    public static AuthService getInstance(Context context) {
        instance.context = context;
        if(instance == null) {
            instance = new AuthService();

        }
        return instance;
    }


    public void login(String numberPhone, String password) {
        loadingDialogService.show(context);

        String email = String.format("%s@gmail.com", numberPhone);
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            database.collection(Constants.KEY_COLLECTION_USERS).document(numberPhone)
                                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()) {
                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                if(documentSnapshot.exists()) {
                                                    PreferenceManager preferenceManager = new PreferenceManager(context);

                                                    preferenceManager.putBoolean(Constants.KEY_IS_LOGGED_IN, true);
                                                    User currentUser = documentSnapshot.toObject(User.class);
                                                    Gson gson = new Gson();
                                                    String objectToJson = gson.toJson(currentUser);
                                                    preferenceManager.putString(Constants.KEY_CURRENT_USER, objectToJson);

                                                    loadingDialogService.dismiss();
                                                    Intent intent = new Intent(context, MainActivity.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    context.startActivity(intent);
                                                }
                                                else {
                                                    loadingDialogService.dismiss();
                                                    Toast.makeText(context, "Đã xảy ra lỗi, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();

                                                }
                                            }
                                            else {
                                                loadingDialogService.dismiss();
                                                Log.d(String.valueOf(context), "Failed with: ", task.getException());
                                                Toast.makeText(context, "Đã xảy ra lỗi, vui lòng thử lại sau!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            loadingDialogService.dismiss();
                            Log.d(String.valueOf(context), "Failed with: ", task.getException());
                            Toast.makeText(context, "Đăng nhập không thành công!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void register(User user, String password) {
        loadingDialogService.show(context);

        String format = String.format("%s@gmail.com", user.getNumberPhone());
        this.auth.createUserWithEmailAndPassword(format, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            long currentTimeMillis = System.currentTimeMillis();
                            user.setCreatedAt(new Date(currentTimeMillis));

                            Map<String, Object> data = new HashMap<>();
                            data.put("numberPhone", user.getNumberPhone());

                            database.collection("Auth").document(auth.getCurrentUser().getUid()).set(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                database.collection("User").document(user.getNumberPhone()).set(user)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()) {
                                                                    loadingDialogService.dismiss();
                                                                    Toast.makeText(context, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();

                                                                    // After registered successfully, auto login and redirect user to main activity
                                                                    new Handler().postDelayed(new Runnable() {
                                                                        @Override
                                                                        public void run() {
                                                                            login(user.getNumberPhone(),password);
                                                                        }
                                                                    }, 500);
                                                                }
                                                                else {
                                                                    loadingDialogService.dismiss();
                                                                    Log.d(String.valueOf(context), "Failed with: ", task.getException());
                                                                    Toast.makeText(context, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            }
                                            else {
                                                loadingDialogService.dismiss();
                                                Log.d(String.valueOf(context), "Failed with: ", task.getException());
                                                Toast.makeText(context, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                        else {
                            loadingDialogService.dismiss();
                            Log.d(String.valueOf(context), "Failed with: ", task.getException());
                            Toast.makeText(context, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
