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

public class HomeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_home_fragment, container, false);

        Button buttonBrowseArtifacts = view.findViewById(R.id.buttonBrowseArtifacts);
        Button buttonScroller = view.findViewById(R.id.buttonSavedArtifacts);
        Button buttonManageArtifacts = view.findViewById(R.id.buttonManageArtifacts);
        Button buttonAddArtifact = view.findViewById(R.id.buttonAddArtifact);
        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonManageArtifacts.setVisibility(View.GONE);
        buttonAddArtifact.setVisibility(View.GONE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            DatabaseReference roleRef = FirebaseDatabase
                    .getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/")
                    .getReference("users")
                    .child(currentUser.getUid())
                    .child("role");

            roleRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String role = snapshot.getValue(String.class);

                    if ("admin".equals(role)) {
                        buttonManageArtifacts.setVisibility(View.VISIBLE);
                        buttonAddArtifact.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Keep admin buttons hidden if the role cannot be loaded.
                    buttonManageArtifacts.setVisibility(View.GONE);
                    buttonAddArtifact.setVisibility(View.GONE);
                }
            });
        }

        buttonBrowseArtifacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new BrowseArtifactsFragment());
            }
        });

        buttonScroller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                loadFragment(new ScrollerFragment());
            }
        });

        buttonManageArtifacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new ManageArtifactsFragment());
            }
        });

        buttonAddArtifact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddArtifactFragment());
            }
        });

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

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
