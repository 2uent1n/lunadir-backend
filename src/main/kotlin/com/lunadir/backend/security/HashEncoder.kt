package com.lunadir.backend.security

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

/**
 * Utility class to hash and check passwords using bcrypt.
 */
@Component
class HashEncoder {
    private val bcryptEncoder = BCryptPasswordEncoder()

    /**
     * Hashes the provided password.
     */
    fun encode(rawPassword: String): String {
        return bcryptEncoder.encode(rawPassword)
    }

    /**
     * Checks that a raw password matches a hashed one.
     */
    fun matches(rawPassword: String, hashedPassword: String): Boolean {
        return bcryptEncoder.matches(rawPassword, hashedPassword)
    }
}
