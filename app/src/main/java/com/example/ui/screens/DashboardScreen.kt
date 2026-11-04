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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LoanEntity
import com.example.data.model.TransactionEntity
import com.example.ui.components.BalanceHeroCard
import com.example.ui.components.ConcentricHabitRingsCard
import com.example.ui.components.DebtLoanCard
import com.example.ui.components.TransactionRowItem
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
import com.example.ui.viewmodel.NavigationTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    state: CalmClarityUiState,
    onTogglePrivacy: () -> Unit,
    onNavigateTab: (NavigationTab) -> Unit,
    onOpenProfile: () -> Unit,
    onQuickAdd: (String) -> Unit,
    onToggleLoanSettled: (LoanEntity) -> Unit,
    onDeleteLoan: (LoanEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    val formattedDate = dateFormat.format(Date())

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            // Executive Header with Avatar, Greeting & Global Privacy Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(TealPrimary)
                            .clickable { onOpenProfile() }
                            .testTag("avatar_profile_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "HB",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column {
                        Text(
                            text = "Good day, HB",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (state.isPrivacyMasked) Color(0xFFE2E8F0) else MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.clickable { onTogglePrivacy() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (state.isPrivacyMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Privacy Eye",
                            tint = if (state.isPrivacyMasked) SlateTextSecondary else TealPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (state.isPrivacyMasked) "Masked" else "Live",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (state.isPrivacyMasked) SlateTextSecondary else TealPrimary
                        )
                    }
                }
            }
        }

        // Net Liquidity Hero Card
        item {
            BalanceHeroCard(
                netLiquidity = state.netLiquidity,
                inflow = state.totalInflow,
                outflow = state.totalOutflow,
                isPrivacyMasked = state.isPrivacyMasked,
                onTogglePrivacy = onTogglePrivacy
            )
        }

        // Concentric Habit Rings Card
        item {
            ConcentricHabitRingsCard(
                habits = state.habits,
                overallPercent = state.overallHabitPercent,
                onCardClick = { onNavigateTab(NavigationTab.ANALYTICS) }
            )
        }

        // Obligations (Lent vs Borrowed) Summary Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ACTIVE OBLIGATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Loans & Receivables",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onNavigateTab(NavigationTab.LOANS) }
                        .padding(4.dp)
                ) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium,
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to Obligations",
                        tint = TealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Pending loans snippet (top 2 active)
        val activeLoans = state.loans.filter { !it.isSettled }.take(2)
        if (activeLoans.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SurfaceCard,
                    border = androidx.compose.foundation.BorderStroke(1.dp, HairlineBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No pending obligations. All loans settled.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SlateTextSecondary
                        )
                    }
                }
            }
        } else {
            items(activeLoans) { loan ->
                DebtLoanCard(
                    loan = loan,
                    isPrivacyMasked = state.isPrivacyMasked,
                    onToggleSettled = { onToggleLoanSettled(loan) },
                    onDelete = { onDeleteLoan(loan) }
                )
            }
        }

        // Recent Transactions Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "RECENT TRANSACTIONS",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextSecondary,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Liquidity Flow",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = SlateTextPrimary
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onNavigateTab(NavigationTab.FINANCES) }
                        .padding(4.dp)
                ) {
                    Text(
                        text = "History",
                        style = MaterialTheme.typography.labelMedium,
                        color = TealPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Go to Finances",
                        tint = TealPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        items(state.recentTransactions) { tx ->
            TransactionRowItem(
                transaction = tx,
                isPrivacyMasked = state.isPrivacyMasked,
                onDelete = { onDeleteTransaction(tx) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}
