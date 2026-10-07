package com.johngardev.personalvault.security

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

class SecurityManager {
  private val settings: Settings = Settings()
  private val KEY_HASH = "master_password_hash"

  // Verifica si es la primera vez que el usuario abre la app
  fun isFirstTime(): Boolean {
    return settings.getString(KEY_HASH, "").isEmpty()
  }

  // Guarda el PIN (En una app real, aquí aplicaríamos un SHA-256)
  // Por ahora, para no complicarte con librerías de criptografía,
  // guardaremos una versión "ofuscada" básica.
  fun saveMasterPin(pin: String) {
    val simpleHash = pin.hashCode().toString() // "Huella" simple
    settings[KEY_HASH] = simpleHash
  }

  // Compara el PIN ingresado con el guardado
  fun verifyPin(pin: String): Boolean {
    val storedHash = settings.getString(KEY_HASH, "")
    return pin.hashCode().toString() == storedHash
  }
}