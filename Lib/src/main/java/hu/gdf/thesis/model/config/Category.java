
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
public class Category {

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("entries")
    @Expose
    private List<Entry> entries = new ArrayList<>();

    @Override
    public String toString() {
        return type;

    }

}
