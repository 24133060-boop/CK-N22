package vn.edu.ute.milktea.config;
import jakarta.servlet.DispatcherType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import vn.edu.ute.milktea.service.JwtTokenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import vn.edu.ute.milktea.repository.UserRepository;
@Configuration
public class SecurityConfig {
    @Bean
    PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    UserDetailsService users(UserRepository repo) {
        return email -> {
            var u = repo.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(email));
            return User.withUsername(u.email).password(u.password).roles(u.role).build();
        }
        ;
    }
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
    @Bean
    SecurityFilterChain chain(HttpSecurity http, JwtTokenService jwt, UserDetailsService users) throws Exception {
        return http
        .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
        .addFilterBefore(new JwtAuthenticationFilter(jwt,users),UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
        // JSP views are forwarded internally to /WEB-INF/views/*.jsp.
        // Without FORWARD permission, /login can redirect to itself indefinitely.
        .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
        .requestMatchers("/api/auth/login", "/payment/vnpay/ipn", "/payment/vnpay/return").permitAll()
        .requestMatchers("/api/**").authenticated()
        .requestMatchers("/products/*/review").hasRole("USER")
        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**",
        "/images/**", "/verify-registration", "/shop/**", "/products/**", "/error", "/forgot-password", "/reset-password").permitAll()
        .requestMatchers("/ws-updates", "/ws-updates/**").authenticated()
        .requestMatchers("/payment/vietqr/**", "/order", "/order/**", "/my-orders", "/my-orders/**", "/cart", "/cart/**", "/favorites", "/favorites/**", "/products/*/review").hasRole("USER")
        .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
        .requestMatchers("/manager", "/manager/**").hasAnyRole("MANAGER", "ADMIN")
        .requestMatchers("/vendor/apply").hasRole("USER")
        .requestMatchers("/vendor", "/vendor/**").hasRole("VENDOR")
        .requestMatchers("/shipper", "/shipper/**").hasRole("SHIPPER")
        .anyRequest().authenticated()
        )
        .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
        .logout(logout -> logout.logoutSuccessUrl("/"))
        .build();
    }
}
