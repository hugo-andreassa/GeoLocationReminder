package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.google.common.truth.Truth
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Medium Test to test the repository
@MediumTest
class RemindersLocalRepositoryTest {

    private lateinit var database: RemindersDatabase
    private lateinit var repository: RemindersLocalRepository

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        repository = RemindersLocalRepository(database.reminderDao(), Dispatchers.Main)
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertReminder_succeeds() = runBlocking {
        val reminderData = ReminderDTO(
            "Test",
            "Test",
            "Local DB",
            3.895487,
            8.85421
        )
        repository.saveReminder(reminderData)

        val result = repository.getReminders()

        result as Result.Success

        assertThat(result.data.size, `is`(1))
        assertThat(result.data.contains(reminderData), `is`(true))
    }

    @Test
    fun retrieveReminder_succeeds() = runBlocking {
        val reminderData = ReminderDTO(
            "Test",
            "Test",
            "Local DB",
            3.895487,
            8.85421
        )
        repository.saveReminder(reminderData)

        val result = repository.getReminder(reminderData.id)

        result as Result.Success
        assertThat(result.data, `is`(CoreMatchers.notNullValue()))
        assertThat(result.data.title, `is`(reminderData.title))
        assertThat(result.data.description, `is`(reminderData.description))
        assertThat(result.data.location, `is`(reminderData.location))
        assertThat(result.data.latitude, `is`(reminderData.latitude))
        assertThat(result.data.longitude, `is`(reminderData.longitude))
    }

    @Test
    fun retrieveReminder_error() = runBlocking {
        val reminderData = ReminderDTO(
            "Test",
            "Test",
            "Local DB",
            3.895487,
            8.85421
        )
        val result = repository.getReminder(reminderData.id)

        result as Result.Error
        assertThat(result.message, `is`("Reminder not found!"))
        assertThat(result.statusCode, `is`(nullValue()))
    }

    @Test
    fun deleteReminders_succeeds() = runBlocking {
        val reminderData = ReminderDTO(
            "Test",
            "Test",
            "Local DB",
            3.895487,
            8.85421
        )
        repository.saveReminder(reminderData)

        repository.deleteAllReminders()

        val result = repository.getReminders()

        result as Result.Success
        assertThat(result.data.isEmpty(), `is`(true))
    }

}