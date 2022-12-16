
package hu.gdf.thesis.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Category {

    @SerializedName("type")
    private String type;

    @SerializedName("endpoints")
    private List<Endpoint> endpoints = new ArrayList<>();

    @Override
    public String toString() {
        return type;

    }

}
