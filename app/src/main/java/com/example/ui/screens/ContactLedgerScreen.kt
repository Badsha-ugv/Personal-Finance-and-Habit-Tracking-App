package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CounterpartEntity
import com.example.data.model.LoanLedgerEntryEntity
import com.example.ui.components.appTextFieldColors
import com.example.ui.theme.AmberLoan
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.DeepSlateHero
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.CalmClarityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ContactLedgerScreen(
    counterpart: CounterpartEntity,
    entries: List<LoanLedgerEntryEntity>,
    isPrivacyMasked: Boolean,
    onBack: () -> Unit,
    onAddEntry: (type: String, amount: Double, notes: String) -> Unit,
    onDeleteEntry: (LoanLedgerEntryEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var entryType by remember { mutableStateOf("LENT") } // LENT, BORROWED, REPAID_BY_THEM, REPAID_BY_YOU

    val totalLent = entries.filter { it.type == "LENT" }.sumOf { it.amount }
    val totalBorrowed = entries.filter { it.type == "BORROWED" }.sumOf { it.amount }
    val totalRepaidByThem = entries.filter { it.type == "REPAID_BY_THEM" }.sumOf { it.amount }
    val totalRepaidByYou = entries.filter { it.type == "REPAID_BY_YOU" }.sumOf { it.amount }
    val netBalance = (totalLent - totalRepaidByThem) - (totalBorrowed - totalRepaidByYou)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("contact_ledger_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("back_from_ledger_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = counterpart.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (counterpart.phoneOrEmail.isNotBlank()) {
                        Text(
                            text = counterpart.phoneOrEmail,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Hero Net Balance Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DeepSlateHero),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "CURRENT BILATERAL BALANCE",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF94A3B8),
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val statusHeadline = when {
                        netBalance > 0 -> "${counterpart.name} owes you"
                        netBalance < 0 -> "You owe ${counterpart.name}"
                        else -> "All obligations settled"
                    }

                    Text(
                        text = statusHeadline,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFE2E8F0)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = CalmClarityViewModel.formatTaka(kotlin.math.abs(netBalance), isPrivacyMasked),
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            netBalance > 0 -> Color(0xFF34D399) // Emerald
                            netBalance < 0 -> Color(0xFFF59E0B) // Amber
                            else -> Color(0xFF94A3B8)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Total Lent", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                Text(
                                    CalmClarityViewModel.formatTaka(totalLent, isPrivacyMasked),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text("Repaid: ${CalmClarityViewModel.formatTaka(totalRepaidByThem, isPrivacyMasked)}", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = Color(0xFF34D399))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E293B)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("Total Borrowed", style = MaterialTheme.typography.labelSmall, color = Color(0xFF94A3B8))
                                Text(
                                    CalmClarityViewModel.formatTaka(totalBorrowed, isPrivacyMasked),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text("Repaid: ${CalmClarityViewModel.formatTaka(totalRepaidByYou, isPrivacyMasked)}", style = MaterialTheme.typography.bodySmall, fontSize = 10.sp, color = Color(0xFFF59E0B))
                            }
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        entryType = "LENT"
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_lend_to_person"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPositive)
                ) {
                    Text("+ Lent Money", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        entryType = "BORROWED"
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_borrow_from_person"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AmberLoan)
                ) {
                    Text("+ Borrowed", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }

                Button(
                    onClick = {
                        entryType = if (netBalance > 0) "REPAID_BY_THEM" else "REPAID_BY_YOU"
                        showAddDialog = true
                    },
                    modifier = Modifier
                        .weight(1.2f)
                        .testTag("btn_repay_person"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Repayment", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                }
            }
        }

        // Ledger History Title
        item {
            Text(
                text = "TRANSACTION LOG (${entries.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        if (entries.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("No entries recorded yet. Tap an action above to log a transaction.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        } else {
            items(entries) { entry ->
                LedgerItemCard(
                    entry = entry,
                    isPrivacyMasked = isPrivacyMasked,
                    onDelete = { onDeleteEntry(entry) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showAddDialog) {
        AddLedgerEntryDialog(
            counterpartName = counterpart.name,
            initialType = entryType,
            onDismiss = { showAddDialog = false },
            onConfirm = { type, amount, notes ->
                onAddEntry(type, amount, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun LedgerItemCard(
    entry: LoanLedgerEntryEntity,
    isPrivacyMasked: Boolean,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM d, yyyy · h:mm a", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(entry.timestamp))

    val (badgeText, badgeBg, badgeColor, icon) = when (entry.type) {
        "LENT" -> Quad(
            "LENT (I gave)",
            Color(0xFFECFDF5),
            EmeraldPositive,
            Icons.Default.ArrowUpward
        )
        "BORROWED" -> Quad(
            "BORROWED (I received)",
            Color(0xFFFEF3C7),
            Color(0xFFB45309),
            Icons.Default.ArrowDownward
        )
        "REPAID_BY_THEM" -> Quad(
            "REPAID TO ME",
            Color(0xFFF0FDF4),
            Color(0xFF16A34A),
            Icons.Default.CheckCircle
        )
        "REPAID_BY_YOU" -> Quad(
            "I REPAID",
            Color(0xFFEFF6FF),
            Color(0xFF2563EB),
            Icons.Default.CheckCircle
        )
        else -> Quad("ENTRY", Color(0xFFF1F5F9), Color(0xFF475569), Icons.Default.CheckCircle)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = badgeColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeBg
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (entry.notes.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = entry.notes,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CalmClarityViewModel.formatTaka(entry.amount, isPrivacyMasked),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = badgeColor
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete Entry",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun AddLedgerEntryDialog(
    counterpartName: String,
    initialType: String,
    onDismiss: () -> Unit,
    onConfirm: (type: String, amount: Double, notes: String) -> Unit
) {
    var selectedType by remember { mutableStateOf(initialType) }
    var amountText by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Record with $counterpartName", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Select Action Type", style = MaterialTheme.typography.labelMedium)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(
                        "LENT" to "I Lent (I gave money)",
                        "BORROWED" to "I Borrowed (I took money)",
                        "REPAID_BY_THEM" to "They Repaid Me (Partial / Full)",
                        "REPAID_BY_YOU" to "I Repaid Them (Partial / Full)"
                    ).forEach { (type, label) ->
                        val isSel = selectedType == type
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedType = type }
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Amount (৳ BDT)") },
                    placeholder = { Text("e.g. 50000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Reference / Note") },
                    placeholder = { Text("e.g. Bank transfer, cash, invoice") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = amountText.toDoubleOrNull() ?: 0.0
                    if (parsed > 0) {
                        onConfirm(selectedType, parsed, notes)
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Entry", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
