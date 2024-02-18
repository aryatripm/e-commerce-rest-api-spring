package id.arya.portofolio.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static id.arya.portofolio.ecommerce.user.Role.ADMIN;
import static id.arya.portofolio.ecommerce.user.Role.USER;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private static final String[] WHITE_LIST_URL = {
            "/api/v1/auth/**",
            "/error",
            "/actuator/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"
    };

    @Autowired
    private JwtAuthenticationFilter jwtAuthFilter;
    @Autowired
    private AuthenticationProvider authenticationProvider;
    @Autowired
    private LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req -> req
                        .requestMatchers(WHITE_LIST_URL)
                        .permitAll()
                        .requestMatchers("/api/v1/users/change-password", "/api/v1/users/current").hasAnyRole(ADMIN.name(), USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/", "/api/v1/users/*").hasAnyRole(ADMIN.name())
                        .requestMatchers("/api/v1/users/**").hasAnyRole(ADMIN.name(), USER.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").hasAnyRole(ADMIN.name(), USER.name())
                        .requestMatchers("/api/v1/products/**").hasAnyRole(ADMIN.name())
                        .requestMatchers(HttpMethod.GET, "/api/v1/categories/**").hasAnyRole(ADMIN.name(), USER.name())
                        .requestMatchers("/api/v1/categories/**").hasAnyRole(ADMIN.name())
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/orders/**").hasAnyRole(ADMIN.name())
                        .requestMatchers("/api/v1/orders/**").hasAnyRole(ADMIN.name(), USER.name())
                        .requestMatchers("/api/v1/discounts/**").hasAnyRole(ADMIN.name())
                        .requestMatchers("/api/v1/cart/**").hasAnyRole(ADMIN.name(), USER.name())
                        .anyRequest()
                        .authenticated()
                )
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout(logout ->
                        logout.logoutUrl("/api/v1/auth/logout")
                                .addLogoutHandler(logoutHandler)
                                .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
                )
        ;

        return http.build();
    }
}
