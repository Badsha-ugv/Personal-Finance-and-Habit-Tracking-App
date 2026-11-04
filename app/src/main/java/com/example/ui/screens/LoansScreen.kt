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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoanEntity
import com.example.ui.components.DebtLoanCard
import com.example.ui.components.appTextFieldColors
import com.example.ui.theme.AmberLoan
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.CalmClarityUiState
import com.example.ui.viewmodel.CalmClarityViewModel
import com.example.ui.viewmodel.CounterpartSummary

@Composable
fun LoansScreen(
    state: CalmClarityUiState,
    onOpenCounterpartLedger: (Long) -> Unit,
    onAddCounterpart: (name: String, phoneOrEmail: String, notes: String) -> Unit,
    onQuickAddLoan: () -> Unit,
    onToggleSettled: (LoanEntity) -> Unit,
    onDeleteLoan: (LoanEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddCounterpartDialog by remember { mutableStateOf(false) }
    var activeSubView by remember { mutableStateOf("PEOPLE") } // "PEOPLE" (Ledgers) or "ALL_LOANS"

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("loans_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Loan Ledgers & Debt",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Counterpart ledgers, repayments & credit",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { showAddCounterpartDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.testTag("register_contact_btn")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Contact", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Summary Metric Cards (Lent vs Borrowed)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Lent (Receivables)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_lent_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFECFDF5)
                        ) {
                            Text(
                                text = "TOTAL RECEIVABLE",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPositive,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = CalmClarityViewModel.formatTaka(state.totalLent, state.isPrivacyMasked),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Owed to you",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Total Borrowed (Liabilities)
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .testTag("metric_borrowed_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFFEF3C7)
                        ) {
                            Text(
                                text = "TOTAL PAYABLE",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFB45309),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = CalmClarityViewModel.formatTaka(state.totalBorrowed, state.isPrivacyMasked),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "You owe others",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Sub-view Toggle: Person Ledgers vs Individual Loan Tickets
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubView = "PEOPLE" }
                        .testTag("tab_people_ledgers"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeSubView == "PEOPLE") MaterialTheme.colorScheme.surface else Color.Transparent,
                    shadowElevation = if (activeSubView == "PEOPLE") 2.dp else 0.dp
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Person Ledgers (${state.counterparts.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (activeSubView == "PEOPLE") FontWeight.Bold else FontWeight.Medium,
                            color = if (activeSubView == "PEOPLE") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubView = "ALL_LOANS" }
                        .testTag("tab_all_loans"),
                    shape = RoundedCornerShape(8.dp),
                    color = if (activeSubView == "ALL_LOANS") MaterialTheme.colorScheme.surface else Color.Transparent,
                    shadowElevation = if (activeSubView == "ALL_LOANS") 2.dp else 0.dp
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "Loan Tranches (${state.loans.size})",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (activeSubView == "ALL_LOANS") FontWeight.Bold else FontWeight.Medium,
                            color = if (activeSubView == "ALL_LOANS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        if (activeSubView == "PEOPLE") {
            if (state.counterparts.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No contacts registered. Tap '+ Contact' above to register someone.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(state.counterparts) { summary ->
                    CounterpartSummaryCard(
                        summary = summary,
                        isPrivacyMasked = state.isPrivacyMasked,
                        onClick = { onOpenCounterpartLedger(summary.counterpart.id) }
                    )
                }
            }
        } else {
            // Individual Loan Tickets
            if (state.loans.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                            Text("No loan obligations.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(state.loans) { loan ->
                    DebtLoanCard(
                        loan = loan,
                        isPrivacyMasked = state.isPrivacyMasked,
                        onToggleSettled = { onToggleSettled(loan) },
                        onDelete = { onDeleteLoan(loan) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showAddCounterpartDialog) {
        RegisterCounterpartDialog(
            onDismiss = { showAddCounterpartDialog = false },
            onConfirm = { name, phone, notes ->
                onAddCounterpart(name, phone, notes)
                showAddCounterpartDialog = false
            }
        )
    }
}

@Composable
private fun CounterpartSummaryCard(
    summary: CounterpartSummary,
    isPrivacyMasked: Boolean,
    onClick: () -> Unit
) {
    val net = summary.netBalance
    val isReceivable = net > 0
    val isPayable = net < 0
    val isSettled = net == 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("counterpart_card_${summary.counterpart.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Surface(
                modifier = Modifier.size(46.dp),
                shape = CircleShape,
                color = when {
                    isReceivable -> Color(0xFFE6FFFA)
                    isPayable -> Color(0xFFFEF3C7)
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = summary.counterpart.initials,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isReceivable -> TealPrimary
                            isPayable -> Color(0xFFB45309)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.counterpart.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                val subtitle = when {
                    isReceivable -> "Owes you · Tap for ledger"
                    isPayable -> "You owe · Tap for ledger"
                    else -> "Settled · Tap for ledger"
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = when {
                        isReceivable -> EmeraldPositive
                        isPayable -> AmberLoan
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Medium
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CalmClarityViewModel.formatTaka(kotlin.math.abs(net), isPrivacyMasked),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        isReceivable -> EmeraldPositive
                        isPayable -> AmberLoan
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                Spacer(modifier = Modifier.height(2.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Open Ledger",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun RegisterCounterpartDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, phone: String, notes: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register Contact for Loan Ledger", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Person / Business Name *") },
                    placeholder = { Text("e.g. Rahim Chowdhury, Tech Syndicate") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / Email (Optional)") },
                    placeholder = { Text("e.g. +880 1711-234567") },
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Relationship / Memo") },
                    placeholder = { Text("e.g. Cofounder, Vendor, Investor") },
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
                    if (name.isNotBlank()) onConfirm(name, phone, notes)
                },
                enabled = name.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Register Contact", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
