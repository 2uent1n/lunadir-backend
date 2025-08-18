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
    fun getAll(): ResponseEntity<List<User>> {
        val users = userService.getAll()
        return ResponseEntity.ok(users)
    }

    @PostMapping
    fun create(
        @RequestBody userDto: UserDto,
    ): ResponseEntity<User> {
        val createdUser = userService.create(userDto)
        return ResponseEntity(
            createdUser,
            HttpStatus.CREATED
        )
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable("id") userId: UUID,
    ): ResponseEntity<User> {
        val user = userService.getById(userId)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)

    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") userId: UUID,
        @RequestBody userDto: UserDto,
    ): ResponseEntity<User> {
        val updatedUser = userService.update(userId, userDto)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedUser)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable("id") userId: UUID,
    ): ResponseEntity<Void> {
        return if (!userService.delete(userId)) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.noContent().build()
        }
    }
}
