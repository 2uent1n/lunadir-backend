package com.lunadir.backend.users

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun getAllUsers(): List<User> {
        return userRepository.findAll().toList()
    }

    fun createUser(userDto: UserDto): User {
        return userRepository.save(
            User(
                name = userDto.name,
                email = userDto.email,
            )
        )
    }

    fun getUserById(id: UUID): User? {
        return userRepository.findByIdOrNull(id)
    }

    fun updateUserById(id: UUID, userDto: UserDto): User? {
        val existingUser = getUserById(id) ?: return null
        val updatedUser = existingUser.copy(name = userDto.name, email = userDto.email)
        return userRepository.save(updatedUser)
    }

    fun deleteUserById(id: UUID): Boolean {
        if (!userRepository.existsById(id)) return false
        userRepository.deleteById(id)
        return true
    }
}