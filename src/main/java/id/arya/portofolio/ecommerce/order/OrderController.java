package id.arya.portofolio.ecommerce.order;

import id.arya.portofolio.ecommerce.util.PagingResponse;
import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping(
            path = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<OrderResponse> create(CreateOrderRequest orderRequest) {
        OrderResponse orderResponse = orderService.create(orderRequest);
        return WebResponse.<OrderResponse>builder().data(orderResponse).build();
    }

    @GetMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<OrderResponse>> list(int page, int size) {
        Page<OrderResponse> orderResponses = orderService.list(page, size);
        return WebResponse.<List<OrderResponse>>builder()
                .data(orderResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(orderResponses.getNumber())
                        .size(orderResponses.getSize())
                        .totalPage(orderResponses.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<OrderResponse>> listByUser(int page, int size, @PathVariable String username, Principal principal) {
        Page<OrderResponse> orderResponses = orderService.listByUser(principal, page, size, username);
        return WebResponse.<List<OrderResponse>>builder()
                .data(orderResponses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(orderResponses.getNumber())
                        .size(orderResponses.getSize())
                        .totalPage(orderResponses.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<OrderResponse> get(@PathVariable Integer id, Principal principal) {
        OrderResponse orderResponse = orderService.get(principal, id);
        return WebResponse.<OrderResponse>builder().data(orderResponse).build();
    }

    @PatchMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<OrderResponse> updateStatus(@PathVariable Integer id, @RequestBody UpdateOrderStatusRequest request, Principal principal) {
        OrderResponse orderResponse = orderService.updateStatus(principal, id, request);
        return WebResponse.<OrderResponse>builder().data(orderResponse).build();
    }
}
