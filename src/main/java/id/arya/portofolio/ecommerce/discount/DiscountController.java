package id.arya.portofolio.ecommerce.discount;

import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/discounts")
public class DiscountController {

    @Autowired
    private DiscountService discountService;

    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DiscountResponse> create(@RequestBody CreateDiscountRequest request) {
        DiscountResponse response = discountService.create(request);
        return WebResponse.<DiscountResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DiscountResponse> get(@PathVariable Integer id) {
        DiscountResponse response = discountService.get(id);
        return WebResponse.<DiscountResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<DiscountResponse>> list() {
        List<DiscountResponse> response = discountService.list();
        return WebResponse.<List<DiscountResponse>>builder().data(response).build();
    }

    @PatchMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<DiscountResponse> update(@RequestBody UpdateDiscountRequest request, @PathVariable Integer id) {
        DiscountResponse response = discountService.update(id, request);
        return WebResponse.<DiscountResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(@PathVariable Integer id) {
        discountService.delete(id);
        return WebResponse.<String>builder().data("OK").build();
    }
}
