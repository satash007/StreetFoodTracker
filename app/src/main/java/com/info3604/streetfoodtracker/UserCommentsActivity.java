package com.info3604.streetfoodtracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserCommentsActivity extends AppCompatActivity {

    private RecyclerView comments_Recycler_View;
    private Button post_comment;
    private EditText EditText_comment;
    DatabaseReference databaseReference,vendor_ref;
    private String vendor_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_comments);

        vendor_key = getIntent().getExtras().getString("uID");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId = user.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserBase");

        vendor_ref = FirebaseDatabase.getInstance().getReference().child(vendor_key).child("comments");

        comments_Recycler_View = findViewById(R.id.comments_Recycler_View);
        comments_Recycler_View.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        comments_Recycler_View.setLayoutManager(linearLayoutManager);

        post_comment = findViewById(R.id.post_comment_btn);
        EditText_comment = findViewById(R.id.EditText_comment);

        post_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                databaseReference.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            String username = dataSnapshot.child("name").getValue().toString();
                            addComment(username);
                            EditText_comment.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            private void addComment(String username) {
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
                    commentMap.put("Vendor Name", username);

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

}