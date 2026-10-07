package com.johngardev.personalvault.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSecretScreen(
  onBack: () -> Unit,
  onSave: (String, String) -> Unit
) {
  var title by remember { mutableStateOf("") }
  var secretContent by remember { mutableStateOf("") }

  Scaffold(
    containerColor = VaultDarkBackground,
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("Nuevo Registro", color = Color.White, fontSize = 18.sp) },
        navigationIcon = {
          IconButton(onClick = onBack) {
            Icon(Icons.Default.ArrowBack, "Volver", tint = Color.White)
          }
        },
        actions = {
          TextButton(onClick = {
            onSave(title, secretContent)
          }) {
            Text("GUARDAR", color = VaultAccentBlue, fontWeight = FontWeight.Bold)
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = VaultDarkBackground)
      )
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(24.dp)
    ) {
      // Título del Secreto
      Text("TÍTULO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = title,
        onValueChange = { title = it },
        placeholder = { Text("Ej. Contraseña del Banco", color = Color.DarkGray) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        colors = TextFieldDefaults.outlinedTextFieldColors(
          focusedBorderColor = VaultAccentBlue,
          unfocusedBorderColor = VaultCardBackground,
          containerColor = VaultCardBackground,
          textColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Contenido Secreto
      Text("CONTENIDO SECRETO", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
      Spacer(modifier = Modifier.height(8.dp))
      OutlinedTextField(
        value = secretContent,
        onValueChange = { secretContent = it },
        placeholder = { Text("Escribe tu secreto aquí...", color = Color.DarkGray) },
        modifier = Modifier
          .fillMaxWidth()
          .height(200.dp), // Lo hacemos más alto para texto largo
        colors = TextFieldDefaults.outlinedTextFieldColors(
          focusedBorderColor = VaultAccentBlue,
          unfocusedBorderColor = VaultCardBackground,
          containerColor = VaultCardBackground,
          textColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
      )

      Spacer(modifier = Modifier.height(8.dp))
      Text(
        text = "Este contenido será encriptado localmente antes de ser guardado.",
        color = Color.Gray,
        fontSize = 12.sp
      )

      Spacer(modifier = Modifier.weight(1f)) // Empuja el botón hacia abajo

      // Botón Generar Contraseña (Como en el diseño)
      Button(
        onClick = { /* Generar contraseña aleatoria */ },
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VaultAccentBlue),
        shape = RoundedCornerShape(28.dp)
      ) {
        Text("*** Generar Contraseña Segura", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
      }
    }
  }
}