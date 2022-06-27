package hcmute.edu.vn.zalo_05.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import hcmute.edu.vn.zalo_05.MainActivity;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.AuthService;
import hcmute.edu.vn.zalo_05.Services.LoadingDialogService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;
import hcmute.edu.vn.zalo_05.Utilities.PreferenceManager;


public class LoginActivity extends AppCompatActivity {
    public static final String TAG = LoginActivity.class.getName();

    private FirebaseFirestore database;
    private DocumentReference documentReference;
    private FirebaseAuth auth;
    private PreferenceManager preferenceManager;

    private LoadingDialogService loadingDialogService;

    private FloatingActionButton actionButton;
    private MaterialToolbar toolbar;
    private TextInputLayout textInputNumberPhone;
    private TextInputLayout textInputPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.loadingDialogService = LoadingDialogService.getInstance();
        this.auth = FirebaseAuth.getInstance();

        this.database = FirebaseFirestore.getInstance();
        this.preferenceManager = new PreferenceManager(getApplicationContext());
        //Mapping
        this.actionButton = findViewById(R.id.activity_login_next_btn);
        this.actionButton.setEnabled(false);
        this.toolbar = findViewById(R.id.activity_login_toolbar);
        this.setSupportActionBar(this.toolbar);
        this.getSupportActionBar().setDisplayShowHomeEnabled(true);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.textInputNumberPhone = findViewById(R.id.activity_login_text_input_number_phone);
        this.textInputPassword = findViewById(R.id.activity_login_text_input_password);

        this.textInputNumberPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(validate()) {
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

        this.textInputPassword.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(validate()) {
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

        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputNumberPhone = textInputNumberPhone.getEditText().getText().toString().trim();
                String inputPassword = textInputPassword.getEditText().getText().toString().trim();

                AuthService.getInstance(LoginActivity.this).login(inputNumberPhone, inputPassword);


            }
        });
    }

    private boolean validate() {
        String inputNumberPhone = textInputNumberPhone.getEditText().getText().toString().trim();
        String inputPassword = textInputPassword.getEditText().getText().toString().trim();

        if(!inputPassword.isEmpty() && !inputPassword.isEmpty()) {
            return true;
        }
        return false;
    }
}