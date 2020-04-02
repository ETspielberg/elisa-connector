package org.unidue.ub.libintel.elisaconnector.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * general Web security configuration for Spring Security, opening the ports for the submission of purchase requests.
 */
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic()
                .and()
                .authorizeRequests()
                .antMatchers("/sendEav", "/receiveEav").permitAll()
                .antMatchers("/sendToElisa").authenticated()
                .and()
                .csrf().disable();
    }

}
