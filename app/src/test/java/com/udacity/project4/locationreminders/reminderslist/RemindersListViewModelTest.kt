package com.udacity.project4.locationreminders.reminderslist


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.udacity.project4.locationreminders.MainCoroutineRule
import com.udacity.project4.locationreminders.data.FakeDataSource
import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.stopKoin
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertFalse

@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class RemindersListViewModelTest {

    private lateinit var viewModel: RemindersListViewModel
    private lateinit var dataSource: FakeDataSource

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    //Initialize dataSource & viewModel
    @Before
    fun setup(){
        dataSource = FakeDataSource()
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
    }

    @After
    fun teardown(){
        stopKoin()
    }


    @Test
    fun loadRemindersTest_withEmptyList_returnTrue(){

        viewModel.loadReminders()

        // Assert remindersList is empty
        assertEquals(emptyList<ReminderDataItem>(),viewModel.remindersList.getOrAwaitValue())
    }


    @Test
    fun loadRemindersTest_withThreeReminders_returnFalse(){

        val reminder1 = ReminderDTO("T1","hello 1","location 1",
            30.254191579122224, 31.455338343915514)


        val reminder2 = ReminderDTO("T2","hello 2","location 2",
            30.256518719233206, 31.50189038390654)



        val reminder3 = ReminderDTO("T3","hello 3","location 3",
            30.17187552186255, 31.492371551805117 )


        val list = mutableListOf<ReminderDTO>(reminder1,reminder2,reminder3)
        dataSource = FakeDataSource(list)
        viewModel = RemindersListViewModel(ApplicationProvider.getApplicationContext(),dataSource)
        viewModel.loadReminders()

        // Assert that remindersList is Not empty after add three reminders to list
        assertFalse(emptyList<ReminderDataItem>() == viewModel.remindersList.getOrAwaitValue())

    }


    @Test
    fun checkLoading() {
        mainCoroutineRule.pauseDispatcher()
        viewModel.loadReminders()
        // Assert showLoading has data
        assertTrue(viewModel.showLoading.getOrAwaitValue())

        mainCoroutineRule.resumeDispatcher()

        //check showLoading has no data
        assertFalse(viewModel.showLoading.getOrAwaitValue())
    }


    @Test
    fun checkError() {
        dataSource.setReturnError(true)

        viewModel.loadReminders()
        // Assert showSnackbar value is Returning data is not found
        assertEquals("data is not found",viewModel.showSnackBar.getOrAwaitValue())
    }
}