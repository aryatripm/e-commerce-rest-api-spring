package id.arya.portofolio.ecommerce.product;

import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductCategoryResponse> create(@RequestBody CreateProductCategoryRequest request) {
        ProductCategoryResponse response = productCategoryService.create(request);
        return WebResponse.<ProductCategoryResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<ProductCategoryResponse>> getAll() {
        List<ProductCategoryResponse> response = productCategoryService.list();
        return WebResponse.<List<ProductCategoryResponse>>builder().data(response).build();
    }

    @PatchMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<ProductCategoryResponse> update(@RequestBody UpdateProductCategoryRequest request, Integer id) {
        ProductCategoryResponse response = productCategoryService.update(request, id);
        return WebResponse.<ProductCategoryResponse>builder().data(response).build();
    }

    @DeleteMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> delete(@PathVariable Integer id) {
        productCategoryService.delete(id);
        return WebResponse.<String>builder().data("Category Deleted").build();
    }
}
