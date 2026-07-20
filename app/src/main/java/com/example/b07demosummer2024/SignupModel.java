package com.example.b07demosummer2024;

import com.google.firebase.auth.FirebaseAuth;

public class SignupModel implements SignupContract.Model {

    private final FirebaseAuth firebaseAuth;

    public SignupModel() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signup(
            String email,
            String password,
            LoginContract.LoginCallback callback) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();
                    } else {
                        String message = "Sign-up failed";

                        if (task.getException() != null &&
                                task.getException().getMessage() != null) {
                            message = task.getException().getMessage();
                        }

                        callback.onFailure(message);
                    }
                });
    }
}