package com.example.skillbridge.data

import kotlinx.coroutines.flow.Flow

class JobRepository(
    private val jobDao: JobDao,
    private val jobApplicationDao: JobApplicationDao
) {

    suspend fun addJob(job: Job) {
        jobDao.insertJob(job)
    }

    suspend fun updateJob(job: Job) {
        jobDao.updateJob(job)
    }

    suspend fun deleteJob(job: Job) {
        jobDao.deleteJob(job)
    }

    fun getProviderJobs(providerId: Int) =
        jobDao.getJobsByProvider(providerId)

    fun getAllJobs() =
        jobDao.getAllJobs()

    suspend fun applyForJob(jobId: Int, seekerId: Int) {
        val application = JobApplication(jobId = jobId, seekerId = seekerId)
        jobApplicationDao.insertApplication(application)
    }

    fun getApplicationsForProvider(providerId: Int): Flow<List<JobApplicationWithSeeker>> =
        jobApplicationDao.getApplicationsForProvider(providerId)

    fun getApplicationsForSeeker(seekerId: Int) =
        jobApplicationDao.getApplicationsForSeeker(seekerId)

    suspend fun updateApplicationStatus(applicationId: Int, status: String) {
        jobApplicationDao.updateApplicationStatus(applicationId, status)
    }
}
