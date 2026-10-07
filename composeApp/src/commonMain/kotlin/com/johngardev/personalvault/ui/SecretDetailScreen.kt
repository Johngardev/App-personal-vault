package com.johngardev.personalvault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.johngardev.personalvault.security.VaultPort
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.ui.Alignment

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretDetailScreen(
  secret: SecretItem,
  vault: VaultPort,
  masterKey: String,
  onBack: () -> Unit,
  onDelete: () -> Unit,
  onUpdate: (String, String) -> Unit
) {
  // Estados para controlar la edición
  var isEditing by remember { mutableStateOf(false) }
  var editedTitle by remember { mutableStateOf(secret.title) }
  var editedContent by remember { mutableStateOf("") }

  //Gestor de portapapeles
  val clipboardManager = LocalClipboardManager.current

  // Estado para mostrar el contenido original al cargar
  var decryptedText by remember { mutableStateOf("Desencrypted...") }

  // Desencriptamos al cargar la pantalla
  LaunchedEffect(secret) {
    try {
      val text = vault.decryptData(secret.encryptedData, masterKey)
      decryptedText = text
      editedContent = text // Pre-llenamos el campo de edición
    } catch (e: Exception) {
      decryptedText = "Error al desencriptar. Clave incorrecta."
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(VaultDarkBackground)
      .padding(16.dp)
      .padding(top = 24.dp)
  ) {
    // --- BARRA SUPERIOR CON BOTONES ---
    Row(
      horizontalArrangement = Arrangement.SpaceBetween,
      modifier = Modifier.fillMaxWidth()
    ) {
      IconButton(onClick = onBack) {
        Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
      }

      Row {
        if (isEditing) {
          // Botón para Guardar Cambios
          IconButton(onClick = {
            onUpdate(editedTitle, editedContent)
            isEditing = false
          }) {
            Icon(Icons.Default.Check, "Guardar", tint = Color.Green)
          }
          // Botón para Cancelar Edición
          IconButton(onClick = {
            isEditing = false
            editedTitle = secret.title // Restauramos título
            editedContent = decryptedText // Restauramos contenido
          }) {
            Icon(Icons.Default.Close, "Cancelar", tint = Color.Red)
          }
        } else {
          // Botón para Entrar a Modo Edición
          IconButton(onClick = { isEditing = true }) {
            Icon(Icons.Default.Edit, "Editar", tint = Color.White)
          }
          // Botón de Eliminar
          IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, "Eliminar", tint = Color.Red)
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // --- ZONA DE CONTENIDO (DINÁMICA) ---
    if (isEditing) {
      // MODO EDICIÓN: Campos de texto editables
      OutlinedTextField(
        value = editedTitle,
        onValueChange = { editedTitle = it },
        label = { Text("Título") },
        modifier = Modifier.fillMaxWidth(),
        colors = TextFieldDefaults.outlinedTextFieldColors(
          focusedBorderColor = VaultAccentBlue,
          unfocusedBorderColor = VaultCardBackground,
          containerColor = VaultCardBackground,
          cursorColor = VaultAccentBlue,
          textColor = Color.White
        )
      )
      Spacer(modifier = Modifier.height(16.dp))
      OutlinedTextField(
        value = editedContent,
        onValueChange = { editedContent = it },
        label = { Text("Secreto") },
        modifier = Modifier.fillMaxWidth().weight(1f),
        colors = TextFieldDefaults.outlinedTextFieldColors(
          focusedBorderColor = VaultAccentBlue,
          unfocusedBorderColor = VaultCardBackground,
          containerColor = VaultCardBackground,
          cursorColor = VaultAccentBlue,
          textColor = Color.White
        )
      )
    } else {
      Text(
        text = secret.title,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
      )
      Spacer(modifier = Modifier.height(24.dp))

      // Envolvemos el texto y el botón de copiar en un Row o Card
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(VaultCardBackground, RoundedCornerShape(12.dp))
          .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = decryptedText,
          fontSize = 18.sp,
          color = Color.White,
          modifier = Modifier.weight(1f) // Toma el espacio disponible
        )

        // Botón mágico para copiar
        IconButton(
          onClick = {
            clipboardManager.setText(AnnotatedString(decryptedText))
          }
        ) {
          Icon(
            imageVector = Icons.Default.ContentCopy,
            contentDescription = "Copiar al portapapeles",
            tint = VaultAccentBlue // Le damos tu color azul eléctrico
          )
        }
      }
    }
  }
}