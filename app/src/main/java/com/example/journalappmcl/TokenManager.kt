package com.example.journalappmcl

import android.content.Context
import android.net.Uri
import android.util.Log
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.GrantTypeValues
import net.openid.appauth.TokenRequest

class TokenManager(private val context: Context) {

    companion object {
        private const val TAG = "TokenManager"
        private const val CLIENT_ID = "00ef6e5a-2874-4a00-aef3-cc8d1740c2f1"
        private const val TOKEN_ENDPOINT = "https://auth.globus.org/v2/oauth2/token"
        private const val AUTH_ENDPOINT = "https://auth.globus.org/v2/oauth2/authorize"
    }

    private val authService = AuthorizationService(context)
    private val authServiceConfig = AuthorizationServiceConfiguration(
        Uri.parse(AUTH_ENDPOINT),
        Uri.parse(TOKEN_ENDPOINT)
    )

    fun refreshAccessToken(onResult: (Boolean) -> Unit) {
        Log.i(TAG, "Starting token refresh flow")

        val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val refreshToken = prefs.getString("refresh_token", null)

        if (refreshToken == null) {
            Log.e(TAG, "No refresh token found. Cannot refresh access token.")
            onResult(false)
            return
        }

        val tokenRequest = TokenRequest.Builder(
            authServiceConfig,
            CLIENT_ID
        )
            .setGrantType(GrantTypeValues.REFRESH_TOKEN)
            .setRefreshToken(refreshToken)
            .build()

        Log.i(TAG, "Performing token refresh request")
        authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
            if (tokenResponse != null) {
                val newAccessToken = tokenResponse.accessToken
                val newRefreshToken = tokenResponse.refreshToken ?: refreshToken // Keep old one if new not returned

                Log.i(TAG, "New Access Token: $newAccessToken")

                // Save new tokens
                with(prefs.edit()) {
                    putString("access_token", newAccessToken)
                    putString("refresh_token", newRefreshToken)
                    apply()
                }

                Log.i(TAG, "✅ Tokens refreshed and stored.")
                onResult(true)

            } else {
                Log.e(TAG, "Token refresh failed: $exception")
                onResult(false)
            }
        }
    }

    fun dispose() {
        authService.dispose()
    }
}
