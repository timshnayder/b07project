package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

public class ArtifactDetailFragment extends Fragment {
    private Artifact artifact;

    public static ArtifactDetailFragment newInstance(Artifact artifact) {
        ArtifactDetailFragment fragment = new ArtifactDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable("artifact", artifact);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            artifact = (Artifact) getArguments().getSerializable("artifact");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artifact_detail, container, false);

        ImageView imageViewDetailArtifact = view.findViewById(R.id.imageViewDetailArtifact);
        TextView textViewName = view.findViewById(R.id.textViewDetailName);
        TextView textViewLotNumber = view.findViewById(R.id.textViewDetailLotNumber);
        TextView textViewCategory = view.findViewById(R.id.textViewDetailCategory);
        TextView textViewDynastyPeriod = view.findViewById(R.id.textViewDetailDynastyPeriod);
        TextView textViewMaterial = view.findViewById(R.id.textViewDetailMaterial);
        
        TextView textViewCulturalOrigin = view.findViewById(R.id.textViewDetailCulturalOrigin);
        TextView textViewDimensions = view.findViewById(R.id.textViewDetailDimensions);
        TextView textViewConditionReport = view.findViewById(R.id.textViewDetailConditionReport);
        TextView textViewCurrentLocation = view.findViewById(R.id.textViewDetailCurrentLocation);
        TextView textViewAcquisitionMethod = view.findViewById(R.id.textViewDetailAcquisitionMethod);
        TextView textViewProvenance = view.findViewById(R.id.textViewDetailProvenance);
        TextView textViewAccessionNumber = view.findViewById(R.id.textViewDetailAccessionNumber);
        TextView textViewNotes = view.findViewById(R.id.textViewDetailNotes);
        
        TextView textViewDescription = view.findViewById(R.id.textViewDetailDescription);
        Button buttonBack = view.findViewById(R.id.buttonDetailBack);

        if (artifact != null) {
            Glide.with(this)
                    .load(artifact.getImageUrl())
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(imageViewDetailArtifact);
            setFieldText(textViewName, artifact.getName());
            setFieldText(textViewLotNumber, artifact.getLotNumber());
            setFieldText(textViewCategory, artifact.getCategory());
            setFieldText(textViewDynastyPeriod, artifact.getDynastyPeriod());
            setFieldText(textViewMaterial, artifact.getMaterial());
            
            setFieldText(textViewCulturalOrigin, artifact.getCulturalOrigin());
            setFieldText(textViewDimensions, artifact.getDimensions());
            setFieldText(textViewConditionReport, artifact.getConditionReport());
            setFieldText(textViewCurrentLocation, artifact.getCurrentLocation());
            setFieldText(textViewAcquisitionMethod, artifact.getAcquisitionMethod());
            setFieldText(textViewProvenance, artifact.getProvenance());
            setFieldText(textViewAccessionNumber, artifact.getAccessionNumber());
            setFieldText(textViewNotes, artifact.getNotes());
            
            setFieldText(textViewDescription, artifact.getDescription());
        }

        //pop fragment to go back to browse
        buttonBack.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    /** Changes the text of textView to value
     *
     * @param textView text box to change value of
     * @param value new string that textView will use
     */
    private void setFieldText(TextView textView, String value) {
        if (textView == null) return;
        if (value != null && !value.trim().isEmpty()) {
            textView.setText(value);
        } else {
            //this function just exists so i dont have to do a lot of lambdas
            textView.setText("N/A");
        }
    }
}
