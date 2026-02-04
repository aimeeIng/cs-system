package com.cs.cs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
public class UserConfig {

    @Bean
    public UserDetailsService userDetailsService() {

        return new InMemoryUserDetailsManager(
                User.withUsername("teller")
                        .password("{noop}teller123")
                        .roles("TELLER")
                        .build(),

                User.withUsername("admin")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build()
        );
    }
}
