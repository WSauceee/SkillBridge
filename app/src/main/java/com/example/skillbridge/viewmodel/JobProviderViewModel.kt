package com.example.skillbridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillbridge.data.Job
import com.example.skillbridge.data.JobRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class JobProviderViewModel(
    private val repository: JobRepository
) : ViewModel() {

    private val _jobs = MutableStateFlow<List<Job>>(emptyList())

    val jobs: StateFlow<List<Job>> = _jobs.asStateFlow()

    fun loadJobs(providerId: Int) {

        viewModelScope.launch {

            repository.getProviderJobs(providerId)
                .collect { jobList ->

                    _jobs.value = jobList

                }
        }
    }

    fun addJob(
        providerId: Int,
        companyName: String,
        title: String,
        description: String,
        location: String,
        salary: String,
        jobType: String,
        requiredSkills: String,
        requiredEducation: String
    ) {

        viewModelScope.launch {

            val job = Job(
                providerId = providerId,
                companyName = companyName,
                title = title,
                description = description,
                location = location,
                salary = salary,
                jobType = jobType,
                requiredSkills = requiredSkills,
                requiredEducation = requiredEducation
            )

            repository.addJob(job)
        }
    }

    fun updateJob(job: Job) {

        viewModelScope.launch {

            repository.updateJob(job)
        }
    }

    fun deleteJob(job: Job) {

        viewModelScope.launch {

            repository.deleteJob(job)
        }
    }
}