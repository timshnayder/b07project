package com.example.b07demosummer2024;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.HashMap;
import java.util.Map;

public class SignupModel implements SignupContract.Model {

    private final FirebaseAuth firebaseAuth;

    public SignupModel() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void signup(String email, String username, String password, LoginContract.LoginCallback callback) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/").getReference("users");

        //check if username is taken
        usersRef.orderByChild("username").equalTo(username).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onFailure("Username is already in use");
                    return; 
                }

                //register user
                //email already checked if in use by createUserWithEmailAndPassword
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || firebaseAuth.getCurrentUser() == null) {
                        String message = task.getException() != null && task.getException().getMessage() != null
                                ? task.getException().getMessage()
                                : "Sign-up failed";
                        callback.onFailure(message);
                        return; 
                    }

                    String uid = firebaseAuth.getCurrentUser().getUid();

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("uid", uid);
                    userMap.put("username", username);
                    userMap.put("email", email);
                    userMap.put("role", "user");

                    usersRef.child(uid).setValue(userMap).addOnCompleteListener(dbTask -> {
                        if (dbTask.isSuccessful()) {
                            callback.onSuccess();
                        } else {
                            //clean up if db write fails
                            if (firebaseAuth.getCurrentUser() != null) {
                                firebaseAuth.getCurrentUser().delete();
                            }
                            String dbMessage = dbTask.getException() != null ? dbTask.getException().getMessage() : "Failed to save user data";
                            callback.onFailure(dbMessage);
                        }
                    });
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}