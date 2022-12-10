package hu.gdf.thesis;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
public class AlertEmailContent {

    private String serverHost;
    private String category;
    private String restURL;
    private String fieldPath;
    private String fieldValue;
    private String operator;
    private String value;


    @Override
    public String toString() {
        return "Alert! " + "\n" +
                "Server: " + serverHost + "\n" +
                "Category: " + category + "\n" +
                "REST URL: " + restURL + "\n" +
                "Field Path: " + fieldPath + "\n" +
                "Field Value: " + fieldValue + "\n" +
                "Operator: " + operator + "\n" +
                "Baseline value: " + value;
    }
}
