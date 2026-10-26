package com.annu.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.annu.ai.core.ActionRegistry
import com.annu.ai.core.HealthSafety
import com.annu.ai.data.model.ChatMessage
import com.annu.ai.data.model.MessageRole
import com.annu.ai.data.network.NetworkResult
import com.annu.ai.data.network.TokenManager
import com.annu.ai.data.repository.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isThinking: Boolean = false,
    val currentInput: String = "",
    val activeLanguage: String = "Hinglish", // Hinglish, Hindi, English
    val activePersonality: String = "Friendly", // Friendly, Professional, Playful, Concise
    val errorBanner: String? = null,
    val pendingAction: ActionRegistry.ProposedAction? = null,
    val lastLatencyMs: Long = 0L,
    val activeModel: String = "gemini-3.8-flash",
    val isEmergencyDetected: Boolean = false
)

class ChatViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    private val repository = GeminiRepository(tokenManager)

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        // Initial warm welcome message from Annu
        val initialGreeting = ChatMessage(
            role = MessageRole.ASSISTANT,
            text = "Namaste! Main Annu hoon, aapki personal AI companion. Main Hindi, Hinglish aur English me baat kar sakti hoon. Main aapki kya madad karoon?",
            model = tokenManager.getSelectedModel()
        )
        _uiState.update {
            it.copy(
                messages = listOf(initialGreeting),
                activeModel = tokenManager.getSelectedModel()
            )
        }
    }

    fun onInputChange(input: String) {
        _uiState.update { it.copy(currentInput = input) }
    }

    fun setLanguage(lang: String) {
        _uiState.update { it.copy(activeLanguage = lang) }
    }

    fun setPersonality(personality: String) {
        _uiState.update { it.copy(activePersonality = personality) }
    }

    fun sendMessage(inputOverride: String? = null) {
        val textToSend = (inputOverride ?: _uiState.value.currentInput).trim()
        if (textToSend.isBlank() || _uiState.value.isThinking) return

        val userMsg = ChatMessage(
            role = MessageRole.USER,
            text = textToSend
        )

        val isEmergency = HealthSafety.looksLikeEmergency(textToSend)

        _uiState.update {
            it.copy(
                messages = it.messages + userMsg,
                currentInput = "",
                isThinking = true,
                errorBanner = null,
                isEmergencyDetected = isEmergency
            )
        }

        viewModelScope.launch {
            // Build history pairs (excluding system/error messages)
            val history = _uiState.value.messages
                .filter { !it.isError && it.role != MessageRole.SYSTEM }
                .map {
                    val role = if (it.role == MessageRole.USER) "user" else "assistant"
                    role to it.text
                }

            val currentModel = tokenManager.getSelectedModel()

            when (val result = repository.sendMessage(history, textToSend, currentModel)) {
                is NetworkResult.Success -> {
                    val reply = result.data.reply
                    val proposedAction = ActionRegistry.parseAction(reply)

                    val assistantMsg = ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = reply,
                        model = result.data.model ?: currentModel,
                        tokensUsed = result.data.tokensUsed,
                        latencyMs = result.data.latencyMs,
                        proposedAction = proposedAction
                    )

                    _uiState.update {
                        it.copy(
                            messages = it.messages + assistantMsg,
                            isThinking = false,
                            lastLatencyMs = result.data.latencyMs ?: 0L,
                            activeModel = result.data.model ?: currentModel,
                            pendingAction = if (proposedAction?.requiresConfirmation == true) proposedAction else null
                        )
                    }
                }
                is NetworkResult.Error -> {
                    val errorMsg = ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = "Maaf kijiye, ek issue aa gaya: ${result.message}",
                        isError = true
                    )
                    _uiState.update {
                        it.copy(
                            messages = it.messages + errorMsg,
                            isThinking = false,
                            errorBanner = result.message
                        )
                    }
                }
            }
        }
    }

    fun clearPendingAction() {
        _uiState.update { it.copy(pendingAction = null) }
    }

    fun markActionExecuted(messageId: String) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map { msg ->
                    if (msg.id == messageId) msg.copy(actionExecuted = true) else msg
                },
                pendingAction = null
            )
        }
    }

    fun dismissError() {
        _uiState.update { it.copy(errorBanner = null) }
    }

    fun clearHistory() {
        val resetGreeting = ChatMessage(
            role = MessageRole.ASSISTANT,
            text = "Conversation history reset ho gaya hai! Main aapki aur kya madad kar sakti hoon?",
            model = tokenManager.getSelectedModel()
        )
        _uiState.update {
            it.copy(
                messages = listOf(resetGreeting),
                errorBanner = null,
                pendingAction = null,
                isEmergencyDetected = false
            )
        }
    }
}
