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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.DeepSlateHero
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.PrivacyMaskFill
import com.example.ui.theme.SlateBorder
import com.example.ui.theme.SlateMuted
import com.example.ui.viewmodel.CalmClarityViewModel

@Composable
fun BalanceHeroCard(
    netLiquidity: Double,
    inflow: Double,
    outflow: Double,
    isPrivacyMasked: Boolean,
    onTogglePrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("balance_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = DeepSlateHero),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Header Row: Label + Privacy Eye
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldPositive)
                    )
                    Text(
                        text = "TOTAL NET LIQUIDITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateMuted,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onTogglePrivacy,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("privacy_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isPrivacyMasked) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = "Toggle Privacy Mask",
                        tint = if (isPrivacyMasked) Color(0xFF6BD8CB) else SlateMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Large Currency Figure (Bangladeshi Taka ৳)
            if (isPrivacyMasked) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF1E293B),
                    modifier = Modifier
                        .padding(vertical = 4.dp)
                        .testTag("privacy_masked_indicator")
                ) {
                    Text(
                        text = "৳ ••••••••",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 3.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "৳",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color(0xFF6BD8CB),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = CalmClarityViewModel.formatTaka(netLiquidity, false).removePrefix("৳ "),
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Sub-metrics Split Cards: Inflow & Outflow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Inflow Sub-Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp)),
                    color = Color(0xFF162036),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF064E3B)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Inflow Icon",
                                tint = EmeraldPositive,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Total Inflow",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateMuted
                            )
                            Text(
                                text = CalmClarityViewModel.formatTaka(inflow, isPrivacyMasked),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Outflow Sub-Card
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp)),
                    color = Color(0xFF162036),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SlateBorder)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF4C0519)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Outflow Icon",
                                tint = CoralExpense,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Total Outflow",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateMuted
                            )
                            Text(
                                text = CalmClarityViewModel.formatTaka(outflow, isPrivacyMasked),
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
