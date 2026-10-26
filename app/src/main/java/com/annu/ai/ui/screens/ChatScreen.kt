package com.annu.ai.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.annu.ai.core.DeviceActions
import com.annu.ai.data.model.ChatMessage
import com.annu.ai.data.model.MessageRole
import com.annu.ai.theme.BorderNavy
import com.annu.ai.theme.CardNavy
import com.annu.ai.theme.CoralRed
import com.annu.ai.theme.CyanGlow
import com.annu.ai.theme.DeepNavy
import com.annu.ai.theme.EmeraldGreen
import com.annu.ai.theme.NeonCyan
import com.annu.ai.theme.RadiantViolet
import com.annu.ai.theme.TextMuted
import com.annu.ai.theme.TextPrimary
import com.annu.ai.theme.TextSecondary
import com.annu.ai.theme.VoidBlack
import com.annu.ai.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatViewModel: ChatViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToBrain: () -> Unit
) {
    val state by chatViewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val context = LocalContext.current
    var showClearConfirm by remember { mutableStateOf(false) }

    BackHandler { onNavigateBack() }

    // Auto-scroll when new messages arrive
    LaunchedEffect(state.messages.size, state.isThinking) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    // Confirmation dialog for sensitive proposed actions
    state.pendingAction?.let { action ->
        AlertDialog(
            onDismissRequest = { chatViewModel.clearPendingAction() },
            containerColor = CardNavy,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Confirm",
                        tint = NeonCyan
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirm Action: ${action.type.actionName}",
                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary)
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = "Annu proposes the following action:\n${action.type.description}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DeepNavy)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = action.params.entries.joinToString("\n") { "${it.key}: ${it.value}" },
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan, fontFamily = FontFamily.Monospace)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        DeviceActions.executeAction(context, action)
                        chatViewModel.clearPendingAction()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = VoidBlack)
                ) {
                    Text("Allow & Run", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { chatViewModel.clearPendingAction() }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    // Clear history confirm
    if (showClearConfirm) {
        AlertDialog(
            onDismissRequest = { showClearConfirm = false },
            containerColor = CardNavy,
            title = { Text("Clear Chat History?", color = TextPrimary) },
            text = { Text("This will reset the conversation thread.", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        chatViewModel.clearHistory()
                        showClearConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed)
                ) {
                    Text("Clear")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirm = false }) {
                    Text("Cancel", color = TextMuted)
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(CyanGlow)
                                .border(1.dp, NeonCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("A", fontWeight = FontWeight.Bold, color = NeonCyan)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Annu AI",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EmeraldGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${state.activeModel} • ${state.activeLanguage}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("chat_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onNavigateToBrain,
                        modifier = Modifier.testTag("chat_brain_shortcut")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Brain Settings",
                            tint = NeonCyan
                        )
                    }
                    IconButton(
                        onClick = { showClearConfirm = true },
                        modifier = Modifier.testTag("chat_clear_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear Chat",
                            tint = TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepNavy
                )
            )
        },
        containerColor = VoidBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Emergency Alert Banner
            AnimatedVisibility(visible = state.isEmergencyDetected) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF3B1219)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Emergency Alert",
                                tint = CoralRed
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency symptoms detected. Seek emergency medical attention immediately.",
                                style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary)
                            )
                        }
                        Button(
                            onClick = {
                                DeviceActions.openUrl(context, "tel:112")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                            modifier = Modifier.padding(start = 8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Dial 112", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("112", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Message Thread
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                contentPadding = PaddingValues(top = 10.dp, bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.messages, key = { it.id }) { msg ->
                    MessageBubble(
                        message = msg,
                        onExecuteAction = { action ->
                            DeviceActions.executeAction(context, action)
                            chatViewModel.markActionExecuted(msg.id)
                        },
                        onCopyText = { text ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Annu AI", text))
                            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                        }
                    )
                }

                if (state.isThinking) {
                    item {
                        ThinkingIndicator()
                    }
                }
            }

            // Quick Suggestion Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Namaste Annu! Kaise ho?",
                    "Search YouTube for Hindi lo-fi",
                    "Health guidelines",
                    "Device capabilities"
                ).forEach { suggestion ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardNavy)
                            .border(1.dp, BorderNavy, RoundedCornerShape(12.dp))
                            .clickable { chatViewModel.sendMessage(suggestion) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary),
                            maxLines = 1
                        )
                    }
                }
            }

            // Input Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepNavy)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.currentInput,
                    onValueChange = { chatViewModel.onInputChange(it) },
                    placeholder = {
                        Text("Message Annu in Hindi or English...", color = TextMuted, fontSize = 14.sp)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { chatViewModel.sendMessage() },
                    enabled = state.currentInput.isNotBlank() && !state.isThinking,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (state.currentInput.isNotBlank()) NeonCyan else CardNavy)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (state.currentInput.isNotBlank()) VoidBlack else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    onExecuteAction: (com.annu.ai.core.ActionRegistry.ProposedAction) -> Unit,
    onCopyText: (String) -> Unit
) {
    val isUser = message.role == MessageRole.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.82f else 0.92f)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isUser) {
                        RadiantViolet
                    } else if (message.isError) {
                        Color(0xFF381218)
                    } else {
                        CardNavy
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (isUser) RadiantViolet else if (message.isError) CoralRed else BorderNavy,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(14.dp)
        ) {
            Column {
                if (!isUser) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ANNU",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            )
                            if (message.model != null) {
                                Text(
                                    text = " • ${message.model}",
                                    style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onCopyText(message.text) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy message",
                                tint = TextMuted,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = if (isUser) VoidBlack else TextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                )

                // Tool Action proposal block
                if (message.proposedAction != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepNavy),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Proposed Action: ${message.proposedAction.type.actionName}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeonCyan,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                if (message.actionExecuted) {
                                    Text(
                                        text = "Executed ✓",
                                        style = MaterialTheme.typography.labelSmall.copy(color = EmeraldGreen)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = message.proposedAction.params.entries.joinToString(", ") { "${it.key}: ${it.value}" },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            )

                            if (!message.actionExecuted) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { onExecuteAction(message.proposedAction) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NeonCyan,
                                        contentColor = VoidBlack
                                    ),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Execute Action", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                // Telemetry footer
                if (!isUser && (message.latencyMs != null || message.tokensUsed != null)) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "${message.latencyMs ?: 0}ms • ${message.tokensUsed ?: 0} tokens",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ThinkingIndicator() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CardNavy)
            .border(1.dp, BorderNavy, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(16.dp),
            strokeWidth = 2.dp,
            color = NeonCyan
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "Annu is thinking...",
            style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
        )
    }
}
