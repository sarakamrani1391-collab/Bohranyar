package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Toys
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.MavaSettingsEntity
import com.example.data.local.entity.OfflineMediaEntity
import com.example.data.model.MavaCharacterId
import com.example.data.model.MavaEmotionType
import com.example.ui.components.LessonVideoPlayerCard
import com.example.ui.components.MavaCharacterAvatar
import com.example.ui.theme.MavaCreamYellow
import com.example.ui.theme.MavaInk
import com.example.ui.theme.MavaInkSoft
import com.example.ui.theme.MavaLavender
import com.example.ui.theme.MavaMint
import com.example.ui.theme.MavaPeach
import com.example.ui.theme.MavaPink
import com.example.ui.theme.MavaSkyBlue
import com.example.ui.viewmodel.MavaUiState
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

private enum class MavaSection(val titleFa: String) {
    HOME("مأوا"),
    GAMES("بازی‌های کوچولوها"),
    STORIES("قصه‌خانه مأوا"),
    CALM("لحظه‌های آرامش"),
    FEELINGS("دنیای احساسات"),
    CARTOONS("کارتون‌های مأوا"),
    DISCOVERY("دنیای کشف و یادگیری"),
    PARENT_CONTROLS("تنظیمات والدین")
}

private enum class AdultGateAction {
    OPEN_PARENT_SETTINGS,
    EXIT_MAVA
}

private val MavaPastelColorScheme = lightColorScheme(
    primary = Color(0xFF7B6C9E),
    onPrimary = Color.White,
    primaryContainer = MavaLavender,
    onPrimaryContainer = MavaInk,
    secondary = Color(0xFF5D9682),
    onSecondary = Color.White,
    secondaryContainer = MavaMint,
    onSecondaryContainer = MavaInk,
    tertiary = Color(0xFFC87D8C),
    onTertiary = Color.White,
    tertiaryContainer = MavaPink,
    onTertiaryContainer = MavaInk,
    background = Color(0xFFFFFBF7),
    onBackground = MavaInk,
    surface = Color(0xFFFFFFFF),
    onSurface = MavaInk,
    surfaceVariant = Color(0xFFF6F1FA),
    onSurfaceVariant = MavaInkSoft,
    outline = Color(0xFFE0D7EC)
)

