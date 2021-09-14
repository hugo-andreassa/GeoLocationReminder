package com.udacity.project4.locationreminders.savereminder

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import com.udacity.project4.locationreminders.util.MainCoroutineRule
import com.udacity.project4.locationreminders.util.getOrAwaitValue

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class SaveReminderViewModelTest {

    private lateinit var fakeDataSource: FakeDataSource
    private lateinit var viewModel: SaveReminderViewModel

    @get:Rule
    val instantTaskExecRule = InstantTaskExecutorRule()

    @get:Rule
    val mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setup() {
        stopKoin()

        fakeDataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            fakeDataSource
        )
    }

    @Test
    fun viewModel_savingValidItem_success() {
        val reminder = ReminderDataItem(
            "Test",
            "Test",
            "McDonald's",
            5.679893,
            2.53673
        )

        viewModel.validateAndSaveReminder(reminder)

        assertThat(
            viewModel.showToast.getOrAwaitValue(),
            Matchers.`is`(
                ApplicationProvider.getApplicationContext<Application>()
                    .getString(R.string.reminder_saved)
            )
        )
        assertThat(
            viewModel.navigationCommand.getOrAwaitValue(),
            Matchers.`is`(NavigationCommand.Back)
        )
    }

    @Test
    fun viewModel_savingInvalidItem_failed() {
        val reminder = ReminderDataItem(
            "",
            "Test",
            "McDonald's",
            5.679893,
            2.53673
        )

        viewModel.validateAndSaveReminder(reminder)

        assertThat(
            viewModel.showSnackBarInt.getOrAwaitValue(),
            Matchers.`is`(R.string.err_enter_title)
        )
    }

    @Test
    fun viewModel_showLoading() {
        val reminder = ReminderDataItem(
            "Test",
            "Test",
            "McDonald's",
            5.679893,
            2.53673
        )

        mainCoroutineRule.pauseDispatcher()

        viewModel.validateAndSaveReminder(reminder)
        assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            Matchers.`is`(true)
        )

        mainCoroutineRule.resumeDispatcher()
        assertThat(
            viewModel.showLoading.getOrAwaitValue(),
            Matchers.`is`(false)
        )
    }
}