package com.johngardev.personalvault.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  secrets: List<SecretItem>, // <-- Recibimos la lista real desde App.kt
  onCreateSecret: () -> Unit,
  onSecretClick: (SecretItem) -> Unit
) {
  Scaffold(
    containerColor = VaultDarkBackground,
    topBar = {
      CenterAlignedTopAppBar(
        title = { Text("Personal Vault", color = Color.White, fontWeight = FontWeight.Bold) },
        navigationIcon = {
          Icon(Icons.Default.Security, "Logo", tint = VaultAccentBlue, modifier = Modifier.padding(start = 16.dp))
        },
        actions = {
          IconButton(onClick = {}) {
            Icon(Icons.Default.Person, "Perfil", tint = Color.White)
          }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = VaultDarkBackground)
      )
    },
    floatingActionButton = {
      FloatingActionButton(
        onClick = { onCreateSecret() },
        containerColor = VaultAccentBlue,
        contentColor = Color.White,
        shape = CircleShape
      ) {
        Icon(Icons.Default.Add, "Agregar")
      }
    }
  ) { padding ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(padding)
        .padding(horizontal = 16.dp)
    ) {
      Text(
        text = "Mis Secretos",
        color = Color.White,
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = "Protegido y cifrado en tu bóveda personal.",
        color = Color.Gray,
        fontSize = 14.sp
      )

      Spacer(modifier = Modifier.height(24.dp))

      // Lógica para mostrar la lista o un mensaje de vacío
      if (secrets.isEmpty()) {
        Text(
          text = "Tu bóveda está vacía. ¡Presiona el botón + para añadir tu primer secreto!",
          color = Color.Gray,
          modifier = Modifier.padding(top = 16.dp)
        )
      } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          // Iteramos sobre la lista REAL que viene de la memoria
          items(secrets) { secreto ->
            SecretCard(secreto) { onSecretClick(secreto) }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecretCard(item: SecretItem, onClick: () -> Unit) {
  Card(
    onClick = onClick,
    modifier = Modifier.fillMaxWidth(),
    colors = CardDefaults.cardColors(containerColor = VaultCardBackground),
    shape = RoundedCornerShape(16.dp)
  ) {
    Row(
      modifier = Modifier.padding(16.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .background(Color(0xFF1E232B), CircleShape),
        contentAlignment = Alignment.Center
      ) {
        // Usamos el candado (Lock) por defecto como ícono para todos los secretos
        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
      }
      Spacer(modifier = Modifier.width(16.dp))
      Column {
        Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text("Última modificación: ${item.date}", color = Color.Gray, fontSize = 12.sp)
      }
    }
  }
}