package com.annu.ai.data.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val errorType: BrainErrorType, val message: String, val code: Int? = null) : NetworkResult<Nothing>()
}

enum class BrainErrorType {
    INVALID_KEY_OR_TOKEN,
    RATE_LIMIT_EXCEEDED,
    MODEL_UNAVAILABLE,
    NETWORK_FAILURE,
    SERVER_ERROR,
    UNKNOWN
}

class NetworkClient(private val tokenManager: TokenManager) {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun createOkHttpClient(): OkHttpClient {
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val token = tokenManager.getAuthToken()
            val builder = original.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")

            if (token.isNotBlank()) {
                builder.header("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(35, TimeUnit.SECONDS)
            .writeTimeout(25, TimeUnit.SECONDS)
            .build()
    }

    fun getService(): GeminiApiService {
        var baseUrl = tokenManager.getBackendUrl()
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/"
        }
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(createOkHttpClient())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }

    companion object {
        fun mapThrowableToError(throwable: Throwable): NetworkResult.Error {
            return when (throwable) {
                is SocketTimeoutException -> NetworkResult.Error(
                    BrainErrorType.NETWORK_FAILURE,
                    "Connection timed out. Please check your backend URL and internet connection."
                )
                is IOException -> NetworkResult.Error(
                    BrainErrorType.NETWORK_FAILURE,
                    "Network unreachable. Please check proxy connectivity: ${throwable.localizedMessage}"
                )
                else -> NetworkResult.Error(
                    BrainErrorType.UNKNOWN,
                    throwable.localizedMessage ?: "An unexpected error occurred"
                )
            }
        }

        fun mapHttpCodeToError(code: Int, errorBody: String?): NetworkResult.Error {
            return when (code) {
                401, 403 -> NetworkResult.Error(
                    BrainErrorType.INVALID_KEY_OR_TOKEN,
                    "Authentication failed (HTTP $code). Invalid or expired Bearer Token.",
                    code
                )
                429 -> NetworkResult.Error(
                    BrainErrorType.RATE_LIMIT_EXCEEDED,
                    "Rate limit reached (HTTP 429). Gemini proxy is throttling requests. Please wait a moment.",
                    code
                )
                503 -> NetworkResult.Error(
                    BrainErrorType.MODEL_UNAVAILABLE,
                    "Model temporarily unavailable (HTTP 503). Try switching model in AI Brain settings.",
                    code
                )
                in 500..599 -> NetworkResult.Error(
                    BrainErrorType.SERVER_ERROR,
                    "Backend proxy server error (HTTP $code): ${errorBody?.take(100) ?: "Internal error"}",
                    code
                )
                else -> NetworkResult.Error(
                    BrainErrorType.UNKNOWN,
                    "Request error (HTTP $code): ${errorBody?.take(100) ?: "Unknown"}",
                    code
                )
            }
        }
    }
}
