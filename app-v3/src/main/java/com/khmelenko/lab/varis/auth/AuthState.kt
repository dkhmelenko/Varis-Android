package com.khmelenko.lab.varis.auth

sealed class AuthState {

    object Loading : AuthState()
    object Success : AuthState()
    data class AuthError(val message: String?) : AuthState()
    object ShowTwoFactorAuth : AuthState()
}
