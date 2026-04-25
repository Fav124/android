package com.example.deisaapplication.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.deisaapplication.ui.theme.*

@Composable
fun LoginScreen(viewModel: AuthViewModel, onLoginSuccess: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onLoginSuccess()
    }

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(AppBackground, Color(0xFF0D1117)))),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Logo
            Box(
                Modifier.size(72.dp).background(Primary.copy(alpha = 0.15f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Filled.MedicalServices, null, tint = Primary, modifier = Modifier.size(36.dp))
            }
            Spacer(Modifier.height(16.dp))
            Text("DEIHealth", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = OnAppBackground)
            Text(
                "Sistem Manajemen Kesehatan Santri",
                fontSize = 13.sp, color = MutedText, textAlign = TextAlign.Center,
            )

            Spacer(Modifier.height(36.dp))

            // Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = AppSurface),
                elevation = CardDefaults.cardElevation(0.dp),
            ) {
                Column(Modifier.padding(24.dp)) {
                    Text("Masuk ke Akun", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = OnAppBackground)
                    Text("Khusus Petugas Terdaftar", fontSize = 12.sp, color = MutedText)
                    Spacer(Modifier.height(20.dp))

                    // Email
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        leadingIcon = { Icon(Icons.Filled.Email, null, tint = MutedText) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        colors = fieldColors(),
                    )
                    Spacer(Modifier.height(12.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Filled.Lock, null, tint = MutedText) },
                        trailingIcon = {
                            IconButton(onClick = { showPass = !showPass }) {
                                Icon(
                                    if (showPass) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    null, tint = MutedText,
                                )
                            }
                        },
                        visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        colors = fieldColors(),
                    )

                    // Error
                    if (uiState.error != null) {
                        Spacer(Modifier.height(8.dp))
                        Text(
                            uiState.error!!, color = AppError, fontSize = 13.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AppError.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(10.dp),
                        )
                    }

                    Spacer(Modifier.height(20.dp))

                    Button(
                        onClick = { viewModel.login(email, password) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = !uiState.isLoading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Primary,
                            contentColor = Color.Black,
                        ),
                    ) {
                        if (uiState.isLoading)
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                        else
                            Text("Masuk", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Text("v1.0.0 — DEIHealth Mobile", fontSize = 11.sp, color = MutedText)
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = Primary,
    unfocusedBorderColor = AppSurfaceVariant,
    focusedLabelColor    = Primary,
    cursorColor          = Primary,
    focusedTextColor     = OnAppBackground,
    unfocusedTextColor   = OnAppBackground,
    unfocusedLabelColor  = MutedText,
)
