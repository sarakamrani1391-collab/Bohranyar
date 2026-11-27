package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.ScienceDiagramType
import com.example.ui.theme.DarkOlive
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LightSage
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.WarningAmber
import kotlin.math.sin

@Composable
fun AnimatedScienceDiagramCard(
    diagramType: ScienceDiagramType,
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "science_diagram")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val caption = when (diagramType) {
        ScienceDiagramType.COMBUSTION_CO -> "نمودار مفهومی: احتراق کامل (آبی: CO2) در برابر احتراق ناقص (زرد/دود: CO)"
        ScienceDiagramType.HEAT_TRANSFER -> "نمودار انتقال گرما: صعود هوای داغ با همرفت (کف اتاق هوای تمیزتر دارد)"
        ScienceDiagramType.ELECTRIC_CIRCUIT -> "مدار الکتریکی: عبور جریان در رسانا و قطع ایمن توسط فیوز هنگام اتصال کوتاه"
        ScienceDiagramType.PRESSURE_FORCE -> "نمودار فشار و نیرو: توزیع نیروی آب یا ضربه بر سطح"
        ScienceDiagramType.RESPIRATORY_HEMOGLOBIN -> "تبادل گاز در ریه و رقابت مولکول CO با اکسیژن روی هموگلوبین"
        ScienceDiagramType.EARTH_SEISMIC -> "صفحات سنگ‌کره، کانون زلزله و انتشار امواج لرزه‌ای P و S"
        ScienceDiagramType.WATER_CYCLE_POLLUTION -> "چرخه آب، رواناب سیل و انتقال آلاینده‌ها به منابع آب"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(145.dp)
            ) {
                val w = size.width
                val h = size.height
                drawRoundRect(
                    color = LightSage.copy(alpha = 0.45f),
                    cornerRadius = CornerRadius(14.dp.toPx(), 14.dp.toPx())
                )

                when (diagramType) {
                    ScienceDiagramType.COMBUSTION_CO -> {
                        // Left: Complete blue flame
                        drawOval(
                            color = Color(0xFF1E88E5),
                            topLeft = Offset(w * 0.22f, h * 0.42f),
                            size = Size(w * 0.12f, h * 0.42f)
                        )
                        // Right: Incomplete yellow/orange smoky flame
                        drawOval(
                            color = WarningAmber,
                            topLeft = Offset(w * 0.64f, h * 0.36f),
                            size = Size(w * 0.14f, h * 0.48f)
                        )
                        // Rising CO smoke particles
                        for (i in 0..3) {
                            val progress = (phase + i * 0.25f) % 1f
                            drawCircle(
                                color = Color.DarkGray.copy(alpha = (1f - progress) * 0.6f),
                                radius = (5f + progress * 8f).dp.toPx(),
                                center = Offset(w * 0.71f + sin(progress * 6f) * 10f, h * (0.34f - progress * 0.26f))
                            )
                        }
                    }

                    ScienceDiagramType.HEAT_TRANSFER -> {
                        // Convection loops
                        drawRect(
                            color = EmergencyRed.copy(alpha = 0.7f),
                            topLeft = Offset(w * 0.12f, h * 0.72f),
                            size = Size(w * 0.76f, h * 0.16f)
                        )
                        for (i in 0..4) {
                            val yProg = (phase + i * 0.2f) % 1f
                            drawCircle(
                                color = WarningAmber.copy(alpha = 1f - yProg * 0.6f),
                                radius = 6.dp.toPx(),
                                center = Offset(w * (0.2f + i * 0.15f), h * (0.70f - yProg * 0.52f))
                            )
                        }
                        // Cooler safe floor zone line
                        drawLine(
                            color = SafeGreen,
                            start = Offset(w * 0.08f, h * 0.64f),
                            end = Offset(w * 0.92f, h * 0.64f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }

                    ScienceDiagramType.ELECTRIC_CIRCUIT -> {
                        drawRoundRect(
                            color = DarkOlive,
                            topLeft = Offset(w * 0.15f, h * 0.22f),
                            size = Size(w * 0.70f, h * 0.56f),
                            cornerRadius = CornerRadius(12.dp.toPx(), 12.dp.toPx()),
                            style = Stroke(width = 3.dp.toPx())
                        )
                        // Animated electron dot moving along loop
                        val xElectron = w * (0.15f + phase * 0.70f)
                        drawCircle(
                            color = WarningAmber,
                            radius = 6.dp.toPx(),
                            center = Offset(xElectron, h * 0.22f)
                        )
                        // Lamp / resistor circle
                        drawCircle(
                            color = Color(0xFFFFCA28),
                            radius = 16.dp.toPx(),
                            center = Offset(w * 0.5f, h * 0.78f)
                        )
                    }

                    ScienceDiagramType.PRESSURE_FORCE -> {
                        val waveShift = phase * 30f
                        for (i in 0..3) {
                            val y = h * (0.35f + i * 0.14f)
                            drawLine(
                                color = Color(0xFF0288D1).copy(alpha = 0.75f),
                                start = Offset(w * 0.10f + waveShift, y),
                                end = Offset(w * 0.56f + waveShift, y),
                                strokeWidth = 4.dp.toPx()
                            )
                        }
                        drawRoundRect(
                            color = OliveGreen,
                            topLeft = Offset(w * 0.68f, h * 0.30f),
                            size = Size(w * 0.16f, h * 0.52f),
                            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                        )
                    }

                    ScienceDiagramType.RESPIRATORY_HEMOGLOBIN -> {
                        // Two stylized lungs
                        drawOval(
                            color = Color(0xFFE57373).copy(alpha = 0.45f),
                            topLeft = Offset(w * 0.20f, h * 0.20f),
                            size = Size(w * 0.22f, h * 0.60f)
                        )
                        drawOval(
                            color = Color(0xFFE57373).copy(alpha = 0.45f),
                            topLeft = Offset(w * 0.58f, h * 0.20f),
                            size = Size(w * 0.22f, h * 0.60f)
                        )
                        // Moving oxygen molecules
                        val xMove = w * (0.32f + phase * 0.36f)
                        drawCircle(
                            color = Color(0xFF1E88E5),
                            radius = 7.dp.toPx(),
                            center = Offset(xMove, h * 0.50f)
                        )
                        drawCircle(
                            color = SafeGreen,
                            radius = 7.dp.toPx(),
                            center = Offset(w - xMove, h * 0.38f)
                        )
                    }

                    ScienceDiagramType.EARTH_SEISMIC -> {
                        // Fault line
                        drawLine(
                            color = DarkOlive,
                            start = Offset(w * 0.48f, h * 0.15f),
                            end = Offset(w * 0.52f, h * 0.90f),
                            strokeWidth = 3.dp.toPx()
                        )
                        // Expanding P & S rings from focus
                        val focus = Offset(w * 0.50f, h * 0.72f)
                        val r1 = (phase * 70f + 12f).dp.toPx()
                        val r2 = (((phase + 0.5f) % 1f) * 55f + 8f).dp.toPx()
                        drawCircle(
                            color = OliveGreen.copy(alpha = 1f - phase * 0.7f),
                            radius = r1,
                            center = focus,
                            style = Stroke(width = 2.5.dp.toPx())
                        )
                        drawCircle(
                            color = EmergencyRed.copy(alpha = 0.75f),
                            radius = r2,
                            center = focus,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }

                    ScienceDiagramType.WATER_CYCLE_POLLUTION -> {
                        // Cloud and raindrops
                        drawOval(
                            color = Color(0xFF90CAF9),
                            topLeft = Offset(w * 0.22f, h * 0.12f),
                            size = Size(w * 0.56f, h * 0.24f)
                        )
                        for (i in 0..4) {
                            val dropY = h * (0.38f + ((phase + i * 0.2f) % 1f) * 0.38f)
                            drawCircle(
                                color = Color(0xFF1E88E5),
                                radius = 3.5.dp.toPx(),
                                center = Offset(w * (0.28f + i * 0.11f), dropY)
                            )
                        }
                        // Water surface
                        val path = Path().apply {
                            moveTo(0f, h * 0.80f)
                            lineTo(w, h * 0.80f)
                            lineTo(w, h)
                            lineTo(0f, h)
                            close()
                        }
                        drawPath(path, color = Color(0xFF4FC3F7).copy(alpha = 0.6f))
                    }
                }
            }
            Text(
                text = caption,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
