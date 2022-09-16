package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result

//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO>? = mutableListOf()) : ReminderDataSource {


    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }


    override suspend fun getReminders(): Result<List<ReminderDTO>> {
        // get all reminders

        // if we need to test it with error handling.
        // i make data source return error even if not empty to test error.
        // for the network I talk generally and our app not contain any network.
        // the error could be something with the db

        if(shouldReturnError) {
            return Result.Error("Returning testing error!")
        }
        // if available return Success
        reminders?.let {
            return Result.Success(ArrayList(it))
        }
        // if not available return error
        return Result.Error("Reminders not found!")
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // save reminders
        reminders?.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        //get reminder with id

        // if reminder found with this id
        reminders?.let {
            return Result.Success(it.single { reminder -> reminder.id == id })
        }
        // if reminder not found with this id
        return Result.Error("Reminder not found!")
    }

    override suspend fun deleteAllReminders() {
        //delete all reminders
        reminders?.clear()
    }
}