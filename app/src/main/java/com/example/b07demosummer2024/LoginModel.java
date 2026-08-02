package com.example.b07demosummer2024;

import com.google.firebase.auth.FirebaseAuth;

public class LoginModel implements LoginContract.Model {

    private final FirebaseAuth firebaseAuth;

    public LoginModel() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void login(String email,
                      String password,
                      LoginContract.LoginCallback callback) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String message = "Login failed";

                        if (task.getException() != null &&
                                task.getException().getMessage() != null) {
                            message = task.getException().getMessage();
                        }

                        callback.onFailure(message);
                    }
                });
    }
}