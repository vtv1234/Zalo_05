package hcmute.edu.vn.zalo_05.Profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.LoadingDialogService;
import hcmute.edu.vn.zalo_05.Services.UserService;

public class ChangePasswordActivity extends AppCompatActivity {
    private TextInputLayout textInputPasswordOld;
    private TextInputLayout textInputPasswordNew;
    private TextInputLayout textInputPasswordConfirm;
    private MaterialToolbar toolbar;
    private Button updateBtn;
    
    private FirebaseUser user;
    
    private String oldPassword = null;
    private String newPassword = null;
    private String confirmPassword = null;
    private LoadingDialogService loadingDialogService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        map();
        init();
        setListeners();
    }

    private void init() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user == null) {
            onBackPressed();
            Toast.makeText(this, "Hệ thống đang xảy ra lỗi, vui lòng thử lại sau", Toast.LENGTH_SHORT).show();
        }
        loadingDialogService = LoadingDialogService.getInstance();

    }

    private void map() {
        textInputPasswordOld = findViewById(R.id.changePasswordActivity_textInputLayout_passOld);
        textInputPasswordNew = findViewById(R.id.changePasswordActivity_textInputLayout_passNew);
        textInputPasswordConfirm = findViewById(R.id.changePasswordActivity_textInputLayout_passNewConfirm);
        updateBtn = findViewById(R.id.changePasswordActivity_button_update);
        updateBtn.setEnabled(false);
        toolbar = findViewById(R.id.changePasswordActivity_materialToolbar_title);
    }

    private void setListeners() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        textInputPasswordOld.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                String inputPasswordOld = textInputPasswordOld.getEditText().getText().toString().trim();

                if((!isFocused) && (!inputPasswordOld.isEmpty())) {
                    if(inputPasswordOld.length() >8) {
                        textInputPasswordOld.setErrorEnabled(true);
                        textInputPasswordOld.setError("Mật khẩu yêu cầu tối thiểu 8 ký tự");
                        textInputPasswordOld.setErrorIconDrawable(null);
                        updateBtn.setEnabled(false);
                    }
                }
            }
        });

        textInputPasswordNew.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                String inputPasswordNew = textInputPasswordNew.getEditText().getText().toString().trim();

                if((!isFocused) && (!inputPasswordNew.isEmpty())) {
                    if(inputPasswordNew.length() < 8) {
                        textInputPasswordNew.setErrorEnabled(true);
                        textInputPasswordNew.setError("Mật khẩu yêu cầu tối thiểu 8 ký tự");
                        textInputPasswordNew.setErrorIconDrawable(null);
                        updateBtn.setEnabled(false);
                    }
                }
            }
        });

        textInputPasswordConfirm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean isFocused) {
                String inputPasswordConfirm = textInputPasswordConfirm.getEditText().getText().toString().trim();
                String inputPasswordNew = textInputPasswordNew.getEditText().getText().toString().trim();

                if((!isFocused) && (!inputPasswordConfirm.isEmpty())) {
                    if(!inputPasswordConfirm.equals(inputPasswordNew)) {
                        textInputPasswordConfirm.setErrorEnabled(true);
                        textInputPasswordConfirm.setError("Mật khẩu xác thực không trùng khớp");
                        textInputPasswordConfirm.setErrorIconDrawable(null);
                        updateBtn.setEnabled(false);
                    }
                }
            }
        });


        textInputPasswordOld.getEditText().addTextChangedListener(textWatcher);
        textInputPasswordNew.getEditText().addTextChangedListener(textWatcher);
        textInputPasswordConfirm.getEditText().addTextChangedListener(textWatcher);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialogService.show(ChangePasswordActivity.this);

                if(oldPassword == null ||
                    newPassword == null ||
                    confirmPassword == null)
                {
                    Toast.makeText(ChangePasswordActivity.this, "", Toast.LENGTH_SHORT).show();
                    return;
                }
                User currentUser = UserService.getInstance(getApplicationContext()).getCurrentUser();
                String email = String.format("%s@gmail.com", currentUser.getNumberPhone());
                
                AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);
                user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        reset();
                                        loadingDialogService.dismiss();
                                        onBackPressed();
                                        Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();

                                    } else {
                                        loadingDialogService.dismiss();
                                        Log.d(String.valueOf(ChangePasswordActivity.this), "Failed when change password: " + task.getException());
                                        Toast.makeText(ChangePasswordActivity.this, "Thay đổi mật khẩu thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            loadingDialogService.dismiss();
                            Log.d(String.valueOf(ChangePasswordActivity.this), "Failed when authen before change password: " + task.getException());
                            Toast.makeText(ChangePasswordActivity.this, "Xác thực không thành công", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });
    }

    private void reset() {
        textInputPasswordOld.getEditText().setText("");
        textInputPasswordNew.getEditText().setText("");
        textInputPasswordConfirm.getEditText().setText("");

        oldPassword = null;
        newPassword = null;
        confirmPassword = null;
    }

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(textInputPasswordOld.isErrorEnabled()) {
                textInputPasswordOld.setErrorEnabled(false);
            }
            if(textInputPasswordNew.isErrorEnabled()) {
                textInputPasswordNew.setErrorEnabled(false);
            }
            if(textInputPasswordConfirm.isErrorEnabled()) {
                textInputPasswordConfirm.setErrorEnabled(false);
            }
            if(validate()) {
                oldPassword = textInputPasswordOld.getEditText().getText().toString().trim();
                newPassword = textInputPasswordNew.getEditText().getText().toString().trim();
                confirmPassword = textInputPasswordConfirm.getEditText().getText().toString().trim();
                
                updateBtn.setEnabled(true);
            }

        }
    };

    private boolean validate() {
        if(!(validateIsEmpty(textInputPasswordOld) &&
            validateIsEmpty(textInputPasswordNew) &&
            validateIsEmpty(textInputPasswordConfirm))) {
            return false;
        }
        if(!validateNewPassword()){
            return false;
        }

        return true;

    }

    private boolean validateNewPassword() {
        String inputPasswordConfirm = textInputPasswordConfirm.getEditText().getText().toString().trim();
        String inputPasswordNew = textInputPasswordNew.getEditText().getText().toString().trim();
        if(!inputPasswordConfirm.equals(inputPasswordNew)) {
            textInputPasswordConfirm.setErrorEnabled(true);
            textInputPasswordConfirm.setError("Mật khẩu xác thực không trùng khớp");
            textInputPasswordConfirm.setErrorIconDrawable(null);
            updateBtn.setEnabled(false);

            return false;
        }
        return true;
    }

    private boolean validateIsEmpty(TextInputLayout textInputLayout) {
        String inputText = textInputLayout.getEditText().getText().toString().trim();
        if(inputText.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Trường này không được để trống");
            textInputLayout.setErrorIconDrawable(null);
            updateBtn.setEnabled(false);
            return false;
        }
        else if(inputText.length() < 8) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError("Mật khẩu yêu cầu tối thiểu 8 ký tự");
            textInputLayout.setErrorIconDrawable(null);
            updateBtn.setEnabled(false);
            return false;
        }
        return true;
    }

}