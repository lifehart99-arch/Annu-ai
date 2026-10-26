package com.annu.ai.data.network

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Secure token management system and config vault for the Annu AI prototype.
 * Manages Bearer authorization tokens, proxy URL, model defaults, and developer session keys.
 */
class TokenManager private constructor(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _backendUrlFlow = MutableStateFlow(getBackendUrl())
    val backendUrlFlow: StateFlow<String> = _backendUrlFlow.asStateFlow()

    private val _authTokenFlow = MutableStateFlow(getAuthToken())
    val authTokenFlow: StateFlow<String> = _authTokenFlow.asStateFlow()

    private val _selectedModelFlow = MutableStateFlow(getSelectedModel())
    val selectedModelFlow: StateFlow<String> = _selectedModelFlow.asStateFlow()

    private val _temperatureFlow = MutableStateFlow(getTemperature())
    val temperatureFlow: StateFlow<Float> = _temperatureFlow.asStateFlow()

    private val _systemInstructionFlow = MutableStateFlow(getSystemInstruction())
    val systemInstructionFlow: StateFlow<String> = _systemInstructionFlow.asStateFlow()

    fun getBackendUrl(): String {
        return prefs.getString(KEY_BACKEND_URL, DEFAULT_BACKEND_URL) ?: DEFAULT_BACKEND_URL
    }

    fun setBackendUrl(url: String) {
        val cleanUrl = url.trim().trimEnd('/')
        prefs.edit().putString(KEY_BACKEND_URL, cleanUrl).apply()
        _backendUrlFlow.value = cleanUrl
    }

    fun getAuthToken(): String {
        return prefs.getString(KEY_AUTH_TOKEN, DEFAULT_PROTOTYPE_TOKEN) ?: DEFAULT_PROTOTYPE_TOKEN
    }

    fun setAuthToken(token: String) {
        val cleanToken = token.trim()
        prefs.edit().putString(KEY_AUTH_TOKEN, cleanToken).apply()
        _authTokenFlow.value = cleanToken
    }

    fun getSelectedModel(): String {
        return prefs.getString(KEY_SELECTED_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    }

    fun setSelectedModel(model: String) {
        prefs.edit().putString(KEY_SELECTED_MODEL, model).apply()
        _selectedModelFlow.value = model
    }

    fun getTemperature(): Float {
        return prefs.getFloat(KEY_TEMPERATURE, 0.7f)
    }

    fun setTemperature(temp: Float) {
        prefs.edit().putFloat(KEY_TEMPERATURE, temp).apply()
        _temperatureFlow.value = temp
    }

    fun getSystemInstruction(): String {
        return prefs.getString(KEY_SYSTEM_INSTRUCTION, DEFAULT_SYSTEM_INSTRUCTION) ?: DEFAULT_SYSTEM_INSTRUCTION
    }

    fun setSystemInstruction(instruction: String) {
        prefs.edit().putString(KEY_SYSTEM_INSTRUCTION, instruction).apply()
        _systemInstructionFlow.value = instruction
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
        _backendUrlFlow.value = DEFAULT_BACKEND_URL
        _authTokenFlow.value = DEFAULT_PROTOTYPE_TOKEN
        _selectedModelFlow.value = DEFAULT_MODEL
        _temperatureFlow.value = 0.7f
        _systemInstructionFlow.value = DEFAULT_SYSTEM_INSTRUCTION
    }

    companion object {
        private const val PREFS_NAME = "annu_ai_secure_vault"
        private const val KEY_BACKEND_URL = "key_backend_url"
        private const val KEY_AUTH_TOKEN = "key_auth_token"
        private const val KEY_SELECTED_MODEL = "key_selected_model"
        private const val KEY_TEMPERATURE = "key_temperature"
        private const val KEY_SYSTEM_INSTRUCTION = "key_system_instruction"

        // Default Cloud Run proxy URL for development
        const val DEFAULT_BACKEND_URL = "https://ais-dev-6qnaxhcnos55scya24ymkm-785707029323.asia-northeast1.run.app"
        const val DEFAULT_PROTOTYPE_TOKEN = "annu-dev-prototype-bearer-token-2026"
        const val DEFAULT_MODEL = "gemini-3.8-flash"

        const val DEFAULT_SYSTEM_INSTRUCTION = """You are Annu, a warm, cheerful, and caring female AI companion.
You speak Hindi, Hinglish, and English naturally and conversationally.
You are empathetic, supportive, and helpful. You never claim to be human.
You help with daily tasks, conversation, wellness motivation, and device actions.
If proposing a device action, output valid JSON with { "action": "<action_name>", "params": { ... } }."""

        @Volatile
        private var instance: TokenManager? = null

        fun getInstance(context: Context): TokenManager {
            return instance ?: synchronized(this) {
                instance ?: TokenManager(context.applicationContext).also { instance = it }
            }
        }
    }
}
