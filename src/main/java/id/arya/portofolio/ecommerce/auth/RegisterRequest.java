package id.arya.portofolio.ecommerce.auth;

import id.arya.portofolio.ecommerce.user.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    private String email;
    private Role role;
}
