package id.arya.portofolio.ecommerce.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private Integer id;
    private PaymentType paymentType;
    private String accountNumber;
    private String accountName;
    private String provider;
    private Date expiry;
}
