package com.example.sutraapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sutraapp.ui.theme.SutraAppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SutraAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val session = SessionManager(context)

                NavHost(
                    navController = navController,
                    startDestination =
                        if (session.isLoggedIn()) AppRoutes.HOME
                        else AppRoutes.LOGIN
                ) {
                    composable(AppRoutes.LOGIN) {
                        LoginScreen(navController)
                    }
                    composable(AppRoutes.HOME) {
                        HomeScreen()
                    }
                }
            }
        }
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SutraAppTheme {
        Greeting("Android")
    }
}
