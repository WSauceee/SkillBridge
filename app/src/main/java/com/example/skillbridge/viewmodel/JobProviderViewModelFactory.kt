package com.example.skillbridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillbridge.data.JobRepository

class JobProviderViewModelFactory(
    private val repository: JobRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(JobProviderViewModel::class.java)) {

            @Suppress("UNCHECKED_CAST")

            return JobProviderViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}