package com.example.b07demosummer2024;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Adapter used to display artifact comments in a RecyclerView.
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private boolean isUserAdmin;
    private OnCommentDeleteListener deleteListener;

    // Allows the fragment to handle comment deletion.
    public interface OnCommentDeleteListener {
        void onDeleteComment(String commentId);
    }

    // Creates the adapter with comments and current admin permissions.
    public CommentAdapter(List<Comment> comments, boolean isUserAdmin, OnCommentDeleteListener deleteListener) {
        this.comments = comments;
        this.isUserAdmin = isUserAdmin;
        this.deleteListener = deleteListener;
    }

    // Updates whether the current user has admin access.
    public void setUserAdmin(boolean isAdmin) {
        this.isUserAdmin = isAdmin;
        notifyDataSetChanged();
    }

    // Creates the layout used for each comment.
    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    // Displays the information for one comment.
    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        holder.usernameTextView.setText(comment.getUsername());
        holder.textTextView.setText(comment.getText());
        holder.timestampTextView.setText(formatTimestamp(comment.getTimestamp()));

        // Only admins can see and use the delete button.
        if (isUserAdmin) {
            holder.deleteButton.setVisibility(View.VISIBLE);

            holder.deleteButton.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onDeleteComment(comment.getCommentId());
                }
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    // Returns the number of comments in the list.
    @Override
    public int getItemCount() {
        return comments.size();
    }

    // Converts the timestamp into a readable date and time.
    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Holds the UI components for one comment.
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView textTextView;
        TextView timestampTextView;
        ImageButton deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            // Connect each variable to its view in comment_item.
            usernameTextView = itemView.findViewById(R.id.textViewCommentUsername);
            textTextView = itemView.findViewById(R.id.textViewCommentText);
            timestampTextView = itemView.findViewById(R.id.textViewCommentTimestamp);
            deleteButton = itemView.findViewById(R.id.buttonDeleteComment);
        }
    }
}