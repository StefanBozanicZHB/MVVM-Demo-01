package com.example.mvvmdemo01

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mvvmdemo01.db.Subscriber
import com.example.mvvmdemo01.db.SubscriberRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel() {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    val inputName = MutableLiveData<String>()
    val inputEmail = MutableLiveData<String>()

    val saveOrUpdateButtonText = MutableLiveData<String>()
    val clearAllOrDeleteButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllOrDeleteButtonText.value = "Clear All"
    }

    fun saveOrUpdate() {
        if (inputName.value == null) {
            statusMessage.value = Event("Please enter name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("Please enter email")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("Please enter correct email address")
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = ""
                inputEmail.value = ""
            }
        }
    }

    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            deleteAll()
        }
    }

    private fun insert(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRow = repository.insert(subscriber)
        withContext(Dispatchers.Main) {
            if (numberOfRow > -1) {
                statusMessage.value = Event("Subscriber Inserted Successfully")
            } else {
                statusMessage.value = Event("Subscriber Inserted Failed")
            }
        }
    }

    private fun update(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRow = repository.update(subscriber)
        withContext(Dispatchers.Main) {
            if (numberOfRow > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear"
                statusMessage.value = Event("Subscriber Updated Successfully")
            } else {
                statusMessage.value = Event("Subscriber Updated Failed")
            }
        }
    }

    private fun delete(subscriber: Subscriber) = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRow = repository.delete(subscriber)
        withContext(Dispatchers.Main) {
            if (numberOfRow > 0) {
                inputName.value = ""
                inputEmail.value = ""
                isUpdateOrDelete = false
                saveOrUpdateButtonText.value = "Save"
                clearAllOrDeleteButtonText.value = "Clear"
                statusMessage.value = Event("Subscriber Deleted Successfully")
            } else {
                statusMessage.value = Event("Subscriber Deleted Failed")
            }
        }
    }

    private fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        val numberOfRow = repository.deleteAll()
        withContext(Dispatchers.Main) {
            if (numberOfRow > 0) {
                statusMessage.value = Event("All Subscribers Deleted Successfully")
            } else {
                statusMessage.value = Event("All Subscribers Deleted Failed")
            }
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "Update"
        clearAllOrDeleteButtonText.value = "Delete"
    }
}