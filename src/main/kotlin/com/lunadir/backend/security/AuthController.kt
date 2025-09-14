package com.lunadir.backend.security

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Controller responsible for handling the
 * requests related to authentication.
 */
@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService
) {

    // Data classes

    data class AuthRequest(
        val email: String,
        val password: String,
    )

    data class RefreshRequest(
        val refreshToken: String
    )

    // Endpoints

    /**
     * Register a new User with an email and a password.
     * No token is returned here.
     */
    @PostMapping("/register")
    fun register(
        @RequestBody body: AuthRequest
    ) {
        authService.register(body.email, body.password)
    }

    /**
     * Login as an existing User with an email and a password.
     * If the credentials are valid, a new pair of access and refresh tokens is generated and returned.
     */
    @PostMapping("/login")
    fun login(
        @RequestBody body: AuthRequest,
    ): JwtService.TokenPair {
        return authService.login(body.email, body.password)
    }

    /**
     * Get a new pair of access and refresh tokens by providing a valid refresh token.
     */
    @PostMapping("/refresh")
    fun refresh(
        @RequestBody body: RefreshRequest,
    ): JwtService.TokenPair {
        return authService.refresh(body.refreshToken)
    }
}
