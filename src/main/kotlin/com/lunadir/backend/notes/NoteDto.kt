package com.lunadir.backend.notes

data class NoteDto(
    var title: String,
    val content: String,
    var type: NoteType?,
)
