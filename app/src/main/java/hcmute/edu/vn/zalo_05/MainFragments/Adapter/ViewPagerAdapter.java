package hcmute.edu.vn.zalo_05.MainFragments.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import hcmute.edu.vn.zalo_05.MainFragments.ContactsFragment;
import hcmute.edu.vn.zalo_05.MainFragments.MessagesFragment;
import hcmute.edu.vn.zalo_05.MainFragments.ProfileFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ContactsFragment();
            case 2:
                return new ProfileFragment();
            default:
                return new MessagesFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
