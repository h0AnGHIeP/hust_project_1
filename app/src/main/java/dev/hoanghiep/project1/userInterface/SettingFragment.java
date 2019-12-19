package dev.hoanghiep.project1.userInterface;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dev.hoanghiep.project1.R;
import dev.hoanghiep.project1.data.FirebaseStructure;

import static android.app.Activity.RESULT_OK;

public class SettingFragment extends Fragment {
    
    private static final int REQUEST_IMAGE_CAPTURE = 8;
    private static final String TAG = SettingFragment.class.getSimpleName();
    @BindView(R.id.setting_logout_layout)
    ConstraintLayout mLogoutLayout;
    @BindView(R.id.setting_upload)
    Button mUploadButton;
    
    @BindView(R.id.setting_camera)
    ImageButton mCameraButton;
    @BindView(R.id.setting_image)
    ImageView mImage;
    
    private Bitmap newBitmap;
    
    private Unbinder mUnbind;
    
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }
    
    @OnClick(R.id.setting_camera)
    void takePhoto(ImageButton button) {
        if (button.isEnabled()) {
            Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (i.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivityForResult(i, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    
    @OnClick(R.id.setting_upload)
    void uploadImage(Button button) {
        if (button.isEnabled()) {
            FirebaseStructure.putBitmap(newBitmap, getActivity(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid());
            button.setEnabled(false);
        }
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            newBitmap = (Bitmap) data.getExtras().get("data");
            mImage.setImageBitmap(newBitmap);
            mUploadButton.setEnabled(true);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_setting, container, false);
        mUnbind = ButterKnife.bind(this, v);
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            mCameraButton.setEnabled(false);
        }
        return v;
    }
    
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLogoutLayout.setOnClickListener(v -> {
            // When user click on positive button
            LogoutDialog dialog = new LogoutDialog(() -> {
                FirebaseAuth.getInstance().signOut();
                startActivity(LoginActivity.newIntent(requireActivity()));
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
                    .setTitle(R.string.log_out_title)
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
