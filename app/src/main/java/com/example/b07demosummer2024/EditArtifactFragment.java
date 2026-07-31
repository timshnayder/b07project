package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditArtifactFragment extends Fragment {

    private EditText editTextId;
    private EditText editTextTitle;
    private EditText editTextAuthor;
    private EditText editTextGenre;
    private EditText editTextDescription;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;
    private Artifact loadedArtifact;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_edit_artifact,
                container,
                false
        );

        editTextId = view.findViewById(R.id.editTextEditId);
        editTextTitle = view.findViewById(R.id.editTextEditTitle);
        editTextAuthor = view.findViewById(R.id.editTextEditAuthor);
        editTextGenre = view.findViewById(R.id.editTextEditGenre);
        editTextDescription = view.findViewById(R.id.editTextEditDescription);

        Button buttonLoad =
                view.findViewById(R.id.buttonLoadItem);
        Button buttonUpdate =
                view.findViewById(R.id.buttonUpdateItem);
        Button buttonBack =
                view.findViewById(R.id.buttonEditBack);

        db = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/");
        artifactsRef = db.getReference("artifacts");

        buttonLoad.setOnClickListener(v -> loadItem());
        buttonUpdate.setOnClickListener(v -> updateItem());
        buttonBack.setOnClickListener(
                v -> getParentFragmentManager().popBackStack()
        );

        return view;
    }

    private void loadItem() {
        String id = editTextId.getText().toString().trim();

        if (id.isEmpty()) {
            editTextId.setError("Lot Number / ID is required");
            return;
        }

        artifactsRef.child(id).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        loadedArtifact = snapshot.getValue(Artifact.class);
                        if (loadedArtifact != null) {
                            editTextTitle.setText(loadedArtifact.getName());
                            editTextAuthor.setText(loadedArtifact.getMaterial());
                            editTextGenre.setText(loadedArtifact.getDynastyPeriod());
                            editTextDescription.setText(loadedArtifact.getDescription());
                            Toast.makeText(getContext(), "Artifact loaded successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Artifact not found", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(error ->
                        Toast.makeText(getContext(), "Failed to load artifact", Toast.LENGTH_SHORT).show()
                );
    }

    private void updateItem() {
        String id = editTextId.getText().toString().trim();
        String title =
                editTextTitle.getText().toString().trim();
        String author =
                editTextAuthor.getText().toString().trim();
        String genre =
                editTextGenre.getText().toString().trim();
        String description =
                editTextDescription.getText().toString().trim();

        if (id.isEmpty()
                || title.isEmpty()
                || author.isEmpty()
                || genre.isEmpty()
                || description.isEmpty()) {

            Toast.makeText(
                    getContext(),
                    "Please fill out all fields",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        if (loadedArtifact == null || !id.equals(loadedArtifact.getLotNumber())) {
            Toast.makeText(getContext(), "Load the artifact before updating.", Toast.LENGTH_SHORT).show();
            return;
        }

        loadedArtifact.setName(title);
        loadedArtifact.setMaterial(author);
        loadedArtifact.setDynastyPeriod(genre);
        loadedArtifact.setDescription(description);

        artifactsRef.child(id).setValue(loadedArtifact)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                getContext(),
                                "Artifact updated",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                getContext(),
                                "Failed to update artifact",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}