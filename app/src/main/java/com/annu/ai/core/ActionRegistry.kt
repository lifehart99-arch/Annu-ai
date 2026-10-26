package com.annu.ai.core

import org.json.JSONObject

/**
 * Validated allowlist registry for Gemini tool actions.
 * Only actions explicitly registered here can ever be proposed or executed.
 */
object ActionRegistry {

    enum class ActionType(
        val actionName: String,
        val isSensitive: Boolean,
        val description: String
    ) {
        OPEN_APP("open_app", true, "Open an installed application"),
        OPEN_URL("open_url", false, "Open a web URL in browser"),
        SEARCH_YOUTUBE("search_youtube", false, "Search YouTube for videos"),
        DRAFT_MESSAGE("draft_message", true, "Draft a text message via Sharesheet"),
        DRAFT_SOCIAL_POST("draft_social_post", true, "Draft a social post via Sharesheet"),
        SET_REMINDER("set_reminder", false, "Schedule a local reminder alarm"),
        GENERATE_CODE("generate_code", false, "Display generated code in workspace"),
        EXPLAIN_CODE("explain_code", false, "Explain code logic"),
        GET_DEVICE_CAPABILITIES("get_device_capabilities", false, "Read supported device features")
    }

    data class ProposedAction(
        val type: ActionType,
        val params: Map<String, String>,
        val requiresConfirmation: Boolean,
        val rawJson: String
    )

    fun parseAction(jsonString: String): ProposedAction? {
        return try {
            val trimmed = jsonString.trim()
            val json = if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                JSONObject(trimmed)
            } else {
                // Try finding first JSON object block in text
                val start = trimmed.indexOf('{')
                val end = trimmed.lastIndexOf('}')
                if (start in 0 until end) {
                    JSONObject(trimmed.substring(start, end + 1))
                } else return null
            }

            val actionName = json.optString("action")
            val type = ActionType.values().firstOrNull { it.actionName.equals(actionName, ignoreCase = true) }
                ?: return null

            val paramsMap = mutableMapOf<String, String>()
            val paramsObj = json.optJSONObject("params")
            if (paramsObj != null) {
                val keys = paramsObj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    paramsMap[key] = paramsObj.optString(key, "")
                }
            } else {
                // Fallback: check top-level keys
                val keys = json.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    if (key != "action") {
                        paramsMap[key] = json.optString(key, "")
                    }
                }
            }

            ProposedAction(
                type = type,
                params = paramsMap,
                requiresConfirmation = type.isSensitive,
                rawJson = json.toString()
            )
        } catch (_: Exception) {
            null
        }
    }
}
