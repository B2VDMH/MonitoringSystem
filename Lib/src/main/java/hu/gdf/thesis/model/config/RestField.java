
package hu.gdf.thesis.model.config;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("jsonschema2pojo")
public class RestField {

    @SerializedName("fieldPath")
    @Expose
    private String fieldPath;
    @SerializedName("operations")
    @Expose
    private List<Operation> operation = new ArrayList<>();

    public String getFieldPath() {
        return fieldPath;
    }

    public void setFieldPath(String fieldPath) {
        this.fieldPath = fieldPath;
    }

    public List<Operation> getOperation() {
        return operation;
    }

    public void setOperation(List<Operation> operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return fieldPath;
    }
}
