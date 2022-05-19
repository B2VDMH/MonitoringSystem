
package hu.gdf.thesis.model.config;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
public class Config {

    @SerializedName("server")
    @Expose
    private Server server;


}
