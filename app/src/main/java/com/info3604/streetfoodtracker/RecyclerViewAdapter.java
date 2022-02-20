package com.info3604.streetfoodtracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private List<PlacesPOJO.CustomA> stLstStores;
    private List<VendorModel> models;

    public RecyclerViewAdapter(List<PlacesPOJO.CustomA> stores, List<VendorModel> vendorModels) {
        stLstStores = stores;
        models = vendorModels;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.store_list_row, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.setData(stLstStores.get(holder.getAdapterPosition()), holder, models.get(holder.getAdapterPosition()));
    }


    @Override
    public int getItemCount() {
        return Math.min(models.size(), stLstStores.size());
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtStoreName;
        TextView txtStoreAddr;
        TextView txtStoreDist;
        VendorModel model;

        public MyViewHolder(View itemView) {
            super(itemView);
            this.txtStoreDist = (TextView) itemView.findViewById(R.id.txtStoreDist);
            this.txtStoreName = (TextView) itemView.findViewById(R.id.txtStoreName);
            this.txtStoreAddr = (TextView) itemView.findViewById(R.id.txtStoreAddr);
        }

        public void setData(PlacesPOJO.CustomA info, MyViewHolder holder, VendorModel vendorModel) {
            this.model = vendorModel;
            holder.txtStoreDist.setText("Rating: " + model.rating);
            holder.txtStoreName.setText(info.name);
            holder.txtStoreAddr.setText(info.vicinity);
        }
    }
}

