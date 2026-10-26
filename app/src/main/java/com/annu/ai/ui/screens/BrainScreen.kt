package com.annu.ai.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.annu.ai.data.network.TokenManager
import com.annu.ai.theme.BorderNavy
import com.annu.ai.theme.CardNavy
import com.annu.ai.theme.CoralRed
import com.annu.ai.theme.CyanGlow
import com.annu.ai.theme.DeepNavy
import com.annu.ai.theme.EmeraldGreen
import com.annu.ai.theme.NeonCyan
import com.annu.ai.theme.NeonPink
import com.annu.ai.theme.RadiantViolet
import com.annu.ai.theme.TextMuted
import com.annu.ai.theme.TextPrimary
import com.annu.ai.theme.TextSecondary
import com.annu.ai.theme.VioletGlow
import com.annu.ai.theme.VoidBlack
import com.annu.ai.viewmodel.BrainViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrainScreen(
    brainViewModel: BrainViewModel,
    onNavigateBack: () -> Unit
) {
    val state by brainViewModel.uiState.collectAsState()

    BackHandler { onNavigateBack() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Brain & Proxy Engine",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Stage 3 • Gemini Backend Pipeline",
                            style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("brain_back_button")
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
                        onClick = { brainViewModel.resetDefaults() },
                        modifier = Modifier.testTag("brain_reset_defaults_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RestartAlt,
                            contentDescription = "Reset Defaults",
                            tint = TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepNavy)
            )
        },
        containerColor = VoidBlack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Connection & Ping Banner Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("connection_test_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when {
                            state.testResult?.isSuccess == true -> EmeraldGreen
                            state.testResult?.isSuccess == false -> CoralRed
                            else -> BorderNavy
                        }
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            when {
                                                state.testResult?.isSuccess == true -> Color(0x3310B981)
                                                state.testResult?.isSuccess == false -> Color(0x33FF4565)
                                                else -> CyanGlow
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = when {
                                            state.testResult?.isSuccess == true -> Icons.Default.CheckCircle
                                            state.testResult?.isSuccess == false -> Icons.Default.Error
                                            else -> Icons.Default.Speed
                                        },
                                        contentDescription = null,
                                        tint = when {
                                            state.testResult?.isSuccess == true -> EmeraldGreen
                                            state.testResult?.isSuccess == false -> CoralRed
                                            else -> NeonCyan
                                        },
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "Proxy Status",
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = when {
                                            state.isTestingConnection -> "Pinging Node proxy..."
                                            state.testResult != null -> state.testResult!!.message
                                            else -> "Ready to ping backend"
                                        },
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (state.testResult?.isSuccess == false) CoralRed else TextSecondary
                                        )
                                    )
                                }
                            }

                            if (state.testResult != null) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DeepNavy)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "${state.testResult!!.latencyMs}ms",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeonCyan,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { brainViewModel.testConnection() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("test_connection_button"),
                            enabled = !state.isTestingConnection,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonCyan,
                                contentColor = VoidBlack
                            )
                        ) {
                            if (state.isTestingConnection) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    strokeWidth = 2.dp,
                                    color = VoidBlack
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Testing Connection...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Test Connection & Ping", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 2. Security Notice Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Security",
                            tint = NeonCyan,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Zero Client Secret Exposure",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonCyan
                                )
                            )
                            Text(
                                text = "Google Gemini API key is managed exclusively on the backend proxy server. The client communicates via authenticated Bearer tokens.",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                            )
                        }
                    }
                }
            }

            // 3. Backend Proxy URL Editor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Backend Proxy URL",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "HTTPS",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = EmeraldGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.backendUrl,
                            onValueChange = { brainViewModel.updateBackendUrl(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("backend_url_input"),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderNavy,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DeepNavy,
                                unfocusedContainerColor = DeepNavy
                            ),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Text(
                                text = "Default Cloud Run Instance",
                                style = MaterialTheme.typography.labelSmall.copy(color = RadiantViolet),
                                modifier = Modifier.clickable {
                                    brainViewModel.updateBackendUrl(TokenManager.DEFAULT_BACKEND_URL)
                                }
                            )
                        }
                    }
                }
            }

            // 4. Secure Bearer Auth Token
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Bearer Authorization Token",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Used to authenticate requests against Node backend /chat and /models",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.authToken,
                            onValueChange = { brainViewModel.updateAuthToken(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_token_input"),
                            shape = RoundedCornerShape(12.dp),
                            visualTransformation = if (state.isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(
                                    onClick = { brainViewModel.toggleTokenVisibility() },
                                    modifier = Modifier.testTag("toggle_token_visibility")
                                ) {
                                    Icon(
                                        imageVector = if (state.isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Toggle Visibility",
                                        tint = TextMuted
                                    )
                                }
                            },
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = RadiantViolet,
                                unfocusedBorderColor = BorderNavy,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DeepNavy,
                                unfocusedContainerColor = DeepNavy
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // 5. Gemini Model Selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Active Gemini Model",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = "Fetched from /models",
                                style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        state.availableModels.forEach { modelName ->
                            val isSelected = state.selectedModel == modelName
                            val modelTag = when (modelName) {
                                "gemini-3.8-flash" -> "General Tasks • Recommended"
                                "gemini-3.1-flash-lite" -> "Ultra Low-Latency • Fast"
                                "gemini-4-argon" -> "Complex Tasks & Reasoning"
                                else -> "Gemini Model"
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) VioletGlow else DeepNavy)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) RadiantViolet else BorderNavy,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { brainViewModel.selectModel(modelName) }
                                    .padding(14.dp)
                                    .testTag("model_option_$modelName"),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = modelName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) NeonCyan else TextPrimary,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    )
                                    Text(
                                        text = modelTag,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Selected",
                                        tint = RadiantViolet,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 6. Temperature Dial
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Temperature (Creativity)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                            Text(
                                text = String.format("%.2f", state.temperature),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    color = NeonPink,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Slider(
                            value = state.temperature,
                            onValueChange = { brainViewModel.updateTemperature(it) },
                            valueRange = 0.0f..1.0f,
                            steps = 9,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("temperature_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = NeonPink,
                                activeTrackColor = NeonPink,
                                inactiveTrackColor = BorderNavy
                            )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("0.0 (Precise)", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Text("0.7 (Balanced)", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                            Text("1.0 (Creative)", style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
                        }
                    }
                }
            }

            // 7. System Instruction Editor
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "System Persona Instruction",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Text(
                            text = "Defines Annu's personality, tone, language habits, and tool calling instructions",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = state.systemInstruction,
                            onValueChange = { brainViewModel.updateSystemInstruction(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .testTag("system_instruction_input"),
                            shape = RoundedCornerShape(12.dp),
                            textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderNavy,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedContainerColor = DeepNavy,
                                unfocusedContainerColor = DeepNavy
                            )
                        )
                    }
                }
            }

            // 8. Telemetry & Token Usage Statistics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Live Telemetry & Diagnostics",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            TelemetryBadge("Total Tokens", "${state.totalTokensUsed}", NeonCyan)
                            TelemetryBadge("Last Latency", "${state.lastLatencyMs} ms", RadiantViolet)
                            TelemetryBadge("Auth Mode", "Bearer", EmeraldGreen)
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Error Code Reference:\n• 401: Invalid Bearer Token / Unauthorized\n• 429: Rate Limit reached on proxy / Gemini\n• 503: Model temporarily unavailable\n• Network failure: Proxy host unreachable or connection timeout",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextMuted, lineHeight = 16.sp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TelemetryBadge(label: String, value: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(DeepNavy)
            .border(1.dp, BorderNavy, RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = label, style = MaterialTheme.typography.labelSmall.copy(color = TextMuted))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}
