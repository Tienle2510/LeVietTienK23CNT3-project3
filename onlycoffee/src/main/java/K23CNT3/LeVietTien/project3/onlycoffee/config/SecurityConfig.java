package K23CNT3.LeVietTien.project3.onlycoffee.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // PUBLIC URLs - Không cần đăng nhập
                        .requestMatchers(
                                "/",
                                "/onlycoffee",
                                "/onlycoffee/",
                                "/onlycoffee/home",
                                "/onlycoffee/products/**",
                                "/onlycoffee/about",
                                "/onlycoffee/contact",
                                "/onlycoffee/login",
                                "/onlycoffee/register",
                                "/onlycoffee/forgot-password",
                                "/onlycoffee/reset-password",
                                "/onlycoffee/access-denied",
                                "/onlycoffee/verify/**",
                                "/onlycoffee/api/public/**",

                                // Static resources
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/uploads/**",
                                "/webjars/**",
                                "/favicon.ico",
                                "/error"
                        ).permitAll()

                        // ADMIN URLs
                        .requestMatchers(
                                "/onlycoffee/admin/**",
                                "/onlycoffee/dashboard/**",
                                "/onlycoffee/manage/**"
                        ).hasAnyRole("ADMIN", "MANAGER")

                        // USER URLs
                        .requestMatchers(
                                "/onlycoffee/user/**",
                                "/onlycoffee/cart/**",
                                "/onlycoffee/orders/**",
                                "/onlycoffee/profile/**",
                                "/onlycoffee/checkout/**",
                                "/onlycoffee/payment/**"
                        ).hasAnyRole("USER", "CUSTOMER", "MANAGER", "ADMIN")

                        // Tất cả request còn lại cần đăng nhập
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/onlycoffee/login")
                        .loginProcessingUrl("/onlycoffee/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/onlycoffee/home", true)
                        .failureUrl("/onlycoffee/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/onlycoffee/logout", "GET"))
                        .logoutSuccessUrl("/onlycoffee/login?logout=true")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID", "remember-me")
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/onlycoffee/access-denied")
                )
                .rememberMe(remember -> remember
                        .key("onlycoffeeSecretKey2024")
                        .tokenValiditySeconds(7 * 24 * 60 * 60) // 7 ngày
                        .rememberMeParameter("remember-me")
                        .userDetailsService(userDetailsService)
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                        .expiredUrl("/onlycoffee/login?expired=true")
                );

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}