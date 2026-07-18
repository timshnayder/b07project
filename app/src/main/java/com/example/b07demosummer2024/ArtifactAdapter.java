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

public class ArtifactAdapter extends RecyclerView.Adapter<ArtifactAdapter.ArtifactViewHolder> {
    private List<Artifact> artifactList;
    private List<Artifact> fullList;
    private List<Artifact> filteredList;
    private int totalPages = 0;
    private int curPage = 0;
    private int curLimit = -1;
    private String curQuery = "";
    private OnArtifactClickListener clickListener;

    public interface OnArtifactClickListener {
        void onArtifactClick(Artifact artifact);
    }

    public ArtifactAdapter(List<Artifact> artifactList, OnArtifactClickListener clickListener) {
        this.artifactList = new ArrayList<>(artifactList);
        this.fullList = new ArrayList<>(artifactList);
        this.filteredList = new ArrayList<>(artifactList);
        this.clickListener = clickListener;
    }

    public void updateArtifacts(List<Artifact> newArtifacts){
        this.fullList = new ArrayList<>(newArtifacts);
        filter(curQuery, curLimit);
    }

    @NonNull
    @Override
    public ArtifactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_artifact_adapter, parent, false);
        return new ArtifactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtifactViewHolder holder, int position) {
        Artifact artifact = artifactList.get(position);
        holder.textViewName.setText(artifact.getName());
        String categoryAndPeriod = artifact.getCategory() + " | " + artifact.getDynastyPeriod();
        holder.textViewCategoryAndPeriod.setText(categoryAndPeriod);
        holder.textViewDescription.setText(artifact.getDescription());

        //load image
        Glide.with(holder.itemView.getContext())
                .load(artifact.getImageUrl())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .into(holder.imageViewArtifact);

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

    //individual short artifact description
    public static class ArtifactViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName, textViewCategoryAndPeriod, textViewDescription;
        ImageView imageViewArtifact;

        public ArtifactViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewName);
            textViewCategoryAndPeriod = itemView.findViewById(R.id.textViewCategoryAndPeriod);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            imageViewArtifact = itemView.findViewById(R.id.imageViewArtifact);
        }
    }
    public int getTotalPages(){
        return totalPages;
    }
    public int getCurPage(){
        return curPage;
    }
}
