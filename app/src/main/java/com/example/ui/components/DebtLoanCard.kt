package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteOutline
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
import com.example.ui.theme.AmberLoan
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.viewmodel.CalmClarityViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DebtLoanCard(
    loan: LoanEntity,
    isPrivacyMasked: Boolean,
    onToggleSettled: () -> Unit,
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isLent = loan.direction == "LENT"
    val daysRemaining = ((loan.returnDueDate - System.currentTimeMillis()) / (86400 * 1000L)).toInt()
    val dueText = when {
        loan.isSettled -> "Settled"
        daysRemaining < 0 -> "Overdue by ${-daysRemaining}d"
        daysRemaining == 0 -> "Due Today"
        else -> "Due in ${daysRemaining}d"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("loan_card_${loan.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, HairlineBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Amber Gold 4dp vertical indicator bar
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(96.dp)
                    .background(if (loan.isSettled) SlateOutline else AmberLoan)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Avatar Initials Pill
                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = if (isLent) Color(0xFFE6FFFA) else Color(0xFFFEF3C7)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = loan.counterpartAvatarInitials,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isLent) Color(0xFF0D9488) else Color(0xFFB45309)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Details (Name, Direction Pill, Due Date)
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = loan.counterpartName,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (loan.isSettled) SlateTextSecondary else SlateTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (isLent) Color(0xFFECFDF5) else Color(0xFFFFF1F2)
                        ) {
                            Text(
                                text = if (isLent) "LENT (RECEIVABLE)" else "BORROWED (LIABILITY)",
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isLent) EmeraldPositive else CoralExpense,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "·  $dueText",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (daysRemaining <= 0 && !loan.isSettled) CoralExpense else SlateTextSecondary,
                            fontWeight = if (daysRemaining <= 0 && !loan.isSettled) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    if (loan.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = loan.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Right Column: Amount & Settle action
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = CalmClarityViewModel.formatTaka(loan.amount, isPrivacyMasked),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (loan.isSettled) SlateTextSecondary else SlateTextPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onToggleSettled,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("settle_loan_${loan.id}")
                        ) {
                            Icon(
                                imageVector = if (loan.isSettled) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                                contentDescription = if (loan.isSettled) "Mark Unsettled" else "Mark Settled",
                                tint = if (loan.isSettled) EmeraldPositive else SlateOutline
                            )
                        }

                        IconButton(
                            onClick = onDelete,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("delete_loan_${loan.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.DeleteOutline,
                                contentDescription = "Delete Loan",
                                tint = SlateTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
