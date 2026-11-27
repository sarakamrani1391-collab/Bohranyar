package com.example.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.LabSimulationType
import com.example.data.model.VirtualLabExperiment
import com.example.data.seed.ScienceAndLabSeed
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.DarkOlive
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.LightSage
import com.example.ui.theme.OliveGreen
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun VirtualLabScreen(
    isOnline: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val experiments = ScienceAndLabSeed.virtualLabExperiments
    var activeIndex by rememberSaveable { mutableIntStateOf(0) }
    val activeExp = experiments.getOrElse(activeIndex) { experiments.first() }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "آزمایشگاه مجازی علوم و بحران",
                onBackClick = onBack,
                isOnline = isOnline
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 720.dp)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        experiments.forEachIndexed { idx, exp ->
                            FilterChip(
                                selected = activeIndex == idx,
                                onClick = { activeIndex = idx },
                                leadingIcon = {
                                    Icon(Icons.Filled.Science, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                label = { Text(exp.title) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("lab_tab_${exp.id}")
                            )
                        }
                    }
                }

                item {
                    ExperimentInteractiveCard(experiment = activeExp)
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatusBadge(
                                text = activeExp.gradeReference,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "توضیح علمی مشاهده",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activeExp.scientificExplanation,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                item {
                    Surface(
                        color = WarningAmberSoft,
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, WarningAmber.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.Info, contentDescription = null, tint = WarningAmber)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = activeExp.safetyDisclaimer,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun ExperimentInteractiveCard(experiment: VirtualLabExperiment) {
    val transition = rememberInfiniteTransition(label = "lab_anim")
    val animPhase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "lab_phase"
    )

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = experiment.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = experiment.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (experiment.type) {
                LabSimulationType.HEAT_TRANSFER -> HeatTransferLab(animPhase)
                LabSimulationType.COMBUSTION -> CombustionLab(animPhase)
                LabSimulationType.SEISMIC_WAVES -> SeismicWavesLab(animPhase)
                LabSimulationType.ELECTRIC_CIRCUIT -> ElectricCircuitLab(animPhase)
                LabSimulationType.WATER_FILTRATION -> WaterFiltrationLab(animPhase)
                LabSimulationType.STRUCTURE_STABILITY -> StructureStabilityLab(animPhase)
            }
        }
    }
}

@Composable
private fun HeatTransferLab(animPhase: Float) {
    var tempDiff by rememberSaveable { mutableFloatStateOf(70f) }
    var isMetalConductor by rememberSaveable { mutableStateOf(true) }
    val transferRate = if (isMetalConductor) (tempDiff * 0.9f).roundToInt() else (tempDiff * 0.18f).roundToInt()

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(color = LightSage.copy(alpha = 0.45f), cornerRadius = CornerRadius(14.dp.toPx()))

        // Left hot source
        drawCircle(
            color = EmergencyRed.copy(alpha = (0.35f + (tempDiff / 100f) * 0.65f).coerceIn(0.2f, 1f)),
            radius = 28.dp.toPx(),
            center = Offset(w * 0.16f, h * 0.55f)
        )
        // Connecting bar (metal vs wood)
        val barColor = if (isMetalConductor) Color(0xFFB0BEC5) else Color(0xFFA1887F)
        drawRoundRect(
            color = barColor,
            topLeft = Offset(w * 0.22f, h * 0.48f),
            size = Size(w * 0.56f, 18.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx())
        )
        // Heat pulse moving from hot to cold
        val pulseSpeed = if (isMetalConductor) 1f else 0.35f
        val progress = (animPhase * pulseSpeed) % 1f
        val pulseX = w * (0.24f + progress * 0.50f)
        drawCircle(
            color = WarningAmber,
            radius = 7.dp.toPx(),
            center = Offset(pulseX, h * 0.54f)
        )
        // Right cold block warming proportionally
        val coldHeatAlpha = (transferRate / 100f).coerceIn(0.1f, 0.9f)
        drawCircle(
            color = Color(0xFF29B6F6).copy(alpha = 1f - coldHeatAlpha * 0.7f),
            radius = 24.dp.toPx(),
            center = Offset(w * 0.84f, h * 0.55f)
        )
    }

    Surface(color = SafeGreenSoft, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = "جنس میله: ${if (isMetalConductor) "فلز مس (رسانا)" else "چوب خشک (عایق)"} • نرخ انتقال گرما: $transferRate٪",
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(10.dp)
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("استفاده از رسانا (فلز) به جای عایق چوبی", style = MaterialTheme.typography.bodySmall)
        Switch(checked = isMetalConductor, onCheckedChange = { isMetalConductor = it })
    }
    Text("اختلاف دما: ${tempDiff.roundToInt()} درجه سانتی‌گراد", style = MaterialTheme.typography.labelMedium)
    Slider(
        value = tempDiff,
        onValueChange = { tempDiff = it },
        valueRange = 10f..100f,
        modifier = Modifier.testTag("lab_heat_slider")
    )
}

