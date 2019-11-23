package dev.hoanghiep.project1.userInterface;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import dev.hoanghiep.project1.MainActivity;

public class SignUpActivity extends MainActivity {
    @Override
    public Fragment getFragment() {
        return SignUpFragment.newInstance();
    }

    public static Intent newIntent(Context packageContext) {
        return new Intent(packageContext, SignUpActivity.class);
    }

}
