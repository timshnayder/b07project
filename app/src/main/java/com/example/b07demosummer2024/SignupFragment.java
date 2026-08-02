package com.example.b07demosummer2024;

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
import androidx.fragment.app.FragmentTransaction;

public class SignupFragment extends Fragment implements SignupContract.View {

    private EditText editTextSignupEmail;
    private EditText editTextSignupUsername;
    private EditText editTextSignupPassword;
    private SignupContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_signup, container, false);

        editTextSignupEmail = view.findViewById(R.id.editTextSignupEmail);
        editTextSignupUsername = view.findViewById(R.id.editTextSignupUsername);
        editTextSignupPassword = view.findViewById(R.id.editTextSignupPassword);

        Button buttonSignup = view.findViewById(R.id.buttonSignup);
        Button buttonBackToLogin = view.findViewById(R.id.buttonBackToLogin);

        presenter = new SignupPresenter(this, new SignupModel());

        buttonSignup.setOnClickListener(v -> {
            String email = editTextSignupEmail.getText().toString();
            String username = editTextSignupUsername.getText().toString();
            String password = editTextSignupPassword.getText().toString();

            presenter.signup(email, username, password);
        });

        buttonBackToLogin.setOnClickListener(v ->
                getParentFragmentManager().popBackStack()
        );

        return view;
    }

    @Override
    public void showEmailError(String message) {
        editTextSignupEmail.setError(message);
    }

    @Override
    public void showUsernameError(String message) {
        editTextSignupUsername.setError(message);
    }

    @Override
    public void showPasswordError(String message) {
        editTextSignupPassword.setError(message);
    }

    @Override
    public void showSignupSuccess() {
        Toast.makeText(
                requireContext(),
                "Account created successfully",
                Toast.LENGTH_SHORT
        ).show();

        getParentFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction =
                getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, new HomeFragment());
        transaction.commit();
    }

    @Override
    public void showSignupError(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void showLoading() {
        // Loading indicator can be added later.
    }

    @Override
    public void hideLoading() {
        // Loading indicator can be added later.
    }
}