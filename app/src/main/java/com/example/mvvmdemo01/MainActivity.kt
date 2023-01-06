package com.example.mvvmdemo01

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmdemo01.databinding.ActivityMainBinding
import com.example.mvvmdemo01.db.Subscriber
import com.example.mvvmdemo01.db.SubscriberDatabase
import com.example.mvvmdemo01.db.SubscriberRepository

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var subscriberViewModel: SubscriberViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val dao = SubscriberDatabase.getInstance(application).subscriberDAO
        val repository = SubscriberRepository(dao)
        val factory = SubscriberViewModelFactory(repository)
        subscriberViewModel = ViewModelProvider(this, factory)[SubscriberViewModel::class.java]
        binding.myViewModel = subscriberViewModel
        binding.lifecycleOwner = this
        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.subscriberRecyclerView.layoutManager = LinearLayoutManager(this)
        displaySubscribersList()

    }

    private fun displaySubscribersList() {
        subscriberViewModel.subscribers.observe(this) {
            binding.subscriberRecyclerView.adapter = MyRecyclerViewAdapter(
                it
            ) { selectedItem: Subscriber -> listItemClicked(selectedItem) }
        }
    }

    private fun listItemClicked(subscriber: Subscriber) {
        subscriberViewModel.initUpdateAndDelete(subscriber)
    }
}