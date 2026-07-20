package com.example.b07demosummer2024;

public interface SignupContract {

    interface View {
        void showEmailError(String message);

        void showUsernameError(String message);

        void showPasswordError(String message);

        void showSignupSuccess();

        void showSignupError(String message);

        void showLoading();

        void hideLoading();
    }

    interface Presenter {
        void signup(String email, String username, String password);
    }

    interface Model {
        void signup(String email,
                    String password,
                    LoginContract.LoginCallback callback);
    }
}