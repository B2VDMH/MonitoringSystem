
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
public class RestField {

    @SerializedName("fieldPath")
    @Expose
    private String fieldPath;
    @SerializedName("operations")
    @Expose
    private List<Operation> operations = new ArrayList<>();

    @Override
    public String toString() {
        return fieldPath;
    }
}
