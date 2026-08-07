package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// this is the home page displayed after a successful login.
// regular users can browse, view their saved artifacts, and logout
// admin users can additionally be given access to artifact management
// the user's role is retrieved from firebase realtime database
public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // get the home page layout
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);
        // get references to the navigation buttons
        Button buttonBrowseArtifacts = view.findViewById(R.id.buttonBrowseArtifacts);
        Button buttonSavedArtifacts = view.findViewById(R.id.buttonSavedArtifacts);
        Button buttonManageArtifacts = view.findViewById(R.id.buttonManageArtifacts);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);

        // artifact management is an admin-only feature,
        // so keep the button hidden until the current user's role has been successfully verified
        buttonManageArtifacts.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // if not empty, load the logged-in user's role from firebase
        if (currentUser != null) {
            DatabaseReference roleRef = FirebaseDatabase
                    .getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/")
                    .getReference("users")
                    .child(currentUser.getUid())
                    .child("role");
            //the role only needs to be read once when the home screen is created,
            // so a single-value listener is used.

            roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String role = snapshot.getValue(String.class);
                    // Show admin management controls only to admins.
                    if ("admin".equals(role)) {
                        buttonManageArtifacts.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Keep admin buttons hidden if the role cannot be loaded.
                    buttonManageArtifacts.setVisibility(View.GONE);
                }
            });
        }
        // Open the artifact browsing/search page.
        buttonBrowseArtifacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new BrowseArtifactsFragment());
            }
        });
        // Open the current user's saved artifact collection.
        buttonSavedArtifacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new SavedArtifactsFragment());
            }
        });
        // Open admin artifact management.
        buttonManageArtifacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ManageArtifactsFragment());
            }
        });
        // Sign the user out, clear the navigation history, and return to the login screen.
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                getParentFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new LoginFragment());
                transaction.commit();
            }
        });

        return view;
    }
    // Replaces the current fragment with the requested page and
    // adds the current page to the Android back stack.
    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
