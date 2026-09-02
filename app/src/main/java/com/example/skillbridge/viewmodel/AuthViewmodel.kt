package com.example.skillbridge.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skillbridge.data.AccountType
import com.example.skillbridge.data.AuthRepository
import com.example.skillbridge.data.User
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Idle)
        private set

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            uiState = AuthUiState.Error("Please fill in both fields")
            return
        }
        uiState = AuthUiState.Loading
        viewModelScope.launch {
            val user = repository.login(email.trim(), password)
            uiState = if (user != null) {
                AuthUiState.Success(user)
            } else {
                AuthUiState.Error("Incorrect email or password")
            }
        }
    }

    fun register(
        fullName: String,
        email: String,
        password: String,
        confirmPassword: String,
        accountType: AccountType,
        extraInfo: String
    ) {
        if (fullName.isBlank() || email.isBlank() || password.isBlank()) {
            uiState = AuthUiState.Error("Please fill in all required fields")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = AuthUiState.Error("Enter a valid email address")
            return
        }
        if (password.length < 6) {
            uiState = AuthUiState.Error("Password must be at least 6 characters")
            return
        }
        if (password != confirmPassword) {
            uiState = AuthUiState.Error("Passwords do not match")
            return
        }

        uiState = AuthUiState.Loading
        viewModelScope.launch {
            val newUser = User(
                fullName = fullName.trim(),
                email = email.trim(),
                password = password,
                accountType = accountType,
                extraInfo = extraInfo.ifBlank { null }
            )
            val result = repository.register(newUser)
            uiState = result.fold(
                onSuccess = { id -> AuthUiState.Success(newUser.copy(id = id.toInt())) },
                onFailure = { e -> AuthUiState.Error(e.message ?: "Registration failed") }
            )
        }
    }

    fun resetState() {
        uiState = AuthUiState.Idle
    }

    fun logout() {
        uiState = AuthUiState.Idle
    }
}

class AuthViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthViewModel(repository) as T
    }
}