@Composable
private fun CombustionLab(animPhase: Float) {
    var oxygenLevel by rememberSaveable { mutableFloatStateOf(85f) }
    val isComplete = oxygenLevel >= 65f

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(color = LightSage.copy(alpha = 0.45f), cornerRadius = CornerRadius(14.dp.toPx()))

        val flameColor = if (isComplete) Color(0xFF1E88E5) else WarningAmber
        val sway = sin(animPhase * 2f * PI.toFloat()) * 6f
        drawOval(
            color = flameColor,
            topLeft = Offset(w * 0.42f + sway, h * 0.34f),
            size = Size(w * 0.16f, h * 0.48f)
        )
        drawOval(
            color = Color.White.copy(alpha = 0.7f),
            topLeft = Offset(w * 0.46f + sway * 0.5f, h * 0.52f),
            size = Size(w * 0.08f, h * 0.24f)
        )
        if (!isComplete) {
            for (i in 0..4) {
                val p = (animPhase + i * 0.2f) % 1f
                drawCircle(
                    color = Color.DarkGray.copy(alpha = (1f - p) * 0.75f),
                    radius = (6f + p * 8f).dp.toPx(),
                    center = Offset(w * 0.5f + sin(p * 8f) * 14f, h * (0.32f - p * 0.25f))
                )
            }
        }
    }

    Surface(
        color = if (isComplete) SafeGreenSoft else WarningAmberSoft,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = if (isComplete) {
                "اکسیژن: ${oxygenLevel.roundToInt()}٪ • احتراق کامل (شعله آبی و تولید CO2)"
            } else {
                "اکسیژن: ${oxygenLevel.roundToInt()}٪ • احتراق ناقص (شعله زرد، دوده و خطر CO!)"
            },
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(10.dp)
        )
    }
    Text("درصد دسترسی به اکسیژن و تهویه", style = MaterialTheme.typography.labelMedium)
    Slider(
        value = oxygenLevel,
        onValueChange = { oxygenLevel = it },
        valueRange = 20f..100f,
        modifier = Modifier.testTag("lab_combustion_slider")
    )
}

@Composable
private fun SeismicWavesLab(animPhase: Float) {
    var distanceKm by rememberSaveable { mutableFloatStateOf(40f) }
    val pTimeSec = (distanceKm / 6.0f)
    val sTimeSec = (distanceKm / 3.5f)
    val warningGapSec = (sTimeSec - pTimeSec)

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(color = LightSage.copy(alpha = 0.45f), cornerRadius = CornerRadius(14.dp.toPx()))

        // Epicenter on left, building on right
        val origin = Offset(w * 0.12f, h * 0.65f)
        val bldgX = w * (0.35f + (distanceKm / 120f) * 0.52f)
        drawCircle(color = EmergencyRed, radius = 8.dp.toPx(), center = origin)
        drawRect(
            color = DarkOlive,
            topLeft = Offset(bldgX - 14.dp.toPx(), h * 0.25f),
            size = Size(28.dp.toPx(), h * 0.48f)
        )

        // P wave (faster) and S wave (slower)
        val travelMax = (bldgX - origin.x).coerceAtLeast(30f)
        val pX = origin.x + ((animPhase * 1.4f) % 1f) * travelMax
        val sX = origin.x + ((animPhase * 0.85f) % 1f) * travelMax
        drawLine(
            color = Color(0xFF1E88E5),
            start = Offset(pX, h * 0.40f),
            end = Offset(pX, h * 0.80f),
            strokeWidth = 4.dp.toPx()
        )
        drawLine(
            color = EmergencyRed,
            start = Offset(sX, h * 0.45f),
            end = Offset(sX, h * 0.85f),
            strokeWidth = 4.dp.toPx()
        )
    }

    Surface(color = SafeGreenSoft, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = "فاصله تا کانون: ${distanceKm.roundToInt()} کیلومتر • رسیدن موج P: ${"%.1f".format(pTimeSec)} ثانیه • رسیدن موج S: ${"%.1f".format(sTimeSec)} ثانیه • فاصله هشدار طلایی: ${"%.1f".format(warningGapSec)} ثانیه",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(10.dp)
        )
    }
    Text("فاصله ساختمان تا کانون زلزله (کیلومتر)", style = MaterialTheme.typography.labelMedium)
    Slider(
        value = distanceKm,
        onValueChange = { distanceKm = it },
        valueRange = 10f..120f,
        modifier = Modifier.testTag("lab_seismic_slider")
    )
}

