package com.example.skillbridge.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "jobs")
data class Job(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val providerId: Int,

    val companyName: String,

    val title: String,

    val description: String,

    val location: String,

    val salary: String,

    val jobType: String,

    val requiredSkills: String
)