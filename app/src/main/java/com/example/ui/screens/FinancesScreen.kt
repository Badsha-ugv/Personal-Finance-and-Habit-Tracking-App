package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AccountEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.TransactionRowItem
import com.example.ui.theme.CoralExpense
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

@Composable
fun FinancesScreen(
    state: CalmClarityUiState,
    onQuickAddTransaction: () -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }

    val categories = listOf("All", "Income", "Expense", "Investment", "Software", "Office", "Travel", "Health")

    val filteredTransactions = state.transactions.filter { tx ->
        val matchesQuery = tx.title.contains(searchQuery, ignoreCase = true) ||
                tx.subtitle.contains(searchQuery, ignoreCase = true) ||
                tx.accountName.contains(searchQuery, ignoreCase = true)
        val matchesCategory = when (selectedCategoryFilter) {
            "All" -> true
            "Income" -> tx.type == "INCOME"
            "Expense" -> tx.type == "EXPENSE"
            else -> tx.category.equals(selectedCategoryFilter, ignoreCase = true)
        }
        matchesQuery && matchesCategory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .padding(horizontal = 20.dp)
            .testTag("finances_screen"),
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
                        text = "Liquidity & Accounts",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Real-time balance breakdown",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = onQuickAddTransaction,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                    modifier = Modifier.testTag("add_transaction_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Transaction",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Record", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Horizontal Accounts Carousel
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                state.accounts.forEach { acc ->
                    AccountCard(
                        account = acc,
                        isPrivacyMasked = state.isPrivacyMasked
                    )
                }
            }
        }

        // Inflow vs Outflow Ratio Card
        item {
            InflowOutflowBarCard(
                inflow = state.totalInflow,
                outflow = state.totalOutflow,
                isPrivacyMasked = state.isPrivacyMasked
            )
        }

        // Search Bar & Filter Chips
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search transactions, accounts, notes...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = SlateTextSecondary
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_tx_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = SlateOutline,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSel = selectedCategoryFilter == cat
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isSel) TealPrimary else SurfaceCard,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) TealPrimary else SlateOutline),
                        modifier = Modifier
                            .clickable { selectedCategoryFilter = cat }
                            .testTag("filter_$cat")
                    ) {
                        Text(
                            text = cat,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSel) Color.White else SlateTextPrimary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Filtered Transactions Count
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "TRANSACTIONS (${filteredTransactions.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        if (filteredTransactions.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HairlineBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No matching transactions found.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredTransactions) { tx ->
                TransactionRowItem(
                    transaction = tx,
                    isPrivacyMasked = state.isPrivacyMasked,
                    onDelete = { onDeleteTransaction(tx) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun AccountCard(
    account: AccountEntity,
    isPrivacyMasked: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(180.dp)
            .testTag("account_card_${account.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, HairlineBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = account.accountType.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = account.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = SlateTextPrimary,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = CalmClarityViewModel.formatTaka(account.balance, isPrivacyMasked),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )
        }
    }
}

@Composable
private fun InflowOutflowBarCard(
    inflow: Double,
    outflow: Double,
    isPrivacyMasked: Boolean
) {
    val total = (inflow + outflow).coerceAtLeast(1.0)
    val inflowFraction = (inflow / total).toFloat().coerceIn(0.05f, 0.95f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, HairlineBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cashflow Dynamics",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateTextPrimary
                )
                Text(
                    text = "Net Margin: +${((inflow - outflow) / total * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldPositive
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dual Progress bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(inflowFraction)
                        .background(EmeraldPositive)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f - inflowFraction)
                        .background(CoralExpense)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(EmeraldPositive))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Inflow: ${CalmClarityViewModel.formatTaka(inflow, isPrivacyMasked)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(RoundedCornerShape(2.dp)).background(CoralExpense))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Outflow: ${CalmClarityViewModel.formatTaka(outflow, isPrivacyMasked)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateTextSecondary
                    )
                }
            }
        }
    }
}
