// Added by me (Abhinav Tripathi)
package com.example.sutraapp

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


// *** UI Skeleton ***
@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "SUTRA",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                error = validateLogin(email, password, navController, context)
            }
        ) {
            Text("Login")
        }

        if (error.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = error, color = Color.Red)
        }
    }
}

// INPUT VALIDATION + AUTH LOGIC
// Login logic complete without backend
fun validateLogin(
    email: String,
    password: String,
    navController: NavController,
    context: Context
): String {

    if (email.isBlank() || password.isBlank()) {
        return "All fields are required"
    }

    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        return "Invalid email format"
    }

    // Dummy authentication
    if (email == "test@sutra.com" && password == "123456") {
        SessionManager(context).setLoggedIn(true)

        navController.navigate(AppRoutes.HOME) {
            popUpTo(AppRoutes.LOGIN) { inclusive = true }
        }
        return ""
    }

    return "Invalid credentials"
}

