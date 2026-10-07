package com.johngardev.personalvault.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.johngardev.personalvault.ui.SecretItem
import kotlinx.coroutines.flow.Flow

@Dao
interface SecretDao {
  //Insert new secret
  @Insert
  suspend fun  insertSecret(secret: SecretEntity)

  //Get all secrets and emit them as a flow
  @Query("SELECT * FROM secrets_table ORDER BY id DESC")
  fun getAllSecrets(): Flow<List<SecretEntity>>

  //Delete one specific secret
  @Delete
  suspend fun deleteSecret(secret: SecretEntity)

  @Update
  suspend fun updateSecret(secret: SecretEntity)
}