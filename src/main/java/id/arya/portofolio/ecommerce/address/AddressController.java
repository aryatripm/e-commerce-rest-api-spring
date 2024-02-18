package id.arya.portofolio.ecommerce.address;

import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/users")
@RestController
public class AddressController {

    @Autowired
    private AddressService addressService;

    @PostMapping(
            path = "/{username}/address",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> create(@RequestBody CreateAddressRequest request, @PathVariable String username) {
        AddressResponse response = addressService.create(request, username);
        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/{username}/address/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> get(@PathVariable Integer id, @PathVariable String username) {
        AddressResponse response = addressService.get(id, username);
        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/{username}/address",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<AddressResponse>> getAll(@PathVariable String username) {
        List<AddressResponse> response = addressService.list(username);
        return WebResponse.<List<AddressResponse>>builder().data(response).build();
    }

    @PatchMapping(
            path = "/{username}/address/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<AddressResponse> update(@RequestBody UpdateAddressRequest request, @PathVariable Integer id, @PathVariable String username) {
        AddressResponse response = addressService.update(id, request, username);
        return WebResponse.<AddressResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/{username}/address/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(@PathVariable Integer id, @PathVariable String username) {
        addressService.delete(id, username);
        return WebResponse.<String>builder().data("OK").build();
    }
}
