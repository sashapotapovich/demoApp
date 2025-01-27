package com.example.demo;

import com.example.demo.security.CustomRequestCache;
import com.example.demo.security.MySimpleUrlAuthenticationSuccessHandler;
import com.example.demo.security.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@EnableGlobalMethodSecurity(securedEnabled = true)
@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private static final String LOGIN_PROCESSING_URL = "/login";
    private static final String LOGIN_FAILURE_URL = "/login?error";
    private static final String LOGOUT_SUCCESS_URL = "/welcome";

    private final UserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public SecurityConfiguration(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()
            // Register our CustomRequestCache, that saves unauthorized access attempts, so
            // the user is redirected after login.
            .requestCache().requestCache(new CustomRequestCache())
            // Restrict access to our application.
            .and().authorizeRequests()
            // Allow all flow internal requests.
            .requestMatchers(SecurityUtils::isFrameworkInternalRequest)
            .permitAll()
            .anyRequest().authenticated()
            // Allow all requests by logged in users.
            .and().formLogin().loginPage(LOGIN_PROCESSING_URL).permitAll().loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)
            // Register the success handler that redirects users to the page they last tried
            // to access
            .successHandler(new MySimpleUrlAuthenticationSuccessHandler())
            // Configure logout
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)
        ;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                // Vaadin Flow static resources
                "/VAADIN/**",
                // the standard favicon URI
                "/favicon.ico",
                // the robots exclusion standard
                "/robots.txt",
                // web application manifest
                "/manifest.webmanifest",
                "/sw.js",
                "/offline-page.html",
                // icons and images
                "/icons/**",
                "/images/**",
                // (development mode) static resources
                "/frontend/**",
                // (development mode) webjars
                "/webjars/**",
                // (development mode) H2 debugging console
                "/frontend-es5/**", "/frontend-es6/**");
    }
}
