package com.johngardev.personalvault.utilities

import androidx.compose.runtime.Composable

interface BiometricAuthenticator {
  fun authenticate(
    reason: String,
    onSuccess: () -> Unit,
    onError: (error: String) -> Unit
  )
}
@Suppress("NO_ACTUAL_FOR_EXPECT")
@Composable
expect fun rememberBiometricAuthenticator(): BiometricAuthenticator
