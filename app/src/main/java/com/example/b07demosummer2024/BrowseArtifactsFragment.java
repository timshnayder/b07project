package com.example.b07demosummer2024;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment that displays a list of artifacts fetched from a Firebase Database.
 * It supports searching and pagination.
 */
public class BrowseArtifactsFragment extends Fragment {
    private List<Artifact> artifactList;
    private RecyclerView recyclerView;
    private ArtifactAdapter artifactAdapter;
    private Spinner spinnerPagination;
    private SearchView searchView;
    private Button buttonPrev;
    private Button buttonNext;
    private TextView textViewPageInfo;
 
    private FirebaseDatabase db;
    private DatabaseReference itemsRef;
 
    //Current pagination limit (-1 for All, 12, 24)
    private int curLimit = -1;
    private String curQuery = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.browse_artifacts_fragment, container, false);
 
        artifactList = new ArrayList<>();
        artifactAdapter = new ArtifactAdapter(artifactList, artifact -> loadDetailFragment(artifact));
 
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(artifactAdapter);
 
        spinnerPagination = view.findViewById(R.id.spinnerPagination);
        setupSpinner();
 
        searchView = view.findViewById(R.id.searchView);
        setupSearchView();
 
        buttonPrev = view.findViewById(R.id.buttonPrev);
        buttonNext = view.findViewById(R.id.buttonNext);
        setupButtons();
 
        textViewPageInfo = view.findViewById(R.id.textViewPageInfo);
 
        db = FirebaseDatabase.getInstance("https://b07project-97f73-default-rtdb.firebaseio.com/");
        fetchArtifactsFromDatabase();
 
        return view;
    }

    /**
     * Replaces the current fragment in the fragment container and adds the transaction to the back stack.
     *
     * @param artifact The artifact whose details should be displayed.
     */
    private void loadDetailFragment(Artifact artifact) {
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, ArtifactDetailFragment.newInstance(artifact));
        transaction.addToBackStack(null);
        transaction.commit();
    }
 
    /**
     * Updates the page information text indicator with the current page and total page count.
     */
    private void updatePageInfo() {
        if (textViewPageInfo != null && artifactAdapter != null) {
            textViewPageInfo.setText("Page " + (artifactAdapter.getCurPage() + 1) + " of " + artifactAdapter.getTotalPages());
        }
    }

    /**
     * Sets up click listeners for the pagination navigation buttons (previous and next).
     */
    private void setupButtons(){
        buttonPrev.setOnClickListener(v -> {
            if(artifactAdapter.getCurPage() > 0){
                artifactAdapter.loadPage(artifactAdapter.getCurPage()-1);
                updatePageInfo();
            }
        });
        buttonNext.setOnClickListener(v -> {
            if(artifactAdapter.getCurPage() < artifactAdapter.getTotalPages()-1){
                artifactAdapter.loadPage(artifactAdapter.getCurPage()+1);
                updatePageInfo();
            }
        });
    }

    /**
     * Configures the Spinner pagination menu, loads the saved limit preference from SharedPreferences,
     * and sets up selection listeners to filter list.
     */
    private void setupSpinner(){
        String[] options = {"All", "12", "24"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPagination.setAdapter(adapter);
 
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("app_settings", Context.MODE_PRIVATE);
        curLimit = sharedPref.getInt("PAGE_LIMIT", -1);
 
        //sync Spinner state with curLimit preference
        //-1 = All
        if(curLimit == -1){
            spinnerPagination.setSelection(0);
        }else if(curLimit == 12){
            spinnerPagination.setSelection(1);
        }else{
            spinnerPagination.setSelection(2);
        }
 
        spinnerPagination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 0){
                    curLimit = -1;
                }else if(i == 1){
                    curLimit = 12;
                }else{
                    curLimit = 24;
                }
                //save chosen page limit to SharedPreferences
                sharedPref.edit().putInt("PAGE_LIMIT", curLimit).apply();
                artifactAdapter.filter(curQuery, curLimit);
                updatePageInfo();
            }
 
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
 
            }
        });
    }

    /**
     * Initializes the SearchView query listener to filter adapter artifacts on text changes or submit.
     */
    private void setupSearchView(){
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query){
                curQuery = query;
                artifactAdapter.filter(curQuery,curLimit);
                updatePageInfo();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText){
                curQuery = newText;
                artifactAdapter.filter(curQuery,curLimit);
                updatePageInfo();
                return false;
            }
        });
    }

    /**
     * Fetches the full list of artifacts from the Firebase Database.
     * Listen for updates in the artifacts node, populates artifactList, and updates the adapter and page info.
     */
    private void fetchArtifactsFromDatabase() {
        itemsRef = db.getReference("artifacts");
        itemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Artifact> loadedArtifacts = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Artifact artifact = snapshot.getValue(Artifact.class);
                    if (artifact != null) {
                        loadedArtifacts.add(artifact);
                    }
                }
                //send retrieved list into the adapter
                artifactAdapter.updateArtifacts(loadedArtifacts);
                updatePageInfo();
            }
 
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors
            }
        });
    }
}