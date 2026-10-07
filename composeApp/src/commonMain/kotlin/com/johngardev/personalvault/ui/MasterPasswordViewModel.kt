package com.johngardev.personalvault.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.johngardev.personalvault.security.SecurityManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class MasterPasswordViewModel(private val securityManager: SecurityManager = SecurityManager()) : ViewModel() {

  private val _uiState = MutableStateFlow(MasterPasswordState())
  val uiState = _uiState.asStateFlow()

  // Variable para saber si estamos configurando o desbloqueando
  val isFirstTime = securityManager.isFirstTime()

  fun onPasswordChanged(newPassword: String) {
    _uiState.value = _uiState.value.copy(passwordInput = newPassword, errorMessage = null)
  }

  fun handleAction(onSuccess: (String) -> Unit) {
    val pin = _uiState.value.passwordInput
    if (pin.length < 4) {
      _uiState.value = _uiState.value.copy(errorMessage = "Mínimo 4 dígitos")
      return
    }

    _uiState.value = _uiState.value.copy(isUnlocking = true)

    viewModelScope.launch {
      delay(500)
      if (isFirstTime) {
        // MODO REGISTRO: Guardamos el PIN por primera vez
        securityManager.saveMasterPin(pin)
        onSuccess(pin)
      } else {
        // MODO LOGIN: Verificamos si coincide
        if (securityManager.verifyPin(pin)) {
          onSuccess(pin)
        } else {
          _uiState.value = _uiState.value.copy(
            isUnlocking = false,
            errorMessage = "PIN Incorrecto",
            passwordInput = ""
          )
        }
      }
    }
  }
}
