package org.qtee.dashboard.configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

public class SecurityConfigRest extends WebSecurityConfigurerAdapter {
    //TODO Rest endpoints are insecure for now.
    //     Need to setup multiple security configurations
    //     https://stackoverflow.com/questions/43524211/how-do-i-add-http-basic-auth-for-a-specific-endpoint-with-spring-security
    //     https://docs.spring.io/spring-security/site/docs/current/reference/html5/#multiple-httpsecurity

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/services/**").authenticated()
                .and().httpBasic();
    }
}
