package com.udacity.project4.locationreminders.reminderslist

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Set the main coroutines dispatcher for unit testing.
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    // Use a fake data source to be injected into the view model.
    private lateinit var dataSource: FakeDataSource

    private lateinit var remindersListViewModel: RemindersListViewModel


    @Before
    fun setupViewModel() {
        stopKoin()

        // Initialise the data source with no reminders.
        dataSource = FakeDataSource()

        // Initialize the view model
        remindersListViewModel = RemindersListViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource)
    }

    @Test
    fun loadReminders_listNotEmpty() = mainCoroutineRule.runBlockingTest {

        // GIVEN empty reminders list
        dataSource.deleteAllReminders()
        val reminder = ReminderDTO("Title", "Description", "Location", 1.1, 2.2)
        dataSource.saveReminder(reminder)

        // WHEN request reminders from data source
        remindersListViewModel.loadReminders()


        // show that remindersList is not empty
        MatcherAssert.assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue().isNotEmpty(),
            CoreMatchers.`is`(true)
        )

        // show that showNoData is false
        MatcherAssert.assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            CoreMatchers.`is`(false)
        )
    }

    @Test
    fun loadReminders_returnEmptyList() = mainCoroutineRule.runBlockingTest {

        // GIVEN empty reminders list
        dataSource.deleteAllReminders()

        // WHEN request reminders from data source
        remindersListViewModel.loadReminders()

        // show that remindersList is empty
        MatcherAssert.assertThat(
            remindersListViewModel.remindersList.getOrAwaitValue().isEmpty(),
            CoreMatchers.`is`(true)
        )

        // show that showNoData is true
        MatcherAssert.assertThat(
            remindersListViewModel.showNoData.getOrAwaitValue(),
            CoreMatchers.`is`(true)
        )
    }

    @Test
    fun loadReminders_returnError() = mainCoroutineRule.runBlockingTest {

        // assure that data source return error or not
        dataSource.setReturnError(true)

        // WHEN request reminders from data source
        remindersListViewModel.loadReminders()

        // show that showSnackBar value is Returning testing error!
        MatcherAssert.assertThat(
            remindersListViewModel.showSnackBar.getOrAwaitValue(),
            CoreMatchers.`is`("Returning testing error!")
        )
    }

}