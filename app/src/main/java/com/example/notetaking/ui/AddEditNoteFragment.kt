package com.example.notetaking.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.notetaking.R
import com.example.notetaking.data.NoteRepository
import com.example.notetaking.databinding.FragmentAddEditNoteBinding
class AddEditNoteFragment : Fragment() {

    private var _binding: FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!

    private var noteId: Long = NO_ID

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteId = arguments?.getLong(ARG_NOTE_ID, NO_ID) ?: NO_ID
        val existingNote = if (noteId != NO_ID) NoteRepository.getNoteById(noteId) else null

        // Populate the toolbar/title text and pre-fill fields when editing.
        if (existingNote != null) {
            binding.textScreenTitle.text = getString(R.string.edit_note)
            binding.editTitle.setText(existingNote.title)
            binding.editContent.setText(existingNote.content)
        } else {
            binding.textScreenTitle.text = getString(R.string.add_note)
        }

        binding.buttonSave.setOnClickListener { onSaveClicked() }
        binding.buttonCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun onSaveClicked() {
        val title = binding.editTitle.text?.toString()?.trim().orEmpty()
        val content = binding.editContent.text?.toString()?.trim().orEmpty()

        var isValid = true
        if (title.isEmpty()) {
            binding.layoutTitle.error = getString(R.string.error_title_required)
            isValid = false
        } else {
            binding.layoutTitle.error = null
        }

        if (content.isEmpty()) {
            binding.layoutContent.error = getString(R.string.error_content_required)
            isValid = false
        } else {
            binding.layoutContent.error = null
        }

        if (!isValid) return

        if (noteId != NO_ID) {
            NoteRepository.updateNote(noteId, title, content)
        } else {
            NoteRepository.addNote(title, content)
        }

        parentFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_NOTE_ID = "arg_note_id"
        private const val NO_ID = -1L

        fun newInstance(noteId: Long = NO_ID): AddEditNoteFragment {
            return AddEditNoteFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_NOTE_ID, noteId)
                }
            }
        }
    }
}
