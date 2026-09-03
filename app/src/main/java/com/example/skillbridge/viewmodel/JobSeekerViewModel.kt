package com.example.skillbridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillbridge.data.Job
import com.example.skillbridge.data.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobSeekerViewModel(
    private val repository: JobRepository
) : ViewModel() {

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())
    val jobs: StateFlow<List<Job>> = _jobs.asStateFlow()

    init {
        loadAllJobs()
    }

    private fun loadAllJobs() {
        viewModelScope.launch {
            repository.getAllJobs().collect { jobList ->
                _jobs.value = jobList
            }
        }
    }
}