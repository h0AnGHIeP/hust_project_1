package dev.hoanghiep.project1.userInterface;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import dev.hoanghiep.project1.MainActivity;

public class LoginActivity extends MainActivity {
    @Override
    public Fragment getFragment() {
        return LoginFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, LoginActivity.class);
    }

}
