package com.annu.ai.data.network

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageDto(
    @Json(name = "role") val role: String,
    @Json(name = "content") val content: String
)

@JsonClass(generateAdapter = true)
data class ChatProxyRequest(
    @Json(name = "model") val model: String,
    @Json(name = "messages") val messages: List<MessageDto>,
    @Json(name = "temperature") val temperature: Double? = 0.7,
    @Json(name = "systemInstruction") val systemInstruction: String? = null
)

@JsonClass(generateAdapter = true)
data class ChatProxyResponse(
    @Json(name = "reply") val reply: String,
    @Json(name = "model") val model: String? = null,
    @Json(name = "tokensUsed") val tokensUsed: Int? = null,
    @Json(name = "latencyMs") val latencyMs: Long? = null,
    @Json(name = "proposedAction") val proposedAction: String? = null
)

@JsonClass(generateAdapter = true)
data class ModelsResponse(
    @Json(name = "models") val models: List<String> = emptyList(),
    @Json(name = "defaultModel") val defaultModel: String? = null
)

@JsonClass(generateAdapter = true)
data class AuthVerifyResponse(
    @Json(name = "authenticated") val authenticated: Boolean,
    @Json(name = "user") val user: String? = null,
    @Json(name = "message") val message: String? = null
)
