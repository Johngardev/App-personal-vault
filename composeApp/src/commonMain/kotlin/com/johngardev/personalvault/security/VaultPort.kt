package com.johngardev.personalvault.security

interface VaultPort {
    fun encryptData(data: String, secretKey: String): String
    fun decryptData(encryptedData: String, secretKey: String): String
}