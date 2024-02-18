package id.arya.portofolio.ecommerce;

import id.arya.portofolio.ecommerce.auth.RegisterRequest;
import id.arya.portofolio.ecommerce.user.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import static id.arya.portofolio.ecommerce.user.Role.ADMIN;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class UsersApplication {

    public static void main(String[] args) {
        SpringApplication.run(UsersApplication.class, args);
    }

    @Bean
    public CommandLineRunner run(UserService service) {
        return args -> {
            var admin = RegisterRequest.builder()
                    .username("admin")
                    .email("admin@mail.com")
                    .password("password")
                    .role(ADMIN)
                    .build();
            service.register(admin);
            System.out.println("ADMIN CREDENTIAL CREATED: admin:password");
        };
    }

}
