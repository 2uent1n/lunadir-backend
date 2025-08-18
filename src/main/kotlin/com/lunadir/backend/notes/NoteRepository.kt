package com.lunadir.backend.notes

import org.springframework.data.repository.CrudRepository
import java.util.UUID

interface NoteRepository: CrudRepository<Note, UUID> {
    fun findAllByUserId(userId: UUID): List<Note>
}
