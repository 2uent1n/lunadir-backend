package com.lunadir.backend.notes

import com.lunadir.backend.users.User
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.Instant
import java.util.UUID

@Entity
@Table(name = "notes")
data class Note(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var title: String,
    val content: String,
    @Enumerated(EnumType.STRING)
    var type: NoteType,
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User,
    @Column(name = "created_at")
    var createdAt: Instant,
)
