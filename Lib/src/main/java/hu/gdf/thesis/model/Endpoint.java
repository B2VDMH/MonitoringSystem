
package hu.gdf.thesis.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Endpoint {

    @SerializedName("restURL")
    private String restURL;

    @SerializedName("alert")
    private boolean alert;

    @SerializedName("fields")
    private List<Field> fields = new ArrayList<>();

    @Override
    public String toString() {
        return restURL + " - "+"Alert: " + alert;

    }
}
