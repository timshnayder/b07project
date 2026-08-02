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

public class LoginFragment extends Fragment implements LoginContract.View {

    private EditText editTextLoginEmail;
    private EditText editTextLoginPassword;
    private LoginContract.Presenter presenter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextLoginEmail = view.findViewById(R.id.editTextLoginEmail);
        editTextLoginPassword = view.findViewById(R.id.editTextLoginPassword);
        Button buttonLogin = view.findViewById(R.id.buttonLogin);
        Button buttonGoToSignup = view.findViewById(R.id.buttonGoToSignup);

        presenter = new LoginPresenter(this, new LoginModel());

        buttonLogin.setOnClickListener(v -> {
            String email = editTextLoginEmail.getText().toString();
            String password = editTextLoginPassword.getText().toString();

            presenter.login(email, password);
        });

        buttonGoToSignup.setOnClickListener(v -> {
            FragmentTransaction transaction =
                    getParentFragmentManager().beginTransaction();

            transaction.replace(R.id.fragment_container, new SignupFragment());
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    @Override
    public void showEmailError(String message) {
        editTextLoginEmail.setError(message);
    }

    @Override
    public void showPasswordError(String message) {
        editTextLoginPassword.setError(message);
    }

    @Override
    public void showLoginSuccess() {
        Toast.makeText(requireContext(), "Login successful", Toast.LENGTH_SHORT).show();

        getParentFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);

        FragmentTransaction transaction =
                getParentFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, new HomeFragment());
        transaction.commit();
    }

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