package id.arya.portofolio.ecommerce.product;

import id.arya.portofolio.ecommerce.util.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProductCategoryService {

    @Autowired
    private ValidationService validationService;

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    public ProductCategoryResponse create(CreateProductCategoryRequest request) {
        validationService.validate(request);

        var productCategory = ProductCategory.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        productCategoryRepository.save(productCategory);

        return toProductCategoryResponse(productCategory);
    }

    public List<ProductCategoryResponse> list() {
        List<ProductCategory> productCategories = productCategoryRepository.findAll();
        return productCategories.stream()
                .map(this::toProductCategoryResponse)
                .toList();
    }

    public ProductCategoryResponse update(UpdateProductCategoryRequest request, Integer id) {
        validationService.validate(request);

        var productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Category not found"));

        productCategory.setName(request.getName());
        productCategory.setDescription(request.getDescription());

        productCategoryRepository.save(productCategory);

        return toProductCategoryResponse(productCategory);
    }

    public void delete(Integer id) {
        var productCategory = productCategoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Category not found"));

        productCategoryRepository.delete(productCategory);
    }

    private ProductCategoryResponse toProductCategoryResponse(ProductCategory productCategory) {
        return ProductCategoryResponse.builder()
                .id(productCategory.getId())
                .name(productCategory.getName())
                .description(productCategory.getDescription())
                .build();
    }
}
