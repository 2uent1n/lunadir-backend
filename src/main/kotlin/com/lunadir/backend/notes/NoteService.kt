package com.lunadir.backend.notes

import com.lunadir.backend.security.AuthContextService
import com.lunadir.backend.users.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.util.UUID

@Service
class NoteService(
    private val noteRepository: NoteRepository,
    private val userRepository: UserRepository,
    private val authContextService: AuthContextService,
) {

    /**
     * Gets all the Notes of the authenticated User.
     */
    fun getAllForAuthenticatedUser(): List<Note> {
        val userId = authContextService.getAuthenticatedUserId()
        return noteRepository.findAllByUserId(userId)
    }

    /**
     * Gets a Note of the authenticated User by its ID.
     */
    fun getByIdForAuthenticatedUser(id: UUID): Note? {
        val userId = authContextService.getAuthenticatedUserId()
        return noteRepository.findByIdAndUserId(id, userId)
    }

    /**
     * Creates a Note for the authenticated User.
     */
    fun create(noteDto: NoteDto): Note {
        val userId = authContextService.getAuthenticatedUserId()
        /**
         * Using the reference saves an SQL request, but will result
         * in a runtime error if the User does not exist, which
         * is unlikely here.
         */
        val user = userRepository.getReferenceById(userId)
        val noteType = noteDto.type
            ?: throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid note type.")
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

    /**
     * Asserts that the provided Note is owned by the User with the provided userId.
     *
     * @throws ResponseStatusException with a FORBIDDEN status if the owner of the Note is not the provided User.
     */
    private fun assertNoteIsOwnedByUser(note: Note, userId: UUID) {
        if (note.user.id != userId) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "User is not the owner of this note.")
        }
    }

    /**
     * Updates a Note if the authenticated User is its owner.
     */
    fun update(noteId: UUID, noteDto: NoteDto): Note {
        val existingNote = noteRepository.findByIdOrNull(noteId)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Note with such ID does not exist.")
        val authenticatedUserId = authContextService.getAuthenticatedUserId()
        assertNoteIsOwnedByUser(existingNote, authenticatedUserId)
        val updatedNote = existingNote.copy(
            title = noteDto.title,
            content = noteDto.content,
        )
        return noteRepository.save(updatedNote)
    }

    /**
     * Deletes a Note if the authenticated User is its owner.
     */
    fun delete(id: UUID) {
        val existingNote = noteRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Note with such ID does not exist.")
        val authenticatedUserId = authContextService.getAuthenticatedUserId()
        assertNoteIsOwnedByUser(existingNote, authenticatedUserId)
        noteRepository.deleteById(id)
    }
}
