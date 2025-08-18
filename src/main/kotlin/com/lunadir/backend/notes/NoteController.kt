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
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/api/notes")
class NoteController(
    private val noteService: NoteService
) {

    @GetMapping
    fun getAllByUserId(
        @RequestParam(required = true) userId: UUID,
    ): ResponseEntity<List<Note>> {
        val notes = noteService.getAllByUserId(userId)
        return ResponseEntity.ok(notes)
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable("id") userId: UUID
    ): ResponseEntity<Note> {
        val note = noteService.getById(userId)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(note)
    }

    @PostMapping
    fun create(
        @RequestBody noteDto: NoteDto,
    ): ResponseEntity<Note> {
        val createdNote = noteService.create(noteDto)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity(
            createdNote,
            HttpStatus.CREATED
        )
    }

    @PutMapping("/{id}")
    fun update(
        @PathVariable("id") noteId: UUID,
        @RequestBody noteDto: NoteDto,
    ): ResponseEntity<Note> {
        val updatedNote = noteService.update(noteId, noteDto)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(updatedNote)
    }

    @DeleteMapping("/{id}")
    fun delete(
        @PathVariable("id") noteId: UUID
    ): ResponseEntity<Void> {
        return if (!noteService.delete(noteId)) {
            ResponseEntity.notFound().build()
        } else
            ResponseEntity.noContent().build()
    }
}
