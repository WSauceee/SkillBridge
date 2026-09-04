package com.example.skillbridge.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface JobApplicationDao {
    @Insert
    suspend fun insertApplication(application: JobApplication): Long

    @Query("SELECT * FROM job_applications WHERE jobId = :jobId")
    fun getApplicationsForJob(jobId: Int): Flow<List<JobApplication>>

    @Query("SELECT * FROM job_applications WHERE seekerId = :seekerId")
    fun getApplicationsForSeeker(seekerId: Int): Flow<List<JobApplication>>

    @Transaction
    @Query("""
        SELECT ja.* FROM job_applications ja
        INNER JOIN jobs j ON ja.jobId = j.id
        WHERE j.providerId = :providerId
        ORDER BY ja.appliedAt DESC
    """)
    fun getApplicationsForProvider(providerId: Int): Flow<List<JobApplicationWithSeeker>>

    @Query("UPDATE job_applications SET status = :status WHERE id = :applicationId")
    suspend fun updateApplicationStatus(applicationId: Int, status: String): Int
}
