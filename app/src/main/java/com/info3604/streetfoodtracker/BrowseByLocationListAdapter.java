package com.info3604.streetfoodtracker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class BrowseByLocationListAdapter extends FirebaseRecyclerAdapter<LocationTypeFirebaseModel, BrowseByLocationListAdapter.LocationTypeViewholder> {

    private Activity activity;

    public BrowseByLocationListAdapter(@NonNull FirebaseRecyclerOptions<LocationTypeFirebaseModel> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull LocationTypeViewholder holder, int position, @NonNull LocationTypeFirebaseModel locationTypeModel)
    {

        holder.locationName.setText(locationTypeModel.getLocationName());

        Picasso.get()
                .load(locationTypeModel.getImageLink())
                .placeholder(R.drawable.logo_streetfoodtracker)
                .resize(200, 200)
                //.transform(new CircleTransform())
                .centerCrop()
                .into(holder.image);


        holder.relLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(locationTypeModel.getLocationName());

                ViewPager mviewPager = (ViewPager) activity.findViewById(R.id.viewpager);

                mviewPager.setCurrentItem(1);

            }
        });
    }


    @NonNull
    @Override
    public LocationTypeViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vertical_list, parent, false);
        return new BrowseByLocationListAdapter.LocationTypeViewholder(view);
    }


    class LocationTypeViewholder
            extends RecyclerView.ViewHolder {
        TextView locationName;
        ImageView image;
        RelativeLayout relLayout;

        public LocationTypeViewholder(@NonNull View itemView)
        {
            super(itemView);

            locationName = itemView.findViewById(R.id.txtview);
            image = itemView.findViewById(R.id.imageview);
            relLayout = itemView.findViewById(R.id.container);
        }
    }

}


/*
REFERENCES:
https://www.geeksforgeeks.org/how-to-populate-recyclerview-with-firebase-data-using-firebaseui-in-android-studio/
https://stackoverflow.com/questions/46886569/how-to-move-to-another-tab-without-clicking-the-tab-on-android-studio
 */