package api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressResponse extends BaseModel {
    private String display;
    private String uuid;
    private Boolean preferred;
    private String cityVillage;
    private String country;
    private String address1;
    private String address2;
    private String stateProvince;
    private String postalCode;
}
