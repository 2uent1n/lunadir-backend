package com.lunadir.backend.users

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {

    /**
     * Get the data of the authenticated User.
     */
    @GetMapping
    fun getAuthenticatedUser(): ResponseEntity<User> {
        val user = userService.getAuthenticatedUser()
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }

    /**
     * Update the data of the authenticated User.
     */
    @PutMapping
    fun updateAuthenticatedUser(
        @RequestBody userDto: UserDto,
    ): ResponseEntity<User> {
        val updatedUser = userService.updateAuthenticatedUser(userDto)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedUser)
    }

    /**
     * Delete the authenticated User.
     */
    @DeleteMapping
    fun deleteAuthenticatedUser(): ResponseEntity<Void> {
        userService.deleteAuthenticatedUser()
        return ResponseEntity.noContent().build()
    }
}
