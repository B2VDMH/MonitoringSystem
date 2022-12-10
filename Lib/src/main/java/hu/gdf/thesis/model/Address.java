package hu.gdf.thesis.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Data
public class Address {

    @SerializedName("address")
    private String address;

    @Override
    public String toString() {
        return address;
    }
}
