package id.arya.portofolio.ecommerce.payment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePaymentRequest {
    private PaymentType paymentType;
    @NotBlank
    private String accountNumber;
    @NotBlank
    private String accountName;
    @NotBlank
    private String provider;
    private Date expiry;
}
