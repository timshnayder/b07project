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

public class EditItemFragment extends Fragment {

    private EditText editTextId;
    private EditText editTextTitle;
    private EditText editTextAuthor;
    private EditText editTextGenre;
    private EditText editTextDescription;

    private DatabaseReference categoriesRef;
    private String currentCategory;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(
                R.layout.fragment_edit_item,
                container,
                false
        );

        editTextId = view.findViewById(R.id.editTextEditId);
        editTextTitle = view.findViewById(R.id.editTextEditTitle);
        editTextAuthor = view.findViewById(R.id.editTextEditAuthor);
        editTextGenre = view.findViewById(R.id.editTextEditGenre);
        editTextDescription =
                view.findViewById(R.id.editTextEditDescription);

        Button buttonLoad =
                view.findViewById(R.id.buttonLoadItem);
        Button buttonUpdate =
                view.findViewById(R.id.buttonUpdateItem);
        Button buttonBack =
                view.findViewById(R.id.buttonEditBack);

        categoriesRef = FirebaseDatabase
                .getInstance()
                .getReference("categories");

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
            editTextId.setError("Item ID is required");
            return;
        }

        categoriesRef.get()
                .addOnSuccessListener(snapshot -> {
                    boolean found = false;

                    for (DataSnapshot categorySnapshot
                            : snapshot.getChildren()) {

                        DataSnapshot itemSnapshot =
                                categorySnapshot.child(id);

                        if (itemSnapshot.exists()) {
                            Item item =
                                    itemSnapshot.getValue(Item.class);

                            if (item != null) {
                                currentCategory =
                                        categorySnapshot.getKey();

                                editTextTitle.setText(item.getArtifactName());
                                editTextAuthor.setText(item.getMaterial());
                                editTextGenre.setText(item.getDynastyPeriod());
                                editTextDescription.setText(
                                        item.getDescription()
                                );

                                found = true;
                                break;
                            }
                        }
                    }

                    if (!found) {
                        Toast.makeText(
                                getContext(),
                                "Item not found",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .addOnFailureListener(error ->
                        Toast.makeText(
                                getContext(),
                                "Failed to load item",
                                Toast.LENGTH_SHORT
                        ).show()
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

        if (currentCategory == null) {
            Toast.makeText(
                    getContext(),
                    "Load an item before updating",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        Item updatedItem = new Item(
                id,                 // lotNumber
                title,              // artifactName
                description,
                currentCategory,    // category
                author,             // material
                genre,              // dynastyPeriod
                "",                 // culturalOrigin
                "",                 // dimensions
                "",                 // conditionReport
                "",                 // currentLocation
                "",                 // acquisitionMethod
                "",                 // provenance
                "",                 // accessionNumber
                "",                 // notes
                ""                  // imageUrl
        );

        categoriesRef
                .child(currentCategory)
                .child(id)
                .setValue(updatedItem)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(
                                getContext(),
                                "Item updated",
                                Toast.LENGTH_SHORT
                        ).show();
                    } else {
                        Toast.makeText(
                                getContext(),
                                "Failed to update item",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }
}
