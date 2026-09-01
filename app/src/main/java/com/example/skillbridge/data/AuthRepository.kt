package com.example.skillbridge.data

class AuthRepository(private val userDao: UserDao) {

    suspend fun register(user: User): Result<Long> {
        val existing = userDao.getUserByEmail(user.email)
        return if (existing != null) {
            Result.failure(Exception("An account with this email already exists"))
        } else {
            Result.success(userDao.insertUser(user))
        }
    }

    suspend fun login(email: String, password: String): User? {
        return userDao.login(email, password)
    }

    suspend fun updateProfile(user: User) {
        userDao.updateUser(user)
    }
}