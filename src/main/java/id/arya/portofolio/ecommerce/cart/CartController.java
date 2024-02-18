package id.arya.portofolio.ecommerce.cart;

import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CartResponse> createOrUpdate(@RequestBody CreateCartRequest request, Principal principal) {
        CartResponse response = cartService.createOrUpdate(request, principal);
        return WebResponse.<CartResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<CartResponse> get(Principal principal) {
        CartResponse response = cartService.get(principal);
        return WebResponse.<CartResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(Principal principal) {
        cartService.delete(principal);
        return WebResponse.<String>builder().data("OK").build();
    }

    @DeleteMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(@RequestBody DeleteCartItemRequest request, Principal principal) {
        cartService.deleteOne(request, principal);
        return WebResponse.<String>builder().data("OK").build();
    }
}
