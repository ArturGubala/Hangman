package com.hangman.app.core.domain.util

sealed interface DataError {
    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN
    }
}
