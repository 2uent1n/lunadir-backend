package com.lunadir.backend.security.refresh_tokens

import com.lunadir.backend.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

/**
 * Tokens used for refreshing user access tokens.
 */
@Entity
@Table(name = "refresh_tokens")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
    @Column(name = "expires_at")
    val expiresAt: Instant,
    @Column(name = "hashed_token")
    val hashedToken: String,
    @Column(name = "created_at")
    val createdAt: Instant = Instant.now(),
    )
