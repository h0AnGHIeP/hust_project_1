package dev.hoanghiep.project1.userInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dev.hoanghiep.project1.R;

public class LoginFragment extends Fragment {

    @BindView(R.id.login_email)
    EditText mEmailEdit;
    @BindView(R.id.login_pass)
    EditText mPassEdit;

    @BindView(R.id.login_sign_in)
    Button mSignInButton;
    @BindView(R.id.login_create_account)
    Button mSignUpButton;

    private Unbinder mUnbinder;

    private FirebaseAuth mAuth;

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mAuth = FirebaseAuth.getInstance();
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @OnClick(R.id.login_sign_in)
    public void login() {
        String email = mEmailEdit.getText().toString();
        String pass = mPassEdit.getText().toString();
        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(getActivity(), "Type in password and email", Toast.LENGTH_SHORT).show();
        } else {
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            startActivity(ListChatActivity.newIntent(getActivity()));
                            Toast.makeText(getActivity(), "Sign in Completed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to sign in", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }

    @OnClick(R.id.login_create_account)
    public void createAccount() {
        startActivity(SignUpActivity.newIntent(getActivity()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
