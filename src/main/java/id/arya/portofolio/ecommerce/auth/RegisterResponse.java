package id.arya.portofolio.ecommerce.auth;

import id.arya.portofolio.ecommerce.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    private String token;
    private String refreshToken;
    private User user;
}
