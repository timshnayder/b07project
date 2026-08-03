package com.example.b07demosummer2024;

public class SignupPresenter implements SignupContract.Presenter {

    private final SignupContract.View view;
    private final SignupContract.Model model;

    public SignupPresenter(SignupContract.View view, SignupContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void signup(String email, String username, String password) {
        String trimmedEmail = email == null ? "" : email.trim();
        String trimmedUsername = username == null ? "" : username.trim();
        String trimmedPassword = password == null ? "" : password.trim();

        if (trimmedEmail.isEmpty()) {
            view.showEmailError("Email is required");
            return;
        }

        if (trimmedUsername.isEmpty()) {
            view.showUsernameError("Username is required");
            return;
        }

        if (trimmedPassword.isEmpty()) {
            view.showPasswordError("Password is required");
            return;
        }

        if (trimmedPassword.length() < 6) {
            view.showPasswordError("Password must be at least 6 characters");
            return;
        }

        view.showLoading();

        model.signup(
                trimmedEmail,
                trimmedUsername,
                trimmedPassword,
                new LoginContract.LoginCallback() {
                    @Override
                    public void onSuccess() {
                        view.hideLoading();
                        view.showSignupSuccess();
                    }

                    @Override
                    public void onFailure(String message) {
                        view.hideLoading();
                        view.showSignupError(message);
                    }
                }
        );
    }
}