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

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditArtifactFragment extends Fragment {

    private EditText editTextLotNumber, editTextName, editTextDescription;
    private EditText editTextCulturalOrigin, editTextDimensions, editTextConditionReport;
    private EditText editTextCurrentLocation, editTextAcquisitionMethod, editTextProvenance;
    private EditText editTextAccessionNumber, editTextNotes;

    private Spinner spinnerMaterial, spinnerCategory, spinnerDynastyPeriod;
    private Button buttonLoad, buttonUpdate, buttonSelectImage;
    private ImageView imageViewPreview;

    private FirebaseDatabase db;
    private DatabaseReference artifactsRef;
    private Artifact loadedArtifact;

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
                        imageViewPreview.setImageURI(uri);
                        imageViewPreview.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_artifact, container, false);

        editTextLotNumber = view.findViewById(R.id.editTextEditLotNumber);
        editTextName = view.findViewById(R.id.editTextEditName);
        editTextDescription = view.findViewById(R.id.editTextEditDescription);

        editTextCulturalOrigin = view.findViewById(R.id.editTextEditCulturalOrigin);
        editTextDimensions = view.findViewById(R.id.editTextEditDimensions);
        editTextConditionReport = view.findViewById(R.id.editTextEditConditionReport);
        editTextCurrentLocation = view.findViewById(R.id.editTextEditCurrentLocation);
        editTextAcquisitionMethod = view.findViewById(R.id.editTextEditAcquisitionMethod);
        editTextProvenance = view.findViewById(R.id.editTextEditProvenance);
        editTextAccessionNumber = view.findViewById(R.id.editTextEditAccessionNumber);
        editTextNotes = view.findViewById(R.id.editTextEditNotes);

        spinnerMaterial = view.findViewById(R.id.spinnerEditMaterial);
        spinnerCategory = view.findViewById(R.id.spinnerEditCategory);
        spinnerDynastyPeriod = view.findViewById(R.id.spinnerEditDynastyPeriod);

        buttonLoad = view.findViewById(R.id.buttonLoadArtifact);
        buttonUpdate = view.findViewById(R.id.buttonUpdateArtifact);
        buttonSelectImage = view.findViewById(R.id.buttonSelectEditImage);
        imageViewPreview = view.findViewById(R.id.imageViewEditPreview);

        Button buttonBack = view.findViewById(R.id.buttonEditBack);

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

        db = FirebaseDatabase.getInstance(
                "https://b07project-97f73-default-rtdb.firebaseio.com/");
        artifactsRef = db.getReference("artifacts");

        buttonUpdate.setEnabled(false);

        buttonLoad.setOnClickListener(v -> loadItem());
        buttonUpdate.setOnClickListener(v -> updateItem());
        buttonSelectImage.setOnClickListener(v -> selectImageLauncher.launch("image/*"));
        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    //load artifact using lot number
    private void loadItem() {
        String lotNumber = editTextLotNumber.getText().toString().trim();

        if (lotNumber.isEmpty()) {
            editTextLotNumber.setError("Lot Number is required");
            return;
        }

        artifactsRef.child(lotNumber).get()
                .addOnSuccessListener(snapshot -> {
                    if (snapshot.exists()) {
                        loadedArtifact = snapshot.getValue(Artifact.class);

                        if (loadedArtifact != null) {
                            editTextName.setText(loadedArtifact.getName());
                            editTextDescription.setText(loadedArtifact.getDescription());
                            editTextCulturalOrigin.setText(loadedArtifact.getCulturalOrigin());
                            editTextDimensions.setText(loadedArtifact.getDimensions());
                            editTextConditionReport.setText(loadedArtifact.getConditionReport());
                            editTextCurrentLocation.setText(loadedArtifact.getCurrentLocation());
                            editTextAcquisitionMethod.setText(loadedArtifact.getAcquisitionMethod());
                            editTextProvenance.setText(loadedArtifact.getProvenance());
                            editTextAccessionNumber.setText(loadedArtifact.getAccessionNumber());
                            editTextNotes.setText(loadedArtifact.getNotes());

                            setSpinner(spinnerCategory, loadedArtifact.getCategory());
                            setSpinner(spinnerMaterial, loadedArtifact.getMaterial());
                            setSpinner(spinnerDynastyPeriod, loadedArtifact.getDynastyPeriod());

                            if (loadedArtifact.getImageUrl() != null
                                    && !loadedArtifact.getImageUrl().isEmpty()) {

                                imageViewPreview.setVisibility(View.VISIBLE);

                                Glide.with(this)
                                        .load(loadedArtifact.getImageUrl())
                                        .placeholder(android.R.drawable.ic_menu_gallery)
                                        .into(imageViewPreview);
                            }

                            //lot number cannot be changed after loading
                            editTextLotNumber.setEnabled(false);
                            buttonLoad.setEnabled(false);
                            buttonUpdate.setEnabled(true);

                            Toast.makeText(getContext(),
                                    "Artifact loaded successfully",
                                    Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getContext(),
                                "Artifact not found",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(error ->
                        Toast.makeText(getContext(),
                                "Failed to load artifact",
                                Toast.LENGTH_SHORT).show()
                );
    }

    //update the loaded artifact
    private void updateItem() {
        if (loadedArtifact == null) {
            Toast.makeText(getContext(),
                    "Load an artifact before updating",
                    Toast.LENGTH_SHORT).show();
            return;
        }

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

        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(getContext(),
                    "Please fill out mandatory fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            buttonUpdate.setEnabled(false);

            imageUploader.uploadImage(
                    selectedImageUri,
                    loadedArtifact.getLotNumber(),
                    new SupabaseImageUploader.UploadCallback() {
                        @Override
                        public void onSuccess(String publicUrl) {
                            saveItem(name, description, category, material, dynastyPeriod,
                                    culturalOrigin, dimensions, conditionReport, currentLocation,
                                    acquisitionMethod, provenance, accessionNumber, notes, publicUrl);
                        }

                        @Override
                        public void onError(String message) {
                            buttonUpdate.setEnabled(true);
                            Toast.makeText(getContext(),
                                    "Image upload failed: " + message,
                                    Toast.LENGTH_LONG).show();
                        }
                    });

        } else {
            saveItem(name, description, category, material, dynastyPeriod,
                    culturalOrigin, dimensions, conditionReport, currentLocation,
                    acquisitionMethod, provenance, accessionNumber, notes,
                    loadedArtifact.getImageUrl());
        }
    }

    //save all edited fields back into firebase
    private void saveItem(
            String name, String description, String category,
            String material, String dynastyPeriod, String culturalOrigin,
            String dimensions, String conditionReport, String currentLocation,
            String acquisitionMethod, String provenance,
            String accessionNumber, String notes, String imageUrl) {

        loadedArtifact.setName(name);
        loadedArtifact.setDescription(description);
        loadedArtifact.setCategory(category);
        loadedArtifact.setMaterial(material);
        loadedArtifact.setDynastyPeriod(dynastyPeriod);

        loadedArtifact.setCulturalOrigin(culturalOrigin);
        loadedArtifact.setDimensions(dimensions);
        loadedArtifact.setConditionReport(conditionReport);
        loadedArtifact.setCurrentLocation(currentLocation);
        loadedArtifact.setAcquisitionMethod(acquisitionMethod);
        loadedArtifact.setProvenance(provenance);
        loadedArtifact.setAccessionNumber(accessionNumber);
        loadedArtifact.setNotes(notes);
        loadedArtifact.setImageUrl(imageUrl);

        artifactsRef.child(loadedArtifact.getLotNumber())
                .setValue(loadedArtifact)
                .addOnCompleteListener(task -> {
                    buttonUpdate.setEnabled(true);

                    if (task.isSuccessful()) {
                        selectedImageUri = null;

                        Toast.makeText(getContext(),
                                "Artifact updated",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(),
                                "Failed to update artifact",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //find the matching dropdown value from the loaded artifact
    private void setSpinner(Spinner spinner, String value) {
        if (value == null) return;

        for (int i = 0; i < spinner.getCount(); i++) {
            if (value.equals(spinner.getItemAtPosition(i).toString())) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}