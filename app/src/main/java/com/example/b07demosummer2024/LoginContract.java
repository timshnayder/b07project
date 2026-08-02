package com.example.b07demosummer2024;

public interface LoginContract {

    interface View {
        void showEmailError(String message);

        void showPasswordError(String message);

        void showLoginSuccess();

        void showLoginError(String message);

        void showLoading();

        void hideLoading();
    }

    interface Presenter {
        void login(String email, String password);
    }

    interface Model {
        void login(String email, String password, LoginCallback callback);
    }

    interface LoginCallback {
        void onSuccess();

        void onFailure(String message);
    }
}