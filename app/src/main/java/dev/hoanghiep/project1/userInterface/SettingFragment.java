package dev.hoanghiep.project1.userInterface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dev.hoanghiep.project1.R;

public class SettingFragment extends Fragment {
    
    @BindView(R.id.setting_logout_layout)
    ConstraintLayout mLogoutLayout;
    
    private Unbinder mUnbind;
    
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        mUnbind = ButterKnife.bind(this, v);
        return v;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLogoutLayout.setOnClickListener(v -> {
            LogoutDialog dialog = new LogoutDialog(new LogoutDialog.Listener() {
                @Override
                // When user click on positive button
                public void onPositive() {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(LoginActivity.newIntent(requireActivity()));
                }
            });
            dialog.show(getFragmentManager(), "dial");
        });
    }
    
    public static class LogoutDialog extends DialogFragment {
        interface Listener {
            void onPositive();
        }
        
        private Listener mListener;
        
        LogoutDialog(Listener listener) {
            mListener = listener;
        }
        
        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            super.onCreateDialog(savedInstanceState);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.setting_logout_dialog)
                    .setTitle(R.string.setting_logout_title)
                    .setPositiveButton(R.string.setting_logout_positive_btn,
                            (dialog, which) -> {
                                mListener.onPositive();
                                dismiss();
                            })
                    .setNegativeButton(R.string.setting_logout_negative_btn,
                            (dialog, which) -> {
                                dismiss();
                    });
            return builder.create();
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        mUnbind.unbind();
    }
}
