package com.johngardev.personalvault.auth

import androidx.biometric.BiometricPrompt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.johngardev.personalvault.utilities.BiometricAuthenticator

class BiometricAuthenticator(
  private val activity: FragmentActivity
) : BiometricAuthenticator {

  override fun authenticate(reason: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
    val executor = ContextCompat.getMainExecutor(activity)

    val biometricPrompt = BiometricPrompt(
      activity,
      executor,
      object : BiometricPrompt.AuthenticationCallback() {
        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
          super.onAuthenticationError(errorCode, errString)
          onError(errString.toString())
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
          super.onAuthenticationSucceeded(result)
          onSuccess()
        }

        override fun onAuthenticationFailed() {
          super.onAuthenticationFailed()
          onError("Huella no reconocida. Intenta de nuevo.")
        }
      }
    )

    // Textos que verá el usuario
    val promptInfo = BiometricPrompt.PromptInfo.Builder()
      .setTitle("Bóveda Segura")
      .setSubtitle(reason)
      .setNegativeButtonText("Usar PIN Maestro") // Botón por si falla la huella
      .build()

    // Lanzamos la petición
    biometricPrompt.authenticate(promptInfo)
  }
}

// Conectamos el "actual" con el "expect" que hicimos en commonMain
@Suppress("NO_ACTUAL_FOR_EXPECT")
@Composable
actual fun rememberBiometricAuthenticator(): BiometricAuthenticator {
  // Obtenemos el contexto actual de Compose
  val context = LocalContext.current

  return remember(context) {
    // Convertimos el contexto a FragmentActivity (lo cual es seguro
    // porque lo cambiamos en el paso anterior en el MainActivity)
    AndroidBiometricAuthenticator(context as FragmentActivity)
  }
}