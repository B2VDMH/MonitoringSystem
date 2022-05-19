
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
public class Entry {

    @SerializedName("restURL")
    @Expose
    private String restURL;
    @SerializedName("alert")
    @Expose
    private boolean alert;
    @SerializedName("restFields")
    @Expose
    private List<RestField> restFields = new ArrayList<>();

    @Override
    public String toString() {
        return restURL + " - "+"Alert: " + alert;

    }
}
