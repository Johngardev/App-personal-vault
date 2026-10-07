package com.johngardev.personalvault

import com.johngardev.personalvault.security.VaultPort
import java.lang.Exception
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.util.Base64
import java.util.function.IntFunction
import java.util.stream.Collectors
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


class SecurityEngine : VaultPort {

    override fun encryptData(data: String, secretKey: String): String {
        try {
            // 1. Generar Salt y IV aleatorios
            val salt = ByteArray(16)
            val iv = ByteArray(12)
            val random = SecureRandom()
            random.nextBytes(salt)
            random.nextBytes(iv)

            // 2. Derivar la llave (esto usa tu método PBKDF2 existente)
            val secretKeySpec = deriveKeyFromPassword(secretKey.toCharArray(), salt)

            // 3. Preparar AES-GCM
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val parameterSpec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, parameterSpec)

            // 4. Cifrar la información
            val cipherText = cipher.doFinal(data.toByteArray(Charsets.UTF_8))

            // 5. Empaquetar Salt + IV + Texto Cifrado
            val byteBuffer = ByteBuffer.allocate(salt.size + iv.size + cipherText.size)
            byteBuffer.put(salt)
            byteBuffer.put(iv)
            byteBuffer.put(cipherText)

            // 6. Convertir a Base64 para mostrar en pantalla
            val bytesFinales = byteBuffer.array()
            return Base64.getEncoder().encodeToString(bytesFinales)

        } catch (e: Exception) {
            e.printStackTrace()
            return "Error crítico de cifrado: ${e.message}"
        }
    }

    override fun decryptData(encryptedData: String, secretKey: String): String {
        try {
            val encryptedBytes = Base64.getDecoder().decode(encryptedData)

            if (encryptedBytes.size < 28) {
                return "Error: los datos cifrados estan corruptos o incompletos, OJO CON ESO MANITO!"
            }

            val salt = encryptedBytes.copyOfRange(0, 16)
            val iv = encryptedBytes.copyOfRange(16, 28)

            val cipherText = encryptedBytes.copyOfRange(28, encryptedBytes.size)

            val secretKeySpec = deriveKeyFromPassword(secretKey.toCharArray(), salt)

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val parameterSpec = GCMParameterSpec(128, iv)
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, parameterSpec)

            val decryptedBytes = cipher.doFinal(cipherText)

            return String(decryptedBytes, Charsets.UTF_8)

        } catch (e: Exception) {
            e.printStackTrace()
            return "Error crítico de descifrado: ${e.message}"
        }
    }

    /**
     * Deriva una clave AES de 256 bits a partir de una contraseña y un salt.
     */
    fun deriveKeyFromPassword(
        password: CharArray,
        salt: ByteArray
    ): SecretKeySpec {
        // Combinamos el password con el pepper
        val pepperedPassword = (String(password) + SECRET_PEPPER).toCharArray()

        val spec: javax.crypto.spec.PBEKeySpec =
            javax.crypto.spec.PBEKeySpec(pepperedPassword, salt, 65536, 256)
        val factory: javax.crypto.SecretKeyFactory =
            javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        return SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES")
    }

    /**
   * Genera un Salt aleatorio de 16 bytes (128 bits).
   * */
    fun generateSalt(): ByteArray {
        val random: SecureRandom = SecureRandom()
        val salt = ByteArray(16)
        random.nextBytes(salt)
        return salt
    }


    fun encrypt(
        data: ByteArray,
        key: SecretKeySpec?,
        salt: ByteArray
    ): ByteArray? {
        val cipher: javax.crypto.Cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val spec: javax.crypto.spec.GCMParameterSpec = javax.crypto.spec.GCMParameterSpec(128, iv)
        cipher.init(Cipher.ENCRYPT_MODE, key, spec)

        val cipherText: ByteArray = cipher.doFinal(data)

        return java.nio.ByteBuffer.allocate(salt.size + iv.size + cipherText.size)
            .put(salt)
            .put(iv)
            .put(cipherText)
            .array()
    }

    fun decrypt(fullPackage: ByteArray, password: CharArray): ByteArray {
        val bb: java.nio.ByteBuffer = java.nio.ByteBuffer.wrap(fullPackage)

        // 1. Extraer el Salt
        val salt = ByteArray(16)
        bb.get(salt)

        // 2. Re-derivar la llave usando la password del usuario y el salt extraído
        val key: SecretKeySpec = deriveKeyFromPassword(password, salt)

        // 3. Extraer el IV
        val iv = ByteArray(12)
        bb.get(iv)

        // 4. El resto es el contenido cifrado
        val cipherText = ByteArray(bb.remaining())
        bb.get(cipherText)

        val cipher: javax.crypto.Cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        val spec: javax.crypto.spec.GCMParameterSpec = javax.crypto.spec.GCMParameterSpec(128, iv)
        cipher.init(Cipher.DECRYPT_MODE, key, spec)

        return cipher.doFinal(cipherText)
    }

    /**
     * Genera un par de llaves de identidad asimétrica.
     * Estas llaves permiten al usuario firmar documentos y autenticarse.
     */
    fun generateIdentityKeyPair(): KeyPair? {
        // Ed25519 es parte de la Java Standard Edition desde Java 15+
        val kpg: KeyPairGenerator =
            KeyPairGenerator.getInstance("Ed25519")
        return kpg.generateKeyPair()
    }

    /**
     * Firma un mensaje usando la llave privada Ed25519.
     */
    fun signMessage(message: String, encodedPrivateKey: String?): String? {
        val privateKeyBytes: ByteArray = Base64.getDecoder().decode(encodedPrivateKey)
        val spec: java.security.spec.PKCS8EncodedKeySpec =
            java.security.spec.PKCS8EncodedKeySpec(privateKeyBytes)
        val kf: java.security.KeyFactory = java.security.KeyFactory.getInstance("Ed25519")

        val sig: java.security.Signature = java.security.Signature.getInstance("Ed25519")
        sig.initSign(kf.generatePrivate(spec))
        sig.update(message.toByteArray())

        return Base64.getEncoder().encodeToString(sig.sign())
    }

    /**
     * Verifica si una firma es válida para un mensaje y una llave pública dada.
     */
    fun verifySignature(message: String, signature: String?, encodedPublicKey: String?): Boolean {
        val publicKeyBytes: ByteArray = Base64.getDecoder().decode(encodedPublicKey)
        val spec: java.security.spec.X509EncodedKeySpec =
            java.security.spec.X509EncodedKeySpec(publicKeyBytes)
        val kf: java.security.KeyFactory = java.security.KeyFactory.getInstance("Ed25519")

        val sig: java.security.Signature = java.security.Signature.getInstance("Ed25519")
        sig.initVerify(kf.generatePublic(spec))
        sig.update(message.toByteArray())

        val sigBytes: ByteArray = Base64.getDecoder().decode(signature)
        return sig.verify(sigBytes)
    }

    fun extractSalt(encryptedPackage: ByteArray): ByteArray {
        val salt = ByteArray(16)
        System.arraycopy(encryptedPackage, 0, salt, 0, 16)
        return salt
    }

    fun generateMnemonic(): String {
        // Lista simplificada de 32 palabras para la prueba (BIP-39 real tiene 2048)
        // En producción usaríamos la lista completa, pero para la Beta esto es perfecto.
        val wordList = arrayOf<String?>(
            "abandon", "ability", "able", "about", "above", "absent", "absorb", "abstract",
            "absurd", "abuse", "access", "accident", "account", "accuse", "achieve", "acid",
            "acoustic", "acquire", "across", "act", "action", "actor", "actress", "actual",
            "adapt", "add", "addict", "address", "adjust", "admit", "adult", "advance"
        )

        val random: SecureRandom = SecureRandom()
        return random.ints(12, 0, wordList.size)
            .mapToObj<String?>(IntFunction { i: Int -> wordList[i] })
            .collect(Collectors.joining(" "))
    }

    /**
     * Firma un mensaje usando la llave privada Ed25519 del usuario
     **/
    fun sign(message: String, privateKey: PrivateKey?): String? {
        try {
            val s: java.security.Signature = java.security.Signature.getInstance("Ed25519")
            s.initSign(privateKey)
            s.update(message.toByteArray(StandardCharsets.UTF_8))
            val signature: ByteArray? = s.sign()
            return Base64.getEncoder().encodeToString(signature)
        } catch (e: Exception) {
            throw java.lang.RuntimeException("Error al generar sello digital", e)
        }
    }

    /**
     * Verifica si una firma es válida para un mensaje y una llave pública dados.
     */
    fun verify(
        message: String,
        signatureBase64: String?,
        publicKey: PublicKey?
    ): Boolean {
        try {
            val s: java.security.Signature = java.security.Signature.getInstance("Ed25519")
            s.initVerify(publicKey)
            s.update(message.toByteArray(StandardCharsets.UTF_8))
            val signature: ByteArray = Base64.getDecoder().decode(signatureBase64)
            return s.verify(signature)
        } catch (e: Exception) {
            // Si hay un error en el formato de la firma, asumimos que no es válida
            return false
        }
    }

    fun decodePrivateKey(base64Key: String?): PrivateKey? {
        val keyBytes: ByteArray = Base64.getDecoder().decode(base64Key)
        return java.security.KeyFactory.getInstance("Ed25519")
            .generatePrivate(java.security.spec.PKCS8EncodedKeySpec(keyBytes))
    }

    fun decodePublicKey(base64Key: String?): PublicKey? {
        val keyBytes: ByteArray = Base64.getDecoder().decode(base64Key)
        return java.security.KeyFactory.getInstance("Ed25519")
            .generatePublic(java.security.spec.X509EncodedKeySpec(keyBytes))
    }

    companion object {
        private const val ITERATIONS = 600000
        private const val KEY_LENGTH = 256
        private const val ALGORITHM = "PBKDF2WithHmacSHA256"
        private const val SECRET_PEPPER = "S0v3r31gn_P3pp3r_2026!#"
    }
}