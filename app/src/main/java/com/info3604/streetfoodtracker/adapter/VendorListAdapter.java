package com.info3604.streetfoodtracker.adapter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.info3604.streetfoodtracker.R;
import com.info3604.streetfoodtracker.model.VendorModel;

import java.util.List;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class VendorListAdapter extends RecyclerView.Adapter<VendorListAdapter.MyViewHolder> {

    private List<VendorModel> models;
    private Activity activity;

    public VendorListAdapter(Activity activity, List<VendorModel> vendorModels) {
        models = vendorModels;
        this.activity = activity;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.vendor_list_row, parent, false);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(holder, models.get(holder.getAdapterPosition()));
    }


    @Override
    public int getItemCount() {
        return models.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtVendorName;
        TextView txtVendorAddr;
        TextView txtVendorType;
        TextView txtVendorDistance;
        RatingBar rbVendorRat;
        VendorModel model;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtVendorDistance = (TextView) itemView.findViewById(R.id.txtVendorDistance);
            this.txtVendorName = (TextView) itemView.findViewById(R.id.txtVendorName);
            this.txtVendorAddr = (TextView) itemView.findViewById(R.id.txtVendorAddr);
            this.rbVendorRat = (RatingBar) itemView.findViewById(R.id.current_rating);
            this.txtVendorType = (TextView) itemView.findViewById(R.id.txtVendorType);

            ViewPager mviewPager = (ViewPager) activity.findViewById(R.id.viewpager);

            int currentFrag = mviewPager.getCurrentItem();

            if(itemView!=null && currentFrag==2)
                displayTutorial(itemView);

        }

        public void setData(MyViewHolder holder, VendorModel vendorModel) {
            this.model = vendorModel;

            holder.txtVendorName.setText(model.name);
            holder.txtVendorAddr.setText(model.address);
            holder.txtVendorDistance.setText(model.distance + "km away");
            holder.rbVendorRat.setRating(Float.parseFloat(model.rating));
            holder.txtVendorType.setText("Source: " + model.type);

            }

    }

    public void displayTutorial(View view){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 1s = 1000ms
                SharedPreferences pref = activity.getSharedPreferences("TutSharedPreferences",0);
                SharedPreferences.Editor editor= pref.edit();
                boolean firstRun = pref.getBoolean("firstRunTutorialVendorList", true);

                if(firstRun) {
                    final TapTargetSequence sequence = new TapTargetSequence(activity)
                            .targets(
                                    TapTarget.forView(view, "View Profile", "Tap on any vendor in this list to access additional information and the leave reviews/ratings.")
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
                                            .targetRadius(40)                  // Specify the target radius (in dp)
                            );
                    sequence.start();

                    editor.putBoolean("firstRunTutorialVendorList",false);
                    editor.commit();
                }
            }
        }, 1000);
    }

}

