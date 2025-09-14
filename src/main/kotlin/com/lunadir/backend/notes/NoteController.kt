package com.lunadir.backend.notes

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
@RequestMapping("/notes")
class NoteController(
    private val noteService: NoteService
) {

    /**
     * Create a Note, owned by the authenticated User.
     */
    @PostMapping
    fun create(
        @RequestBody noteDto: NoteDto,
    ): ResponseEntity<Note> {
        val createdNote = noteService.create(noteDto)
        return ResponseEntity(
            createdNote,
            HttpStatus.CREATED
        )
    }

    /**
     * Get all the Notes of the authenticated User.
     */
    @GetMapping
    fun getAll(): ResponseEntity<List<Note>> {
        val notes = noteService.getAllForAuthenticatedUser()
        return ResponseEntity.ok(notes)
    }

    /**
     * Get a Note by its ID, only if its owner is the authenticated User.
     */
    @GetMapping("/{id}")
    fun getById(
        @PathVariable("id") noteId: UUID
    ): ResponseEntity<Note> {
        val note = noteService.getByIdForAuthenticatedUser(noteId)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(note)
    }

    /**
     * Update a Note, only if its owner is the authenticated User.
     */
    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") noteId: UUID,
        @RequestBody noteDto: NoteDto,
    ): ResponseEntity<Note> {
        val updatedNote = noteService.update(noteId, noteDto)
        return ResponseEntity.ok(updatedNote)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable("id") noteId: UUID
    ): ResponseEntity<Void> {
        noteService.delete(noteId)
        return ResponseEntity.noContent().build()
    }
}
