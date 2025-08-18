package com.lunadir.backend.notes

import com.lunadir.backend.users.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.UUID

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository
) {

    fun getAllByUserId(userId: UUID): List<Note> {
        return noteRepository.findAllByUserId(userId)
    }

    fun getById(id: UUID): Note? {
        return noteRepository.findByIdOrNull(id)
    }

    fun create(noteDto: NoteDto): Note? {
        val user = userRepository.findByIdOrNull(noteDto.userId)
            ?: return null
        val noteType = noteDto.type ?: return null
        val noteToCreate = Note(
            title = noteDto.title,
            content = noteDto.content,
            createdAt = Instant.now(),
            type = noteType,
            user = user,
        )
        return noteRepository.save(
            noteToCreate
        )}

    fun update(noteId: UUID, noteDto: NoteDto): Note? {
        val existingNote = getById(noteId) ?: return null
        if (existingNote.user.id != noteDto.userId) {
            return null
        }
        val updatedNote = existingNote.copy(
            title = noteDto.title,
            content = noteDto.content,
        )
        return noteRepository.save(updatedNote)
    }

    fun delete(id: UUID): Boolean {
        if (!noteRepository.existsById(id)) return false
        noteRepository.deleteById(id)
        return true
    }
}
