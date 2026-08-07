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

// Login screen that communicates with the login presenter.
public class LoginFragment extends Fragment implements LoginContract.View {

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private LoginContract.Presenter presenter;

    // Creates and initializes the login screen.
    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // Connect the login inputs and buttons.
        editTextLoginEmail = view.findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = view.findViewById(R.id.editTextLoginPassword);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonGoToSignup = view.findViewById(R.id.buttonGoToSignup);

        // Create the presenter used for login logic.
        presenter = new LoginPresenter(this, new LoginModel());

        // Send the entered login information to the presenter.
        buttonLogin.setOnClickListener(v -> {
            String email = editTextLoginEmail.getText().toString();
            String password = editTextLoginPassword.getText().toString();

            presenter.login(email, password);
        });

        // Open the signup page for new users.
        buttonGoToSignup.setOnClickListener(v -> {
            FragmentTransaction transaction =
                    getParentFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, new SignupFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    // Show an error on the email field.
    @Override
    public void showEmailError(String message) {
        editTextLoginEmail.setError(message);
    }

    // Show an error on the password field.
    @Override
    public void showPasswordError(String message) {
        editTextLoginPassword.setError(message);
    }

    // Move the user to the home screen after login.
    @Override
    public void showLoginSuccess() {
        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();

        // Clear the old fragment history after login.
        getParentFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction =
                getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, new HomeFragment());
        transaction.commit();
    }

    // Display an unsuccessful login message.
    @Override
    public void showLoginError(String message) {
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