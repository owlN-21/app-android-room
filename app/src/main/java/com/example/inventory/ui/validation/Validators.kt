package com.example.inventory.ui.validation

import android.util.Patterns
import org.intellij.lang.annotations.Pattern

object Validators {
    fun nameErrorOrNull(value: String): String? = when{
        value.isBlank() -> "The name is required"
        else -> null
    }

    fun emailErrorOrNull(value: String): String?{
        val v = value.replace(" ", "")
        return when {
            v.isBlank() -> "E-mailis required"
            !Patterns.EMAIL_ADDRESS.matcher(v.trim()).matches() -> "Incorrect e-mail"
            else -> null
        }
    }

    fun phoneErrorOrNull(value: String): String? {
        val v = value.replace(" ", "")
        return when {
            v.isBlank() -> "Phone number is required"
            !Regex("^\\+?\\d{10,15}$").matches(v) -> "Формат: +71234567890"
            else -> null
        }
    }
}