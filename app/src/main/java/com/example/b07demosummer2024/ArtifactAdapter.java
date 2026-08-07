package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter class for binding a list of {@link Artifact} objects to a RecyclerView.
 * This adapter handles local filtering (searching) and pagination logic.
 */
public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    // List of artifacts currently displayed on the current page
    private List<Artifact> artifactList;
    // Complete list of all artifacts fetched from the database
    private List<Artifact> fullList;
    // List of artifacts matching the search query, before pagination limits are applied
    private List<Artifact> filteredList;
    // Total number of pages available based on the filtered list and pagination limit
    private int totalPages = 0;
    // Current active page index (0-indexed)
    private int curPage = 0;
    // Maximum number of items per page. -1 indicates no limit (show all)
    private int curLimit = -1;
    // The query text currently used to filter artifacts
    private String curQuery = "";
    // Callback listener for handling clicks on artifact items
    private OnArtifactClickListener clickListener;

    /**
     * Interface definition for a callback to be invoked when an artifact is clicked.
     */
    public interface OnArtifactClickListener {
        /**
         * Called when an artifact item has been clicked.
         *
         * @param artifact The artifact that was clicked.
         */
        void onArtifactClick(Artifact artifact);
    }

    /**
     * Constructs a new ArtifactAdapter.
     *
     * @param artifactList The initial list of artifacts to display.
     * @param clickListener The listener for artifact click events.
     */
    public ArtifactAdapter(List<Artifact> artifactList, OnArtifactClickListener clickListener) {
        this.artifactList = new ArrayList<>(artifactList);
        this.fullList = new ArrayList<>(artifactList);
        this.filteredList = new ArrayList<>(artifactList);
        this.clickListener = clickListener;
    }

    /**
     * Updates the full list of artifacts and reapplies the current filter and limit.
     *
     * @param newArtifacts The new list of artifacts.
     */
    public void updateArtifacts(List<Artifact> newArtifacts){
        this.fullList = new ArrayList<>(newArtifacts);
        filter(curQuery, curLimit);
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate individual artifact item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_artifact_adapter, parent, false);
        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        // Retrieve artifact for the current position
        Artifact artifact = artifactList.get(position);
        holder.textViewName.setText(artifact.getName());
        String categoryAndPeriod = artifact.getCategory() + " | " + artifact.getDynastyPeriod();
        holder.textViewCategoryAndPeriod.setText(categoryAndPeriod);
        holder.textViewDescription.setText(artifact.getDescription());
 
        // Load artifact image from URL, using a default gallery placeholder if loading
        Glide.with(holder.itemView.getContext())
                .load(artifact.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imageViewArtifact);
 
        // Set item click listener to trigger the callback
        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onArtifactClick(artifact);
            }
        });
    }

    @Override
    public int getItemCount() {
        return artifactList.size();
    }

    /** Filters fullList into filteredList so that text is contained somewhere in artifact data,
     * and updates totalPages based on how many items in the filtered list, and pagination limit.
     *
     * @param text The text query from user
     * @param limit The limit of pages to view
     */
    public void filter(String text, int limit){
        this.curQuery = text;
        this.curLimit = limit;
        filteredList.clear();
        if(text==null || text.trim().isEmpty()){
            filteredList.addAll(fullList);
        }else{
            String search = text.trim().toLowerCase();
            for(Artifact artifact: fullList){
                if((artifact.getName() != null && artifact.getName().contains(search)) ||
                        (artifact.getDescription() != null && artifact.getDescription().contains(search)) ||
                        (artifact.getCategory() != null && artifact.getCategory().contains(search)) ||
                        (artifact.getDynastyPeriod() != null && artifact.getDynastyPeriod().contains(search)) ||
                        (artifact.getMaterial() != null && artifact.getMaterial().contains(search))){
                    filteredList.add(artifact);
                }
            }
        }

        if (limit > 0 && filteredList.size() > limit){
            totalPages = (int) Math.ceil((double) filteredList.size()/limit);
        }else{
            totalPages = 1;
        }
        loadPage(0);
    }

    /** Updates artifact list so it only contains the filtered artifacts based on the current page
     *
     * @param page The page number that the user is currently on
     */
    public void loadPage(int page){
        // pages are 0-indexed
        if(page < 0){
            page = 0;
        }
        if(page>=totalPages){
            page=totalPages-1;
        }
        curPage=page;
        artifactList.clear();
        if(curLimit > 0 && !filteredList.isEmpty()){
            artifactList.addAll(filteredList.subList(curPage * curLimit, Math.min(curPage * curLimit + curLimit, filteredList.size())));
        }else{
            artifactList.addAll(filteredList);
        }
        notifyDataSetChanged();
    }

    /**
     * ViewHolder class that caches child views for a single artifact list item
     * to avoid repeated findViewById lookups.
     */
    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewCategoryAndPeriod, textViewDescription;
        ImageView imageViewArtifact;
 
        /**
         * Constructs a new ArtifactViewHolder and binds the UI elements.
         *
         * @param itemView The parent view of the artifact item.
         */
        public ArtifactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCategoryAndPeriod = itemView.findViewById(R.id.textViewCategoryAndPeriod);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imageViewArtifact = itemView.findViewById(R.id.imageViewArtifact);
        }
    }
    /**
     * Gets the total number of pages based on current filters and page limit.
     *
     * @return The total number of pages.
     */
    public int getTotalPages(){
        return totalPages;
    }

    /**
     * Gets the current active page index (0-indexed).
     *
     * @return The current page number.
     */
    public int getCurPage(){
        return curPage;
    }
}
