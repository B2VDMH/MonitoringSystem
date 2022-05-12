
package hu.gdf.thesis.model.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
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
    @SerializedName("categories")
    @Expose
    private List<Category> categories = new ArrayList<>();

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getRefreshTimer() {
        return refreshTimer;
    }

    public void setRefreshTimer(Integer refreshTimer) {
        this.refreshTimer = refreshTimer;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }



}
