package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.IndigoHabit
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.TealPrimary

@Composable
fun appTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = MaterialTheme.colorScheme.onSurface,
    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    cursorColor = MaterialTheme.colorScheme.primary,
    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickEntryBottomSheet(
    sheetState: SheetState,
    initialTab: String,
    onDismiss: () -> Unit,
    onAddTransaction: (title: String, subtitle: String, category: String, amount: Double, type: String, accountName: String) -> Unit,
    onAddLoan: (counterpartName: String, direction: String, amount: Double, dueDays: Int, notes: String) -> Unit,
    onAddHabit: (name: String, category: String, targetMinutes: Int, colorHex: String, ringIndex: Int) -> Unit,
    onAddShoppingTrip: (title: String, budget: Double, targetAccount: String) -> Unit = { _, _, _ -> }
) {
    var selectedSection by remember { mutableStateOf(initialTab) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("quick_entry_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Entry Log",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_quick_entry_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Sheet",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Selector Tabs: Transaction / Loan / Habit / Shopping
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    "TRANSACTION" to "Transaction",
                    "LOAN" to "Loan / Debt",
                    "HABIT" to "Habit",
                    "SHOPPING" to "Shopping Trip"
                ).forEach { (key, label) ->
                    val isSelected = selectedSection == key
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { selectedSection = key }
                            .testTag("tab_${key.lowercase()}"),
                        color = if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent,
                        shadowElevation = if (isSelected) 2.dp else 0.dp
                    ) {
                        Box(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            when (selectedSection) {
                "TRANSACTION" -> TransactionEntryForm(onSubmit = onAddTransaction)
                "LOAN" -> LoanEntryForm(onSubmit = onAddLoan)
                "HABIT" -> HabitEntryForm(onSubmit = onAddHabit)
                "SHOPPING" -> ShoppingEntryForm(onSubmit = onAddShoppingTrip)
            }
        }
    }
}

@Composable
private fun TransactionEntryForm(
    onSubmit: (title: String, subtitle: String, category: String, amount: Double, type: String, accountName: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") }
    var category by remember { mutableStateOf("Software") }
    var accountName by remember { mutableStateOf("Operating Liquid") }

    val categories = listOf("Investment", "Software", "Income", "Office", "Health", "Travel", "Tax", "Market")
    val accounts = listOf("Operating Liquid", "Prime Treasury", "Investment Pool", "Cash Reserve")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Income vs Expense Segment
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { type = "EXPENSE" }
                    .testTag("type_expense"),
                color = if (type == "EXPENSE") Color(0xFFFFE4E6) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (type == "EXPENSE") CoralExpense else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Expense (Outflow)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (type == "EXPENSE") CoralExpense else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { type = "INCOME" }
                    .testTag("type_income"),
                color = if (type == "INCOME") Color(0xFFECFDF5) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (type == "INCOME") EmeraldPositive else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Income (Inflow)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (type == "INCOME") EmeraldPositive else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Amount input
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Amount (৳ BDT)") },
            placeholder = { Text("e.g. 45000") },
            leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_tx_amount"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Title input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Transaction Title") },
            placeholder = { Text("e.g. AWS Cloud Infrastructure") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_tx_title"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Subtitle / Memo input
        OutlinedTextField(
            value = subtitle,
            onValueChange = { subtitle = it },
            label = { Text("Subtitle / Memo") },
            placeholder = { Text("e.g. Monthly server tier") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_tx_subtitle"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Category Pills
        Text("Category", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEach { cat ->
                val isSel = category == cat
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable { category = cat }
                ) {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Account Selector
        Text("Account", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accounts.forEach { acc ->
                val isSel = accountName == acc
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable { accountName = acc }
                ) {
                    Text(
                        text = acc,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val parsedAmount = amountText.toDoubleOrNull() ?: 0.0
                if (title.isNotBlank() && parsedAmount > 0) {
                    onSubmit(title, subtitle, category, parsedAmount, type, accountName)
                }
            },
            enabled = title.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_transaction_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Record Transaction", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun LoanEntryForm(
    onSubmit: (counterpartName: String, direction: String, amount: Double, dueDays: Int, notes: String) -> Unit
) {
    var counterpartName by remember { mutableStateOf("") }
    var direction by remember { mutableStateOf("LENT") } // LENT or BORROWED
    var amountText by remember { mutableStateOf("") }
    var dueDays by remember { mutableIntStateOf(14) }
    var notes by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { direction = "LENT" }
                    .testTag("direction_lent"),
                color = if (direction == "LENT") Color(0xFFECFDF5) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (direction == "LENT") EmeraldPositive else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "I Lent (Receivable)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (direction == "LENT") EmeraldPositive else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { direction = "BORROWED" }
                    .testTag("direction_borrowed"),
                color = if (direction == "BORROWED") Color(0xFFFEF3C7) else MaterialTheme.colorScheme.surfaceVariant,
                border = androidx.compose.foundation.BorderStroke(1.dp, if (direction == "BORROWED") Color(0xFFF59E0B) else MaterialTheme.colorScheme.outlineVariant)
            ) {
                Box(modifier = Modifier.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = "I Borrowed (Liability)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (direction == "BORROWED") Color(0xFFB45309) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        OutlinedTextField(
            value = counterpartName,
            onValueChange = { counterpartName = it },
            label = { Text("Counterpart / Entity Name") },
            placeholder = { Text("e.g. Tariqul Islam, Apex Syndicate") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_loan_counterpart"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it },
            label = { Text("Loan Amount (৳ BDT)") },
            placeholder = { Text("e.g. 150000") },
            leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_loan_amount"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text("Return Timeframe", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(7 to "7 Days", 14 to "14 Days", 30 to "30 Days", 60 to "60 Days").forEach { (days, label) ->
                val isSel = dueDays == days
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable { dueDays = days }
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Terms / Purpose Notes") },
            placeholder = { Text("e.g. Short-term bridge swap") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_loan_notes"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val parsed = amountText.toDoubleOrNull() ?: 0.0
                if (counterpartName.isNotBlank() && parsed > 0) {
                    onSubmit(counterpartName, direction, parsed, dueDays, notes)
                }
            },
            enabled = counterpartName.isNotBlank() && (amountText.toDoubleOrNull() ?: 0.0) > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_loan_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Register Obligation", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@Composable
private fun HabitEntryForm(
    onSubmit: (name: String, category: String, targetMinutes: Int, colorHex: String, ringIndex: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Executive Focus") }
    var targetMinutes by remember { mutableIntStateOf(60) }
    var ringIndex by remember { mutableIntStateOf(0) }

    val categories = listOf("Executive Focus", "Physical Stamina", "Strategic Review")
    val ringColors = listOf("#6366F1", "#0D9488", "#10B981")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Habit / Routine Title") },
            placeholder = { Text("e.g. Deep Architecture Study") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_habit_name"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text("Category & Ring Assignment", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            categories.forEachIndexed { idx, cat ->
                val isSel = ringIndex == idx
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) IndigoHabit.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) IndigoHabit else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable {
                        ringIndex = idx
                        category = cat
                    }
                ) {
                    Text(
                        text = cat,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) IndigoHabit else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Text("Target Daily Duration", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(20 to "20m", 30 to "30m", 45 to "45m", 60 to "60m", 90 to "90m", 120 to "2h").forEach { (mins, label) ->
                val isSel = targetMinutes == mins
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isSel) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable { targetMinutes = mins }
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                if (name.isNotBlank()) {
                    val color = ringColors.getOrElse(ringIndex) { "#6366F1" }
                    onSubmit(name, category, targetMinutes, color, ringIndex)
                }
            },
            enabled = name.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_habit_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndigoHabit)
        ) {
            Text("Add Habit Routine", fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun ShoppingEntryForm(
    onSubmit: (title: String, budget: Double, targetAccount: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var budgetText by remember { mutableStateOf("") }
    var targetAccount by remember { mutableStateOf("Operating Liquid") }

    val accounts = listOf("Operating Liquid", "Prime Treasury", "Cash Reserve")

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Shopping Trip Title") },
            placeholder = { Text("e.g. Weekly Organic Market Run") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_shopping_title"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = budgetText,
            onValueChange = { budgetText = it },
            label = { Text("Planned Budget (৳ BDT)") },
            placeholder = { Text("e.g. 15000") },
            leadingIcon = { Text("৳", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(start = 12.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_shopping_budget"),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Text("Target Account for Expense Sync", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            accounts.forEach { acc ->
                val isSel = targetAccount == acc
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSel) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.clickable { targetAccount = acc }
                ) {
                    Text(
                        text = acc,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                val budget = budgetText.toDoubleOrNull() ?: 0.0
                if (title.isNotBlank() && budget > 0) {
                    onSubmit(title, budget, targetAccount)
                }
            },
            enabled = title.isNotBlank() && (budgetText.toDoubleOrNull() ?: 0.0) > 0,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("submit_shopping_trip_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Create Shopping List", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}
