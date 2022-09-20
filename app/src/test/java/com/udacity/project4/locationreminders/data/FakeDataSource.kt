package com.udacity.project4.locationreminders.data

import com.udacity.project4.locationreminders.data.dto.ReminderDTO
import com.udacity.project4.locationreminders.data.dto.Result


//Use FakeDataSource that acts as a test double to the LocalDataSource
class FakeDataSource(var reminders: MutableList<ReminderDTO> = mutableListOf()) : ReminderDataSource {

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


        if (shouldReturnError) {
            return Result.Error("Returning testing error!")
        }

        //as you order. when no reminders are found,
        // Room simply returns an empty list and the data source actually returns Result.Success
        if (reminders.isEmpty()) {
            return Result.Success(reminders)
        } else {
            return Result.Success(reminders)
        }
    }

    override suspend fun saveReminder(reminder: ReminderDTO) {
        // save reminders
        reminders.add(reminder)
    }

    override suspend fun getReminder(id: String): Result<ReminderDTO> {
        //get reminder with id

        if(shouldReturnError) {
            return Result.Error("Returning testing error!")
        }

        // if reminder found with this id
        val result = reminders.firstOrNull{it.id == id}
        result?.let {
            return Result.Success(it)
        }

        // if reminder not found with this id
        return Result.Error("This Reminder not available")

    }

    override suspend fun deleteAllReminders() {
        //delete all reminders
        reminders.clear()
    }

}