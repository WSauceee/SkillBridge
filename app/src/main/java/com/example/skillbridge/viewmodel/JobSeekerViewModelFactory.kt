package com.example.skillbridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skillbridge.data.JobRepository

class JobSeekerViewModelFactory(
    private val repository: JobRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JobSeekerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JobSeekerViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}