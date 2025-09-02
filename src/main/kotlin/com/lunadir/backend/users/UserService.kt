package com.lunadir.backend.users

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class UserService(
    private val userRepository: UserRepository,
) {

    fun getAll(): List<User> {
        return userRepository.findAll().toList()
    }

    fun getById(id: UUID): User? {
        return userRepository.findByIdOrNull(id)
    }

    fun update(id: UUID, userDto: UserDto): User? {
        val existingUser = getById(id) ?: return null
        val updatedUser = existingUser.copy(name = userDto.name, email = userDto.email)
        return userRepository.save(updatedUser)
    }

    fun delete(id: UUID): Boolean {
        if (!userRepository.existsById(id)) return false
        userRepository.deleteById(id)
        return true
    }
}
