package com.example.b07demosummer2024;

public class LoginPresenter implements LoginContract.Presenter {

    private final LoginContract.View view;
    private final LoginContract.Model model;

    public LoginPresenter(LoginContract.View view, LoginContract.Model model) {
        this.view = view;
        this.model = model;
    }

    @Override
    public void login(String email, String password) {
        String trimmedEmail = email == null ? "" : email.trim();
        String trimmedPassword = password == null ? "" : password.trim();

        if (trimmedEmail.isEmpty()) {
            view.showEmailError("Email is required");
            return;
        }

        if (trimmedPassword.isEmpty()) {
            view.showPasswordError("Password is required");
            return;
        }

        view.showLoading();

        model.login(trimmedEmail, trimmedPassword, new LoginContract.LoginCallback() {
            @Override
            public void onSuccess() {
                view.hideLoading();
                view.showLoginSuccess();
            }

            @Override
            public void onFailure(String message) {
                view.hideLoading();
                view.showLoginError(message);
            }
        });
    }
}