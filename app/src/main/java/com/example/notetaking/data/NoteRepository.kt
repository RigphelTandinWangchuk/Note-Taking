package com.example.notetaking.data

import com.example.notetaking.model.Note

object NoteRepository {

    private val notes = mutableListOf<Note>()
    private var nextId = 1L

    fun getNotes(): List<Note> = notes.toList()

    fun getNoteById(id: Long): Note? = notes.find { it.id == id }

    fun addNote(title: String, content: String): Note {
        val note = Note(id = nextId++, title = title, content = content)
        notes.add(0, note)
        return note
    }

    fun updateNote(id: Long, title: String, content: String) {
        val note = getNoteById(id) ?: return
        note.title = title
        note.content = content
    }

    fun deleteNote(id: Long) {
        notes.removeAll { it.id == id }
    }
}
