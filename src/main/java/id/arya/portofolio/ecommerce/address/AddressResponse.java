package id.arya.portofolio.ecommerce.address;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Integer id;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String phoneNumber;
}
