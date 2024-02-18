package id.arya.portofolio.ecommerce.payment;

import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping(
            path = "/{username}/payments",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<PaymentResponse> create(@RequestBody CreatePaymentRequest request, @PathVariable String username) {
        PaymentResponse response = paymentService.create(request, username);
        return WebResponse.<PaymentResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/{username}/payments/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<PaymentResponse> get(@PathVariable Integer id, @PathVariable String username) {
        PaymentResponse response = paymentService.get(id, username);
        return WebResponse.<PaymentResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/{username}/payments",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<PaymentResponse>> getAll(@PathVariable String username) {
        List<PaymentResponse> response = paymentService.list(username);
        return WebResponse.<List<PaymentResponse>>builder().data(response).build();
    }

    @PatchMapping(
            path = "/{username}/payments/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<PaymentResponse> update(@RequestBody UpdatePaymentRequest request, @PathVariable Integer id, @PathVariable String username) {
        PaymentResponse response = paymentService.update(id, request, username);
        return WebResponse.<PaymentResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/{username}/payments/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(@PathVariable Integer id, @PathVariable String username) {
        paymentService.delete(id, username);
        return WebResponse.<String>builder().data("OK").build();
    }
}
