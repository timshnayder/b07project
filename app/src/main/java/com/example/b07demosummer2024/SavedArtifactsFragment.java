package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SavedArtifactsFragment extends Fragment {
    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private List<Artifact> savedArtifactsList;
    private TextView textViewEmptyState;
    private Button buttonBack;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference userSavedRef;
    private DatabaseReference artifactsRef;
    private String currentUserId;
    private final Set<String> savedLotNumbers = new HashSet<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_artifacts, container, false);
        
        textViewEmptyState = view.findViewById(R.id.textViewEmptyState);
        buttonBack = view.findViewById(R.id.buttonBack);

        savedArtifactsList = new ArrayList<>();
        artifactAdapter = new ArtifactAdapter(savedArtifactsList, artifact -> loadDetailFragment(artifact));

        recyclerView = view.findViewById(R.id.recyclerViewSaved);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(artifactAdapter);

        setupButtons();

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }

        if (currentUserId != null) {
            userSavedRef = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/")
                .getReference("users")
                .child(currentUserId)
                .child("saved");
            artifactsRef = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/")
                .getReference("artifacts");

            loadSavedArtifacts();
        } else {
            textViewEmptyState.setVisibility(View.VISIBLE);
        }

        return view;
    }

    private void setupButtons(){
        buttonBack.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void loadSavedArtifacts() {
        userSavedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot savedSnapshot) {
                savedLotNumbers.clear();
                for (DataSnapshot snapshot : savedSnapshot.getChildren()) {
                    String lotNumber = snapshot.getKey();
                    Boolean isSaved = snapshot.getValue(Boolean.class);
                    if (lotNumber != null && Boolean.TRUE.equals(isSaved)) {
                        savedLotNumbers.add(lotNumber);
                    }
                }

                if (savedLotNumbers.isEmpty()) {
                    //no saved artifacts so show empty state
                    savedArtifactsList.clear();
                    artifactAdapter.updateArtifacts(savedArtifactsList);
                    textViewEmptyState.setVisibility(View.VISIBLE);
                } else {
                    //has saved artifacts so show list
                    textViewEmptyState.setVisibility(View.GONE);
                    fetchArtifactDetails();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void fetchArtifactDetails() {
        artifactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot artifactsSnapshot) {
                savedArtifactsList.clear();
                for (DataSnapshot snapshot : artifactsSnapshot.getChildren()) {
                    Artifact artifact = snapshot.getValue(Artifact.class);
                    if (artifact != null && savedLotNumbers.contains(artifact.getLotNumber())) {
                        savedArtifactsList.add(artifact);
                    }
                }

                artifactAdapter.updateArtifacts(savedArtifactsList);

                if (savedArtifactsList.isEmpty()) {
                    //no saved artifacts so show empty state
                    textViewEmptyState.setVisibility(View.VISIBLE);
                } else {
                    //has saved artifacts so show list
                    textViewEmptyState.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void loadDetailFragment(Artifact artifact) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, ArtifactDetailFragment.newInstance(artifact));
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
