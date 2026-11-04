package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HabitEntity
import com.example.ui.theme.EmeraldPositive
import com.example.ui.theme.HairlineBorder
import com.example.ui.theme.IndigoHabit
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SlateTextPrimary
import com.example.ui.theme.SlateTextSecondary
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TealPrimary

@Composable
fun ConcentricHabitRingsCard(
    habits: List<HabitEntity>,
    overallPercent: Int,
    onCardClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("habit_rings_card")
            .clickable { onCardClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = androidx.compose.foundation.BorderStroke(1.dp, HairlineBorder),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "EXECUTIVE HABIT RINGS",
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateTextSecondary,
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Daily Precision & Focus",
                        style = MaterialTheme.typography.titleMedium,
                        color = SlateTextPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.padding(2.dp)
                ) {
                    Text(
                        text = "$overallPercent% Today",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Concentric Ring Canvas
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(136.dp)
                        .testTag("concentric_canvas_box")
                ) {
                    ConcentricCanvasRings(
                        habits = habits,
                        modifier = Modifier.size(136.dp)
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "$overallPercent%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextPrimary
                        )
                        Text(
                            text = "DONE",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SlateTextSecondary,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Habits List Legends
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val displayHabits = habits.take(3)
                    displayHabits.forEachIndexed { index, habit ->
                        val ringColor = when (index) {
                            0 -> IndigoHabit
                            1 -> TealPrimary
                            else -> EmeraldPositive
                        }
                        val progressFraction = if (habit.targetMinutes > 0) {
                            val done = if (habit.completedToday) habit.targetMinutes else habit.completedMinutes
                            (done.toFloat() / habit.targetMinutes.toFloat()).coerceIn(0f, 1f)
                        } else 0f

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                modifier = Modifier.size(10.dp),
                                shape = CircleShape,
                                color = ringColor
                            ) {}
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = habit.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = SlateTextPrimary,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (habit.completedToday) "Completed · ${habit.currentStreakDays}d streak"
                                    else "${habit.completedMinutes}/${habit.targetMinutes}m · ${habit.currentStreakDays}d streak",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateTextSecondary
                                )
                            }
                            Text(
                                text = "${(progressFraction * 100).toInt()}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (habit.completedToday) EmeraldPositive else SlateTextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConcentricCanvasRings(
    habits: List<HabitEntity>,
    modifier: Modifier = Modifier
) {
    val h0 = habits.getOrNull(0)
    val h1 = habits.getOrNull(1)
    val h2 = habits.getOrNull(2)

    val target0 = h0?.targetMinutes ?: 60
    val done0 = if (h0?.completedToday == true) target0 else (h0?.completedMinutes ?: 0)
    val fraction0 = (done0.toFloat() / target0.toFloat()).coerceIn(0f, 1f)

    val target1 = h1?.targetMinutes ?: 45
    val done1 = if (h1?.completedToday == true) target1 else (h1?.completedMinutes ?: 0)
    val fraction1 = (done1.toFloat() / target1.toFloat()).coerceIn(0f, 1f)

    val target2 = h2?.targetMinutes ?: 30
    val done2 = if (h2?.completedToday == true) target2 else (h2?.completedMinutes ?: 0)
    val fraction2 = (done2.toFloat() / target2.toFloat()).coerceIn(0f, 1f)

    val animProgress0 by animateFloatAsState(targetValue = fraction0, animationSpec = tween(700), label = "ring0")
    val animProgress1 by animateFloatAsState(targetValue = fraction1, animationSpec = tween(700), label = "ring1")
    val animProgress2 by animateFloatAsState(targetValue = fraction2, animationSpec = tween(700), label = "ring2")

    Canvas(modifier = modifier) {
        val strokeWidth = 8.dp.toPx()
        val spacing = 5.dp.toPx()
        val center = Offset(size.width / 2f, size.height / 2f)

        // Outer Ring (Indigo)
        val radius0 = (size.minDimension / 2f) - (strokeWidth / 2f)
        drawCircle(
            color = SlateOutline.copy(alpha = 0.4f),
            radius = radius0,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        if (animProgress0 > 0f) {
            drawArc(
                color = IndigoHabit,
                startAngle = -90f,
                sweepAngle = 360f * animProgress0,
                useCenter = false,
                topLeft = Offset(center.x - radius0, center.y - radius0),
                size = Size(radius0 * 2, radius0 * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Middle Ring (Teal)
        val radius1 = radius0 - strokeWidth - spacing
        drawCircle(
            color = SlateOutline.copy(alpha = 0.4f),
            radius = radius1,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        if (animProgress1 > 0f) {
            drawArc(
                color = TealPrimary,
                startAngle = -90f,
                sweepAngle = 360f * animProgress1,
                useCenter = false,
                topLeft = Offset(center.x - radius1, center.y - radius1),
                size = Size(radius1 * 2, radius1 * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }

        // Inner Ring (Emerald)
        val radius2 = radius1 - strokeWidth - spacing
        drawCircle(
            color = SlateOutline.copy(alpha = 0.4f),
            radius = radius2,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        if (animProgress2 > 0f) {
            drawArc(
                color = EmeraldPositive,
                startAngle = -90f,
                sweepAngle = 360f * animProgress2,
                useCenter = false,
                topLeft = Offset(center.x - radius2, center.y - radius2),
                size = Size(radius2 * 2, radius2 * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}
