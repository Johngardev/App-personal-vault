package com.johngardev.personalvault.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secrets_table")
data class SecretEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val title: String,
  val encryptedData: String,
  val createdDate: String

)