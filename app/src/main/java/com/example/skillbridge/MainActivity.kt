package com.example.skillbridge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.skillbridge.data.AppDatabase
import com.example.skillbridge.data.AuthRepository
import com.example.skillbridge.ui.navigation.NavGraph
import com.example.skillbridge.ui.theme.SkillBridgeTheme
import com.example.skillbridge.viewmodel.AuthViewModel
import com.example.skillbridge.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkillBridgeTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SkillBridgeApp()
                }
            }
        }
    }
}

@Composable
fun SkillBridgeApp() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember { AuthRepository(database.userDao()) }
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(repository))

    NavGraph(authViewModel = authViewModel)
}