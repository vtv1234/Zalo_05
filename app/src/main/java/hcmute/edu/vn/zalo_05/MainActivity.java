package hcmute.edu.vn.zalo_05;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import hcmute.edu.vn.zalo_05.MainFragments.Adapter.ViewPagerAdapter;

public class MainActivity extends BaseActivity {
    private BottomNavigationView bottomNavigationView;
    private ViewPager2 viewPager2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.bottomNavigationView = findViewById(R.id.activity_main_bottom_navigation);
        this.viewPager2 = findViewById(R.id.activity_main_view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        this.viewPager2.setAdapter(viewPagerAdapter);

        this.viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
//                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.navigiation_messages).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.navigiation_contacts).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.navigiation_profile).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        this.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if(id == R.id.navigiation_messages) {
                viewPager2.setCurrentItem(0);
            } else if(id == R.id.navigiation_contacts) {
                viewPager2.setCurrentItem(1);
            } else if(id == R.id.navigiation_profile) {
                viewPager2.setCurrentItem(2);
            }
            return true;
        });

        new Handler(this.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                BadgeDrawable badgeDrawable = bottomNavigationView.getOrCreateBadge(R.id.navigiation_messages);
                badgeDrawable.setVisible(true);
                badgeDrawable.setVerticalOffset(dpToPx(MainActivity.this, 3));
                badgeDrawable.setBadgeTextColor(getResources().getColor(R.color.white));
            }
        }, 1000);

        BadgeDrawable badgeMessages = bottomNavigationView.getOrCreateBadge(R.id.navigiation_messages);
        badgeMessages.setVisible(true);
        badgeMessages.setVerticalOffset(dpToPx(MainActivity.this, 3));
        badgeMessages.setNumber(5);
        badgeMessages.setBadgeTextColor(getResources().getColor(R.color.white));
        badgeMessages.setBackgroundColor(getResources().getColor(R.color.red_500));

    }

    public static int dpToPx(Context context, int dp) {
        Resources resources = context.getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()));
    }
}