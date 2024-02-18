package id.arya.portofolio.ecommerce.address;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateAddressRequest {
    private Integer id;
    private String address;
    private String city;
    private String province;
    private String postalCode;
    private String phoneNumber;
    private String username;
}
