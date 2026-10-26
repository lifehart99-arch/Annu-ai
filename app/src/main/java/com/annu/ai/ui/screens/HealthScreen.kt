package com.annu.ai.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.annu.ai.core.DeviceActions
import com.annu.ai.core.HealthSafety
import com.annu.ai.theme.BorderNavy
import com.annu.ai.theme.CardNavy
import com.annu.ai.theme.CoralRed
import com.annu.ai.theme.DeepNavy
import com.annu.ai.theme.EmeraldGreen
import com.annu.ai.theme.NeonCyan
import com.annu.ai.theme.NeonPink
import com.annu.ai.theme.RadiantViolet
import com.annu.ai.theme.TextMuted
import com.annu.ai.theme.TextPrimary
import com.annu.ai.theme.TextSecondary
import com.annu.ai.theme.VoidBlack

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    BackHandler { onNavigateBack() }

    var heightInput by remember { mutableStateOf("170") }
    var weightInput by remember { mutableStateOf("65") }
    var bmiResult by remember { mutableStateOf<Double?>(22.5) }
    var waterReminderEnabled by remember { mutableStateOf(true) }
    var sleepReminderEnabled by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Health & Emergency Module",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("health_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
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
            contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Safety Disclaimer Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF281519)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CoralRed)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Medical Alert",
                            tint = CoralRed,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Annu AI is a supportive companion, NOT a certified medical doctor. Never use AI suggestions for diagnosis or prescription doses.",
                            style = MaterialTheme.typography.bodySmall.copy(color = TextPrimary, lineHeight = 18.sp)
                        )
                    }
                }
            }

            // Emergency Helplines Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = CoralRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Emergency Helplines (Instant Dial)",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        HealthSafety.EMERGENCY_CONTACTS.forEach { contact ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(DeepNavy)
                                    .clickable {
                                        DeviceActions.openUrl(context, "tel:${contact.number}")
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = contact.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                    )
                                    Text(
                                        text = contact.description,
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }
                                Button(
                                    onClick = { DeviceActions.openUrl(context, "tel:${contact.number}") },
                                    colors = ButtonDefaults.buttonColors(containerColor = CoralRed),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(contact.number, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // BMI Calculator
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = NeonCyan,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "BMI Calculator",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = heightInput,
                                onValueChange = { heightInput = it },
                                label = { Text("Height (cm)", color = TextMuted) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = BorderNavy,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                            OutlinedTextField(
                                value = weightInput,
                                onValueChange = { weightInput = it },
                                label = { Text("Weight (kg)", color = TextMuted) },
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonCyan,
                                    unfocusedBorderColor = BorderNavy,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                val h = heightInput.toDoubleOrNull() ?: 170.0
                                val w = weightInput.toDoubleOrNull() ?: 65.0
                                val hM = h / 100.0
                                if (hM > 0) {
                                    bmiResult = w / (hM * hM)
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = VoidBlack),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Calculate BMI", fontWeight = FontWeight.Bold)
                        }

                        if (bmiResult != null) {
                            Spacer(modifier = Modifier.height(12.dp))
                            val bmi = bmiResult!!
                            val category = when {
                                bmi < 18.5 -> "Underweight"
                                bmi < 24.9 -> "Normal weight"
                                bmi < 29.9 -> "Overweight"
                                else -> "Obesity range"
                            }
                            val categoryColor = when {
                                bmi in 18.5..24.9 -> EmeraldGreen
                                bmi < 18.5 -> NeonCyan
                                else -> CoralRed
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DeepNavy)
                                    .padding(12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "BMI: ${String.format("%.1f", bmi)} — $category",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = categoryColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Daily Health Reminders
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderNavy)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = null,
                                tint = RadiantViolet,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Daily Wellness Reminders",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Hourly Hydration Prompt", color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text("Reminds you to drink water", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            Switch(
                                checked = waterReminderEnabled,
                                onCheckedChange = {
                                    waterReminderEnabled = it
                                    Toast.makeText(context, if (it) "Water reminder enabled" else "Reminder disabled", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonCyan, checkedTrackColor = DeepNavy)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Night Sleep Wind-Down (10:30 PM)", color = TextPrimary, fontWeight = FontWeight.Medium)
                                Text("Prompts evening relaxation routine", color = TextMuted, style = MaterialTheme.typography.labelSmall)
                            }
                            Switch(
                                checked = sleepReminderEnabled,
                                onCheckedChange = {
                                    sleepReminderEnabled = it
                                    Toast.makeText(context, if (it) "Sleep reminder enabled" else "Reminder disabled", Toast.LENGTH_SHORT).show()
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = RadiantViolet, checkedTrackColor = DeepNavy)
                            )
                        }
                    }
                }
            }
        }
    }
}
