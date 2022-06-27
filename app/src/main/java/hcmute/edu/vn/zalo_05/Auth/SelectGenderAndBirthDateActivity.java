package hcmute.edu.vn.zalo_05.Auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import hcmute.edu.vn.zalo_05.Models.User;
import hcmute.edu.vn.zalo_05.R;

public class SelectGenderAndBirthDateActivity extends AppCompatActivity {
    private static final String ARG_NEW_USER = "newUser";

    private MaterialToolbar toolbar;
    private CheckBox maleCheckBox;
    private CheckBox femaleCheckBox;
    private DatePicker birthDatePicker;
    private FloatingActionButton actionButton;

    private User newUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_gender_and_birth_date);
        this.newUser = (User) getIntent().getSerializableExtra(ARG_NEW_USER);

        this.toolbar = findViewById(R.id.activity_select_gender_and_birth_date_toolbar);
        this.setSupportActionBar(this.toolbar);

        this.actionButton = findViewById(R.id.activity_select_gender_and_birth_date_next_btn);
        this.actionButton.setEnabled(false);
        this.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String gender = maleCheckBox.isChecked() ? "Male" : "Female";
                String birthdate = String.format("%s/%s/%s", String.valueOf(birthDatePicker.getDayOfMonth()), String.valueOf(birthDatePicker.getMonth() + 1), String.valueOf(birthDatePicker.getYear()));
                newUser.setGender(gender);
                newUser.setBirthdate(birthdate);

                Intent intent = new Intent(SelectGenderAndBirthDateActivity.this, EnterPasswordActivity.class);
                intent.putExtra(ARG_NEW_USER,newUser);
                startActivity(intent);
//                Toast.makeText(SelectGenderAndBirthDateActivity.this, String.format("You are %s, birthdate is %s", gender, birthdate), Toast.LENGTH_SHORT).show();
            }
        });

        this.maleCheckBox = findViewById(R.id.male_check_box);
        this.maleCheckBox.setChecked(false);
        this.maleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    femaleCheckBox.setChecked(false);
                    actionButton.setEnabled(true);
                }
            }
        });

        this.femaleCheckBox = findViewById(R.id.female_check_box);
        this.femaleCheckBox.setChecked(false);
        this.femaleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if(checked) {
                    maleCheckBox.setChecked(false);
                    actionButton.setEnabled(true);
                }
            }
        });

        this.birthDatePicker = findViewById(R.id.birthdate_picker);


    }
}