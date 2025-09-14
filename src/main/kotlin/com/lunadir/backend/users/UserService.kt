package com.lunadir.backend.users

import com.lunadir.backend.security.AuthContextService
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
    private val authContextService: AuthContextService,
) {

    /**
     * Gets the authenticated User's data.
     */
    fun getAuthenticatedUser(): User? {
        val userId = authContextService.getAuthenticatedUserId()
        return userRepository.findByIdOrNull(userId)
    }

    /**
     * Creates a new User with an email and a hashed password.
     */
    fun create(email: String, hashedPassword: String) {
        userRepository.save(
            User(
                email = email,
                hashedPassword = hashedPassword,
            )
        )
    }

    /**
     * Updates the authenticated User's data.
     */
    fun updateAuthenticatedUser(userDto: UserDto): User? {
        val user = getAuthenticatedUser() ?: return null
        val updatedUser = user.copy(name = userDto.name, email = userDto.email)
        return userRepository.save(updatedUser)
    }

    /**
     * Deletes the authenticated User.
     */
    fun deleteAuthenticatedUser() {
        val userId = authContextService.getAuthenticatedUserId()
        if (!userRepository.existsById(userId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        }
        userRepository.deleteById(userId)
    }
}
