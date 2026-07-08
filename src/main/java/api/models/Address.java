package api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    private String cityVillage;
    private String country;
    private String address1;
    private String address2;
    private String stateProvince;
    private String postalCode;
}
