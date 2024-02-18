package id.arya.portofolio.ecommerce.user;

import id.arya.portofolio.ecommerce.auth.AuthService;
import id.arya.portofolio.ecommerce.util.PagingResponse;
import id.arya.portofolio.ecommerce.util.WebResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    @GetMapping(
            path = "/current",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> current(Principal user) {
        UserResponse userResponse = userService.get(user.getName());
        return WebResponse.<UserResponse>builder().data(userResponse).build();
    }

    @PatchMapping("/change-password")
    public WebResponse<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest request, Principal user) {
        ChangePasswordResponse response = authService.changePassword(request, user);
        return WebResponse.<ChangePasswordResponse>builder().data(response).build();
    }

    @GetMapping(
            path = "/",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<List<UserResponse>> findAll(
            @RequestParam(value = "query", required = false, defaultValue = "") String query,
            @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size) {
        Page<UserResponse> responses = userService.list(query, page, size);
        return WebResponse.<List<UserResponse>>builder()
                .data(responses.getContent())
                .paging(PagingResponse.builder()
                        .currentPage(responses.getNumber())
                        .size(responses.getSize())
                        .totalPage(responses.getTotalPages())
                        .build())
                .build();
    }

    @GetMapping(
            path = "/{username}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<UserResponse> findByUsername(@PathVariable String username) {
        UserResponse responses = userService.get(username);
        return WebResponse.<UserResponse>builder().data(responses).build();
    }
}
