package com.lunadir.backend.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filters requests with a Bearer Authentication token to validate the access token
 * inside and store the extracted user ID in the SecurityContextHolder.
 *
 * This is useful to access the user ID in other parts of the code.
 */
@Component
class JwtAuthFilter(
    private val jwtService: JwtService,
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (
            authHeader != null
            && authHeader.startsWith("Bearer ")
            && jwtService.validateAccessToken(authHeader)
            ) {
                val userId = jwtService.getUserIdFromToken(authHeader)
                val auth = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                SecurityContextHolder.getContext().authentication = auth
            }
        filterChain.doFilter(request, response)
        }
    }
