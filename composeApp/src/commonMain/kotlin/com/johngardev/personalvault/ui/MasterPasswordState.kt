package com.johngardev.personalvault.ui

data class MasterPasswordState(
  val passwordInput: String = "",
  val isUnlocking: Boolean = false,
  val errorMessage: String? = null
)