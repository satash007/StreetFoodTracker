package com.info3604.streetfoodtracker.datahandling;

import com.info3604.streetfoodtracker.model.PlacesPOJO;
import com.info3604.streetfoodtracker.model.VendorModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SearchData {
    // static variable single_instance of type Singleton
    private static SearchData single_instance = null;
    private  String searchTerm;

    private List<VendorModel> vendorModels;
    public List<PlacesPOJO.CustomA> results;

    // private constructor restricted to this class itself
    private SearchData() {
        this.vendorModels = new ArrayList<>();
        this.results = new ArrayList<>();
        this.searchTerm = new String();

    }

    // static method to create instance of Singleton class
    public static SearchData getInstance() {
        if (single_instance == null)
            single_instance = new SearchData();

        return single_instance;
    }

    public void setSearchTerm(String searchTerm){
        this.searchTerm = searchTerm;
    }

    public String getSearchTerm(){
        return searchTerm;
    }

    public void setStores(List<VendorModel> list, String type) {
        if(type.equals("rating")) {
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
        }else if(type.equals("distance")){
            Collections.sort(list, new Comparator<VendorModel>() {
                @Override
                public int compare(final VendorModel lhs, VendorModel rhs) {
                    double distanceOne = Double.parseDouble(lhs.distance);
                    double distanceTwo = Double.parseDouble(rhs.distance);

                    if (distanceOne == distanceTwo)
                        return 0;
                    if (distanceOne < distanceTwo)
                        return -1;

                    return 1;
                }
            });
            vendorModels = list;
        }
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
