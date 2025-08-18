package com.lunadir.backend.users

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService
) {

    @GetMapping
    fun getAllUsers(): List<User> {
        return userService.getAllUsers()
    }

    @PostMapping
    fun createUser(
        @RequestBody userDto: UserDto,
    ): ResponseEntity<User> {
        val createdUser = userService.createUser(userDto)
        return ResponseEntity(createdUser, HttpStatus.CREATED)
    }

    @GetMapping("/{id}")
    fun getUserById(
        @PathVariable("id") userId: UUID,
    ): ResponseEntity<User> {
        val user = userService.getUserById(userId)
        return if (user == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(user)
        }
    }

    @PutMapping("/{id}")
    fun updateUserById(
        @PathVariable("id") userId: UUID,
        @RequestBody userDto: UserDto,
    ): ResponseEntity<User> {
        val updatedUser = userService.updateUserById(userId, userDto)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/{id}")
    fun deleteUserById(
        @PathVariable("id") userId: UUID,
    ): ResponseEntity<Void> {
        if (!userService.deleteUserById(userId)) {
            return ResponseEntity.notFound().build()
        }
        return ResponseEntity.noContent().build()
    }
}