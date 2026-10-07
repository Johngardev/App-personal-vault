package com.johngardev.personalvault

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.room.Room
import com.johngardev.personalvault.database.AppDatabase
import com.johngardev.personalvault.database.SecretRepository
import com.johngardev.personalvault.database.getRoomDatabase
import com.johngardev.personalvault.ui.App
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      // 1. Creamos la ruta física del archivo .db usando el Context de Android
      val dbFile = applicationContext.getDatabasePath("vault.db")

      // 2. Creamos el Builder nativo de Room
      val roomBuilder = Room.databaseBuilder<AppDatabase>(
        context = applicationContext,
        name = dbFile.absolutePath
      )

      // 3. Construimos la base de datos usando nuestra función compartida
      val database = getRoomDatabase(roomBuilder)

      // 4. Preparamos el Repositorio (Conectamos DAO + Encriptación)
      val vault = SecurityEngine() // Tu clase real de encriptación
      val secretRepository = SecretRepository(
        secretDao = database.secretDao(),
        securityEngine = vault
      )

      window.setFlags(
        WindowManager.LayoutParams.FLAG_SECURE,
        WindowManager.LayoutParams.FLAG_SECURE
      )

      setContent {
        App(vault = vault, repository = secretRepository)
      }
    }
}