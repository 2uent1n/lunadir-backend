package com.lunadir.backend.security

import com.lunadir.backend.security.refresh_tokens.RefreshTokenRepository
import com.lunadir.backend.users.UserRepository
import com.lunadir.backend.users.UserService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID

/**
 * Service responsible for handling the
 * actions related to authentication.
 */
@Service
class AuthService(
    private val jwtService: JwtService,
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val hashEncoder: HashEncoder,
    private val refreshTokenRepository: RefreshTokenRepository,
) {

    /**
     * Creates a new User with the provided email and the hashed password.
     *
     * @throws ResponseStatusException if a user with that email already exists
     */
    fun register(email: String, password: String) {
        if (userRepository.existsByEmail(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "A user with this email already exists.")
        }
        val hashedPassword = hashEncoder.encode(password)
        userService.create(
            email = email,
            hashedPassword = hashedPassword,
        )
    }

    /**
     * Checks the provided user credentials and return a newly generated pair of tokens.
     *
     * @throws ResponseStatusException if no user with that email exists
     * @throws BadCredentialsException if the password is incorrect
     */
    fun login(email: String, password: String): JwtService.TokenPair {
        val user = userRepository.findByEmail(email)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)
        if (!hashEncoder.matches(password, user.hashedPassword)) {
            throw BadCredentialsException("Invalid credentials.")
        }
        return jwtService.generateTokenPair(user)
    }

    /**
     * Generates a new token pair (access and refresh tokens) if a valid refresh token
     * is provided. The old refresh token is deleted from the database and replaced with
     * the newly generated one.
     *
     * This method is annotated with @Transactional to avoid deleting
     * the refresh token without generating a new one.
     *
     * @throws ResponseStatusException if anything goes wrong while validating the refresh token.
     */
    @Transactional
    fun refresh(refreshToken: String): JwtService.TokenPair {
        // Check that the token is valid.
        val tokenIsValid = jwtService.validateRefreshToken(refreshToken)
        if (!tokenIsValid) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token.")
        }
        // Check that the user ID stored in the token is valid (e.g. has not been deleted)
        val userId = jwtService.getUserIdFromToken(refreshToken)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token.")
        val user = userRepository.findByIdOrNull(userId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        // Check if the refresh token is not already used, i.e. check if its hash is still in the database
        val hashedRefreshToken = jwtService.hashRefreshToken(refreshToken)
        val refreshToken = refreshTokenRepository.findByUserIdAndHashedToken(userId, hashedRefreshToken)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not recognized.")
        // Check if the refresh token has expired
        if (Instant.now().isAfter(refreshToken.expiresAt)) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Token expired.")
        }
        // Generate a new token pair and delete the previous refresh token from the database.
        refreshTokenRepository.delete(refreshToken)
        return jwtService.generateTokenPair(user)
    }
}