@Composable
fun MavaRootScreen(
    uiState: MavaUiState,
    onUpdateSettings: (MavaSettingsEntity) -> Unit,
    onPlayTone: (Double) -> Unit,
    onPlayHappyTone: () -> Unit,
    onPlayLullabyStep: (Int) -> Unit,
    onImportLocalCartoon: (String, Uri) -> Unit,
    onRemoveOfflineCartoon: (OfflineMediaEntity) -> Unit,
    onDismissNote: () -> Unit,
    onExitMava: () -> Unit
) {
    var activeSection by rememberSaveable { mutableStateOf(MavaSection.HOME) }
    var gateAction by remember { mutableStateOf<AdultGateAction?>(null) }
    var elapsedSeconds by rememberSaveable { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds++
        }
    }

    val limitSeconds = uiState.settings.dailyLimitMinutes.coerceAtLeast(5) * 60
    val screenTimeExceeded = elapsedSeconds >= limitSeconds

    // Protect hardware back button so toddlers cannot accidentally leave Ma'va without the adult gate
    BackHandler {
        if (activeSection != MavaSection.HOME) {
            activeSection = MavaSection.HOME
        } else {
            gateAction = AdultGateAction.EXIT_MAVA
        }
    }

    MaterialTheme(
        colorScheme = MavaPastelColorScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes
    ) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                MavaTopBar(
                    title = if (activeSection == MavaSection.HOME) "مأوا • دنیای کوچک آرامش و لبخند" else activeSection.titleFa,
                    showBack = activeSection != MavaSection.HOME,
                    soundEnabled = uiState.settings.soundEnabled,
                    onBack = { activeSection = MavaSection.HOME },
                    onToggleSound = {
                        onUpdateSettings(uiState.settings.copy(soundEnabled = !uiState.settings.soundEnabled))
                    },
                    onOpenParentGate = { gateAction = AdultGateAction.OPEN_PARENT_SETTINGS },
                    onRequestExit = { gateAction = AdultGateAction.EXIT_MAVA }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxSize()
                ) {
                    if (screenTimeExceeded && activeSection != MavaSection.PARENT_CONTROLS) {
                        Surface(
                            color = MavaCreamYellow,
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.Bedtime, contentDescription = null, tint = MavaInk)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "پوفی می‌گوید زمان تعیین‌شده تماشا به پایان رسیده؛ وقت استراحت چشم‌ها و بغل مامان و باباست!",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MavaInk
                                )
                            }
                        }
                    }

                    uiState.statusNote?.let { note ->
                        Surface(
                            color = MavaMint,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(note, style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                TextButton(onClick = onDismissNote) { Text("باشه") }
                            }
                        }
                    }

                    when (activeSection) {
                        MavaSection.HOME -> MavaHomeContent(
                            settings = uiState.settings,
                            onSelectSection = {
                                onPlayTone(523.25)
                                activeSection = it
                            },
                            onCharacterTap = { ch ->
                                when (ch) {
                                    MavaCharacterId.PUFI -> onPlayTone(587.33)
                                    MavaCharacterId.TOTO -> onPlayTone(392.00)
                                    MavaCharacterId.BOMBO -> onPlayTone(329.63)
                                    MavaCharacterId.FOFO -> onPlayTone(659.25)
                                }
                            }
                        )
                        MavaSection.GAMES -> MavaGamesContent(onPlayHappyTone = onPlayHappyTone, onPlayTone = onPlayTone)
                        MavaSection.STORIES -> MavaStoriesContent(uiState = uiState, onPlayTone = onPlayTone)
                        MavaSection.CALM -> MavaCalmContent(onPlayLullabyStep = onPlayLullabyStep)
                        MavaSection.FEELINGS -> MavaFeelingsContent(onPlayTone = onPlayTone)
                        MavaSection.CARTOONS -> MavaCartoonsContent(
                            uiState = uiState,
                            onPlayTone = onPlayTone,
                            onImportLocalCartoon = onImportLocalCartoon,
                            onRemoveOfflineCartoon = onRemoveOfflineCartoon
                        )
                        MavaSection.DISCOVERY -> MavaDiscoveryContent(uiState = uiState, onPlayTone = onPlayTone)
                        MavaSection.PARENT_CONTROLS -> MavaParentControlsContent(
                            settings = uiState.settings,
                            elapsedMinutes = elapsedSeconds / 60,
                            onUpdateSettings = onUpdateSettings,
                            onResetTimer = { elapsedSeconds = 0 },
                            onExitMava = onExitMava
                        )
                    }
                }
            }
        }
    }

    gateAction?.let { action ->
        AdultMathGateDialog(
            title = if (action == AdultGateAction.EXIT_MAVA) "خروج از بخش کودکانه مأوا" else "ورود به تنظیمات والدین",
            onDismiss = { gateAction = null },
            onVerified = {
                gateAction = null
                when (action) {
                    AdultGateAction.OPEN_PARENT_SETTINGS -> activeSection = MavaSection.PARENT_CONTROLS
                    AdultGateAction.EXIT_MAVA -> onExitMava()
                }
            }
        )
    }
}

@Composable
private fun MavaTopBar(
    title: String,
    showBack: Boolean,
    soundEnabled: Boolean,
    onBack: () -> Unit,
    onToggleSound: () -> Unit,
    onOpenParentGate: () -> Unit,
    onRequestExit: () -> Unit
) {
    Surface(
        color = MavaLavender.copy(alpha = 0.7f),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBack) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("mava_back_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "بازگشت", tint = MavaInk)
                    }
                } else {
                    IconButton(
                        onClick = onRequestExit,
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("mava_exit_button")
                    ) {
                        Icon(Icons.Filled.Lock, contentDescription = "خروج والدین از مأوا", tint = MavaInk)
                    }
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MavaInk
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.minimumInteractiveComponentSize()
                ) {
                    Icon(
                        imageVector = if (soundEnabled) Icons.Filled.VolumeUp else Icons.Filled.VolumeOff,
                        contentDescription = if (soundEnabled) "قطع صدا" else "وصل صدا",
                        tint = MavaInk
                    )
                }
                IconButton(
                    onClick = onOpenParentGate,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("mava_parent_settings_button")
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "تنظیمات والدین", tint = MavaInk)
                }
            }
        }
    }
}

