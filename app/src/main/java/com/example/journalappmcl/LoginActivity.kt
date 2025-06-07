package com.example.journalappmcl

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import net.openid.appauth.*
import java.security.SecureRandom
import java.util.*

private const val CLIENT_ID = "00ef6e5a-2874-4a00-aef3-cc8d1740c2f1"
private const val COLLECTION_ID = "cdd81df8-db63-4ea5-b017-031ba03f33ae"
private const val REDIRECT_URI = "https://apple.node.rip/"
private const val AUTH_ENDPOINT = "https://auth.globus.org/v2/oauth2/authorize"
private const val TOKEN_ENDPOINT = "https://auth.globus.org/v2/oauth2/token"
private const val REQUEST_CODE_AUTH = 1001
private const val TAG = "OAuth"

class LoginActivity : AppCompatActivity() {

    private lateinit var authService: AuthorizationService
    private lateinit var authServiceConfig: AuthorizationServiceConfiguration
    private lateinit var codeVerifier: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authService = AuthorizationService(this)
        authServiceConfig = AuthorizationServiceConfiguration(
            Uri.parse(AUTH_ENDPOINT),
            Uri.parse(TOKEN_ENDPOINT)
        )

        // Handle potential redirect on app start
        val uri = intent.data
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            Log.i(TAG, "onCreate: Handling redirect URI from initial intent: $uri")
            handleAuthRedirect(intent)
            return
        }

        // Start new auth flow
        startAuthRequest()
    }

    // Generate a secure random code verifier
    @RequiresApi(Build.VERSION_CODES.O)
    private fun generateCodeVerifier(): String {
        val secureRandom = SecureRandom()
        val bytes = ByteArray(64) // 64 bytes = 512 bits
        secureRandom.nextBytes(bytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startAuthRequest() {
        Log.i(TAG, "Starting authorization request flow with PKCE")

        // Generate code verifier and save it for later
        codeVerifier = generateCodeVerifier()
        getSharedPreferences("auth_prefs", MODE_PRIVATE).edit()
            .putString("code_verifier", codeVerifier)
            .apply()

        Log.d(TAG, "Generated code verifier: $codeVerifier")

        val scope = "https://auth.globus.org/scopes/$COLLECTION_ID/https"

        val builder = AuthorizationRequest.Builder(
            authServiceConfig,
            CLIENT_ID,
            ResponseTypeValues.CODE,
            Uri.parse(REDIRECT_URI)
        )
            .setScope(scope)
            .setAdditionalParameters(mapOf("access_type" to "offline"))
            .setCodeVerifier(codeVerifier)

        val authRequest = builder.build()

        val authIntent = authService.getAuthorizationRequestIntent(authRequest)
        startActivityForResult(authIntent, REQUEST_CODE_AUTH)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val uri = intent.data
        if (uri != null && uri.toString().startsWith(REDIRECT_URI)) {
            Log.i(TAG, "onNewIntent: Handling redirect URI: $uri")
            handleAuthRedirect(intent)
        }
    }

    private fun handleAuthRedirect(intent: Intent) {
        val uri = intent.data
        Log.i(TAG, "Handling redirect URI: $uri")

        // Retrieve saved code verifier
        val storedCodeVerifier = getSharedPreferences("auth_prefs", MODE_PRIVATE)
            .getString("code_verifier", null)

        if (storedCodeVerifier == null) {
            Log.e(TAG, "Error: No stored code verifier found")
            finish()
            return
        }

        Log.d(TAG, "Retrieved stored code verifier: $storedCodeVerifier")

        // Extract the authorization code
        val code = uri?.getQueryParameter("code")

        if (code != null) {
            Log.i(TAG, "Extracted authorization code: $code")

            // Create token request with PKCE
            val tokenRequest = TokenRequest.Builder(
                authServiceConfig,
                CLIENT_ID
            )
                .setGrantType(GrantTypeValues.AUTHORIZATION_CODE)
                .setRedirectUri(Uri.parse(REDIRECT_URI))
                .setAuthorizationCode(code)
                .setCodeVerifier(storedCodeVerifier)
                .build()

            Log.i(TAG, "Performing token request with PKCE")
            authService.performTokenRequest(tokenRequest) { tokenResponse, exception ->
                if (tokenResponse != null) {
                    val accessToken = tokenResponse.accessToken
                    val refreshToken = tokenResponse.refreshToken

                    // Print the token as requested
                    Log.i(TAG, "Access Token: $accessToken")

                    storeTokens(accessToken, refreshToken)
                } else {
                    Log.e(TAG, "Token exchange failed: $exception")
                    runOnUiThread { finish() }
                }
            }
        } else {
            Log.e(TAG, "No code parameter found in redirect URI")
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d(TAG, "onActivityResult: requestCode=$requestCode, resultCode=$resultCode")

        if (requestCode == REQUEST_CODE_AUTH) {
            if (data == null) {
                Log.e(TAG, "No data received in onActivityResult")
                finish()
                return
            }

            Log.d(TAG, "Processing result from OAuth browser activity")
            handleAuthRedirect(data)
        }
    }

    private fun storeTokens(accessToken: String?, refreshToken: String?) {
        Log.i(TAG, "Storing tokens in SharedPreferences")

        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        with(prefs.edit()) {
            putString("access_token", accessToken)
            putString("refresh_token", refreshToken)
            putBoolean("isLoggedIn", true)
            apply()
        }

        Log.i(TAG, "✅ Tokens saved. Redirecting to main app.")

        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }

    fun refreshAccessToken() {
        Log.i(TAG, "Starting token refresh flow")

        val prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE)
        val refreshToken = prefs.getString("refresh_token", null)

        if (refreshToken == null) {
            Log.e(TAG, "No refresh token found. Cannot refresh access token.")
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
                val newRefreshToken = tokenResponse.refreshToken ?: refreshToken // Some servers may not return a new one

                Log.i(TAG, "New Access Token: $newAccessToken")

                // Save the new tokens
                with(prefs.edit()) {
                    putString("access_token", newAccessToken)
                    putString("refresh_token", newRefreshToken)
                    apply()
                }

                Log.i(TAG, "✅ Tokens refreshed and stored.")

            } else {
                Log.e(TAG, "Token refresh failed: $exception")
            }
        }
    }
}