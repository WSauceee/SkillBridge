package com.example.skillbridge.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface JobDao {

    @Insert
    suspend fun insertJob(job: Job): Long

    @Update
    suspend fun updateJob(job: Job): Int

    @Delete
    suspend fun deleteJob(job: Job): Int

    @Query("SELECT * FROM jobs WHERE providerId = :providerId ORDER BY id DESC")
    fun getJobsByProvider(providerId: Int): Flow<List<Job>>

    @Query("SELECT * FROM jobs ORDER BY id DESC")
    fun getAllJobs(): Flow<List<Job>>
}