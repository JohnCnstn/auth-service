package com.johncnstn.auth.config;

import com.johncnstn.auth.security.JwtConfigurer;
import com.johncnstn.auth.security.TokensProvider;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

import javax.annotation.PostConstruct;

import static com.johncnstn.auth.generated.api.AuthApi.*;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final CorsFilter corsFilter;
    private final TokensProvider tokensProvider;
    private final UserDetailsService userDetailsService;

    @SneakyThrows
    @PostConstruct
    public void init() {
        authenticationManagerBuilder
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring()
                .antMatchers("/")
                .antMatchers(basePath);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling()

                .and()
                .headers()
                .frameOptions()
                .disable()

                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)

                .and()
                .apply(securityConfigurerAdapter())
                .and()
                .authorizeRequests()

                .antMatchers(POST, signUpPath).permitAll()
                .antMatchers(POST, signInPath).permitAll()
                .antMatchers(POST, refreshTokenPath).permitAll()

                .antMatchers(basePath + "/openapi.*").permitAll()

                .antMatchers(OPTIONS, "/**").permitAll()
                .antMatchers(basePath + "/**").authenticated();
    }

    private JwtConfigurer securityConfigurerAdapter() {
        return new JwtConfigurer(tokensProvider);
    }

}
