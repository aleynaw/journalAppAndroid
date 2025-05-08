package com.example.journalappmcl

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.journalappmcl.model.QuestionResponse
import com.example.journalappmcl.viewmodel.InstantSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import java.time.Instant
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class GlobusUploader {
    companion object {
        private const val TAG = "GlobusUploader"
        private const val HEADER_AUTH = "Authorization"
        private const val HEADER_X_REQUESTED_WITH = "X-Requested-With"
        private const val HEADER_CONTENT_TYPE = "Content-Type"

        // JSON media type with charset
        private val MEDIA_TYPE_JSON = "application/json; charset=utf-8"
            .toMediaType()

        @RequiresApi(Build.VERSION_CODES.O)
        private val json = Json {
            prettyPrint = true
            serializersModule = SerializersModule {
                contextual(Instant::class, InstantSerializer)
            }
        }
    }

    fun uploadResponses(
        responsesJson: String,
        baseUrl: String,
        collectionPath: String = "",
        accessToken: String,
        userId: String,
        filename: String = "responses.json"
    ) {
        // 1) Parse responses
        val responses: List<QuestionResponse> = try {
            json.decodeFromString(responsesJson)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to parse responses JSON: ${e.localizedMessage}")
            return
        }

        // 2) Serialize back to JSON
        val jsonString = try {
            json.encodeToString(responses)
        } catch (e: Exception) {
            Log.e(TAG, "❌ JSON serialization failed: ${e.localizedMessage}")
            return
        }

        // 3) Build URL
        val trimmedBase = if (baseUrl.endsWith("/")) {
            baseUrl.dropLast(1)
        } else {
            baseUrl
        }

        val normalizedPath = buildString {
            var p = collectionPath
            if (!p.startsWith("/")) append("/")
            append(p)
            if (!p.endsWith("/")) append("/")
        }

        // Create filename with user ID and timestamp
        val timestamp = Instant.now().toString()
            .replace(":", "-")
            .replace(".", "-")
        val filenameWithUserId = "${userId}_${timestamp}_$filename"

        val fullUrl = "$trimmedBase$normalizedPath$filenameWithUserId"
        Log.i(TAG, "➡️ Uploading to URL: $fullUrl")

        // 4) Create request
        val body = jsonString.toByteArray().toRequestBody(MEDIA_TYPE_JSON)
        val request = Request.Builder()
            .url(fullUrl)
            .put(body)
            .addHeader(HEADER_AUTH, "Bearer $accessToken")
            .addHeader(HEADER_CONTENT_TYPE, "application/json")
            .addHeader(HEADER_X_REQUESTED_WITH, "XMLHttpRequest")
            .build()

        // 5) Execute request
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e(TAG, "❌ Upload error: ${e.localizedMessage}")
            }
            
            override fun onResponse(call: Call, response: Response) {
                Log.i(TAG, "✅ HTTP ${response.code}")
                response.body?.string()?.let { Log.i(TAG, "📦 Response body: $it") }
                response.close()
            }
        })
    }
} 