package com.example

import com.annu.ai.core.ActionRegistry
import com.annu.ai.core.HealthSafety
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AnnuAiUnitTest {

    @Test
    fun testActionRegistry_validOpenUrl() {
        val json = """{"action": "open_url", "params": {"url": "https://google.com"}}"""
        val parsed = ActionRegistry.parseAction(json)
        assertNotNull("parsed should not be null", parsed)
        assertEquals(ActionRegistry.ActionType.OPEN_URL, parsed!!.type)
        assertEquals("https://google.com", parsed.params["url"])
        assertFalse(parsed.requiresConfirmation)
    }

    @Test
    fun testActionRegistry_sensitiveActionRequiresConfirmation() {
        val json = """{"action": "draft_message", "params": {"text": "Namaste dost"}}"""
        val parsed = ActionRegistry.parseAction(json)
        assertNotNull("parsed should not be null", parsed)
        assertEquals(ActionRegistry.ActionType.DRAFT_MESSAGE, parsed!!.type)
        assertTrue(parsed.requiresConfirmation)
    }

    @Test
    fun testActionRegistry_disallowedActionRejected() {
        val json = """{"action": "run_shell_script", "params": {"cmd": "rm -rf /"}}"""
        val parsed = ActionRegistry.parseAction(json)
        assertNull("Disallowed action must be rejected by ActionRegistry", parsed)
    }

    @Test
    fun testHealthSafety_emergencyDetection() {
        assertTrue(HealthSafety.looksLikeEmergency("I am having severe chest pain"))
        assertTrue(HealthSafety.looksLikeEmergency("Mujhe chhati me dard ho raha hai"))
        assertTrue(HealthSafety.looksLikeEmergency("Patient is having a heart attack"))
        assertFalse(HealthSafety.looksLikeEmergency("What is the weather today?"))
        assertFalse(HealthSafety.looksLikeEmergency("How to cook dal makhani?"))
    }

    @Test
    fun testHealthSafety_emergencyContactsPresent() {
        val contacts = HealthSafety.EMERGENCY_CONTACTS
        assertTrue(contacts.isNotEmpty())
        assertTrue(contacts.any { it.number == "112" })
    }
}
