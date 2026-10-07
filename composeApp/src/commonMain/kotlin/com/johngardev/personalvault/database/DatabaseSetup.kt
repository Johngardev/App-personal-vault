package com.johngardev.personalvault.database

import androidx.room.RoomDatabase

fun getRoomDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase {
  return builder
    .fallbackToDestructiveMigration(true)
    .build()
}