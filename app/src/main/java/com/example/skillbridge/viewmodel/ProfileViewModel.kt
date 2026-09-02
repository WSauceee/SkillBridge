package com.example.skillbridge.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillbridge.data.AuthRepository
import com.example.skillbridge.data.EducationEntry
import com.example.skillbridge.data.ExperienceEntry
import com.example.skillbridge.data.User
import com.example.skillbridge.data.decodeEducation
import com.example.skillbridge.data.decodeExperience
import com.example.skillbridge.data.decodeSkills
import com.example.skillbridge.data.encodeEducation
import com.example.skillbridge.data.encodeExperience
import com.example.skillbridge.data.encodeSkills
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: AuthRepository) : ViewModel() {

    var currentUser by mutableStateOf<User?>(null)
        private set

    fun setUser(user: User) {
        currentUser = user
    }

    fun logout() {
        currentUser = null
    }

    fun addEducation(entry: EducationEntry) = updateUser { user ->
        user.copy(educationRaw = (user.educationRaw.decodeEducation() + entry).encodeEducation())
    }

    fun addSkill(skill: String) = updateUser { user ->
        user.copy(skillsRaw = (user.skillsRaw.decodeSkills() + skill).encodeSkills())
    }

    fun addExperience(entry: ExperienceEntry) = updateUser { user ->
        user.copy(experienceRaw = (user.experienceRaw.decodeExperience() + entry).encodeExperience())
    }

    fun updateBusinessProfile(description: String, location: String, website: String) = updateUser { user ->
        user.copy(
            companyDescription = description,
            companyLocation = location,
            companyWebsite = website
        )
    }

    private fun updateUser(transform: (User) -> User) {
        val updated = currentUser?.let(transform) ?: return
        currentUser = updated
        viewModelScope.launch { repository.updateProfile(updated) }
    }
}

class ProfileViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ProfileViewModel(repository) as T
    }
}