package hcmute.edu.vn.zalo_05.Profile.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.edu.vn.zalo_05.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChangeProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChangeProfileFragment extends Fragment {
    private MaterialToolbar toolbar;
    private CircleImageView imageProfile;
    private CircleImageView pickImageView;
    private TextInputLayout inputTextName;

    private CheckBox maleCheckbox;
    private CheckBox femaleCheckbox;
    private TextView birthdateText;
    private Button updateBtn;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChangeProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChangeProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChangeProfileFragment newInstance(String param1, String param2) {
        ChangeProfileFragment fragment = new ChangeProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_profile, container, false);

        return view;
    }
}