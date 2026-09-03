package com.example.skillbridge.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

import com.example.skillbridge.data.AccountType
import com.example.skillbridge.data.AppDatabase
import com.example.skillbridge.data.AuthRepository
import com.example.skillbridge.data.JobRepository

import com.example.skillbridge.ui.screens.JobProviderHomeScreen
import com.example.skillbridge.ui.screens.JobSeekerHomeScreen
import com.example.skillbridge.ui.screens.LoginScreen
import com.example.skillbridge.ui.screens.PostJobScreen
import com.example.skillbridge.ui.screens.RegisterScreen
import com.example.skillbridge.ui.screens.ResumeCreatorScreen
import com.example.skillbridge.ui.screens.WelcomeScreen

import com.example.skillbridge.viewmodel.AuthViewModel
import com.example.skillbridge.viewmodel.JobProviderViewModel
import com.example.skillbridge.viewmodel.JobProviderViewModelFactory
import com.example.skillbridge.viewmodel.JobSeekerViewModel
import com.example.skillbridge.viewmodel.JobSeekerViewModelFactory
import com.example.skillbridge.viewmodel.ProfileViewModel
import com.example.skillbridge.viewmodel.ProfileViewModelFactory


object Routes {

    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val HOME = "home"
    const val RESUME_CREATOR = "resume_creator"
    const val POST_JOB = "post_job"
}


@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    repository: AuthRepository
) {

    val navController = rememberNavController()

    val profileViewModel: ProfileViewModel =
        viewModel(
            factory = ProfileViewModelFactory(repository)
        )

    val context = LocalContext.current

    val database = AppDatabase.getDatabase(context)

    val jobRepository = JobRepository(database.jobDao())

    val jobProviderViewModel: JobProviderViewModel =
        viewModel(
            factory = JobProviderViewModelFactory(jobRepository)
        )

    val jobSeekerViewModel: JobSeekerViewModel =
        viewModel(
            factory = JobSeekerViewModelFactory(jobRepository)
        )

    val handleLogout = {
        authViewModel.logout()
        profileViewModel.logout()
        navController.navigate(Routes.WELCOME) {
            popUpTo(0) { inclusive = true }
        }
    }


    NavHost(
        navController = navController,
        startDestination = Routes.WELCOME
    ) {



        composable(Routes.WELCOME) {

            WelcomeScreen(

                onLoginClick = {
                    navController.navigate(Routes.LOGIN)
                },

                onRegisterClick = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }


        composable(Routes.LOGIN) {

            LoginScreen(

                viewModel = authViewModel,

                onLoginSuccess = { user ->

                    profileViewModel.setUser(user)

                    navController.navigate(Routes.HOME) {

                        popUpTo(Routes.WELCOME) {
                            inclusive = true
                        }
                    }
                },

                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }


        composable(Routes.REGISTER) {

            RegisterScreen(

                viewModel = authViewModel,

                onRegisterSuccess = { user ->

                    profileViewModel.setUser(user)

                    navController.navigate(Routes.HOME) {

                        popUpTo(Routes.WELCOME) {
                            inclusive = true
                        }
                    }
                },

                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }


        composable(Routes.HOME) {

            profileViewModel.currentUser?.let { user ->

                if (user.accountType == AccountType.JOB_SEEKER) {

                    JobSeekerHomeScreen(

                        user = user,

                        jobs = jobSeekerViewModel
                            .jobs
                            .collectAsState()
                            .value,

                        onResumeCreatorClick = {
                            navController.navigate(
                                Routes.RESUME_CREATOR
                            )
                        },

                        onAddEducation =
                            profileViewModel::addEducation,

                        onAddSkill =
                            profileViewModel::addSkill,

                        onAddExperience =
                            profileViewModel::addExperience,

                        onLogout = handleLogout
                    )

                } else {

                    jobProviderViewModel.loadJobs(user.id)

                    JobProviderHomeScreen(

                        user = user,

                        jobs = jobProviderViewModel
                            .jobs
                            .collectAsState()
                            .value,

                        onPostJobClick = {

                            navController.navigate(
                                Routes.POST_JOB
                            )
                        },

                        onDeleteJob = { job ->

                            jobProviderViewModel.deleteJob(
                                job
                            )
                        },

                        onLogout = handleLogout,

                        onUpdateProfile = { desc, loc, web ->
                            profileViewModel.updateBusinessProfile(desc, loc, web)
                        }
                    )
                }
            }
        }


        composable(Routes.POST_JOB) {

            profileViewModel.currentUser?.let { user ->

                PostJobScreen(

                    onBackClick = {
                        navController.navigateUp()
                    },

                    onPostJob = {

                            companyName,
                            title,
                            description,
                            location,
                            salary,
                            jobType,
                            skills,
                            education ->

                        jobProviderViewModel.addJob(

                            providerId = user.id,

                            companyName = companyName,

                            title = title,

                            description = description,

                            location = location,

                            salary = salary,

                            jobType = jobType,

                            requiredSkills = skills,

                            requiredEducation = education
                        )

                        navController.navigateUp()
                    }
                )
            }
        }


        composable(Routes.RESUME_CREATOR) {

            profileViewModel.currentUser?.let { user ->

                ResumeCreatorScreen(

                    user = user,

                    onBackClick = {
                        navController.navigateUp()
                    }
                )
            }
        }
    }
}