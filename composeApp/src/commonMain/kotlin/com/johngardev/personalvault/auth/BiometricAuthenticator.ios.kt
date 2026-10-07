package com.johngardev.personalvault.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.LocalAuthentication.LAContext
import platform.LocalAuthentication.LAPolicyDeviceOwnerAuthenticationWithBiometrics
import platform.Foundation.NSError
import com.johngardev.personalvault.utilities.BiometricAuthenticator

// Esta clase maneja la lógica nativa de iOS (FaceID / TouchID)
// Esta clase maneja la lógica nativa de iOS (FaceID / TouchID)
class IosBiometricAuthenticator : BiometricAuthenticator {

  override fun authenticate(reason: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val context = LAContext()

    // 1. Verificamos si el iPhone tiene FaceID/TouchID configurado
    if (context.canEvaluatePolicy(LAPolicyDeviceOwnerAuthenticationWithBiometrics, null)) {
      // 2. Lanzamos la petición
      context.evaluatePolicy(
        LAPolicyDeviceOwnerAuthenticationWithBiometrics,
        localizedReason = reason
      ) { success: Boolean, nsError: NSError? ->  // <--- AÑADIMOS LOS TIPOS AQUÍ
        if (success) {
          onSuccess()
        } else {
          val errorMessage = nsError?.localizedDescription ?: "Error de autenticación"
          onError(errorMessage)
        }
      }
    } else {
      onError("Biometría no disponible en este dispositivo.")
    }
  }
}

// Conectamos el "actual" para iOS
@Suppress("NO_ACTUAL_FOR_EXPECT")
@Composable
actual fun rememberBiometricAuthenticator(): BiometricAuthenticator {
  return remember { IosBiometricAuthenticator() }
}