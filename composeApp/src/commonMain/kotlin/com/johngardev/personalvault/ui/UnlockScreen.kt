package com.johngardev.personalvault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.johngardev.personalvault.utilities.rememberBiometricAuthenticator

//Definimos colores
val VaultDarkBackground = Color(0xFF0B0E14)
val VaultCardBackground = Color(0xFF161B22)
val VaultAccentBlue = Color(0xFF005AC1)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnlockScreen(viewModel: MasterPasswordViewModel, onSuccess: (String) -> Unit) {
  val uiState by viewModel.uiState.collectAsState()
  val isFirstTime = viewModel.isFirstTime

  // 1. Estado para el "Ojito" de la contraseña
  var passwordVisible by remember { mutableStateOf(false) }

  // 2. Obtenemos el puente biométrico multiplataforma
  val biometricAuthenticator = rememberBiometricAuthenticator()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultDarkBackground)
      .padding(24.dp),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {
    // Ícono de Bóveda
    Box(
      modifier = Modifier
        .size(100.dp)
        .background(VaultCardBackground, CircleShape),
      contentAlignment = Alignment.Center
    ) {
      Icon(
        imageVector = Icons.Default.Lock,
        contentDescription = "Lock",
        tint = Color.White,
        modifier = Modifier.size(40.dp)
      )
    }

    Spacer(modifier = Modifier.height(32.dp))

    // Título de Bienvenida Dinámico
    Text(
      text = if (isFirstTime) "Configura tu\nBóveda personal" else "Bienvenido a tu\nBóveda personal soberana",
      color = Color.White,
      fontSize = 32.sp,
      fontWeight = FontWeight.Bold,
      lineHeight = 40.sp,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(48.dp))

    // Campo de Contraseña con el "Ojito"
    OutlinedTextField(
      value = uiState.passwordInput,
      onValueChange = { viewModel.onPasswordChanged(it) },
      label = { Text(if (isFirstTime) "Crea tu PIN Maestro" else "PIN Maestro", color = Color.Gray) },
      modifier = Modifier.fillMaxWidth(),
      singleLine = true,

      // Alternamos entre ver el texto o los puntitos
      visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),

      // Agregamos el ícono del ojo al final
      trailingIcon = {
        val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
        val description = if (passwordVisible) "Ocultar PIN" else "Mostrar PIN"

        IconButton(onClick = { passwordVisible = !passwordVisible }) {
          Icon(imageVector = image, contentDescription = description, tint = Color.Gray)
        }
      },

      colors = TextFieldDefaults.outlinedTextFieldColors(
        focusedBorderColor = VaultAccentBlue,
        unfocusedBorderColor = VaultCardBackground,
        containerColor = VaultCardBackground,
        cursorColor = VaultAccentBlue,
        focusedLabelColor = VaultAccentBlue,
        textColor = Color.White
      ),
      shape = RoundedCornerShape(12.dp),
      isError = uiState.errorMessage != null
    )

    // Mensaje de error si la contraseña falla
    uiState.errorMessage?.let { error ->
      Text(
        text = error,
        color = Color.Red,
        fontSize = 14.sp,
        modifier = Modifier.padding(top = 8.dp)
      )
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Botón de Desbloquear / Configurar
    Button(
      onClick = { viewModel.handleAction(onSuccess) },
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
      colors = ButtonDefaults.buttonColors(containerColor = VaultAccentBlue),
      shape = RoundedCornerShape(28.dp),
      enabled = !uiState.isUnlocking
    ) {
      if (uiState.isUnlocking) {
        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
      } else {
        Text(
          text = if (isFirstTime) "Configurar Bóveda" else "Desbloquear",
          fontSize = 18.sp,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    Spacer(modifier = Modifier.height(40.dp))

    // 3. Opción de Biometría implementada
    if (!isFirstTime) {
      IconButton(
        onClick = {
          biometricAuthenticator.authenticate(
            reason = "Desbloquea tu Bóveda",
            onSuccess = {
              // Por ahora pasamos un texto indicando que fue biométrico.
              // Nota: Esto nos abrirá la app, pero la BD fallará al desencriptar.
              onSuccess("BIOMETRIC_UNLOCK")
            },
            onError = { error ->
              println("Error biométrico: $error")
            }
          )
        },
        modifier = Modifier.size(64.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Fingerprint,
          contentDescription = "Fingerprint",
          tint = Color.Gray,
          modifier = Modifier.size(48.dp)
        )
      }
    }
  }
}