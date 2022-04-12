package com.info3604.streetfoodtracker;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.info3604.streetfoodtracker.ImageHandling.CircleTransform;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class BrowseByFoodTypeListAdapter extends FirebaseRecyclerAdapter<FoodTypeFirebaseModel, BrowseByFoodTypeListAdapter.FoodTypeViewholder> {

    private Activity activity;

    public BrowseByFoodTypeListAdapter(@NonNull FirebaseRecyclerOptions<FoodTypeFirebaseModel> options, Activity activity) {
        super(options);
        this.activity = activity;
    }

    @Override
    protected void onBindViewHolder(@NonNull FoodTypeViewholder holder, int position, @NonNull FoodTypeFirebaseModel foodTypeModel)
    {

        holder.foodName.setText(foodTypeModel.getFoodName());

        Picasso.get()
                .load(foodTypeModel.getImageLink())
                .placeholder(R.drawable.logo_streetfoodtracker)
                .resize(200, 200)
                .transform(new CircleTransform())
                //.centerCrop()
                .into(holder.image);


        holder.linLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EventBus.getDefault().post(foodTypeModel.getFoodName());

                ViewPager mviewPager = (ViewPager) activity.findViewById(R.id.viewpager);

                mviewPager.setCurrentItem(1);

            }
        });


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 3s = 3000ms
                SharedPreferences pref = activity.getSharedPreferences("TutSharedPreferences",0);
                SharedPreferences.Editor editor= pref.edit();
                boolean firstRun = pref.getBoolean("firstRunExploreTutorial4", true);

                if(firstRun) {
                    final TapTargetSequence sequence = new TapTargetSequence(activity)
                            .targets(
                                    TapTarget.forView(holder.cardLayout, "Browse by Food Type", "Tap any food type to view all the vendors that sell them.")
                                            // All options below are optional
                                            //.outerCircleColor(R.color.myPrimaryColorTransparent)      // Specify a color for the outer circle
                                            //.outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                                            .targetCircleColor(R.color.red_200)   // Specify a color for the target circle
                                            .titleTextSize(30)                  // Specify the size (in sp) of the title text
                                            .titleTextColor(R.color.black)      // Specify the color of the title text
                                            .descriptionTextSize(16)            // Specify the size (in sp) of the description text
                                            .descriptionTextColor(R.color.myColorPrimary)  // Specify the color of the description text
                                            .textColor(R.color.black)            // Specify a color for both the title and description text
                                            .textTypeface(ResourcesCompat.getFont(activity, R.font.poppins_reg))  // Specify a typeface for the text
                                            .dimColor(R.color.black)            // If set, will dim behind the view with 30% opacity of the given color
                                            .drawShadow(true)                   // Whether to draw a drop shadow or not
                                            .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                            .tintTarget(false)                   // Whether to tint the target view's color
                                            .transparentTarget(true)           // Specify whether the target is transparent (displays the content underneath)
                                            .targetRadius(80)                  // Specify the target radius (in dp)
                            );
                    sequence.start();

                    editor.putBoolean("firstRunExploreTutorial4",false);
                    editor.commit();
                }
            }
        }, 3000);
    }


    @NonNull
    @Override
    public FoodTypeViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view
                = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_horizontal_list, parent, false);
        return new BrowseByFoodTypeListAdapter.FoodTypeViewholder(view);
    }


    class FoodTypeViewholder
            extends RecyclerView.ViewHolder {
        TextView foodName;
        ImageView image;
        LinearLayout linLayout;
        CardView cardLayout;

        public FoodTypeViewholder(@NonNull View itemView)
        {
            super(itemView);

            foodName = itemView.findViewById(R.id.txtview);
            image = itemView.findViewById(R.id.imageview);
            linLayout = itemView.findViewById(R.id.linLayout);
            cardLayout = itemView.findViewById(R.id.cardLayout);

        }
    }

}


/*
REFERENCES:
https://www.geeksforgeeks.org/how-to-populate-recyclerview-with-firebase-data-using-firebaseui-in-android-studio/
https://stackoverflow.com/questions/46886569/how-to-move-to-another-tab-without-clicking-the-tab-on-android-studio
 */