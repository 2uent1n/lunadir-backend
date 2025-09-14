package com.lunadir.backend.security.refresh_tokens

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface RefreshTokenRepository: CrudRepository<RefreshToken, UUID> {
    fun findByUserIdAndHashedToken(userId: UUID, hashedToken: String): RefreshToken?
}
