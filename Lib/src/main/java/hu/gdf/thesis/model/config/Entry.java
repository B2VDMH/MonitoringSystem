
package hu.gdf.thesis.model.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class Entry {

    @SerializedName("restURL")
    @Expose
    private String restURL;
    @SerializedName("restFields")
    @Expose
    private List<RestField> restFields = new ArrayList<>();

    public String getRestURL() {
        return restURL;
    }

    public void setRestURL(String restURL) {
        this.restURL = restURL;
    }

    public List<RestField> getRestFields() {
        return restFields;
    }

    public void setRestFields(List<RestField> restFields) {
        this.restFields = restFields;
    }

    @Override
    public String toString() {
        return restURL;

    }
}
