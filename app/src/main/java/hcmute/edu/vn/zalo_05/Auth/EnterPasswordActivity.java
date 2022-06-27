package hcmute.edu.vn.zalo_05.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.AuthService;
import hcmute.edu.vn.zalo_05.Services.LoadingDialogService;

public class EnterPasswordActivity extends AppCompatActivity {
    private static final String ARG_NEW_USER = "newUser";

    private FirebaseFirestore database;
    private FirebaseAuth auth;
    private LoadingDialogService loadingDialogService;
    private AuthService authService;

    private MaterialToolbar toolbar;
    private FloatingActionButton actionButton;
    private TextInputLayout textInputPassword;

    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_password);

        this.authService = AuthService.getInstance(EnterPasswordActivity.this);

        this.loadingDialogService = LoadingDialogService.getInstance();
        this.database = FirebaseFirestore.getInstance();
        this.auth = FirebaseAuth.getInstance();
        this.newUser = (User) getIntent().getSerializableExtra(ARG_NEW_USER);

        this.toolbar = findViewById(R.id.activity_enter_password_toolbar);
        setSupportActionBar(this.toolbar);
        this.actionButton = findViewById(R.id.activity_enter_password_next_btn);
        this.actionButton.setEnabled(false);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                loadingDialogService.show(EnterPasswordActivity.this);

//                String emailFormat = String.format("%s@gmail.com", newUser.getNumberPhone());
                String passwordInput = textInputPassword.getEditText().getText().toString().trim();
                authService.register(newUser, passwordInput);
//                auth.createUserWithEmailAndPassword(emailFormat, passwordInput)
//                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<AuthResult> task) {
//                                        if(task.isSuccessful()) {
//                                            long currentTimeMillis = System.currentTimeMillis();
//                                            newUser.setCreatedAt(new Date(currentTimeMillis));
//                                            Map<String, Object> data = new HashMap<>();
//                                            data.put("numberPhone", newUser.getNumberPhone());
//                                            database.collection("Auth").document(auth.getCurrentUser().getUid()).set(data)
//                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                        @Override
//                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                            if(task.isSuccessful()) {
//                                                                database.collection("Users").document(newUser.getNumberPhone()).set(newUser)
//                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        if(task.isSuccessful()) {
//                                                                                            loadingDialogService.dismiss();
//                                                                                            Toast.makeText(EnterPasswordActivity.this, "Đăng ký tài khoản thành công", Toast.LENGTH_SHORT).show();
//                                                                                        }
//                                                                                        else {
//                                                                                            loadingDialogService.dismiss();
//                                                                                            Log.d(String.valueOf(EnterPasswordActivity.this), "Failed with: ", task.getException());
//                                                                                            Toast.makeText(EnterPasswordActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
//                                                                                        }
//                                                                                    }
//                                                                                });
//                                                            }
//                                                            else {
//                                                                loadingDialogService.dismiss();
//                                                                Log.d(String.valueOf(EnterPasswordActivity.this), "Failed with: ", task.getException());
//                                                                Toast.makeText(EnterPasswordActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
//                                                            }
//                                                        }
//                                                    });
//                                        }
//                                        else {
//                                            loadingDialogService.dismiss();
//                                            Log.d(String.valueOf(EnterPasswordActivity.this), "Failed with: ", task.getException());
//                                            Toast.makeText(EnterPasswordActivity.this, "Đã xảy ra lỗi!", Toast.LENGTH_SHORT).show();
//                                        }
//                                    }
//                                });
            }
        });

        this.textInputPassword = findViewById(R.id.activity_enter_password_text_input_password);
        this.textInputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String passwordInput = textInputPassword.getEditText().getText().toString().trim();
                if(passwordInput.length() >= 8) {
                    actionButton.setEnabled(true);
                }
                else {
                    actionButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


}