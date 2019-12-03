package dev.hoanghiep.project1.userInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import dev.hoanghiep.project1.R;

public class ListChatActivity extends AppCompatActivity {
    private ListFriendAdapter mAdapter = new ListFriendAdapter(getSupportFragmentManager(),
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);

    @BindView(R.id.list_chat_pager)
    ViewPager mPager;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_friend_pager);
        ButterKnife.bind(this);
        mPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mPager);
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, ListChatActivity.class);
    }

    private static class ListFriendAdapter extends FragmentPagerAdapter {

        private ListFriendAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ListChatFragment.newInstance();
                case 1:
                    return ListChatLookUp.newInstance();
                default:
                    return SettingFragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Look up";
                case 1:
                    return "Friends";
                case 2:
                    return "Settings";
                default:
                    return "UNKNOWN";
            }
        }
    }

}
