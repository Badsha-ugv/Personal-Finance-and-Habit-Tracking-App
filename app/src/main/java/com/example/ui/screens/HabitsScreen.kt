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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.outlined.DeleteOutline
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
import com.example.data.model.HabitEntity
import com.example.ui.components.ConcentricCanvasRings
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.IndigoHabit
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SurfaceCanvas
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.CalmClarityUiState

@Composable
fun HabitsScreen(
    state: CalmClarityUiState,
    onQuickAddHabit: () -> Unit,
    onToggleHabitCompleted: (HabitEntity) -> Unit,
    onDeleteHabit: (HabitEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(SurfaceCanvas)
            .padding(horizontal = 20.dp)
            .testTag("habits_screen"),
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
                        text = "Executive Habits",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = SlateTextPrimary
                    )
                    Text(
                        text = "Daily precision rings & discipline",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SlateTextSecondary
                    )
                }

                Button(
                    onClick = onQuickAddHabit,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoHabit),
                    modifier = Modifier.testTag("add_habit_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Habit",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        // Focal Hero Rings Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("habit_screen_rings_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = androidx.compose.foundation.BorderStroke(1.dp, HairlineBorder),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(160.dp)
                    ) {
                        ConcentricCanvasRings(
                            habits = state.habits,
                            modifier = Modifier.size(160.dp)
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${state.overallHabitPercent}%",
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextPrimary
                            )
                            Text(
                                text = "COMPLETE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = SlateTextSecondary,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        LegendPill(color = IndigoHabit, label = "Outer: Focus")
                        LegendPill(color = TealPrimary, label = "Mid: Stamina")
                        LegendPill(color = EmeraldPositive, label = "Inner: Review")
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ACTIVE PROTOCOLS (${state.habits.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = SlateTextSecondary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        items(state.habits) { habit ->
            HabitRowCard(
                habit = habit,
                onToggle = { onToggleHabitCompleted(habit) },
                onDelete = { onDeleteHabit(habit) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
private fun HabitRowCard(
    habit: HabitEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val ringColor = when (habit.ringIndex) {
        0 -> IndigoHabit
        1 -> TealPrimary
        else -> EmeraldPositive
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("habit_item_${habit.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (habit.completedToday) Color(0xFFF0FDF4) else SurfaceCard
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (habit.completedToday) EmeraldPositive.copy(alpha = 0.5f) else HairlineBorder
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Interactive Checkbox Button
            Surface(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .clickable { onToggle() }
                    .testTag("toggle_habit_${habit.id}"),
                color = if (habit.completedToday) EmeraldPositive else Color.Transparent,
                border = androidx.compose.foundation.BorderStroke(
                    2.dp,
                    if (habit.completedToday) EmeraldPositive else SlateOutline
                )
            ) {
                Box(contentAlignment = Alignment.Center) {
                    if (habit.completedToday) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = SlateTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = ringColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = habit.category,
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = ringColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = "Streak",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "${habit.currentStreakDays}d streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = SlateTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Duration Target
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (habit.completedToday) "${habit.targetMinutes}m"
                    else "${habit.completedMinutes}/${habit.targetMinutes}m",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (habit.completedToday) EmeraldPositive else SlateTextPrimary
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(28.dp)
                        .testTag("delete_habit_${habit.id}")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.DeleteOutline,
                        contentDescription = "Delete Habit",
                        tint = SlateTextSecondary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LegendPill(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = SlateTextSecondary
        )
    }
}
