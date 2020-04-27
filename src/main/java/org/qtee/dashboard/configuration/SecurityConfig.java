package org.qtee.dashboard.configuration;

import org.qtee.dashboard.data.UserRepository;
import org.qtee.dashboard.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    public static class RestConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/services/**")
                    .authorizeRequests().anyRequest().hasRole("ADMINISTRATOR")
                    .and().httpBasic()
                    .and()
                    .csrf()
                    .ignoringAntMatchers("/h2-console/**", "/services/**")
                    .and().exceptionHandling().accessDeniedPage("/");
        }
    }

    @Configuration
    @Order(2)
    public class App1ConfigurationAdapter extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers( "/logout", "/")
                    .access("hasAnyRole('ROLE_USER', 'ROLE_ADMINISTRATOR')")
                    .antMatchers("/register")
                    .access("hasRole('ROLE_ADMINISTRATOR')")
                    .antMatchers("/**")
                    .access("permitAll")

                    .and()
                    .formLogin()
                    .loginPage("/login")

                    .and()
                    .logout()
                    .logoutSuccessUrl("/")

                    // Make H2-Console non-secured; for debug purposes
                    .and()
                    .csrf()
                    .ignoringAntMatchers("/h2-console/**")

                    // Allow pages to be loaded in frames from the same origin; needed for H2-Console
                    .and()
                    .headers()
                    .frameOptions()
                    .sameOrigin()
            ;

        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth)
                throws Exception {

            auth
                    .userDetailsService(userDetailsService)
                    .passwordEncoder(encoder());

        }

        @Override
        public void init(WebSecurity web) throws Exception {
            initAdminAccount();

            super.init(web);
        }
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder(5);
    }

    private void initAdminAccount() {
        User existingUser = repository.findByUsername("admin");
        if (existingUser == null) {
            existingUser = new User("admin", encoder().encode("1829"), 1,  true, "Головний адміністратор", "", null);
            repository.save(existingUser);
        }
    }
}
