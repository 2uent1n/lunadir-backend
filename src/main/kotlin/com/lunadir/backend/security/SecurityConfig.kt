package com.lunadir.backend.security

import jakarta.servlet.DispatcherType
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 * This is the main Spring Security configuration that defines how requests
 * should be secured, which endpoints need authentication, etc.
 */
@Configuration
class SecurityConfig(
    private val jwtAuthFilter: JwtAuthFilter,
) {

    @Bean
    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            // Disable CSRF protection since we're using JWT tokens (stateless API)
            // CSRF protection is mainly for session-based authentication with cookies
            .csrf { it.disable() }

            // Configure session management to be STATELESS
            // This means Spring Security won't create or use HTTP sessions
            // Perfect for JWT-based APIs where each request is independent
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }

            // Configure which requests require authentication
            .authorizeHttpRequests {
                it
                    // Allow all requests to /auth/** endpoints (login, register, etc.)
                    // These endpoints don't require authentication
                    .requestMatchers("/auth/**")
                    .permitAll()

                    // Allow internal Spring dispatcher requests (error pages, forwards)
                    // These are internal Spring operations that shouldn't require auth
                    .dispatcherTypeMatchers(
                        DispatcherType.ERROR,
                        DispatcherType.FORWARD,
                    )
                    .permitAll()

                    // All other requests require authentication
                    .anyRequest()
                    .authenticated()
            }

            // Configure what happens when an unauthenticated user tries to access protected resources
            .exceptionHandling {
                // Return HTTP 401 Unauthorized status
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }

            // Add the custom JWT filter BEFORE the default username/password filter
            // This ensures JWT tokens are checked before Spring tries other authentication methods
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter::class.java)

            // Build and return the configured SecurityFilterChain
            .build()
    }

    /**
     * Custom UserDetailsService to prevents Spring Security
     * from creating a default user with generated password.
     */
    @Bean
    fun emptyUserDetailsService(): UserDetailsService {
        return UserDetailsService { username ->
            // Always throw exception since we don't use username/password authentication
            // The JWT filter handles authentication before this service is ever called
            throw UsernameNotFoundException("Username/password authentication not supported. Use JWT tokens.")
        }
    }
}
