package com.johngardev.personalvault.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import com.johngardev.personalvault.database.SecretRepository
import com.johngardev.personalvault.security.VaultPort
import kotlinx.coroutines.launch
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.runtime.DisposableEffect

sealed class Screen {
  object Unlock : Screen()
  object Dashboard : Screen()
  object CreateSecret : Screen()
  data class SecretDetail(val secret: SecretItem) : Screen()
}

data class SecretItem(
  val id: Long,
  val title: String,
  val encryptedData: String,
  val date: String
)

@Composable
fun App(vault: VaultPort, repository: SecretRepository) {
  MaterialTheme {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Unlock) }

    // Obtenemos la lista de secretos desde la BD
    val secretsList by repository.getAllSecretsForDashboard().collectAsState(initial = emptyList())

    // Necesitamos esto para ejecutar funciones 'suspend' (como guardar en BD)
    val coroutineScope = rememberCoroutineScope()

    var currentMasterKey by remember { mutableStateOf("") }

    var lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
      val observer = LifecycleEventObserver { _, event ->
        // Cuando la app pasa a segundo plano (se minimiza o se apaga la pantalla)
        if (event == Lifecycle.Event.ON_STOP) {
          // Volvemos a la pantalla de bloqueo
          currentScreen = Screen.Unlock
          // Borramos la clave maestra de la memoria por seguridad
          currentMasterKey = ""
        }
      }

      lifecycleOwner.lifecycle.addObserver(observer)

      onDispose {
        lifecycleOwner.lifecycle.removeObserver(observer)
      }
    }

    when (val screen = currentScreen) {
      Screen.Unlock -> {
        val viewModel = MasterPasswordViewModel()
        UnlockScreen(viewModel = viewModel) { pinExitoso ->
          currentMasterKey = pinExitoso
          currentScreen = Screen.Dashboard
        }
      }

      Screen.Dashboard -> {
        // Le pasamos la lista real de secretos al Dashboard
        DashboardScreen(
          secrets = secretsList,
          onCreateSecret = { currentScreen = Screen.CreateSecret },
          onSecretClick = { selectedSecret ->
            currentScreen = Screen.SecretDetail(selectedSecret)
          }
        )
      }

      Screen.CreateSecret -> {
        CreateSecretScreen(
          onSave = { title, secretContent ->
            coroutineScope.launch {
              repository.saveNewSecret(title, secretContent, vault, currentMasterKey)
              currentScreen = Screen.Dashboard
            }
          },
          onBack = { currentScreen = Screen.Dashboard }
        )
      }

      is Screen.SecretDetail -> {
        SecretDetailScreen(
          secret = screen.secret,
          vault = vault,
          masterKey = currentMasterKey,
          onBack = { currentScreen = Screen.Dashboard },
          onDelete = {
            coroutineScope.launch {
              repository.deleteSecret(screen.secret)
              currentScreen = Screen.Dashboard
            }
          },
          onUpdate = { newTitle, newContent ->
            coroutineScope.launch {
              repository.updateSecret(
                secret = screen.secret,
                newTitle = newTitle,
                newContent = newContent,
                vault = vault,
                masterKey = currentMasterKey
              )
              currentScreen = Screen.Dashboard
            }
          }
        )
      }
    }
  }
}
