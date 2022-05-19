package hu.gdf.thesis.model.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.*;

@Getter
@Setter
@EqualsAndHashCode
public class Address {

    @SerializedName("addresses")
    @Expose
    private String address;

    @Override
    public String toString() {
        return address;
    }
}
