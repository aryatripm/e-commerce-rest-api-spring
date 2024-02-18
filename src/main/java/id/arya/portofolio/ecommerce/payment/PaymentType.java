package id.arya.portofolio.ecommerce.payment;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum PaymentType {

        CREDIT_CARD,
        BANK_TRANSFER,
        E_WALLET;

}
