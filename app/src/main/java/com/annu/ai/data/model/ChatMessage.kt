package com.annu.ai.data.model

import com.annu.ai.core.ActionRegistry
import java.util.UUID

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val model: String? = null,
    val tokensUsed: Int? = null,
    val latencyMs: Long? = null,
    val isError: Boolean = false,
    val proposedAction: ActionRegistry.ProposedAction? = null,
    val actionExecuted: Boolean = false
)
