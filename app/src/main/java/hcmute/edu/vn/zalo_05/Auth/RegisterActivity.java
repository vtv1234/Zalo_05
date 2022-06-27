package hcmute.edu.vn.zalo_05.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hcmute.edu.vn.zalo_05.Auth.Fragments.EnterNameFragment;
import hcmute.edu.vn.zalo_05.Auth.Fragments.EnterNumberPhoneFragment;
import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;

public class RegisterActivity extends AppCompatActivity implements EnterNameFragment.EnterNameInteractionListener {

    private ViewPager2 viewPager2;
    private FloatingActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

//        this.viewPager2 = findViewById(R.id.activity_register_view_pager2);
        this.actionButton = findViewById(R.id.activity_register_next_btn);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.activity_register_content_frame, new EnterNameFragment(this));
//        RegisterAdapter registerAdapter = new RegisterAdapter(this, this);
//        viewPager2.setAdapter(registerAdapter);
        fragmentTransaction.commit();

    }

    @Override
    public void getNameInput(String nameInput) {
        User newUser = new User();
        newUser.setZaloName(nameInput);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        EnterNumberPhoneFragment enterNumberPhoneFragment = EnterNumberPhoneFragment.newInstance(newUser);
        fragmentTransaction.replace(R.id.activity_register_content_frame, enterNumberPhoneFragment);
        fragmentTransaction.addToBackStack(EnterNumberPhoneFragment.TAG);
        fragmentTransaction.commit();
    }
}