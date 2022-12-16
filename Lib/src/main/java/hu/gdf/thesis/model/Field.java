
package hu.gdf.thesis.model;

import java.util.ArrayList;
import java.util.List;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Data
public class Field {

    @SerializedName("fieldPath")
    private String fieldPath;
    @SerializedName("operations")
    private List<Operation> operations = new ArrayList<>();

    @Override
    public String toString() {
        return fieldPath;
    }
}
