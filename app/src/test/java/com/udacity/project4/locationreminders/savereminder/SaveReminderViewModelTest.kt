package com.udacity.project4.locationreminders.savereminder

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.R
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import junit.framework.Assert

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class SaveReminderViewModelTest {


    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Use a fake data source to be injected into the view model.
    private lateinit var dataSource: FakeDataSource

    // Subject under test
    private lateinit var saveReminderViewModel: SaveReminderViewModel


    @Before
    fun setupViewModel() {
        stopKoin()

        // Initialise the data source with no reminders.
        dataSource = FakeDataSource()

        // Initialize the view model
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource)
    }

    @Test
    fun onClear_returnNull() {
        // WHEN clear view model
        saveReminderViewModel.onClear()

        // THEN reminder values are null
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderDescription.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.selectedPOI.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.latitude.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )
        MatcherAssert.assertThat(
            saveReminderViewModel.longitude.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )
    }

    @Test
    fun saveNewReminder_newReminderSaved() = mainCoroutineRule.runBlockingTest {
        // GIVEN a reminder item
        val reminder = ReminderDataItem("Title", "Description", "Location", 1.1, 2.2)

        // WHEN request save reminder from view model
        saveReminderViewModel.saveReminder(reminder)

        // THEN show toast, and navigate back
        MatcherAssert.assertThat(
            saveReminderViewModel.showToast.value,
            CoreMatchers.`is`(saveReminderViewModel.app.getString(R.string.reminder_saved))
        )
        Assert.assertEquals(
            saveReminderViewModel.navigationCommand.getOrAwaitValue(),
            NavigationCommand.Back
        )
    }

    @Test
    fun saveNewReminder_titleIsEmpty_returnFalse_showSnackBar() {
        // GIVEN a reminder item without title
        val reminder = ReminderDataItem("", "Description", "Location", 1.1, 2.2)

        // WHEN validate reminder data
        val validReminder = saveReminderViewModel.validateEnteredData(reminder)

        // THEN
        MatcherAssert.assertThat(validReminder, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.getOrAwaitValue(),
            CoreMatchers.`is`(R.string.err_enter_title)
        )
    }

    @Test
    fun saveNewReminder_locationIsEmpty_returnFalse_showSnackBar() {
        // GIVEN a reminder item without location
        val reminder = ReminderDataItem("Title", "Description", "", 1.1, 2.2)

        // WHEN validate reminder data
        val validReminder = saveReminderViewModel.validateEnteredData(reminder)

        // THEN
        MatcherAssert.assertThat(validReminder, CoreMatchers.`is`(false))
        MatcherAssert.assertThat(
            saveReminderViewModel.showSnackBarInt.value,
            CoreMatchers.`is`(R.string.err_select_location)
        )
    }

    @Test
    fun saveReminder_showLoading() {
        // GIVEN a reminder item
        val reminder = ReminderDataItem("Title", "Description", "Location", 1.1, 2.2)

        // WHEN request save reminder from view model
        mainCoroutineRule.pauseDispatcher()
        saveReminderViewModel.saveReminder(reminder)

        // THEN
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()

        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }
}