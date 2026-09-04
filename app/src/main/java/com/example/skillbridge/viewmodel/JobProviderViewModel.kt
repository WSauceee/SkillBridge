package com.example.skillbridge.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.skillbridge.data.Job
import com.example.skillbridge.data.JobApplication
import com.example.skillbridge.data.JobApplicationWithSeeker
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

    private val _applications = MutableStateFlow<List<JobApplicationWithSeeker>>(emptyList())
    val applications: StateFlow<List<JobApplicationWithSeeker>> = _applications.asStateFlow()

    fun loadJobs(providerId: Int) {
        viewModelScope.launch {
            repository.getProviderJobs(providerId)
                .collect { jobList ->
                    _jobs.value = jobList
                }
        }
    }

    fun loadApplications(providerId: Int) {
        viewModelScope.launch {
            repository.getApplicationsForProvider(providerId)
                .collect { appList ->
                    _applications.value = appList
                }
        }
    }

    fun acceptApplication(applicationId: Int) {
        viewModelScope.launch {
            repository.updateApplicationStatus(applicationId, "Accepted")
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
