package com.example.b07demosummer2024;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArtifactDetailFragment extends Fragment {
    private Artifact artifact;
    private Button buttonLike;
    private TextView textViewLikeCount;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference artifactsRef;
    private String currentUserId;
    private boolean userHasLiked = false;
    private String artifactId;

    private EditText editTextCommentInput;
    private Button buttonPostComment;
    private RecyclerView recyclerViewComments;
    private CommentAdapter commentAdapter;
    private List<Comment> commentsList = new ArrayList<>();
    private String currentUsername;
    private boolean isCurrentUserAdmin = false;

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

        firebaseAuth = FirebaseAuth.getInstance();
        artifactsRef = FirebaseDatabase.getInstance().getReference("artifacts");
        if (firebaseAuth.getCurrentUser() != null) {
            currentUserId = firebaseAuth.getCurrentUser().getUid();
        }

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
        buttonLike = view.findViewById(R.id.buttonLike);
        textViewLikeCount = view.findViewById(R.id.textViewLikeCount);
        editTextCommentInput = view.findViewById(R.id.editTextCommentInput);
        buttonPostComment = view.findViewById(R.id.buttonPostComment);
        recyclerViewComments = view.findViewById(R.id.recyclerViewComments);

        setupCommentsRecyclerView();

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

            artifactId = artifact.getLotNumber();
            loadLikeData();
            setupLikeButton();
            checkUserAdminRole();
            loadCommentsData();
            setupCommentButton();
        }

        //pop fragment to go back to browse
        buttonBack.setOnClickListener(v -> {
            if (getParentFragmentManager() != null) {
                getParentFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void setFieldText(TextView textView, String value) {
        if (textView == null) return;
        if (value != null && !value.trim().isEmpty()) {
            textView.setText(value);
        } else {
            textView.setText("N/A");
        }
    }

    private void loadLikeData() {
        if (artifactId == null || currentUserId == null) return;

        artifactsRef.child(artifactId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Long likeCount = dataSnapshot.child("likeCount").getValue(Long.class);
                    if (likeCount != null) {
                        artifact.setLikeCount(likeCount);
                    }
                    updateLikeCountDisplay();

                    DataSnapshot likedBySnapshot = dataSnapshot.child("likedBy");
                    userHasLiked = likedBySnapshot.hasChild(currentUserId);
                    updateLikeButton();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading likes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupLikeButton() {
        if (currentUserId == null) {
            buttonLike.setEnabled(false);
            buttonLike.setText("Login to Like");
            return;
        }

        buttonLike.setOnClickListener(v -> toggleLike());
    }

    private void toggleLike() {
        if (artifactId == null || currentUserId == null || artifact == null) return;

        DatabaseReference artifactRef = artifactsRef.child(artifactId);

        if (userHasLiked) {
            artifact.setLikeCount(Math.max(0, artifact.getLikeCount() - 1));
            artifactRef.child("likedBy").child(currentUserId).removeValue();
            userHasLiked = false;
        } else {
            artifact.setLikeCount(artifact.getLikeCount() + 1);
            artifactRef.child("likedBy").child(currentUserId).setValue(true);
            userHasLiked = true;
        }

        updateLikeCountDisplay();
        updateLikeButton();
        artifactRef.child("likeCount").setValue(artifact.getLikeCount());
    }

    private void updateLikeCountDisplay() {
        if (textViewLikeCount != null && artifact != null) {
            long likeCount = artifact.getLikeCount();
            String likeText = likeCount + " " + (likeCount == 1 ? "Like" : "Likes");
            textViewLikeCount.setText(likeText);
        }
    }

    private void updateLikeButton() {
        if (buttonLike == null) return;

        if (userHasLiked) {
            buttonLike.setText("❤️ Liked");
            buttonLike.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#FF6F00")));
        } else {
            buttonLike.setText("🤍 Like");
            buttonLike.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    android.graphics.Color.parseColor("#999999")));
        }
    }

    private void setupCommentsRecyclerView() {
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(getContext()));
        CommentAdapter.OnCommentDeleteListener deleteListener = commentId -> deleteComment(commentId);
        commentAdapter = new CommentAdapter(commentsList, isCurrentUserAdmin, deleteListener);
        recyclerViewComments.setAdapter(commentAdapter);
    }

    private void loadCommentsData() {
        if (artifactId == null) return;

        artifactsRef.child(artifactId).child("comments").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();

                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    if (comment != null) {
                        comment.setCommentId(commentSnapshot.getKey());
                        commentsList.add(comment);
                    }
                }

                Collections.sort(commentsList, (c1, c2) -> Long.compare(c2.getTimestamp(), c1.getTimestamp()));
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error loading comments", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupCommentButton() {
        if (currentUserId == null) {
            editTextCommentInput.setEnabled(false);
            buttonPostComment.setEnabled(false);
            editTextCommentInput.setHint("Login to comment");
            return;
        }

        getCurrentUsername();
        buttonPostComment.setOnClickListener(v -> postComment());
    }

    private void getCurrentUsername() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(currentUserId).child("username").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUsername = dataSnapshot.getValue(String.class);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void postComment() {
        String commentText = editTextCommentInput.getText().toString().trim();

        if (commentText.isEmpty()) {
            Toast.makeText(getContext(), "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentUserId == null || currentUsername == null) {
            Toast.makeText(getContext(), "Please login to comment", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment newComment = new Comment();
        newComment.setUserId(currentUserId);
        newComment.setUsername(currentUsername);
        newComment.setText(commentText);
        newComment.setTimestamp(System.currentTimeMillis());

        DatabaseReference commentsRef = artifactsRef.child(artifactId).child("comments");
        String commentId = commentsRef.push().getKey();

        if (commentId != null) {
            commentsRef.child(commentId).setValue(newComment);
            editTextCommentInput.setText("");
            Toast.makeText(getContext(), "Comment posted", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkUserAdminRole() {
        if (currentUserId == null) {
            isCurrentUserAdmin = false;
            return;
        }

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.child(currentUserId).child("role").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String role = snapshot.getValue(String.class);
                isCurrentUserAdmin = "admin".equals(role);
                if (commentAdapter != null) {
                    commentAdapter.setUserAdmin(isCurrentUserAdmin);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                isCurrentUserAdmin = false;
            }
        });
    }

    private void deleteComment(String commentId) {
        if (artifactId == null) return;

        DatabaseReference commentRef = artifactsRef.child(artifactId).child("comments").child(commentId);
        commentRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getContext(), "Comment deleted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Failed to delete comment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
