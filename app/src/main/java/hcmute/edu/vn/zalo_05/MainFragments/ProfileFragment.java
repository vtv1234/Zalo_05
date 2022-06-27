package hcmute.edu.vn.zalo_05.MainFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import hcmute.edu.vn.zalo_05.Auth.AuthActivity;
import hcmute.edu.vn.zalo_05.MainActivity;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.Profile.ChangePasswordActivity;
import hcmute.edu.vn.zalo_05.Profile.UserProfileActivity;
import hcmute.edu.vn.zalo_05.R;
import hcmute.edu.vn.zalo_05.Services.LoadingDialogService;
import hcmute.edu.vn.zalo_05.Services.UserService;
import hcmute.edu.vn.zalo_05.Utilities.PreferenceManager;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {
    private View view;
    private Button infoProfileBtn;
    private Button logoutBtn;
    private Button changePasswordBtn;
    private ShapeableImageView avatarImage;
    private TextView nameText;

    private PreferenceManager preferenceManager;
    private LoadingDialogService loadingDialogService;
    private User currentUser;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.preferenceManager = new PreferenceManager(getActivity());
        this.currentUser = UserService.getInstance(getActivity()).getCurrentUser();
        this.loadingDialogService = LoadingDialogService.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        infoProfileBtn = view.findViewById(R.id.profileFragment_button_info);
        avatarImage = view.findViewById(R.id.profileFragment_imageView_avatar);
        nameText = view.findViewById(R.id.profileFragment_textView_username);
        if(currentUser.getUserName() != null) {
            nameText.setText(currentUser.getUserName());
        } else {
            nameText.setText(currentUser.getZaloName());
        }
        if(currentUser.getAvatarImageUrl() != null && currentUser.getAvatarImageUrl() != "") {
            Picasso.get().load(currentUser.getAvatarImageUrl()).into(avatarImage);
        }

        logoutBtn = view.findViewById(R.id.profileFragment_button_logout);
        changePasswordBtn = view.findViewById(R.id.profileFragment_button_password);

        setListeners();

        return view;
    }

//    use to set event listeners for elements in view
    private void setListeners() {
//        Navigate to UserProfile activity when click to profile btn
        infoProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(intent);
            }
        });


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                show loading dialog
                loadingDialogService.show(getActivity());

//                call to logout function handled by Firebase auth
                FirebaseAuth.getInstance().signOut();

//                After sign out, clear all information about current user in sharedPreference
                preferenceManager.clear();
//                dismiss loading dialog
                loadingDialogService.dismiss();

//                After sign out, navigate user to Auth Activity
                Intent intent = new Intent(getActivity(), AuthActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        changePasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ChangePasswordActivity.class);
                startActivity(intent);
            }
        });
    }
}