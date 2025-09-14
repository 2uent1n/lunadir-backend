package com.lunadir.backend.security

import com.lunadir.backend.security.refresh_tokens.RefreshToken
import com.lunadir.backend.security.refresh_tokens.RefreshTokenRepository
import com.lunadir.backend.users.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest
import java.time.Instant
import java.util.Base64
import java.util.Date
import java.util.UUID

/**
 * Handles the operations related to JWT Tokens.
 */
@Service
class JwtService(
    @Value("\${jwt.secret}") private val jwtSecret: String, // TODO: use newer syntax when updating to Spring Boot 4
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    private val secretKey = Keys.hmacShaKeyFor(jwtSecret.toByteArray())
    private val accessTokenValidityMs = 15L * 60 * 1000 // 15 min
    private val refreshTokenValidityMs = 7L * 24 * 60 * 60 * 1000 // 7 days

    enum class TokenType {
        ACCESS, REFRESH
    }

    data class TokenPair(
        val accessToken: String,
        val refreshToken: String
    )

    /**
     * Generic function to generate a JWT Token.
     * The token type is stored in the claims.
     *
     * @return the generated token, signed with the secret key
     */
    private fun generateToken(
        userId: UUID,
        tokenType: TokenType,
        validityMs: Long,
    ): String {
        val now = Date()
        val expiryDate = Date(now.time + validityMs)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("tokenType", tokenType)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256) // A simple algorithm is enough here, because the key is generated randomly
            .compact()
    }

    /**
     * Generates an access token using the provided user ID.
     */
    private fun generateAccessToken(userId: UUID): String {
        return generateToken(userId, TokenType.ACCESS, accessTokenValidityMs)
    }

    /**
     * Generates a refresh token using the provided user ID.
     */
    private fun generateRefreshToken(userId: UUID): String {
        return generateToken(userId, TokenType.REFRESH, refreshTokenValidityMs)
    }

    /**
     * Tries to parse the payload (claims) from a JWT token, using the secret key.
     *
     * @return the parsed payload, or null if the parsing failed.
     */
    private fun parseAllClaims(rawToken: String): Claims? {
        val token = if (rawToken.startsWith("Bearer")) {
            rawToken.removePrefix("Bearer ")
        } else {
            rawToken
        }
        return try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Generic function to validate a JWT token.
     * A token is deemed valid if its payload can be parsed using the secret key.
     * The expected token type (access or refresh) must also be provided to be
     * checked against the token type stored in the token payload.
     */
    private fun validateToken(rawToken: String, expectedTokenType: TokenType): Boolean {
        val claims = parseAllClaims(rawToken) ?: return false
        val tokenTypeString = claims["tokenType"] as? String ?: return false
        val actualTokenType = try {
            TokenType.valueOf(tokenTypeString)
        } catch (_: IllegalArgumentException) {
            return false
        }
        return expectedTokenType == actualTokenType
    }

    /**
     * Validates an access token.
     */
    fun validateAccessToken(rawToken: String): Boolean {
        return validateToken(rawToken, TokenType.ACCESS)
    }

    /**
     * Validates a refresh token.
     */
    fun validateRefreshToken(rawToken: String): Boolean {
        return validateToken(rawToken, TokenType.REFRESH)
    }

    /**
     * Extracts the user ID from the payload of the provided token.
     *
     * @return the user ID as a UUID, or null if it could not be extracted.
     */
    fun getUserIdFromToken(rawToken: String): UUID? {
        val claims = parseAllClaims(rawToken) ?: return null
        return UUID.fromString(claims.subject)
    }

    /**
     * Hashes the provided refresh token using SHA-256, and
     * returns it as a Base64 string.
     */
    fun hashRefreshToken(refreshToken: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(refreshToken.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    /**
     * Stores a refresh token for a user.
     * The token is hashed before persistence for security.
     */
    private fun storeRefreshToken(user: User, refreshToken: String) {
        val hashedRefreshToken = hashRefreshToken(refreshToken)
        val expiresAt: Instant = Instant.now().plusMillis(refreshTokenValidityMs)
        refreshTokenRepository.save(
            RefreshToken(
                user = user,
                expiresAt = expiresAt,
                hashedToken = hashedRefreshToken,
            )
        )
    }

    /**
     * Generate a pair of access and refresh tokens for a user.
     * The refresh token is persisted in database.
     */
    fun generateTokenPair(user: User): TokenPair {
        val userId = user.id!!
        val newAccessToken = generateAccessToken(userId)
        val newRefreshToken = generateRefreshToken(userId)
        storeRefreshToken(user, newRefreshToken)
        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

}
