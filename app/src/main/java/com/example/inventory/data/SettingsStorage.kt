package com.example.inventory.data

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import androidx.core.content.edit

class SettingsStorage(context: Context) {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    // EncryptedSharedPreferences
    private val prefs = EncryptedSharedPreferences.create(
        context,
        "secure_settings",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun setHideSensitiveData(value: Boolean) {
        prefs.edit { putBoolean("hide_sensitive_data", value) }
    }

    fun isHideSensitiveDataEnabled(): Boolean {
        return prefs.getBoolean("hide_sensitive_data", false)
    }

    fun setSendingDataDisabled(value: Boolean){
        prefs.edit { putBoolean("key_disable_sending", value) }
    }

    fun isSendingDataDisabled(): Boolean{
        return prefs.getBoolean("key_disable_sending", false)
    }



    fun setUseDefaultQuantity(value: Boolean){
        prefs.edit { putBoolean("use_default_quantity", value)}
    }

    fun isUseDefaultQuantity(): Boolean {
        return prefs.getBoolean("use_default_quantity", false)
    }

    fun setDefaultQuantity(value: Int) {
        prefs.edit { putInt("default_quantity", value) }
    }

    fun getDefaultQuantity(): Int {
        return prefs.getInt("default_quantity", 5)
    }

}
