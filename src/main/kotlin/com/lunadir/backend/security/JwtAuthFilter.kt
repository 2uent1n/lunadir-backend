package com.lunadir.backend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filters requests with a Bearer Authentication token to validate
 * the access token inside and extract the user ID.
 **/
@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
): OncePerRequestFilter() { /* Extends OncePerRequestFilter to ensure that the filter runs
    only once per request (e.g. redirections, forwards). */

    /**
     * Main method that executes for every HTTP request.
     */
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain // The chain of next filters to execute
    ) {
        val authHeader = request.getHeader("Authorization")
        if (
            authHeader != null
            && authHeader.startsWith("Bearer ")
            && jwtService.validateAccessToken(authHeader)
            ) {
                val userId = jwtService.getUserIdFromToken(authHeader)
                // // Create a Spring Security authentication object (no credentials, since we're using JWT)
                val auth = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                // Place the authentication in the security context. Now it can be accessed throughout the application.
                SecurityContextHolder.getContext().authentication = auth
            }
        /**
         * Pass control to the next filter in the chain.
         * Without this, the request would stop here and never reach the controller.
         */
        filterChain.doFilter(request, response)
        }
    }
