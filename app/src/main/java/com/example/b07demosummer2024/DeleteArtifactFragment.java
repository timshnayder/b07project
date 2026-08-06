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

public class DeleteArtifactFragment extends Fragment {
    private EditText editTextLotNumber;
    private Button buttonDelete;
    private Button buttonBack;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_artifact, container, false);

        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        buttonDelete = view.findViewById(R.id.buttonDelete);
        buttonBack = view.findViewById(R.id.buttonBack);

        db = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/");
        artifactsRef = db.getReference("artifacts");

        buttonDelete.setOnClickListener(v -> {
            String lotNumber = editTextLotNumber.getText().toString().trim();

            if (lotNumber.isEmpty()) {
                editTextLotNumber.setError("Lot Number is required");
                return;
            }

            new AlertDialog.Builder(requireContext())
                    .setTitle("Delete Artifact")
                    .setMessage("Are you sure you want to delete artifact " + lotNumber + "?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteByLotNumber())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void deleteByLotNumber() {
        String lotNumber = editTextLotNumber.getText().toString().trim();
        if (lotNumber.isEmpty()) {
            editTextLotNumber.setError("Lot Number is required");
            return;
        }

        buttonDelete.setEnabled(false);
        artifactsRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                buttonDelete.setEnabled(true);
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}