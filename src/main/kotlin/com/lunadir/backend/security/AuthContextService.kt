package com.lunadir.backend.security

import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class AuthContextService {

    /**
     * Gets the authenticated User's ID extracted from the access token.
     *
     * @return the User ID as a UUID
     * @throws ResponseStatusException if no User is authenticated, or the parsed ID is not castable as UUID.
     */
    fun getAuthenticatedUserId(): UUID {
        val userId = SecurityContextHolder.getContext().authentication.principal as? UUID
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED)
        return userId
    }
}
