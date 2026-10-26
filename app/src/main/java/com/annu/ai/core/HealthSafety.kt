package com.annu.ai.core

object HealthSafety {

    const val SYSTEM_INSTRUCTION = """
You are Annu, a caring, warm, and empathetic AI companion.
Regarding health:
1. You are NOT a doctor or medical professional. Never diagnose medical conditions or recommend prescription drug dosages.
2. In case of serious symptoms (such as acute chest pain, shortness of breath, sudden numbness, uncontrolled bleeding, or self-harm thoughts), immediately direct the user to emergency medical services and advise them to dial emergency responders.
3. For general wellness, provide helpful lifestyle, hydration, nutrition, and stress management guidance while always reminding the user to seek certified healthcare provider consultation.
4. Speak warmly in the user's chosen language (Hindi, Hinglish, or English).
"""

    private val EMERGENCY_KEYWORDS = listOf(
        "chest pain", "heart attack", "can't breathe", "cannot breathe", "shortness of breath",
        "stroke", "numbness in arm", "slurred speech", "suicide", "kill myself", "want to die",
        "heavy bleeding", "unconscious", "poison", "overdose", "severe burn",
        "dil ka daura", "saans nahi aa rahi", "chhati me dard", "jaan deni hai"
    )

    fun looksLikeEmergency(input: String): Boolean {
        val lower = input.lowercase()
        return EMERGENCY_KEYWORDS.any { lower.contains(it) }
    }

    data class EmergencyContact(
        val name: String,
        val number: String,
        val description: String
    )

    val EMERGENCY_CONTACTS = listOf(
        EmergencyContact("National Emergency", "112", "All-in-one Emergency (Police, Fire, Ambulance)"),
        EmergencyContact("Ambulance", "102", "Emergency Medical Transport"),
        EmergencyContact("Universal Helpline", "911", "International / Direct Emergency Response"),
        EmergencyContact("KIRAN Mental Health", "1800-599-0019", "24/7 Mental Health Support")
    )
}
