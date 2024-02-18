package id.arya.portofolio.ecommerce.payment;

import id.arya.portofolio.ecommerce.util.ValidationService;
import id.arya.portofolio.ecommerce.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    public PaymentResponse create(CreatePaymentRequest request, String username) {
        validationService.validate(request);

        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var payment = Payment.builder()
                .paymentType(request.getPaymentType())
                .provider(request.getProvider())
                .accountNumber(request.getAccountNumber())
                .accountName(request.getAccountName())
                .expiry(request.getExpiry())
                .user(user)
                .build();

        paymentRepository.save(payment);

        return toPaymentResponse(payment);
    }

    public PaymentResponse get(Integer id, String username) {
        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var payment = paymentRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        return toPaymentResponse(payment);
    }

    public List<PaymentResponse> list(String username) {
        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var payments = paymentRepository.findAllByUser(user);

        return payments.stream()
                .map(this::toPaymentResponse)
                .collect(Collectors.toList());
    }

    public PaymentResponse update(Integer id, UpdatePaymentRequest request, String username) {
        validationService.validate(request);

        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var payment = paymentRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        payment.setPaymentType(request.getPaymentType());
        payment.setProvider(request.getProvider());
        payment.setAccountNumber(request.getAccountNumber());
        payment.setAccountName(request.getAccountName());
        payment.setExpiry(request.getExpiry());

        paymentRepository.save(payment);

        return toPaymentResponse(payment);
    }

    public void delete(Integer id, String username) {
        var user = userRepository.findById(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        var payment = paymentRepository.findFirstByUserAndId(user, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        paymentRepository.delete(payment);
    }

    private PaymentResponse toPaymentResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentType(payment.getPaymentType())
                .provider(payment.getProvider())
                .accountNumber(payment.getAccountNumber())
                .accountName(payment.getAccountName())
                .expiry(payment.getExpiry())
                .build();
    }
}
