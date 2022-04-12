package com.info3604.streetfoodtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.info3604.streetfoodtracker.model.maproutes.Comment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserCommentsActivity extends AppCompatActivity {

    private RecyclerView RecyclerView_Comments;
    private Button post_comment;
    private EditText EditText_comment;
    DatabaseReference databaseReference,vendor_ref;
    private String user_key;
    private String vendorId;

    Comment comments;   // Comment model initialized

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);

        user_key = getIntent().getExtras().getString("user_key");
        vendorId = getIntent().getExtras().getString("Vendor_Uid");

        comments = new Comment();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user_key;

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Userbase");

        // Database reference to the Comments location
        vendor_ref = FirebaseDatabase.getInstance().getReference().child("Comments");

        RecyclerView_Comments = findViewById(R.id.comments_Recycler_View);
        RecyclerView_Comments.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        RecyclerView_Comments.setLayoutManager(linearLayoutManager);

        post_comment = findViewById(R.id.post_comment_btn);
        EditText_comment = findViewById(R.id.EditText_comment);

        // on click listener to submit a comment and store in the Firebase Database
        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String UserName = dataSnapshot.child("name").getValue().toString();
                            addComment(UserName);
                            EditText_comment.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            // Function to add the comment details such as the comment, commenter username, date and time
            private void addComment(String UserName) {
                String comment_post = EditText_comment.getText().toString();
                if (comment_post.isEmpty()) {
                    Toast.makeText(UserCommentsActivity.this, "please enter a comment", Toast.LENGTH_SHORT).show();
                } else {
                    Calendar date = Calendar.getInstance();
                    SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
                    final String saveCurDate = currentDate.format(date.getTime());

                    Calendar time = Calendar.getInstance();
                    SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm");
                    final String saveCurTime = currentTime.format(time.getTime());

                    final String id = userId + saveCurDate + saveCurTime;

                    HashMap commentMap = new HashMap();
                    commentMap.put("uid",userId);
                    commentMap.put("Comment",comment_post);
                    commentMap.put("Date", saveCurDate);
                    commentMap.put("Time", saveCurTime);
                    //commentMap.put("Vendor Name", Vendor);
                    commentMap.put("Username", UserName);

                    vendor_ref.child(id).updateChildren(commentMap).addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(UserCommentsActivity.this, "Successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UserCommentsActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // a Firebase Recycler Adapter to fetch and display all the comments for a vendor as a view
        FirebaseRecyclerOptions<Comment>options =
                new FirebaseRecyclerOptions.Builder<Comment>()
                .setQuery(vendor_ref,Comment.class)
                .build();
        FirebaseRecyclerAdapter<Comment,CommentsAdapter> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Comment, CommentsAdapter>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull CommentsAdapter holder, int position, @NonNull Comment model) {
                        holder.Comments(getApplication(),model.getComment(),model.getDate(),model.getTime(),model.getUsername());
                    }

                    @NonNull
                    @Override
                    public CommentsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext())
                                .inflate(R.layout.comment_item,parent,false);

                        return new CommentsAdapter(view);
                    }
                };
        firebaseRecyclerAdapter.startListening();
        RecyclerView_Comments.setAdapter(firebaseRecyclerAdapter);
    }
}