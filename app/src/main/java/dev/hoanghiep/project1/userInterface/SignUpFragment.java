package dev.hoanghiep.project1.userInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dev.hoanghiep.project1.R;

public class SignUpFragment extends Fragment {

    @BindView(R.id.sign_up_display_name)
    EditText mDisplayEdit;
    @BindView(R.id.sign_up_email)
    EditText mEmailEdit;
    @BindView(R.id.sign_up_pass)
    EditText mPasswordEdit;
    @BindView(R.id.sign_up_phone)
    EditText mPhoneEdit;
    @BindView(R.id.sign_up_create_account)
    Button mSignUpButton;

    private Unbinder mUnbinder;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseReference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().getRoot();
        return view;
    }

    @OnClick(R.id.sign_up_create_account)
    public void createAccount(View button) {
        String displayName = mDisplayEdit.getText().toString();
        String email = mEmailEdit.getText().toString();
        String password = mPasswordEdit.getText().toString();
        String phone = mPhoneEdit.getText().toString();
        DateFormat formatter = SimpleDateFormat.getDateInstance();

        if (mAuth != null) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String userId = currentUser.getUid();
                            DatabaseReference userRef = mDatabaseReference.child("USERS").child(userId);
                            userRef.child("display").setValue(displayName);
                            userRef.child("phone").setValue(phone);
                            userRef.child("created").setValue(formatter.format(new Date()));
                            startActivity(ListChatActivity.newIntent(getActivity()));
                        } else {
                            Toast.makeText(getActivity(), "Request but not success", Toast.LENGTH_LONG).show();
                        }
                    });
        } else {
            Toast.makeText(getActivity(), "Cannot create user", Toast.LENGTH_SHORT).show();
        }

    }

    public static SignUpFragment newInstance() {
        return new SignUpFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
