package com.info3604.streetfoodtracker;

import android.app.Application;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.ViewHolder {

    TextView comment_Time_Date, userComment;
    public CommentsAdapter(@NonNull View itemView) {
        super(itemView);
    }

    public void Comments(Application application, String Username, String Comment, String Date, String Time) {
        comment_Time_Date = itemView.findViewById(R.id.comment_time_date);
        userComment = itemView.findViewById(R.id.user_comment);


        // Displays the date and time as well as the user's comment to the activity_user_comments xml
        comment_Time_Date.setText(Date + "-" + Time);
        userComment.setText(Username + ":" + Comment);
    }
}
