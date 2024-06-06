package com.example.notesapp.core.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.filters.SmallTest
import com.example.notesapp.core.data.local.entity.NoteDao
import com.example.notesapp.core.data.local.entity.NoteDb
import com.example.notesapp.core.data.local.entity.NoteEntity
import com.example.notesapp.core.di.AppModule
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
@SmallTest
@UninstallModules(AppModule::class)
class NoteDaoTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    lateinit var noteDb: NoteDb
    private lateinit var noteDao: NoteDao

    @Before
    fun setUp() {
        hiltRule.inject()
        noteDao = noteDb.noteDao

    }


    @Test
    fun getAllNotesFromEmptyDb_noteListEmpty() = runTest {
        assertThat(noteDao.getAllNoteEntities().isEmpty())
    }

    @Test
    fun getAllNotesFromDb_noteListIsNotEmpty() = runTest {
        for (i in 1..4) {
            val noteEntity = NoteEntity(
                title = "title $i",
                id = i,
                imageUrl = "image $i",
                dateAdded = System.currentTimeMillis(),
                description = "content $i"
            )
            noteDao.insertNoteEntity(noteEntity)
        }
        assertThat(noteDao.getAllNoteEntities().isNotEmpty())
    }

    @Test
    fun insertNote_noteIsInserted() = runTest {
        val noteEntity = NoteEntity(
            title = "title",
            id = 1,
            imageUrl = "image",
            dateAdded = System.currentTimeMillis(),
            description = "content"
        )
        noteDao.insertNoteEntity(noteEntity)
        assertThat(noteDao.getAllNoteEntities().contains(noteEntity))
    }

    @Test
    fun deleteNote_noteIsDeleted() = runTest {
        val noteEntity = NoteEntity(
            title = "title",
            id = 1,
            imageUrl = "image",
            dateAdded = System.currentTimeMillis(),
            description = "content"
        )
        noteDao.insertNoteEntity(noteEntity)

        assertThat(noteDao.getAllNoteEntities().contains(noteEntity)).isTrue()

        noteDao.deleteNoteEntity(noteEntity)

        assertThat(!noteDao.getAllNoteEntities().contains(noteEntity)).isTrue()
    }


    @After
    fun tearDown() {
        noteDb.close()
    }
}