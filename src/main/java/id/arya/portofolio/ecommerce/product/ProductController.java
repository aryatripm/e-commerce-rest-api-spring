package id.arya.portofolio.ecommerce.product;

import id.arya.portofolio.ecommerce.util.PagingResponse;
import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping(
            path = "/",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> create(@ModelAttribute CreateProductRequest request) throws IOException {
        ProductResponse response = productService.create(request);
        return WebResponse.<ProductResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> get(@PathVariable Integer id) {
        ProductResponse response = productService.get(id);
        return WebResponse.<ProductResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ProductResponse>> getAll(@RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<ProductResponse> response = productService.list(page, size);
        return WebResponse.<List<ProductResponse>>builder().data(response.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(response.getNumber())
                        .size(response.getSize())
                        .totalPage(response.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/category/{categoryId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ProductResponse>> getByCategory(@PathVariable Integer categoryId,
                                                           @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<ProductResponse> response = productService.listByCategory(categoryId, page, size);
        return WebResponse.<List<ProductResponse>>builder().data(response.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(response.getNumber())
                        .size(response.getSize())
                        .totalPage(response.getTotalPages())
                        .build())
                .build();
    }

    @PatchMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> update(@RequestBody UpdateProductRequest request, @PathVariable Integer id) {
        ProductResponse response = productService.update(id, request);
        return WebResponse.<ProductResponse>builder().data(response).build();
    }

    @PatchMapping(
            path = "/{id}/inventory",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductResponse> updateQuantity(@RequestBody UpdateProductInventoryRequest request, @PathVariable Integer id) {
        ProductResponse response = productService.updateQuantity(id, request);
        return WebResponse.<ProductResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(@PathVariable Integer id) {
        productService.delete(id);
        return WebResponse.<String>builder().data("Product deleted").build();
    }
}
