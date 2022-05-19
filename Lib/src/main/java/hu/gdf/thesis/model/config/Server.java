
package hu.gdf.thesis.model.config;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Server {

    @SerializedName("host")
    @Expose
    private String host;
    @SerializedName("port")
    @Expose
    private Integer port;
    @SerializedName("refreshTimer")
    @Expose
    private Integer refreshTimer;
    @SerializedName("addresses")
    @Expose
    private List<Address> addresses = new ArrayList<>();
    @SerializedName("categories")
    @Expose
    private List<Category> categories = new ArrayList<>();


}
