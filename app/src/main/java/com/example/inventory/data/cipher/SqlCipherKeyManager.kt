package com.example.inventory.data.cipher

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import net.sqlcipher.database.SupportFactory
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SqlCipherKeyManager(
    private val sharedPreferences: SharedPreferences
) {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val alias = "sqlcipher_keystore_key"

    init {
        if (!keyStore.containsAlias(alias)) {
            generateKeystoreKey()
        }
        if (!sharedPreferences.contains("encrypted_key")) {
            generateAndStoreSqlCipherKey()
        }
    }

    private fun generateKeystoreKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )

        val spec = KeyGenParameterSpec.Builder(
            alias,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    private fun generateAndStoreSqlCipherKey() {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        val secretKey = getSecretKey()
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        val sqlCipherKey = ByteArray(32)
        SecureRandom().nextBytes(sqlCipherKey)

        val encryptedKey = cipher.doFinal(sqlCipherKey)
        val iv = cipher.iv

        sharedPreferences.edit()
            .putString("encrypted_key", Base64.encodeToString(encryptedKey, Base64.NO_WRAP))
            .putString("iv", Base64.encodeToString(iv, Base64.NO_WRAP))
            .apply()

        sqlCipherKey.fill(0)
    }

    private fun getSecretKey(): SecretKey =
        (keyStore.getEntry(alias, null) as KeyStore.SecretKeyEntry).secretKey

    fun getSupportFactory(): SupportFactory {
        val encryptedKey = Base64.decode(
            sharedPreferences.getString("encrypted_key", null),
            Base64.NO_WRAP
        )
        val iv = Base64.decode(
            sharedPreferences.getString("iv", null),
            Base64.NO_WRAP
        )

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.DECRYPT_MODE,
            getSecretKey(),
            GCMParameterSpec(128, iv)
        )

        val decryptedKey = cipher.doFinal(encryptedKey)
        return SupportFactory(decryptedKey)
    }
}
