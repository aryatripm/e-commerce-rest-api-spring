package id.arya.portofolio.ecommerce.address;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAddressRequest {
    @NotBlank
    private String address;
    @NotBlank
    private String city;
    @NotBlank
    private String province;
    @NotBlank
    private String postalCode;
    @NotBlank
    private String phoneNumber;
}
