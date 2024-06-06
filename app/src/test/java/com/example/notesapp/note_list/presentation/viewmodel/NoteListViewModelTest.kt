package com.example.notesapp.note_list.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.notesapp.MainCoroutineRule
import com.example.notesapp.core.data.repository.FakeNoteRepository
import com.example.notesapp.core.domain.model.NoteItem
import com.example.notesapp.note_list.domain.use_case.DeleteNoteUseCase
import com.example.notesapp.note_list.domain.use_case.GetAllNotesUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NoteListViewModelTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    private lateinit var fakeNoteRepository: FakeNoteRepository
    private lateinit var deleteNoteUseCase: DeleteNoteUseCase
    private lateinit var getAllNotesUseCase: GetAllNotesUseCase

    private lateinit var noteListViewModel: NoteListViewModel

    @Before
    fun setup() {
        fakeNoteRepository = FakeNoteRepository()
        deleteNoteUseCase = DeleteNoteUseCase(fakeNoteRepository)
        getAllNotesUseCase = GetAllNotesUseCase(fakeNoteRepository)
        noteListViewModel = NoteListViewModel(getAllNotesUseCase, deleteNoteUseCase)
    }

    @Test
    fun `get notes from empty list, return empty list`() = runTest {
        fakeNoteRepository.shouldHaveFilledList(false)

        noteListViewModel.loadNotes()

        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        assertThat(noteListViewModel.noteListState.value.isEmpty()).isTrue()
    }

    @Test
    fun `get notes from list, return list`() = runTest {
        fakeNoteRepository.shouldHaveFilledList(true)

        noteListViewModel.loadNotes()

        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        assertThat(noteListViewModel.noteListState.value.isNotEmpty()).isTrue()
    }

    @Test
    fun `delete notes from list, note deleted`() = runTest {
        fakeNoteRepository.shouldHaveFilledList(true)

        noteListViewModel.loadNotes()

        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        val note = noteListViewModel.noteListState.value.first()

        noteListViewModel.deleteNote(note)

        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        assertThat(noteListViewModel.noteListState.value.contains(note)).isFalse()
    }


    @Test
    fun `sort notes by date, note are sorted by date`() = runTest {
        fakeNoteRepository.shouldHaveFilledList(true)

        noteListViewModel.loadNotes()

        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        var notes = noteListViewModel.noteListState.value

        for (i in 0..notes.size - 2) {
            assertThat(notes[i].dateAdded).isLessThan(notes[i + 1].dateAdded)
        }

        noteListViewModel.changeOrder()
        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        noteListViewModel.changeOrder()
        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        notes = noteListViewModel.noteListState.value

        for (i in 0..notes.size - 2) {
            assertThat(notes[i].dateAdded).isLessThan(notes[i + 1].dateAdded)
        }
    }

    @Test
    fun `sort notes by title, note are sorted by title`() = runTest {
        fakeNoteRepository.shouldHaveFilledList(true)

        noteListViewModel.changeOrder()

        mainCoroutineRule.dispatcher.scheduler.advanceUntilIdle()

        val notes = noteListViewModel.noteListState.value

        for (i in 0..notes.size - 2) {
            assertThat(notes[i].title).isLessThan(notes[i + 1].title)
        }

    }

}