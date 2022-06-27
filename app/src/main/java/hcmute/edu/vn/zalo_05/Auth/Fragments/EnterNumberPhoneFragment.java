package hcmute.edu.vn.zalo_05.Auth.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import java.util.concurrent.TimeUnit;

import hcmute.edu.vn.zalo_05.Auth.SelectGenderAndBirthDateActivity;
import hcmute.edu.vn.zalo_05.Auth.VerifyOTPActivity;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.LoadingDialogService;
import hcmute.edu.vn.zalo_05.Utilities.Constants;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterNumberPhoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterNumberPhoneFragment extends Fragment {
    public static final String TAG = EnterNumberPhoneFragment.class.getName();
    private static final String ARG_NEW_USER = "newUser";

    private LoadingDialogService loadingDialogService;
    private FirebaseFirestore database;
    // set up for firebase auth
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBacks;
    private User newUser;

    String[] items = {"VN"};
    private View view;
    private FloatingActionButton actionButton;
    private MaterialAutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> arrayAdapter;
    private MaterialToolbar toolbar;
    private TextInputLayout textInputRegionCode;
    private TextInputLayout textInputNumberPhone;


    public EnterNumberPhoneFragment() {
        // Required empty public constructor
    }

    public static EnterNumberPhoneFragment newInstance(User user) {
        EnterNumberPhoneFragment fragment = new EnterNumberPhoneFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_NEW_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundleReceive = getArguments();
        if(bundleReceive != null) {
            newUser = (User) bundleReceive.getSerializable(ARG_NEW_USER);

        }

        this.loadingDialogService = LoadingDialogService.getInstance();
        this.database = FirebaseFirestore.getInstance();
        // get instance for firebaseAuth
        this.mAuth = FirebaseAuth.getInstance();
        // set call back for firebaseAuth
        mCallBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                loadingDialogService.dismiss();
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
//                progressDialog.dismiss();
                loadingDialogService.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                loadingDialogService.dismiss();
                String numberPhoneInput = textInputNumberPhone.getEditText().getText().toString().trim();
                Intent intent = new Intent(getActivity(), VerifyOTPActivity.class);
                intent.putExtra("mobile", numberPhoneInput);
                intent.putExtra("verificationId", verificationId);
                launcher.launch(intent);
            }
        };

    }

    ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK) {
                        Intent intent = result.getData();
                        newUser.setNumberPhone(intent.getStringExtra("numberPhone"));

                        intent = new Intent(getActivity(), SelectGenderAndBirthDateActivity.class);
                        intent.putExtra(ARG_NEW_USER, newUser);
                        startActivity(intent);
                    }
                }
            }
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view = inflater.inflate(R.layout.fragment_enter_number_phone, container, false);

        this.toolbar = getActivity().findViewById(R.id.activity_register_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(this.toolbar);
        this.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getParentFragmentManager() != null) {
                    getParentFragmentManager().popBackStack();
                }
            }
        });

        this.autoCompleteTextView = view.findViewById(R.id.auto_complete_region_code);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.dropdown_list_layout, items);
        this.autoCompleteTextView.setAdapter(arrayAdapter);
        this.autoCompleteTextView.setText(items[0]);

        this.textInputRegionCode = view.findViewById(R.id.text_input_region_code);

        this.textInputNumberPhone = view.findViewById(R.id.text_input_number_phone);
        this.textInputNumberPhone.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String numberPhoneInput = textInputNumberPhone.getEditText().getText().toString().trim();
                if(numberPhoneInput.length() > 0) {
                    actionButton.setEnabled(true);
                }
                else {
                    actionButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(textInputNumberPhone.isErrorEnabled()) {
                    textInputNumberPhone.setErrorEnabled(false);
                }
                if(textInputRegionCode.isErrorEnabled()) {
                    textInputRegionCode.setErrorEnabled(false);
                }
            }
        });

        this.actionButton = getActivity().findViewById(R.id.activity_register_next_btn);
        this.actionButton.setEnabled(false);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String numberPhoneInput = textInputNumberPhone.getEditText().getText().toString().trim();
                if(validateNumberPhone(numberPhoneInput))
                {
                    openConfirmDialog(Gravity.CENTER);
                }


            }
        });

        return view;
    }


    // Use for validate input number phone
    private boolean validateNumberPhone(String numberPhoneInput) {
        if(numberPhoneInput.length() > 0)
            return true;

        return false;
    }

    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L,TimeUnit.SECONDS)
                        .setActivity(getActivity())
                        .setCallbacks(mCallBacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }



    private void openConfirmDialog(int gravity) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm_layout);

        Window window = dialog.getWindow();
        if(window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);

        if(Gravity.BOTTOM == gravity) {
            dialog.setCancelable(true);
        } else {
            dialog.setCancelable(false);
        }

        TextView confirmTextView = dialog.findViewById(R.id.dialog_confirm_layout_confirm_text_view);
        String numberPhoneInput = textInputNumberPhone.getEditText().getText().toString().trim();
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber swissNumberProto = null;
        try {
            swissNumberProto = phoneNumberUtil.parse(numberPhoneInput, "VN");
        } catch (NumberParseException e) {
            Log.d(String.valueOf(EnterNumberPhoneFragment.this), "NumberParseException was thrown: " + e.toString());
        }
        numberPhoneInput = phoneNumberUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL);
        confirmTextView.setText(confirmTextView.getText() + numberPhoneInput + " ?");

        Button cancelBtn = dialog.findViewById(R.id.dialog_confirm_layout_cancel_btn);
        Button confirmBtn = dialog.findViewById(R.id.dialog_confirm_layout_confirm_btn);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                loadingDialogService.show(getActivity());
                String numberPhoneInput = textInputNumberPhone.getEditText().getText().toString().trim();

                // Check if the phone number already exists
                database.collection(Constants.KEY_COLLECTION_USERS).document(numberPhoneInput.toString()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if(documentSnapshot.exists()) {
                                        loadingDialogService.dismiss();
                                        textInputNumberPhone.setErrorEnabled(true);
                                        textInputRegionCode.setErrorEnabled(true);

                                        textInputNumberPhone.setError("Số điện thoại này đã tồn tại");
                                        textInputNumberPhone.setErrorIconDrawable(null);
                                        textInputRegionCode.setErrorIconDrawable(null);
                                    }
                                    else {
                                        startPhoneNumberVerification("+84" + numberPhoneInput);
                                    }
                                }
                            }
                        });


//                Toast.makeText(getActivity(), "Confirmed number phone to authenticate", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
    }
}