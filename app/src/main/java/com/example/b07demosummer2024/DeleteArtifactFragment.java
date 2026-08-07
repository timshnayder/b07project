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
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

// Fragment used to delete artifacts using their lot number.
public class DeleteArtifactFragment extends Fragment {
    private EditText editTextLotNumber;
    private Button buttonDelete;
    private Button buttonBack;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;

    // Creates the delete artifact screen.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_artifact, container, false);

        // Connect the buttons and input field to the layout.
        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonBack = view.findViewById(R.id.buttonBack);

        // Connect to the artifacts section of Firebase.
        db = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/");
        artifactsRef = db.getReference("artifacts");

        // Check the lot number before attempting deletion.
        buttonDelete.setOnClickListener(v -> {
            String lotNumber = editTextLotNumber.getText().toString().trim();

            if (lotNumber.isEmpty()) {
                editTextLotNumber.setError("Lot Number is required");
                return;
            }

            // Ask the user to confirm before deleting the artifact.
            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Artifact")
                    .setMessage("Are you sure you want to delete artifact " + lotNumber + "?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteByLotNumber())
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // Return to the previous screen.
        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    // Finds and deletes an artifact using its lot number.
    private void deleteByLotNumber() {
        String lotNumber = editTextLotNumber.getText().toString().trim();

        if (lotNumber.isEmpty()) {
            editTextLotNumber.setError("Lot Number is required");
            return;
        }

        // Prevent duplicate clicks while Firebase is working.
        buttonDelete.setEnabled(false);

        artifactsRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Delete the artifact if the lot number exists.
                if (snapshot.exists()) {
                    snapshot.getRef().removeValue().addOnCompleteListener(task -> {
                        buttonDelete.setEnabled(true);

                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Artifact deleted successfully", Toast.LENGTH_SHORT).show();
                            editTextLotNumber.setText("");
                        } else {
                            Toast.makeText(getContext(), "Failed to delete artifact", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    buttonDelete.setEnabled(true);
                    Toast.makeText(getContext(), "Artifact with this Lot Number not found", Toast.LENGTH_SHORT).show();
                }
            }

            // Handle Firebase database errors.
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                buttonDelete.setEnabled(true);
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}