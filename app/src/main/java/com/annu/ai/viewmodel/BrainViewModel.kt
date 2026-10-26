package com.annu.ai.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.annu.ai.data.network.TokenManager
import com.annu.ai.data.repository.ConnectionTestResult
import com.annu.ai.data.repository.GeminiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class BrainUiState(
    val backendUrl: String = TokenManager.DEFAULT_BACKEND_URL,
    val authToken: String = TokenManager.DEFAULT_PROTOTYPE_TOKEN,
    val isTokenVisible: Boolean = false,
    val availableModels: List<String> = listOf("gemini-3.8-flash", "gemini-3.1-flash-lite", "gemini-4-argon"),
    val selectedModel: String = "gemini-3.8-flash",
    val temperature: Float = 0.7f,
    val systemInstruction: String = TokenManager.DEFAULT_SYSTEM_INSTRUCTION,
    val isTestingConnection: Boolean = false,
    val testResult: ConnectionTestResult? = null,
    val totalTokensUsed: Int = 0,
    val lastLatencyMs: Long = 0L,
    val statusMessage: String? = null
)

class BrainViewModel(application: Application) : AndroidViewModel(application) {

    private val tokenManager = TokenManager.getInstance(application)
    val repository = GeminiRepository(tokenManager)

    private val _uiState = MutableStateFlow(
        BrainUiState(
            backendUrl = tokenManager.getBackendUrl(),
            authToken = tokenManager.getAuthToken(),
            selectedModel = tokenManager.getSelectedModel(),
            temperature = tokenManager.getTemperature(),
            systemInstruction = tokenManager.getSystemInstruction()
        )
    )
    val uiState: StateFlow<BrainUiState> = _uiState.asStateFlow()

    init {
        // Collect repository telemetry
        viewModelScope.launch {
            repository.totalTokensUsed.collect { tokens ->
                _uiState.update { it.copy(totalTokensUsed = tokens) }
            }
        }
        viewModelScope.launch {
            repository.lastLatencyMs.collect { latency ->
                _uiState.update { it.copy(lastLatencyMs = latency) }
            }
        }
        // Load models on init
        fetchAvailableModels()
    }

    fun updateBackendUrl(url: String) {
        tokenManager.setBackendUrl(url)
        _uiState.update { it.copy(backendUrl = url) }
    }

    fun updateAuthToken(token: String) {
        tokenManager.setAuthToken(token)
        _uiState.update { it.copy(authToken = token) }
    }

    fun toggleTokenVisibility() {
        _uiState.update { it.copy(isTokenVisible = !it.isTokenVisible) }
    }

    fun selectModel(model: String) {
        tokenManager.setSelectedModel(model)
        _uiState.update { it.copy(selectedModel = model) }
    }

    fun updateTemperature(temp: Float) {
        tokenManager.setTemperature(temp)
        _uiState.update { it.copy(temperature = temp) }
    }

    fun updateSystemInstruction(instruction: String) {
        tokenManager.setSystemInstruction(instruction)
        _uiState.update { it.copy(systemInstruction = instruction) }
    }

    fun testConnection() {
        viewModelScope.launch {
            _uiState.update { it.copy(isTestingConnection = true, testResult = null, statusMessage = "Pinging Node proxy...") }
            val result = repository.testConnection()
            _uiState.update {
                it.copy(
                    isTestingConnection = false,
                    testResult = result,
                    availableModels = if (result.modelsFound.isNotEmpty()) result.modelsFound else it.availableModels,
                    statusMessage = if (result.isSuccess) "Connection verified" else "Connection issue detected"
                )
            }
        }
    }

    fun fetchAvailableModels() {
        viewModelScope.launch {
            val models = repository.fetchModels()
            _uiState.update { it.copy(availableModels = models) }
        }
    }

    fun resetDefaults() {
        tokenManager.resetToDefaults()
        _uiState.update {
            it.copy(
                backendUrl = TokenManager.DEFAULT_BACKEND_URL,
                authToken = TokenManager.DEFAULT_PROTOTYPE_TOKEN,
                selectedModel = TokenManager.DEFAULT_MODEL,
                temperature = 0.7f,
                systemInstruction = TokenManager.DEFAULT_SYSTEM_INSTRUCTION,
                statusMessage = "Settings restored to defaults"
            )
        }
    }

    fun clearStatus() {
        _uiState.update { it.copy(statusMessage = null) }
    }
}