@Composable
private fun MavaHomeContent(
    settings: MavaSettingsEntity,
    onSelectSection: (MavaSection) -> Unit,
    onCharacterTap: (MavaCharacterId) -> Unit
) {
    var activeCharacter by rememberSaveable { mutableStateOf(MavaCharacterId.PUFI) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Gentle friendly welcome card with 4 original characters
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MavaSkyBlue.copy(alpha = 0.65f)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MavaCharacterAvatar(
                        character = activeCharacter,
                        emotion = MavaEmotionType.HAPPY,
                        size = 115.dp
                    )
                    Text(
                        text = "سلام کوچولو! من ${activeCharacter.displayNameFa} (${activeCharacter.speciesFa}) هستم",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MavaInk,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = activeCharacter.traitFa,
                        style = MaterialTheme.typography.bodySmall,
                        color = MavaInkSoft
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        MavaCharacterId.entries.forEach { ch ->
                            Surface(
                                onClick = {
                                    activeCharacter = ch
                                    onCharacterTap(ch)
                                },
                                color = if (activeCharacter == ch) MavaCreamYellow else Color.White.copy(alpha = 0.8f),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(
                                    1.5.dp,
                                    if (activeCharacter == ch) Color(0xFF7B6C9E) else Color.Transparent
                                ),
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("mava_char_${ch.name}")
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    MavaCharacterAvatar(character = ch, size = 44.dp)
                                    Text(
                                        text = ch.displayNameFa,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MavaInk
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Picture-first giant pastel tiles
        val tiles = buildList {
            if (settings.gamesEnabled) add(MavaTileSpec(MavaSection.GAMES, "بازی‌های کوچولوها", "شکل، رنگ و نقاشی", Icons.Filled.Toys, MavaPink))
            if (settings.storiesEnabled) add(MavaTileSpec(MavaSection.STORIES, "قصه‌خانه مأوا", "قصه‌های مهربان", Icons.Filled.AutoStories, MavaCreamYellow))
            if (settings.calmEnabled) add(MavaTileSpec(MavaSection.CALM, "لحظه‌های آرامش", "نفس آرام و لالایی", Icons.Filled.Bedtime, MavaMint))
            if (settings.emotionsEnabled) add(MavaTileSpec(MavaSection.FEELINGS, "دنیای احساسات", "شادی، ترس، آرامش", Icons.Filled.Favorite, MavaLavender))
            if (settings.cartoonsEnabled) add(MavaTileSpec(MavaSection.CARTOONS, "کارتون‌های مأوا", "تماشای امن", Icons.Filled.PlayCircle, MavaPeach))
            if (settings.discoveryEnabled) add(MavaTileSpec(MavaSection.DISCOVERY, "دنیای کشف", "حیوانات و طبیعت", Icons.Filled.Explore, MavaSkyBlue))
        }

        items(tiles.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                pair.forEach { spec ->
                    MavaBigTile(spec = spec, onClick = { onSelectSection(spec.section) }, modifier = Modifier.weight(1f))
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }

        item { Spacer(modifier = Modifier.height(12.dp)) }
    }
}

private data class MavaTileSpec(
    val section: MavaSection,
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val bgColor: Color
)

@Composable
private fun MavaBigTile(
    spec: MavaTileSpec,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = spec.bgColor),
        shape = RoundedCornerShape(26.dp),
        modifier = modifier
            .height(142.dp)
            .testTag("mava_tile_${spec.section.name}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.78f),
                shape = CircleShape,
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = spec.icon,
                        contentDescription = spec.title,
                        tint = MavaInk,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = spec.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MavaInk,
                textAlign = TextAlign.Center
            )
            Text(
                text = spec.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MavaInkSoft,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun MavaGamesContent(
    onPlayHappyTone: () -> Unit,
    onPlayTone: (Double) -> Unit
) {
    var selectedGame by rememberSaveable { mutableIntStateOf(0) }
    val gameTitles = listOf("شکل‌ها", "رنگ‌ها", "حیوان کجاست؟", "پازل ۴تکه", "رنگ‌آمیزی")

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                gameTitles.forEachIndexed { idx, title ->
                    FilterChip(
                        selected = selectedGame == idx,
                        onClick = {
                            selectedGame = idx
                            onPlayTone(523.25)
                        },
                        label = { Text(title, style = MaterialTheme.typography.labelSmall) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }
        }

        item {
            when (selectedGame) {
                0 -> MavaShapeMatchGame(onPlayHappyTone, onPlayTone)
                1 -> MavaColorBubbleGame(onPlayTone)
                2 -> MavaFindAnimalGame(onPlayHappyTone, onPlayTone)
                3 -> MavaSimplePuzzleGame(onPlayHappyTone, onPlayTone)
                else -> MavaTapColoringGame(onPlayTone)
            }
        }
    }
}

@Composable
private fun MavaShapeMatchGame(onPlayHappyTone: () -> Unit, onPlayTone: (Double) -> Unit) {
    val shapes = listOf("دایره", "مربع", "ستاره")
    var targetIdx by rememberSaveable { mutableIntStateOf(0) }
    var feedback by rememberSaveable { mutableStateOf("روی شکل درست بزن کوچولو!") }

    Card(colors = CardDefaults.cardColors(containerColor = MavaPink.copy(alpha = 0.6f)), shape = RoundedCornerShape(26.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "شکل «${shapes[targetIdx]}» کدام است؟",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MavaInk
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                shapes.forEachIndexed { idx, label ->
                    Surface(
                        onClick = {
                            if (idx == targetIdx) {
                                onPlayHappyTone()
                                feedback = "آفرین! خیلی قشنگ پیدا کردی!"
                                targetIdx = (targetIdx + 1) % shapes.size
                            } else {
                                onPlayTone(392.0)
                                feedback = "بیا دوباره با هم امتحان کنیم!"
                            }
                        },
                        color = Color.White,
                        shape = RoundedCornerShape(22.dp),
                        modifier = Modifier.size(92.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Canvas(modifier = Modifier.size(52.dp)) {
                                when (idx) {
                                    0 -> drawCircle(color = Color(0xFFEC8FA3))
                                    1 -> drawRoundRect(color = Color(0xFF7CB69D), cornerRadius = CornerRadius(8.dp.toPx()))
                                    else -> {
                                        val path = Path().apply {
                                            moveTo(size.width * 0.5f, 0f)
                                            lineTo(size.width * 0.65f, size.height * 0.35f)
                                            lineTo(size.width, size.height * 0.40f)
                                            lineTo(size.width * 0.72f, size.height * 0.65f)
                                            lineTo(size.width * 0.80f, size.height)
                                            lineTo(size.width * 0.5f, size.height * 0.80f)
                                            lineTo(size.width * 0.20f, size.height)
                                            lineTo(size.width * 0.28f, size.height * 0.65f)
                                            lineTo(0f, size.height * 0.40f)
                                            lineTo(size.width * 0.35f, size.height * 0.35f)
                                            close()
                                        }
                                        drawPath(path, color = Color(0xFFF3BA5A))
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Text(text = feedback, style = MaterialTheme.typography.titleSmall, color = MavaInk)
        }
    }
}

@Composable
private fun MavaColorBubbleGame(onPlayTone: (Double) -> Unit) {
    val bubbles = listOf(
        Triple("صورتی مهربان", Color(0xFFF29BB2), 523.25),
        Triple("سبز نعنایی", Color(0xFF83C5A8), 587.33),
        Triple("آبی آسمانی", Color(0xFF8CBCE6), 659.25),
        Triple("زرد آفتابی", Color(0xFFF6C768), 783.99)
    )
    var lastColor by rememberSaveable { mutableStateOf("روی حباب‌های رنگی بزن تا صدا بدهند!") }

    Card(colors = CardDefaults.cardColors(containerColor = MavaSkyBlue.copy(alpha = 0.6f)), shape = RoundedCornerShape(26.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(text = lastColor, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                bubbles.forEach { (name, color, freq) ->
                    Surface(
                        onClick = {
                            onPlayTone(freq)
                            lastColor = "چه رنگ قشنگی: $name!"
                        },
                        color = color,
                        shape = CircleShape,
                        modifier = Modifier.size(68.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Star, contentDescription = name, tint = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MavaFindAnimalGame(onPlayHappyTone: () -> Unit, onPlayTone: (Double) -> Unit) {
    val animals = MavaCharacterId.entries
    var targetIdx by rememberSaveable { mutableIntStateOf(0) }
    var message by rememberSaveable { mutableStateOf("دوست کوچولو را پیدا کن!") }
    val target = animals[targetIdx]

    Card(colors = CardDefaults.cardColors(containerColor = MavaMint.copy(alpha = 0.6f)), shape = RoundedCornerShape(26.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "«${target.displayNameFa} (${target.speciesFa})» کجاست؟",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                animals.forEachIndexed { idx, ch ->
                    Surface(
                        onClick = {
                            if (idx == targetIdx) {
                                onPlayHappyTone()
                                message = "هورا! ${ch.displayNameFa} اینجا بود!"
                                targetIdx = (targetIdx + 1) % animals.size
                            } else {
                                onPlayTone(392.0)
                                message = "این ${ch.displayNameFa} است! بیا دنبال ${target.displayNameFa} بگردیم."
                            }
                        },
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        MavaCharacterAvatar(character = ch, size = 72.dp, modifier = Modifier.padding(4.dp))
                    }
                }
            }
            Text(text = message, style = MaterialTheme.typography.titleSmall, color = MavaInk)
        }
    }
}

@Composable
private fun MavaSimplePuzzleGame(onPlayHappyTone: () -> Unit, onPlayTone: (Double) -> Unit) {
    // 4-piece color alignment puzzle: tap pieces to rotate/cycle until all match Mava rainbow!
    val colors = listOf(MavaPink, MavaMint, MavaCreamYellow, MavaSkyBlue)
    val states = remember { mutableStateListOf(1, 2, 3, 0) }
    val isSolved = states.indices.all { states[it] == it }

    Card(colors = CardDefaults.cardColors(containerColor = MavaLavender.copy(alpha = 0.65f)), shape = RoundedCornerShape(26.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isSolved) "آفرین! پازل رنگین‌کمان کامل شد!" else "روی تکه‌ها بزن تا رنگ هر خانه سر جایش بنشیند",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (i in 0..3) {
                    Surface(
                        onClick = {
                            states[i] = (states[i] + 1) % 4
                            if (states.indices.all { states[it] == it }) onPlayHappyTone() else onPlayTone(440.0 + i * 50.0)
                        },
                        color = colors[states[i]],
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(3.dp, if (states[i] == i) Color(0xFF5D9682) else Color.White),
                        modifier = Modifier
                            .weight(1f)
                            .height(82.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("${i + 1}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MavaTapColoringGame(onPlayTone: (Double) -> Unit) {
    val palette = listOf(Color(0xFFF3A7BA), Color(0xFF93D1B6), Color(0xFF96C5EC), Color(0xFFF7D47A))
    var activeBrush by remember { mutableIntStateOf(0) }
    val partColors = remember { mutableStateListOf(Color.White, Color.White, Color.White, Color.White) }

    Card(colors = CardDefaults.cardColors(containerColor = MavaCreamYellow.copy(alpha = 0.6f)), shape = RoundedCornerShape(26.dp)) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("رنگ را انتخاب کن و روی گلبرگ‌ها بزن!", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                palette.forEachIndexed { idx, col ->
                    Surface(
                        onClick = {
                            activeBrush = idx
                            onPlayTone(523.25 + idx * 60)
                        },
                        color = col,
                        shape = CircleShape,
                        border = BorderStroke(if (activeBrush == idx) 3.dp else 1.dp, MavaInk),
                        modifier = Modifier.size(48.dp)
                    ) {}
                }
            }
            // Tap-to-color flower parts
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                for (i in 0..3) {
                    Surface(
                        onClick = {
                            partColors[i] = palette[activeBrush]
                            onPlayTone(659.25)
                        },
                        color = partColors[i],
                        shape = RoundedCornerShape(24.dp),
                        border = BorderStroke(2.dp, MavaInkSoft),
                        modifier = Modifier.size(64.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Filled.Palette, contentDescription = "بخش ${i + 1}", tint = MavaInkSoft)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MavaStoriesContent(uiState: MavaUiState, onPlayTone: (Double) -> Unit) {
    var storyIndex by rememberSaveable { mutableIntStateOf(0) }
    var pageIndex by rememberSaveable { mutableIntStateOf(0) }
    val story = uiState.stories[storyIndex]
    val page = story.pages[pageIndex.coerceIn(0, story.pages.lastIndex)]

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.stories.forEachIndexed { idx, st ->
                    FilterChip(
                        selected = storyIndex == idx,
                        onClick = {
                            storyIndex = idx
                            pageIndex = 0
                            onPlayTone(523.25)
                        },
                        label = { Text(st.title) },
                        modifier = Modifier.minimumInteractiveComponentSize()
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MavaCreamYellow.copy(alpha = 0.65f)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "${story.title} (${story.themeFa})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MavaInk
                    )
                    MavaCharacterAvatar(
                        character = page.character,
                        emotion = page.emotion,
                        size = 140.dp
                    )
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = page.simpleLine,
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            color = MavaInk,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (pageIndex > 0) {
                                    pageIndex--
                                    onPlayTone(440.0)
                                }
                            },
                            enabled = pageIndex > 0,
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text("صفحه قبل")
                        }
                        Text("صفحه ${pageIndex + 1} از ${story.pages.size}", style = MaterialTheme.typography.labelLarge)
                        Button(
                            onClick = {
                                onPlayTone(587.33)
                                pageIndex = if (pageIndex + 1 < story.pages.size) pageIndex + 1 else 0
                            },
                            modifier = Modifier.height(48.dp)
                        ) {
                            Text(if (pageIndex + 1 < story.pages.size) "صفحه بعد" else "از اول")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MavaCalmContent(onPlayLullabyStep: (Int) -> Unit) {
    val transition = rememberInfiniteTransition(label = "mava_breath")
    val breathScale by transition.animateFloat(
        initialValue = 0.72f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breath"
    )
    var lullabyStep by remember { mutableIntStateOf(0) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MavaMint.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("نفس آرام با توتو لاک‌پشت", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Box(
                        modifier = Modifier.size(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = MavaSkyBlue.copy(alpha = 0.7f),
                                radius = (size.minDimension * 0.45f) * breathScale
                            )
                        }
                        MavaCharacterAvatar(character = MavaCharacterId.TOTO, emotion = MavaEmotionType.CALM, size = 100.dp)
                    }
                    Text(
                        text = if (breathScale > 0.86f) "دم آرام... سینه را پر از هوای خوب کن" else "بازدم آرام... مثل نسیم ملایم",
                        style = MaterialTheme.typography.titleMedium,
                        color = MavaInk
                    )
                    Button(
                        onClick = {
                            onPlayLullabyStep(lullabyStep)
                            lullabyStep = (lullabyStep + 1) % 6
                        },
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(54.dp)
                    ) {
                        Icon(Icons.Filled.MusicNote, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("نوای لالایی آرام‌بخش")
                    }
                }
            }
        }
    }
}

@Composable
private fun MavaFeelingsContent(onPlayTone: (Double) -> Unit) {
    var selectedEmotion by rememberSaveable { mutableStateOf(MavaEmotionType.HAPPY) }

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MavaLavender.copy(alpha = 0.75f)),
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MavaCharacterAvatar(
                        character = MavaCharacterId.BOMBO,
                        emotion = selectedEmotion,
                        size = 135.dp
                    )
                    Text(
                        text = "احساس: ${selectedEmotion.titleFa}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = selectedEmotion.simpleMessageFa,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        items(MavaEmotionType.entries.chunked(2)) { pair ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { emo ->
                    val bg = when (emo) {
                        MavaEmotionType.HAPPY -> MavaCreamYellow
                        MavaEmotionType.SAD -> MavaSkyBlue
                        MavaEmotionType.SCARED -> MavaPink
                        MavaEmotionType.CALM -> MavaMint
                    }
                    Surface(
                        onClick = {
                            selectedEmotion = emo
                            onPlayTone(523.25)
                        },
                        color = bg,
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(2.dp, if (selectedEmotion == emo) MavaInk else Color.Transparent),
                        modifier = Modifier
                            .weight(1f)
                            .height(84.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(emo.titleFa, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MavaCartoonsContent(
    uiState: MavaUiState,
    onPlayTone: (Double) -> Unit,
    onImportLocalCartoon: (String, Uri) -> Unit,
    onRemoveOfflineCartoon: (OfflineMediaEntity) -> Unit
) {
    var selectedEpIdx by rememberSaveable { mutableIntStateOf(0) }
    var frameIdx by rememberSaveable { mutableIntStateOf(0) }
    val episode = uiState.cartoons[selectedEpIdx.coerceIn(0, uiState.cartoons.lastIndex)]
    val frame = episode.animatedFrames[frameIdx.coerceIn(0, episode.animatedFrames.lastIndex)]
    val mediaAsset = uiState.cartoonMediaMap[episode.mediaId]

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                uiState.cartoons.forEachIndexed { idx, ep ->
                    FilterChip(
                        selected = idx == selectedEpIdx,
                        onClick = {
                            selectedEpIdx = idx
                            frameIdx = 0
                            onPlayTone(523.25)
                        },
                        label = { Text(ep.title) },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("mava_cartoon_chip_${ep.id}")
                    )
                }
            }
        }

        if (episode.bundledVideoResId != null) {
            item {
                LessonVideoPlayerCard(
                    mediaAsset = mediaAsset,
                    isOnline = uiState.isOnline,
                    bundledVideoResId = episode.bundledVideoResId,
                    onDownloadClick = {},
                    onImportLocalVideo = { uri -> onImportLocalCartoon(episode.mediaId, uri) },
                    onDeleteOfflineVideo = onRemoveOfflineCartoon,
                    modifier = Modifier.testTag("mava_cartoon_player_card")
                )
            }
        }

        // Built-in animated character mini-episode / companion story card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MavaPeach.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(episode.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("پیام مثبت: ${episode.positiveMessage} • ${episode.durationLabel}", style = MaterialTheme.typography.bodySmall)
                    MavaCharacterAvatar(character = frame.character, emotion = frame.emotion, size = 128.dp)
                    Surface(color = Color.White, shape = RoundedCornerShape(16.dp)) {
                        Text(
                            text = frame.simpleLine,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                    Button(
                        onClick = {
                            onPlayTone(587.33)
                            frameIdx = (frameIdx + 1) % episode.animatedFrames.size
                        },
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Text("صحنه بعدی کارتون (${frameIdx + 1}/${episode.animatedFrames.size})")
                    }
                }
            }
        }

        if (episode.bundledVideoResId == null) {
            item {
                LessonVideoPlayerCard(
                    mediaAsset = mediaAsset,
                    isOnline = uiState.isOnline,
                    onDownloadClick = {},
                    onImportLocalVideo = { uri -> onImportLocalCartoon(episode.mediaId, uri) },
                    onDeleteOfflineVideo = onRemoveOfflineCartoon,
                    modifier = Modifier.testTag("mava_cartoon_player_card")
                )
            }
        }
    }
}

@Composable
private fun MavaDiscoveryContent(uiState: MavaUiState, onPlayTone: (Double) -> Unit) {
    var activeItemId by rememberSaveable { mutableStateOf(uiState.discoveryItems.first().id) }
    val activeItem = uiState.discoveryItems.firstOrNull { it.id == activeItemId } ?: uiState.discoveryItems.first()

    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MavaSkyBlue.copy(alpha = 0.75f)),
                shape = RoundedCornerShape(26.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MavaCharacterAvatar(character = activeItem.character, size = 88.dp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(activeItem.groupFa, style = MaterialTheme.typography.labelMedium, color = MavaInkSoft)
                        Text(activeItem.titleFa, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Text(activeItem.detailFa, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        items(uiState.discoveryItems.chunked(2)) { pair ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                pair.forEach { item ->
                    Card(
                        onClick = {
                            activeItemId = item.id
                            onPlayTone(item.toneHz)
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = if (item.id == activeItemId) MavaCreamYellow else Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, MavaLavender),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            MavaCharacterAvatar(character = item.character, size = 56.dp)
                            Text(item.titleFa, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text(item.groupFa, style = MaterialTheme.typography.labelSmall, color = MavaInkSoft)
                        }
                    }
                }
                if (pair.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun MavaParentControlsContent(
    settings: MavaSettingsEntity,
    elapsedMinutes: Int,
    onUpdateSettings: (MavaSettingsEntity) -> Unit,
    onResetTimer: () -> Unit,
    onExitMava: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("تنظیمات والدین مأوا", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("گروه سنی کودک:", style = MaterialTheme.typography.labelLarge)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("1_2" to "۱ تا ۲ سال", "1_3" to "۱ تا ۳ سال", "3_5" to "۳ تا ۵ سال").forEach { (key, label) ->
                            FilterChip(
                                selected = settings.ageGroup == key,
                                onClick = { onUpdateSettings(settings.copy(ageGroup = key)) },
                                label = { Text(label) }
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("صدای ملایم و آواهای تشویقی", style = MaterialTheme.typography.bodyMedium)
                        Switch(
                            checked = settings.soundEnabled,
                            onCheckedChange = { onUpdateSettings(settings.copy(soundEnabled = it)) }
                        )
                    }

                    Text(
                        text = "یادآور زمان استفاده: ${settings.dailyLimitMinutes} دقیقه (زمان فعلی جلسه: $elapsedMinutes دقیقه)",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Slider(
                        value = settings.dailyLimitMinutes.toFloat(),
                        onValueChange = { onUpdateSettings(settings.copy(dailyLimitMinutes = it.roundToInt())) },
                        valueRange = 5f..45f
                    )
                    OutlinedButton(onClick = onResetTimer) {
                        Text("بازنشانی تایمر جلسه")
                    }
                }
            }
        }

        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(20.dp)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("بخش‌های فعال در صفحه کودک", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    ParentSectionToggle("بازی‌های کوچولوها", settings.gamesEnabled) {
                        onUpdateSettings(settings.copy(gamesEnabled = it))
                    }
                    ParentSectionToggle("قصه‌خانه مأوا", settings.storiesEnabled) {
                        onUpdateSettings(settings.copy(storiesEnabled = it))
                    }
                    ParentSectionToggle("لحظه‌های آرامش", settings.calmEnabled) {
                        onUpdateSettings(settings.copy(calmEnabled = it))
                    }
                    ParentSectionToggle("دنیای احساسات", settings.emotionsEnabled) {
                        onUpdateSettings(settings.copy(emotionsEnabled = it))
                    }
                    ParentSectionToggle("کارتون‌های مأوا", settings.cartoonsEnabled) {
                        onUpdateSettings(settings.copy(cartoonsEnabled = it))
                    }
                    ParentSectionToggle("دنیای کشف و یادگیری", settings.discoveryEnabled) {
                        onUpdateSettings(settings.copy(discoveryEnabled = it))
                    }
                }
            }
        }

        item {
            Button(
                onClick = onExitMava,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("mava_parent_exit_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("خروج به برنامه اصلی همیار بحران")
            }
        }
    }
}

@Composable
private fun ParentSectionToggle(title: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.bodyMedium)
        Switch(checked = checked, onCheckedChange = onChange)
    }
}

@Composable
private fun AdultMathGateDialog(
    title: String,
    onDismiss: () -> Unit,
    onVerified: () -> Unit
) {
    val pair = remember { Random.nextInt(4, 10) to Random.nextInt(3, 9) }
    val expected = pair.first + pair.second
    var input by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("تأیید ویژه بزرگسالان: حاصل جمع زیر را وارد کنید:")
                Text(
                    text = "${pair.first} + ${pair.second} = ؟",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = input,
                    onValueChange = {
                        input = it
                        showError = false
                    },
                    label = { Text("پاسخ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("adult_gate_input")
                )
                if (showError) {
                    Text(
                        text = "پاسخ صحیح نیست؛ لطفاً دوباره امتحان کنید.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFC62828)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val normalized = input.trim()
                        .replace('۰', '0').replace('۱', '1').replace('۲', '2')
                        .replace('۳', '3').replace('۴', '4').replace('۵', '5')
                        .replace('۶', '6').replace('۷', '7').replace('۸', '8').replace('۹', '9')
                    if (normalized.toIntOrNull() == expected) {
                        onVerified()
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier.testTag("adult_gate_confirm_button")
            ) {
                Text("تأیید")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("انصراف")
            }
        }
    )
}
