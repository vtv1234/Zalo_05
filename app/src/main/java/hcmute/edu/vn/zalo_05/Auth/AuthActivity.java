package hcmute.edu.vn.zalo_05.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import hcmute.edu.vn.zalo_05.Auth.Adapter.SliderAdapter;
import hcmute.edu.vn.zalo_05.R;

public class AuthActivity extends AppCompatActivity {
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;
    private Button loginBtn;
    private Button registerBtn;

    private TextView[] mDots;

    private SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        // Mapping
        this.loginBtn = findViewById(R.id.login_btn);
        this.registerBtn = findViewById(R.id.register_btn);
        this.mSlideViewPager = findViewById(R.id.activity_auth_slide_view_pager);
        this.mDotLayout = findViewById(R.id.activity_auth_slide_dots);

        // Init slider adapter for manage and show slide content when run auth activity
        this.sliderAdapter = new SliderAdapter(this);
        this.mSlideViewPager.setAdapter(sliderAdapter);
        this.addDotsIndicator(0);
        mSlideViewPager.setOnPageChangeListener(viewListener);

        // Call RegisterActivity when click register button
        this.registerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);

        });

        // Call LoginActivity when click login button
        this.loginBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    // Generate dots indicator for slider
    public void addDotsIndicator(int position) {
        mDots = new TextView[4];
        mDotLayout.removeAllViews();

        for(int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.transparent_white));
            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.primary_color));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}