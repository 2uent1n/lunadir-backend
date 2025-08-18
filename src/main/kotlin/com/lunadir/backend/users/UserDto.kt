package com.lunadir.backend.users

import java.util.UUID

data class UserDto(
    val id: UUID?,
    val name: String,
    val email: String,
)
