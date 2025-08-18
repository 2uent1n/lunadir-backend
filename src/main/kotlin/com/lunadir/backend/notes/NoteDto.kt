package com.lunadir.backend.notes

import java.util.UUID

data class NoteDto(
    var title: String,
    val content: String,
    var type: NoteType?,
    var userId: UUID,
)
