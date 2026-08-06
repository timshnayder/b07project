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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> comments;
    private boolean isUserAdmin;
    private OnCommentDeleteListener deleteListener;

    public interface OnCommentDeleteListener {
        void onDeleteComment(String commentId);
    }

    public CommentAdapter(List<Comment> comments, boolean isUserAdmin, OnCommentDeleteListener deleteListener) {
        this.comments = comments;
        this.isUserAdmin = isUserAdmin;
        this.deleteListener = deleteListener;
    }

    public void setUserAdmin(boolean isAdmin) {
        this.isUserAdmin = isAdmin;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.usernameTextView.setText(comment.getUsername());
        holder.textTextView.setText(comment.getText());
        holder.timestampTextView.setText(formatTimestamp(comment.getTimestamp()));

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

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTextView;
        TextView textTextView;
        TextView timestampTextView;
        ImageButton deleteButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTextView = itemView.findViewById(R.id.textViewCommentUsername);
            textTextView = itemView.findViewById(R.id.textViewCommentText);
            timestampTextView = itemView.findViewById(R.id.textViewCommentTimestamp);
            deleteButton = itemView.findViewById(R.id.buttonDeleteComment);
        }
    }
}
