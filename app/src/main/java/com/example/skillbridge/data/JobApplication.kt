package com.example.skillbridge.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation

@Entity(tableName = "job_applications")
data class JobApplication(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val jobId: Int,
    val seekerId: Int,
    val appliedAt: Long = System.currentTimeMillis(),
    val status: String = "Pending" // Pending, Accepted, Rejected
)

data class JobApplicationWithSeeker(
    @Embedded val application: JobApplication,
    @Relation(
        parentColumn = "seekerId",
        entityColumn = "id"
    )
    val seeker: User
)