@Composable
private fun ElectricCircuitLab(animPhase: Float) {
    // 0: Copper (Conductor), 1: Salt water (Medium conductor), 2: Plastic (Insulator), 3: Short circuit (Fuse blows)
    var mode by rememberSaveable { mutableIntStateOf(0) }
    val modes = listOf("سیم مسی (رسانا)", "آب نمک (رسانا)", "پلاستیک (نارسانا)", "اتصال کوتاه (عملکرد فیوز)")

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(color = LightSage.copy(alpha = 0.45f), cornerRadius = CornerRadius(14.dp.toPx()))

        val circuitOk = mode == 0 || mode == 1
        val fuseTripped = mode == 3
        drawRoundRect(
            color = if (fuseTripped) EmergencyRed else DarkOlive,
            topLeft = Offset(w * 0.14f, h * 0.22f),
            size = Size(w * 0.72f, h * 0.56f),
            cornerRadius = CornerRadius(12.dp.toPx()),
            style = Stroke(width = 3.dp.toPx())
        )
        // Lamp
        drawCircle(
            color = when {
                mode == 0 -> Color(0xFFFFD54F)
                mode == 1 -> Color(0xFFFFE082).copy(alpha = 0.65f)
                else -> Color.LightGray
            },
            radius = 18.dp.toPx(),
            center = Offset(w * 0.50f, h * 0.78f)
        )
        if (circuitOk) {
            val speed = if (mode == 0) 1f else 0.55f
            val dotX = w * (0.14f + ((animPhase * speed) % 1f) * 0.72f)
            drawCircle(color = SafeGreen, radius = 6.dp.toPx(), center = Offset(dotX, h * 0.22f))
        } else if (fuseTripped) {
            // Broken fuse spark indicator
            drawLine(
                color = EmergencyRed,
                start = Offset(w * 0.45f, h * 0.16f),
                end = Offset(w * 0.55f, h * 0.28f),
                strokeWidth = 4.dp.toPx()
            )
        }
    }

    Surface(
        color = if (mode == 3) WarningAmberSoft else SafeGreenSoft,
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = when (mode) {
                0 -> "لامپ روشن کامل: مس رسانای عالی است و جریان به راحتی عبور می‌کند."
                1 -> "لامپ نیمه‌روشن: آب دارای املاح رسانا است؛ به همین دلیل رطوبت کنار برق خطرناک است."
                2 -> "لامپ خاموش: پلاستیک نارسانا است و برای عایق‌کاری سیم‌ها استفاده می‌شود."
                else -> "هشدار اتصال کوتاه! جریان ناگهانی بالا رفت و فیوز مدار را برای جلوگیری از حریق قطع کرد."
            },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(10.dp)
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        modes.forEachIndexed { idx, label ->
            FilterChip(
                selected = mode == idx,
                onClick = { mode = idx },
                label = { Text(label) },
                modifier = Modifier.minimumInteractiveComponentSize()
            )
        }
    }
}

