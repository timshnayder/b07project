package com.example.b07demosummer2024;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddArtifactFragment extends Fragment {
    private EditText editTextLotNumber, editTextName, editTextDescription;
    private EditText editTextCulturalOrigin, editTextDimensions, editTextConditionReport;
    private EditText editTextCurrentLocation, editTextAcquisitionMethod, editTextProvenance;
    private EditText editTextAccessionNumber, editTextNotes;
    
    private Spinner spinnerMaterial, spinnerCategory, spinnerDynastyPeriod;
    private Button buttonSelectImage;
    private ImageView imageViewArtifactPreview;
    private Button buttonAdd;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;

    private Uri selectedImageUri;
    private ActivityResultLauncher<String> selectImageLauncher;
    private SupabaseImageUploader imageUploader;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageUploader = new SupabaseImageUploader(requireContext());
        selectImageLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imageViewArtifactPreview.setImageURI(uri);
                    imageViewArtifactPreview.setVisibility(View.VISIBLE);
                }
            }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_artifact, container, false);

        editTextLotNumber = view.findViewById(R.id.editTextLotNumber);
        editTextName = view.findViewById(R.id.editTextName);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        
        editTextCulturalOrigin = view.findViewById(R.id.editTextCulturalOrigin);
        editTextDimensions = view.findViewById(R.id.editTextDimensions);
        editTextConditionReport = view.findViewById(R.id.editTextConditionReport);
        editTextCurrentLocation = view.findViewById(R.id.editTextCurrentLocation);
        editTextAcquisitionMethod = view.findViewById(R.id.editTextAcquisitionMethod);
        editTextProvenance = view.findViewById(R.id.editTextProvenance);
        editTextAccessionNumber = view.findViewById(R.id.editTextAccessionNumber);
        editTextNotes = view.findViewById(R.id.editTextNotes);

        spinnerMaterial = view.findViewById(R.id.spinnerMaterial);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        spinnerDynastyPeriod = view.findViewById(R.id.spinnerDynastyPeriod);
        
        buttonSelectImage = view.findViewById(R.id.buttonSelectImage);
        imageViewArtifactPreview = view.findViewById(R.id.imageViewArtifactPreview);
        buttonAdd = view.findViewById(R.id.buttonAdd);

        //setup all spinners
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getContext(),
                R.array.materials_array, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaterial.setAdapter(adapter1);

        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(getContext(),
                R.array.categories_array, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter2);

        ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(getContext(),
                R.array.dynasty_periods_array, android.R.layout.simple_spinner_item);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDynastyPeriod.setAdapter(adapter3);

        db = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/");

        buttonSelectImage.setOnClickListener(v -> selectImageLauncher.launch("image/*"));

        buttonAdd.setOnClickListener(v -> addArtifact());

        return view;
    }

    //Tries to add artifact with given inputs to the firebase database
    private void addArtifact() {
        String lotNumber = editTextLotNumber.getText().toString().trim();
        String name = editTextName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        String category = spinnerCategory.getSelectedItem().toString();
        String material = spinnerMaterial.getSelectedItem().toString();
        String dynastyPeriod = spinnerDynastyPeriod.getSelectedItem().toString();

        String culturalOrigin = editTextCulturalOrigin.getText().toString().trim();
        String dimensions = editTextDimensions.getText().toString().trim();
        String conditionReport = editTextConditionReport.getText().toString().trim();
        String currentLocation = editTextCurrentLocation.getText().toString().trim();
        String acquisitionMethod = editTextAcquisitionMethod.getText().toString().trim();
        String provenance = editTextProvenance.getText().toString().trim();
        String accessionNumber = editTextAccessionNumber.getText().toString().trim();
        String notes = editTextNotes.getText().toString().trim();

        //mandatory fields
        if (lotNumber.isEmpty() || name.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(), "Please fill out mandatory fields", Toast.LENGTH_SHORT).show();
            return;
        }

        artifactsRef = db.getReference("artifacts");

        if (selectedImageUri != null) {
            //disable add while uploading so no weird errors
            buttonAdd.setEnabled(false);
            Toast.makeText(getContext(), "Uploading image to Supabase...", Toast.LENGTH_SHORT).show();
            imageUploader.uploadImage(selectedImageUri, lotNumber, new SupabaseImageUploader.UploadCallback() {
                @Override
                public void onSuccess(String publicUrl) {
                    saveArtifactToFirebase(lotNumber, name, description, category, material, dynastyPeriod,
                            culturalOrigin, dimensions, conditionReport, currentLocation, acquisitionMethod,
                            provenance, accessionNumber, notes, publicUrl);
                }

                @Override
                public void onError(String message) {
                    buttonAdd.setEnabled(true);
                    Toast.makeText(getContext(), "Image upload failed: " + message, Toast.LENGTH_LONG).show();
                }
            });
        } else {
            //no image so just use placeholder
            saveArtifactToFirebase(lotNumber, name, description, category, material, dynastyPeriod,
                    culturalOrigin, dimensions, conditionReport, currentLocation, acquisitionMethod,
                    provenance, accessionNumber, notes, "https://via.placeholder.com/150");
        }
    }

    private void saveArtifactToFirebase(String lotNumber, String name, String description,
                                        String category, String material, String dynastyPeriod,
                                        String culturalOrigin, String dimensions, String conditionReport,
                                        String currentLocation, String acquisitionMethod, String provenance,
                                        String accessionNumber, String notes, String imageUrl) {
        artifactsRef.child(lotNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // lot number must be unique
                if (snapshot.exists()) {
                    buttonAdd.setEnabled(true);
                    Toast.makeText(getContext(), "Lot Number already exists! Must be unique.", Toast.LENGTH_SHORT).show();
                } else {
                    //create new artifact object with all fields filled out
                    Artifact artifact = new Artifact();
                    artifact.setLotNumber(lotNumber);
                    artifact.setName(name);
                    artifact.setDescription(description);
                    artifact.setCategory(category);
                    artifact.setMaterial(material);
                    artifact.setDynastyPeriod(dynastyPeriod);
                    
                    artifact.setCulturalOrigin(culturalOrigin.isEmpty() ? "" : culturalOrigin);
                    artifact.setDimensions(dimensions.isEmpty() ? "" : dimensions);
                    artifact.setConditionReport(conditionReport.isEmpty() ? "" : conditionReport);
                    artifact.setCurrentLocation(currentLocation.isEmpty() ? "" : currentLocation);
                    artifact.setAcquisitionMethod(acquisitionMethod.isEmpty() ? "" : acquisitionMethod);
                    artifact.setProvenance(provenance.isEmpty() ? "" : provenance);
                    artifact.setAccessionNumber(accessionNumber.isEmpty() ? "" : accessionNumber);
                    artifact.setNotes(notes.isEmpty() ? "" : notes);
                    artifact.setImageUrl(imageUrl);

                    //put the artifact into the firebase
                    artifactsRef.child(lotNumber).setValue(artifact).addOnCompleteListener(task -> {
                        buttonAdd.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Artifact added", Toast.LENGTH_SHORT).show();
                            // Clear all inputs
                            editTextLotNumber.setText("");
                            editTextName.setText("");
                            editTextDescription.setText("");
                            editTextCulturalOrigin.setText("");
                            editTextDimensions.setText("");
                            editTextConditionReport.setText("");
                            editTextCurrentLocation.setText("");
                            editTextAcquisitionMethod.setText("");
                            editTextProvenance.setText("");
                            editTextAccessionNumber.setText("");
                            editTextNotes.setText("");
                            imageViewArtifactPreview.setVisibility(View.GONE);
                            selectedImageUri = null;
                        } else {
                            Toast.makeText(getContext(), "Failed to add artifact", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                buttonAdd.setEnabled(true);
                Toast.makeText(getContext(), "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
