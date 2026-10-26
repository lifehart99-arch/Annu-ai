package com.annu.ai.data.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface GeminiApiService {

    @POST("chat")
    suspend fun sendChat(
        @Body request: ChatProxyRequest
    ): Response<ChatProxyResponse>

    @GET("models")
    suspend fun getModels(): Response<ModelsResponse>

    @GET("auth/verify")
    suspend fun verifyAuth(): Response<AuthVerifyResponse>
}
