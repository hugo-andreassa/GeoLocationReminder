package com.udacity.project4.locationreminders.data.local

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import com.google.common.truth.Truth
import com.udacity.project4.locationreminders.data.dto.ReminderDTO

import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import kotlinx.coroutines.ExperimentalCoroutinesApi;
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Test

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
//Unit test the DAO
@SmallTest
class RemindersDaoTest {

    private lateinit var database: RemindersDatabase
    private lateinit var dao: RemindersDao

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun initDatabase() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            RemindersDatabase::class.java
        ).build()

        dao = database.reminderDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertReminder_success() = runBlockingTest {
        val reminderData = ReminderDTO(
            "Test",
            "Test",
            "Local DB",
            3.895487,
            8.85421
        )

        dao.saveReminder(reminderData)

        assertThat(dao.getReminders().size, `is`(1))
        assertThat(dao.getReminders().contains(reminderData), `is`(true))
    }

    @Test
    fun retrieveReminder_succeeds() = runBlockingTest {
        val reminderData = ReminderDTO(
            "Test",
            "Test",
            "Local DB",
            3.895487,
            8.85421
        )
        dao.saveReminder(reminderData)

        val reminder = dao.getReminderById(reminderData.id)

        assertThat(reminder, `is`(notNullValue()))
        assertThat(reminder?.title, `is`(reminderData.title))
        assertThat(reminder?.description, `is`(reminderData.description))
        assertThat(reminder?.location, `is`(reminderData.location))
        assertThat(reminder?.latitude, `is`(reminderData.latitude))
        assertThat(reminder?.longitude, `is`(reminderData.longitude))
    }

    @Test
    fun deleteAll_succeeds() = runBlockingTest {
        val reminderData = ReminderDTO(
            "Test",
            "Test",
            "Local DB",
            3.895487,
            8.85421
        )
        dao.saveReminder(reminderData)

        dao.deleteAllReminders()
        assertThat(dao.getReminders().isEmpty(), `is`(true))
    }

}