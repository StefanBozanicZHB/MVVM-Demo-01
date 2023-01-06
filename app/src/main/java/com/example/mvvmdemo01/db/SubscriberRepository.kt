package com.example.mvvmdemo01.db

class SubscriberRepository(private val dao : SubscriberDAO) {

    val subscribers = dao.getAllSubscriber()

    suspend fun insert(subscriber: Subscriber){
        dao.insertSubscriber(subscriber)
    }

    suspend fun delete(subscriber: Subscriber){
        dao.deleteSubscriber(subscriber)
    }

    suspend fun update(subscriber: Subscriber){
        dao.updateSubscriber(subscriber)
    }

    suspend fun deleteAll(){
        dao.deleteAll()
    }
}