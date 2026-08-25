package com.example.notetaking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notetaking.R
import com.example.notetaking.data.NoteRepository
import com.example.notetaking.databinding.FragmentNoteListBinding
import com.example.notetaking.model.Note
class NoteListFragment : Fragment() {

    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: NoteAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = NoteAdapter(
            onNoteClick = { note -> openEditNote(note) },
            onDeleteClick = { note -> confirmDelete(note) }
        )

        binding.recyclerNotes.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerNotes.adapter = adapter

        binding.fabAddNote.setOnClickListener { openAddNote() }

        refreshList()
    }

    override fun onResume() {
        super.onResume()
        // Refresh in case a note was added/edited/deleted by the other fragment.
        refreshList()
    }

    private fun refreshList() {
        val notes = NoteRepository.getNotes()
        adapter.submitList(notes)

        val isEmpty = notes.isEmpty()
        binding.textEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.recyclerNotes.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun openAddNote() {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, AddEditNoteFragment.newInstance(noteId = -1L))
            addToBackStack(null)
        }
    }

    private fun openEditNote(note: Note) {
        parentFragmentManager.commit {
            replace(R.id.fragment_container, AddEditNoteFragment.newInstance(noteId = note.id))
            addToBackStack(null)
        }
    }

    private fun confirmDelete(note: Note) {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_note_title)
            .setMessage(getString(R.string.delete_note_message, note.title))
            .setPositiveButton(R.string.delete) { dialog, _ ->
                NoteRepository.deleteNote(note.id)
                refreshList()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

/**
 * Small helper so fragment transactions read fluently: `commit { ... }`
 * instead of the more verbose beginTransaction()/commit() pair.
 */
private inline fun androidx.fragment.app.FragmentManager.commit(
    body: androidx.fragment.app.FragmentTransaction.() -> Unit
) {
    val transaction = beginTransaction()
    transaction.body()
    transaction.commit()
}
