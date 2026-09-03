package com.example.skillbridge.data

class JobRepository(
    private val jobDao: JobDao
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
}