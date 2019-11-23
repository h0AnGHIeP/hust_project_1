package dev.hoanghiep.project1.userInterface;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dev.hoanghiep.project1.MainActivity;

public class ListChatActivity extends MainActivity {
    @Override
    public Fragment getFragment() {
        return ListChatFragment.newInstance();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, ListChatActivity.class);
    }
}
