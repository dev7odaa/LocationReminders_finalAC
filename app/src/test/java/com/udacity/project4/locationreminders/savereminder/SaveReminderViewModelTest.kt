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


    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Use a fake data source to be injected into the view model.
    private lateinit var dataSource: FakeDataSource

    // Initialise the data source saveReminderViewModel
    private lateinit var saveReminderViewModel: SaveReminderViewModel


    @Before
    fun setupViewModel() {
        stopKoin()

        // Initialise the data source.
        dataSource = FakeDataSource()

        // Initialize the view model with fake data source.
        saveReminderViewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource)
    }

    @Test
    fun onClear_returnNull() {
        // clear view model
        saveReminderViewModel.onClear()

        // check reminderTitle is null
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderTitle.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )

        // show that reminderDescription is null
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderDescription.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )

        // check reminderSelectedLocationStr is null
        MatcherAssert.assertThat(
            saveReminderViewModel.reminderSelectedLocationStr.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )

        // check selectedPOI is null
        MatcherAssert.assertThat(
            saveReminderViewModel.selectedPOI.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )

        // check latitude is null
        MatcherAssert.assertThat(
            saveReminderViewModel.latitude.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )

        // check longitude is null
        MatcherAssert.assertThat(
            saveReminderViewModel.longitude.getOrAwaitValue(),
            CoreMatchers.`is`(CoreMatchers.nullValue())
        )
    }

    @Test
    fun saveNewReminder_newReminderSaved() = mainCoroutineRule.runBlockingTest {
        // GIVEN a reminder item
        val reminder = ReminderDataItem("Title", "Description", "Location", 1.1, 2.2)

        // pass reminder data item to saveReminderViewModel
        saveReminderViewModel.saveReminder(reminder)

        // check saveReminderViewModel is saved
        MatcherAssert.assertThat(
            saveReminderViewModel.showToast.value,
            CoreMatchers.`is`(saveReminderViewModel.app.getString(R.string.reminder_saved))
        )

        // check NavigationCommand.Back isEquals saveReminderViewModel.navigationCommand value
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

        // check validReminder is false
        MatcherAssert.assertThat(validReminder, CoreMatchers.`is`(false))

        // check showSnackBarInt is appear message "please enter title"
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

        // check validReminder is false
        MatcherAssert.assertThat(validReminder, CoreMatchers.`is`(false))

        // check showSnackBarInt is appear message "please select location"
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

        // check showLoading is true
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
        mainCoroutineRule.resumeDispatcher()

        // check showLoading is false
        MatcherAssert.assertThat(
            saveReminderViewModel.showLoading.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }
}