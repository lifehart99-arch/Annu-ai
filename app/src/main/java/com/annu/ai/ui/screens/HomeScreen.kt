package com.annu.ai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.annu.ai.theme.BorderNavy
import com.annu.ai.theme.CardNavy
import com.annu.ai.theme.CyanGlow
import com.annu.ai.theme.DeepNavy
import com.annu.ai.theme.EmeraldGreen
import com.annu.ai.theme.NeonCyan
import com.annu.ai.theme.NeonPink
import com.annu.ai.theme.PinkGlow
import com.annu.ai.theme.RadiantViolet
import com.annu.ai.theme.TextMuted
import com.annu.ai.theme.TextPrimary
import com.annu.ai.theme.TextSecondary
import com.annu.ai.theme.VioletGlow
import com.annu.ai.theme.VoidBlack
import com.annu.ai.ui.components.AiCoreOrb
import com.annu.ai.viewmodel.BrainViewModel
import com.annu.ai.viewmodel.ChatViewModel

@Composable
fun HomeScreen(
    chatViewModel: ChatViewModel,
    brainViewModel: BrainViewModel,
    onNavigateToChat: () -> Unit,
    onNavigateToBrain: () -> Unit,
    onNavigateToHealth: () -> Unit,
    onNavigateToDevice: () -> Unit
) {
    val chatState by chatViewModel.uiState.collectAsState()
    val brainState by brainViewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(VoidBlack)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Top Bar / Status badge
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ANNU AI",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.5.sp,
                            color = NeonCyan
                        )
                    )
                    Text(
                        text = "Personal AI Companion",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted
                    )
                }

                // Status chip
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CardNavy)
                        .border(1.dp, BorderNavy, RoundedCornerShape(20.dp))
                        .clickable { onNavigateToBrain() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = brainState.selectedModel,
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
            }
        }

        // 2. Central Animated AI Core
        item {
            Spacer(modifier = Modifier.height(16.dp))
            AiCoreOrb(
                size = 190.dp,
                isThinking = chatState.isThinking,
                onClick = onNavigateToChat
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = when (chatState.activeLanguage) {
                    "Hindi" -> "नमस्ते! मैं अन्नू हूँ। आज आपकी क्या मदद करूँ?"
                    "English" -> "Hello! I am Annu. How can I assist you today?"
                    else -> "Namaste! Main Annu hoon. Aaj main aapki kya madad karoon?"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Language switcher pills
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("Hinglish", "Hindi", "English").forEach { lang ->
                    val isSelected = chatState.activeLanguage == lang
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) CyanGlow else CardNavy)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) NeonCyan else BorderNavy,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clickable { chatViewModel.setLanguage(lang) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("lang_chip_$lang")
                    ) {
                        Text(
                            text = lang,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isSelected) NeonCyan else TextSecondary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // 3. Quick Action Cards (2x2 grid feel)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickCard(
                    title = "AI Chat",
                    subtitle = "Chat in Hindi/English",
                    icon = Icons.Default.Chat,
                    accentColor = NeonCyan,
                    glowColor = CyanGlow,
                    modifier = Modifier.weight(1f),
                    tag = "nav_chat_card",
                    onClick = onNavigateToChat
                )

                QuickCard(
                    title = "AI Brain",
                    subtitle = "Models & Proxy Tokens",
                    icon = Icons.Default.Psychology,
                    accentColor = RadiantViolet,
                    glowColor = VioletGlow,
                    modifier = Modifier.weight(1f),
                    tag = "nav_brain_card",
                    onClick = onNavigateToBrain
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickCard(
                    title = "Health Care",
                    subtitle = "Emergency & BMI",
                    icon = Icons.Default.Favorite,
                    accentColor = NeonPink,
                    glowColor = PinkGlow,
                    modifier = Modifier.weight(1f),
                    tag = "nav_health_card",
                    onClick = onNavigateToHealth
                )

                QuickCard(
                    title = "Device Tools",
                    subtitle = "YouTube & Intents",
                    icon = Icons.Default.Smartphone,
                    accentColor = EmeraldGreen,
                    glowColor = Color(0x3310B981),
                    modifier = Modifier.weight(1f),
                    tag = "nav_device_card",
                    onClick = onNavigateToDevice
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }

        // 4. Instant Prompt Launcher
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Quick Ask",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = chatState.currentInput,
                        onValueChange = { chatViewModel.onInputChange(it) },
                        placeholder = {
                            Text(
                                "Ask Annu anything (e.g. Namaste, play music...)",
                                color = TextMuted,
                                fontSize = 13.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_quick_input"),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonCyan,
                            unfocusedBorderColor = BorderNavy,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedContainerColor = DeepNavy,
                            unfocusedContainerColor = DeepNavy
                        ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (chatState.currentInput.isNotBlank()) {
                                        chatViewModel.sendMessage()
                                        onNavigateToChat()
                                    }
                                },
                                modifier = Modifier.testTag("home_send_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send",
                                    tint = NeonCyan
                                )
                            }
                        },
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Suggestion pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(DeepNavy)
                            .clickable {
                                chatViewModel.sendMessage("Namaste Annu! Kaise ho?")
                                onNavigateToChat()
                            }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Try prompt",
                            tint = RadiantViolet,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Try: \"Namaste Annu! Kaise ho?\"",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun QuickCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    glowColor: Color,
    modifier: Modifier = Modifier,
    tag: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .testTag(tag),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(glowColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted),
                maxLines = 1
            )
        }
    }
}
