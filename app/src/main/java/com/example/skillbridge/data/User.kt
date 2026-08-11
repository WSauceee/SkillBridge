package com.example.skillbridge.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val fullName: String,        // person's name, or company name for a Business account
    val email: String,
    val password: String,        // plain text for assignment simplicity — see note at the end
    val accountType: AccountType,
    val extraInfo: String? = null // job title (Job Seeker) or industry (Business)
)