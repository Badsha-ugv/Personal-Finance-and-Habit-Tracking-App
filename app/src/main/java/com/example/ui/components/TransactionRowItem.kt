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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TransactionEntity
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.viewmodel.CalmClarityViewModel

@Composable
fun TransactionRowItem(
    transaction: TransactionEntity,
    isPrivacyMasked: Boolean,
    onDelete: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isIncome = transaction.type == "INCOME"
    val (categoryIcon, iconBg, iconTint) = getCategoryStyling(transaction.category, isIncome)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_item_${transaction.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Container (42dp rounded-xl)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = transaction.category,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Middle Column: Title & Subtitle + Account Badge
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = if (transaction.subtitle.isNotBlank()) transaction.subtitle else transaction.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Text(
                        text = "•",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = transaction.accountName,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Right Column: Amount & Timestamp
            Column(
                horizontalAlignment = Alignment.End
            ) {
                val formattedAmount = CalmClarityViewModel.formatTaka(transaction.amount, isPrivacyMasked)
                val displayAmount = if (isPrivacyMasked) {
                    "৳ ••••••"
                } else if (isIncome) {
                    "+ $formattedAmount"
                } else {
                    "- $formattedAmount"
                }

                Text(
                    text = displayAmount,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isIncome) EmeraldPositive else MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = formatRelativeTime(transaction.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .size(28.dp)
                    .padding(start = 2.dp)
                    .testTag("delete_tx_${transaction.id}")
            ) {
                Icon(
                    imageVector = Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

private fun getCategoryStyling(category: String, isIncome: Boolean): Triple<ImageVector, Color, Color> {
    return when (category.lowercase()) {
        "investment" -> Triple(Icons.Default.TrendingUp, Color(0xFFECFDF5), Color(0xFF059669))
        "software" -> Triple(Icons.Default.Computer, Color(0xFFEFF6FF), Color(0xFF2563EB))
        "income" -> Triple(Icons.Default.LocalAtm, Color(0xFFF0FDF4), Color(0xFF16A34A))
        "office" -> Triple(Icons.Default.BusinessCenter, Color(0xFFF5F3FF), Color(0xFF7C3AED))
        "health" -> Triple(Icons.Default.FitnessCenter, Color(0xFFFFF1F2), Color(0xFFE11D48))
        "travel" -> Triple(Icons.Default.CardTravel, Color(0xFFFEF3C7), Color(0xFFD97706))
        "tax" -> Triple(Icons.Default.Receipt, Color(0xFFFFEDD5), Color(0xFFEA580C))
        "market", "shopping" -> Triple(Icons.Default.ShoppingCart, Color(0xFFFDF4FF), Color(0xFFC026D3))
        else -> if (isIncome) {
            Triple(Icons.Default.TrendingUp, Color(0xFFECFDF5), Color(0xFF059669))
        } else {
            Triple(Icons.Default.AccountBalance, Color(0xFFF1F5F9), Color(0xFF475569))
        }
    }
}

private fun formatRelativeTime(timestamp: Long): String {
    val diff = System.currentTimeMillis() - timestamp
    val mins = diff / (60 * 1000L)
    val hours = diff / (3600 * 1000L)
    val days = diff / (86400 * 1000L)
    return when {
        mins < 60 -> "${mins.coerceAtLeast(1)}m ago"
        hours < 24 -> "${hours}h ago"
        days == 1L -> "Yesterday"
        days < 7 -> "${days}d ago"
        else -> "${days / 7}w ago"
    }
}
