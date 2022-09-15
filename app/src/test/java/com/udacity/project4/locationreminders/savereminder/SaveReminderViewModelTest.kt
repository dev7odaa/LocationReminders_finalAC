package com.udacity.project4.locationreminders.savereminder

import android.os.Build
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.getOrAwaitValue
import com.udacity.project4.locationreminders.reminderslist.ReminderDataItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext
import org.robolectric.annotation.Config
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertFalse



@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class SaveReminderViewModelTest {
    private lateinit var viewModel: SaveReminderViewModel
    private lateinit var dataSource: FakeDataSource

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel() {
        dataSource = FakeDataSource()
        viewModel = SaveReminderViewModel(
            ApplicationProvider.getApplicationContext(),
            dataSource
        )
    }

    @After
    fun stopDown() {
        GlobalContext.stop()
    }

    @Test
    fun saveReminderTest() = runBlocking {
        val reminder = ReminderDataItem(
            "title",
            "description",
            "location",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        viewModel.saveReminder(reminder)

        //Assert validation when all data available  Toast Shows "Reminder Saved !"
        assertEquals("Reminder Saved !", viewModel.showToast.getOrAwaitValue())
    }

    @Test
    fun validateAndSaveReminderTest_valid_returnTrue() = runBlocking {
        val reminder = ReminderDataItem(
            "title",
            "description",
            "location",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        val result = viewModel.validateEnteredData(reminder)

        //Assert validation when all data available return true
        assertTrue(result)
    }


    @Test
    fun validateAndSaveReminderTest_emptyTitle_returnFalse() = runBlocking {
        val reminder = ReminderDataItem(
            "",
            "description",
            "location",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        val result = viewModel.validateEnteredData(reminder)

        //Assert validation when no title return false
        assertFalse(result)
    }

    @Test
    fun validateAndSaveReminderTest_emptyLocation_returnFalse() = runBlocking {
        val reminder = ReminderDataItem(
            "title",
            "description",
            "",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        val result = viewModel.validateEnteredData(reminder)

        //Assert validation when no location return false
        assertFalse(result)
    }

    @Test
    fun validateAndSaveReminderTest_emptyTitleAndLocation_returnFalse() = runBlocking {
        val reminder = ReminderDataItem(
            "",
            "description",
            "",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        val result = viewModel.validateEnteredData(reminder)

        //Assert validation when no title & no location return false
        assertFalse(result)
    }

    @Test
    fun checkLoading() {
        mainCoroutineRule.pauseDispatcher()

        val reminder = ReminderDataItem(
            "",
            "description",
            "",
            latitude = 47.5456551,
            longitude = 122.0101731
        )
        viewModel.saveReminder(reminder)

        //Assert showLoading is true
        assertTrue(viewModel.showLoading.getOrAwaitValue())
    }
}