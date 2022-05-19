package hu.gdf.thesis.model;

import lombok.Data;

@Data
public class Response {

    private String hostName;
    private String categoryType;
    private String restURL;
    private String fieldPath;
    private String fieldValue;
    private String color;

}
