package com.johngardev.personalvault.database

import com.johngardev.personalvault.security.VaultPort
import com.johngardev.personalvault.ui.SecretItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SecretRepository(
  private val secretDao: SecretDao,
  private val securityEngine: VaultPort
) {

  //LEER: Obtenemos las entidades de la BD y las transformamos para la Interfaz (UI)
  fun getAllSecretsForDashboard(): Flow<List<SecretItem>> {
    return secretDao.getAllSecrets().map { entities ->
      entities.map { entity ->
        // Transformamos el SecretEntity (BD) a SecretItem (UI)
        SecretItem(
          id = entity.id,
          title = entity.title,
          encryptedData = entity.encryptedData,
          date = entity.createdDate
        )
      }
    }
  }

  //ESCRIBIR(GUARDAR): Encriptamos el contenido y lo guardamos en la BD
  suspend fun saveNewSecret(
    title: String,
    plainContent: String,
    vault: VaultPort,
    masterKey: String
  ) {
    // Usamos tu motor de seguridad para cifrar
    val encryptedContent = securityEngine.encryptData(plainContent, masterKey)

    // Creamos la entidad para la base de datos
    val newEntity = SecretEntity(
      title = title,
      encryptedData = encryptedContent,
      // En una app real usaríamos la fecha del sistema, por ahora la simulamos:
      createdDate = "Hoy"
    )

    // Le decimos a Room que lo guarde
    secretDao.insertSecret(newEntity)
  }

  //BORRAR
  suspend fun deleteSecret(secret: SecretItem) {
    // Reconstruimos la entidad solo con lo necesario para que Room sepa cuál borrar por su ID
    val entityToDelete = SecretEntity(
      id = secret.id,
      title = secret.title,
      encryptedData = secret.encryptedData,
      createdDate = secret.date
    )
    secretDao.deleteSecret(entityToDelete)
  }

  //ACTUALIZAR
  suspend fun updateSecret(
    secret: SecretItem,
    newTitle: String,
    newContent: String,
    vault: VaultPort,
    masterKey: String
  ) {
    val encryptedContent = vault.encryptData(newContent, masterKey)

    val entityToUpdate = SecretEntity(
      id = secret.id,
      title = newTitle,
      encryptedData = encryptedContent,
      createdDate = secret.date
    )

    secretDao.updateSecret(entityToUpdate)
  }
}