@Composable
private fun WaterFiltrationLab(animPhase: Float) {
    var gravelEnabled by rememberSaveable { mutableStateOf(true) }
    var sandEnabled by rememberSaveable { mutableStateOf(true) }
    var charcoalEnabled by rememberSaveable { mutableStateOf(true) }
    val clarityPercent = (15 + (if (gravelEnabled) 25 else 0) + (if (sandEnabled) 30 else 0) + (if (charcoalEnabled) 30 else 0))

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(color = LightSage.copy(alpha = 0.45f), cornerRadius = CornerRadius(14.dp.toPx()))

        // Filter column
        val colLeft = w * 0.30f
        val colW = w * 0.40f
        if (gravelEnabled) {
            drawRect(color = Color(0xFFBCAAA4), topLeft = Offset(colLeft, h * 0.16f), size = Size(colW, h * 0.18f))
        }
        if (sandEnabled) {
            drawRect(color = Color(0xFFFFE082), topLeft = Offset(colLeft, h * 0.36f), size = Size(colW, h * 0.18f))
        }
        if (charcoalEnabled) {
            drawRect(color = Color(0xFF455A64), topLeft = Offset(colLeft, h * 0.56f), size = Size(colW, h * 0.18f))
        }
        // Water droplets passing
        val dropY = h * (0.12f + animPhase * 0.72f)
        drawCircle(
            color = if (clarityPercent > 70) Color(0xFF29B6F6) else Color(0xFF8D6E63),
            radius = 6.dp.toPx(),
            center = Offset(w * 0.50f, dropY)
        )
        // Output pool
        drawRect(
            color = if (clarityPercent > 70) Color(0xFF4FC3F7) else Color(0xFFA1887F),
            topLeft = Offset(colLeft, h * 0.80f),
            size = Size(colW, h * 0.14f)
        )
    }

    Surface(color = SafeGreenSoft, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = "شفافیت فیزیکی خروجی: $clarityPercent٪ (یادآوری علمی: برای پاک‌سازی میکروب‌ها، جوشاندن ضروری است)",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(10.dp)
        )
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(selected = gravelEnabled, onClick = { gravelEnabled = !gravelEnabled }, label = { Text("شن درشت") })
        FilterChip(selected = sandEnabled, onClick = { sandEnabled = !sandEnabled }, label = { Text("ماسه ریز") })
        FilterChip(selected = charcoalEnabled, onClick = { charcoalEnabled = !charcoalEnabled }, label = { Text("زغال فعال") })
    }
}

@Composable
private fun StructureStabilityLab(animPhase: Float) {
    var shakeIntensity by rememberSaveable { mutableFloatStateOf(55f) }
    var hasCrossBracing by rememberSaveable { mutableStateOf(true) }

    val swayFactor = if (hasCrossBracing) 0.25f else 1.0f
    val lateralShift = sin(animPhase * 4f * PI.toFloat()) * (shakeIntensity / 100f) * 28f * swayFactor

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
    ) {
        val w = size.width
        val h = size.height
        drawRoundRect(color = LightSage.copy(alpha = 0.45f), cornerRadius = CornerRadius(14.dp.toPx()))

        val baseLeft = w * 0.36f
        val baseRight = w * 0.64f
        val baseY = h * 0.85f
        val topY = h * 0.20f
        val topLeft = baseLeft + lateralShift
        val topRight = baseRight + lateralShift

        // Ground
        drawLine(color = DarkOlive, start = Offset(w * 0.15f, baseY), end = Offset(w * 0.85f, baseY), strokeWidth = 4.dp.toPx())
        // Frame
        val framePath = Path().apply {
            moveTo(baseLeft, baseY)
            lineTo(topLeft, topY)
            lineTo(topRight, topY)
            lineTo(baseRight, baseY)
        }
        drawPath(framePath, color = if (hasCrossBracing) SafeGreen else EmergencyRed, style = Stroke(width = 4.dp.toPx()))
        if (hasCrossBracing) {
            drawLine(color = OliveGreen, start = Offset(baseLeft, baseY), end = Offset(topRight, topY), strokeWidth = 2.5.dp.toPx())
            drawLine(color = OliveGreen, start = Offset(baseRight, baseY), end = Offset(topLeft, topY), strokeWidth = 2.5.dp.toPx())
        }
    }

    Surface(color = if (hasCrossBracing) SafeGreenSoft else WarningAmberSoft, shape = RoundedCornerShape(12.dp)) {
        Text(
            text = if (hasCrossBracing) {
                "با مهاربند مثلثی (متقاطع): جابه‌جایی جانبی سازه در برابر ارتعاش بسیار کم و پایدار است."
            } else {
                "بدون مهاربند: تغییر شکل جانبی شدید در طبقات بالا و خطر ناپایداری."
            },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(10.dp)
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("فعال‌سازی مهاربند متقاطع سازه", style = MaterialTheme.typography.bodySmall)
        Switch(checked = hasCrossBracing, onCheckedChange = { hasCrossBracing = it })
    }
    Text("شدت لرزش افقی زمین: ${shakeIntensity.roundToInt()}٪", style = MaterialTheme.typography.labelMedium)
    Slider(value = shakeIntensity, onValueChange = { shakeIntensity = it }, valueRange = 15f..100f)
}
