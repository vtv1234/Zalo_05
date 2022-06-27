package hcmute.edu.vn.zalo_05.Auth.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import hcmute.edu.vn.zalo_05.R;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EnterNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EnterNameFragment extends Fragment {
    private View view;
    private TextInputLayout textInputName;
    private FloatingActionButton actionButton;
    private EnterNameInteractionListener interactionListener;
    private MaterialToolbar toolbar;

    private Bundle outState;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String INPUT_SAVED_INSTANCE = "nameInput";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public EnterNameFragment() {
        // Required empty public constructor
    }

    public EnterNameFragment(EnterNameInteractionListener interactionListener) {
        this.interactionListener = interactionListener;
    }

    public interface EnterNameInteractionListener {
        public void getNameInput(String nameInput);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EnterNameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EnterNameFragment newInstance(String param1, String param2) {
        EnterNameFragment fragment = new EnterNameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if savedInstanceState not null, load previous data to textinputname
        if(savedInstanceState != null) {
            this.textInputName.getEditText().setText(savedInstanceState.getString(INPUT_SAVED_INSTANCE, null));
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
//        if outstate not null, initialize new Bundle and set argument for fragment
        if(outState == null) {
            outState = new Bundle();
            this.setArguments(outState);

        }
        String nameInput = textInputName.getEditText().getText().toString().trim();
        if(validateName(nameInput)) {
            outState.putString(INPUT_SAVED_INSTANCE, nameInput);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_enter_name, container, false);

        this.toolbar = getActivity().findViewById(R.id.activity_register_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(this.toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.textInputName = view.findViewById(R.id.text_input_name);
        this.textInputName.requestFocus();

        this.actionButton = getActivity().findViewById(R.id.activity_register_next_btn);
        this.actionButton.setEnabled(false);

//        after entering the name, the user clicks on the action btn
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameInput = textInputName.getEditText().getText().toString().trim();
                if(validateName(nameInput))
                {
//                    trigger interaction listener
                    interactionListener.getNameInput(nameInput);
                }

            }
        });
        this.textInputName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String nameInput = textInputName.getEditText().getText().toString().trim();
                if(nameInput.length() >= 2) {
                    actionButton.setEnabled(true);
                }
                else {
                    actionButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(textInputName.isErrorEnabled()) {
                    textInputName.setErrorEnabled(false);
                }
            }
        });
        return view;
    }

//    validate username entered
    private boolean validateName(String nameInput) {

        if(nameInput.matches(".*[0-9].*")) {
            textInputName.setErrorEnabled(true);
            textInputName.setError("Tên không được chứa chữ số");
            textInputName.setErrorIconDrawable(null);
            return false;
        }
        else if(nameInput.length() > 40) {
            textInputName.setErrorEnabled(true);
            textInputName.setError("Tên quá dài. Tên hợp lệ phải gồm 2-40 ký tự.");
            textInputName.setErrorIconDrawable(null);

            return false;
        }
        else {
            textInputName.setError(null);
            return true;
        }
    }

    @Override
    public void onPause() {
        onSaveInstanceState(this.outState);
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(getArguments() != null) {
            this.textInputName.getEditText().setText(getArguments().getString(INPUT_SAVED_INSTANCE));
        }
    }
}