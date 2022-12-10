
package hu.gdf.thesis.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Config {

    @SerializedName("host")
    private String host;
    @SerializedName("port")
    private Integer port;
    @SerializedName("refreshTimer")
    private Integer refreshTimer;
    @SerializedName("addresses")
    private List<Address> addresses = new ArrayList<>();
    @SerializedName("categories")
    private List<Category> categories = new ArrayList<>();

}
