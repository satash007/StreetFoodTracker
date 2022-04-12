package com.info3604.streetfoodtracker;

import com.info3604.streetfoodtracker.model.PlacesPOJO;
import com.info3604.streetfoodtracker.model.VendorModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchData {
    // static variable single_instance of type Singleton
    private static SearchData single_instance = null;

    private List<VendorModel> vendorModels;
    public List<PlacesPOJO.CustomA> results;

    // private constructor restricted to this class itself
    private SearchData() {
        this.vendorModels = new ArrayList<>();
        this.results = new ArrayList<>();

    }

    // static method to create instance of Singleton class
    public static SearchData getInstance() {
        if (single_instance == null)
            single_instance = new SearchData();

        return single_instance;
    }

    public void setStores(List<VendorModel> list) {
        Collections.sort(list, new Comparator<VendorModel>() {
            @Override
            public int compare(final VendorModel lhs, VendorModel rhs) {
                double ratingOne = Double.parseDouble(lhs.rating);
                double ratingTwo = Double.parseDouble(rhs.rating);

                if (ratingOne == ratingTwo)
                    return 0;
                if (ratingOne > ratingTwo)
                    return -1;

                return 1;
            }
        });
        vendorModels = list;
    }

    public List<VendorModel> getStoreModels() {
        return vendorModels;
    }

    public List<PlacesPOJO.CustomA> getResults() {
        return results;
    }

    public void setResults(List<PlacesPOJO.CustomA> results) {
        this.results = results;
    }

}
