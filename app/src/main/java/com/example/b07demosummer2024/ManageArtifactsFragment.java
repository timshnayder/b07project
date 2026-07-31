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

public class ManageArtifactsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_artifacts, container, false);

        Button buttonAddArtifact = view.findViewById(R.id.buttonAddArtifact);
        Button buttonEditArtifact = view.findViewById(R.id.buttonEditArtifact);
        Button buttonDeleteArtifact = view.findViewById(R.id.buttonDeleteArtifact);
        Button buttonBack = view.findViewById(R.id.buttonBack);

        buttonAddArtifact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFragment(new AddArtifactFragment());
            }
        });

        buttonEditArtifact.setOnClickListener(v -> loadFragment(new EditArtifactFragment()));

        buttonDeleteArtifact.setOnClickListener(v -> loadFragment(new DeleteArtifactFragment()));

        buttonBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
