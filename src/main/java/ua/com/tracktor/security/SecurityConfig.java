package ua.com.tracktor.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ua.com.tracktor.data.UserRepository;

@Configuration
@EnableWebSecurity
@Order(1)
public class SecurityConfig {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository repository;

    @Configuration
    @Order(1)
    public class RestConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/services/**")
                    .authorizeRequests().anyRequest().access("hasAnyRole('ROLE_USER', 'ROLE_ADMINISTRATOR')")
                    .and().httpBasic()
                    .and()
                    .csrf()
                    .ignoringAntMatchers("/h2-console/**", "/services/**")
                    .and().exceptionHandling().accessDeniedPage("/");
        }
    }

    @Configuration
    @Order(2)
    public class ActuatorSecurity extends WebSecurityConfigurerAdapter {

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/actuator/**")
                    .authorizeRequests().anyRequest().access("hasAnyRole('ROLE_ADMINISTRATOR')")
                    .and().httpBasic();
        }

    }

    @Configuration
    @Order(3)
    public class WebAppConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)

                    .and()
                    .authorizeRequests()
                    .antMatchers( "/logout", "/cabinet", "/termsAndConditions")
                    .access("hasAnyRole('ROLE_USER', 'ROLE_ADMINISTRATOR')")
                    .antMatchers("/register")
                    .access("hasRole('ROLE_ADMINISTRATOR')")
                    .antMatchers("/**")
                    .access("permitAll")

                    .and()
                    .formLogin()
                    .loginPage("/")
                    .loginProcessingUrl("/login")
                    .successHandler(new SavedRequestAwareAuthenticationSuccessHandler("/cabinet"))
                    .failureHandler(new CustomAuthenticationFailureHandler())

                    .and()
                    .logout()
                    .logoutSuccessUrl("/")

                    // Make H2-Console non-secured; for debug purposes
                    .and()
                    .csrf()
                    .ignoringAntMatchers("/h2-console/**", "/wayforpay/transaction")

                    // Allow pages to be loaded in frames from the same origin; needed for H2-Console
                    .and()
                    .headers()
                    .frameOptions()
                    .sameOrigin();
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {

            auth
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(encoder());

        }
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(5);
    }

}
