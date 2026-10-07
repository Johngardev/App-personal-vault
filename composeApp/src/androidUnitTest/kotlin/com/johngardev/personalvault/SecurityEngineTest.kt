package com.johngardev.personalvault

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class SecurityEngineTest {

  private val securityEngine = SecurityEngine()

  @Test
  fun prueba_cifrado_y_descifrado_exitoso() {
    val textoOriginal = "Mi secreto financiero"
    val password = "PasswordSuperFuerte123"

    // 1. Ciframos el texto
    val textoCifrado = securityEngine.encryptData(textoOriginal, password)

    // Comprobación A: El texto cifrado NO debe ser igual al original
    assertNotEquals(textoOriginal, textoCifrado)

    // 2. Desciframos el texto usando la MISMA contraseña
    val textoRecuperado = securityEngine.decryptData(textoCifrado, password)

    // Comprobación B: El texto recuperado DEBE ser exactamente igual al original
    assertEquals(textoOriginal, textoRecuperado)
  }

  @Test
  fun prueba_intento_de_hackeo_falla() {
    val textoOriginal = "Mi secreto financiero"
    val passwordCorrecto = "PasswordSuperFuerte123"
    val passwordHacker = "123456"

    // El usuario legítimo cifra su bóveda
    val textoCifrado = securityEngine.encryptData(textoOriginal, passwordCorrecto)

    // El atacante intenta descifrar con una contraseña equivocada
    val intentoDeRobo = securityEngine.decryptData(textoCifrado, passwordHacker)

    // Comprobación C: El hacker NO debe obtener el texto original
    assertNotEquals(textoOriginal, intentoDeRobo)

    // Comprobación D: Debería saltar nuestro mensaje de error programado
    assertEquals("Error crítico de descifrado: Tag mismatch", intentoDeRobo)
  }
}