package com.example.journalappmcl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.ResponseTypeValues

importsim net.openid.appauth.*

private const val CLIENT_ID = "00ef6e5a-2874-4a00-aef3-cc8d1740c2f1"
private const val COLLECTION_ID = "cdd81df8-db63-4ea5-b017-031ba03f33ae"
private const val REDIRECT_URI = "https://apple.node.rip/"
private const val AUTH_ENDPOINT = "https://auth.globus.org/v2/oauth2/authorize"
private const val TOKEN_ENDPOINT = "https://auth.globus.org/v2/oauth2/token"
private const val REQUEST_CODE_AUTH = 1001

class LoginActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService
    private lateinit var authRequest: AuthorizationRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authService = AuthorizationService(this)

        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(AUTH_ENDPOINT),
            Uri.parse(TOKEN_ENDPOINT)
        )

        val scope = "https://auth.globus.org/scopes/$COLLECTION_ID/https"

        val builder = AuthorizationRequest.Builder(
            serviceConfig,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(REDIRECT_URI)
        ).setScope(scope)
            .setAdditionalParameters(mapOf("access_type" to "offline")) // important for refresh token

        authRequest = builder.build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, REQUEST_CODE_AUTH)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_AUTH) {
            val response = AuthorizationResponse.fromIntent(data!!)
            val exception = AuthorizationException.fromIntent(data)

            if (response != null) {
                exchangeCodeForToken(response)
            } else {
                Log.e("OAuth", "Authorization failed: $exception")
            }
        }
    }

    private fun exchangeCodeForToken(response: AuthorizationResponse) {
        val tokenExchangeRequest = response.createTokenExchangeRequest()
        authService.performTokenRequest(tokenExchangeRequest) { tokenResponse, exception ->
            if (tokenResponse != null) {
                val accessToken = tokenResponse.accessToken
                val refreshToken = tokenResponse.refreshToken
                storeAccessToken(accessToken, refreshToken)
            } else {
                Log.e("OAuth", "Token exchange failed: $exception")
            }
        }
    }

    private fun storeAccessToken(accessToken: String?, refreshToken: String?) {
        val sharedPrefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        with(sharedPrefs.edit()) {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            apply()
        }
    }
}