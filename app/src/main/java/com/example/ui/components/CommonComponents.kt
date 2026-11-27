package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.local.entity.MediaDownloadStatus
import com.example.data.local.entity.OfflineMediaEntity
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import kotlinx.coroutines.delay
import java.io.File

fun dialNumber(context: Context, number: String) {
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}

fun openSmsComposer(context: Context, phone: String, message: String) {
    val uri = if (phone.isBlank()) Uri.parse("smsto:") else Uri.parse("smsto:$phone")
    val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
        putExtra("sms_body", message)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(intent) }
}

fun shareEmergencyText(context: Context, text: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, text)
    }
    val chooser = Intent.createChooser(intent, "ارسال پیام اضطراری").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    runCatching { context.startActivity(chooser) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamyarTopBar(
    title: String,
    onBackClick: (() -> Unit)? = null,
    isOnline: Boolean? = null
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "بازگشت"
                    )
                }
            }
        },
        actions = {
            if (isOnline != null) {
                ConnectivityChip(isOnline = isOnline, modifier = Modifier.padding(end = 12.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Composable
fun ConnectivityChip(
    isOnline: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isOnline) SafeGreenSoft else WarningAmberSoft
    val contentColor = if (isOnline) SafeGreen else WarningAmber
    Surface(
        modifier = modifier,
        color = bgColor,
        shape = RoundedCornerShape(50),
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Filled.CloudDone else Icons.Filled.CloudOff,
                contentDescription = if (isOnline) "آنلاین" else "آفلاین",
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = if (isOnline) "آنلاین" else "آفلاین",
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun OfflineStatusBanner(
    isOnline: Boolean,
    lastSyncFormatted: String,
    queuedCount: Int,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isOnline) {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)
    } else {
        WarningAmberSoft
    }
    val iconTint = if (isOnline) MaterialTheme.colorScheme.primary else WarningAmber

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = bgColor,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, iconTint.copy(alpha = 0.25f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isOnline) Icons.Filled.CloudDone else Icons.Filled.CloudOff,
                contentDescription = "وضعیت اتصال",
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isOnline) {
                        "اتصال برقرار است • محتوای آفلاین آماده"
                    } else {
                        "حالت آفلاین فعال • راهنماها، دانشنامه، آزمایشگاه و مأوا در دسترس‌اند"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                val queueInfo = if (queuedCount > 0) " • $queuedCount درخواست در صف ارسال" else ""
                Text(
                    text = "آخرین ذخیره آفلاین: $lastSyncFormatted$queueInfo",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    text: String,
    containerColor: Color,
    contentColor: Color,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = containerColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(13.dp)
                )
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelSmall,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            if (!subtitle.isNullOrBlank()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            Surface(
                onClick = onActionClick,
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun EmergencyDialCard(
    title: String,
    number: String,
    description: String,
    isPrimary: Boolean,
    onDial: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onDial,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .testTag("emergency_dial_$number"),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (isPrimary) EmergencyRed.copy(alpha = 0.28f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isPrimary) EmergencyRedSoft else MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "تماس با $title",
                    tint = if (isPrimary) EmergencyRed else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPrimary) EmergencyRed else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun LessonVideoPlayerCard(
    mediaAsset: OfflineMediaEntity?,
    isOnline: Boolean,
    onDownloadClick: (OfflineMediaEntity) -> Unit,
    onImportLocalVideo: (Uri) -> Unit,
    onDeleteOfflineVideo: (OfflineMediaEntity) -> Unit,
    modifier: Modifier = Modifier,
    @RawRes bundledVideoResId: Int? = null
) {
    val context = LocalContext.current
    val localFile = mediaAsset?.localFilePath?.let { File(it) }?.takeIf { it.exists() }
    val remoteUrl = mediaAsset?.streamUrl?.takeIf { it.isNotBlank() && isOnline }
    val playableUri: Uri? = when {
        localFile != null -> Uri.fromFile(localFile)
        bundledVideoResId != null -> Uri.parse("android.resource://${context.packageName}/$bundledVideoResId")
        remoteUrl != null -> Uri.parse(remoteUrl)
        else -> null
    }
    val isOfflineReady = localFile != null || bundledVideoResId != null

    var isPlaying by remember { mutableStateOf(false) }
    var isFullscreen by remember { mutableStateOf(false) }
    var positionMs by remember { mutableIntStateOf(0) }
    var durationMs by remember { mutableIntStateOf(0) }

    LaunchedEffect(playableUri) {
        isPlaying = false
        positionMs = 0
        durationMs = 0
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        uri?.let { onImportLocalVideo(it) }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = mediaAsset?.title ?: "ویدئوی آموزشی",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(
                    text = when {
                        bundledVideoResId != null && localFile == null -> "آماده پخش آفلاین"
                        else -> mediaAsset?.status?.titleFa ?: "آماده ساختار ویدئو"
                    },
                    containerColor = if (isOfflineReady) SafeGreenSoft else WarningAmberSoft,
                    contentColor = if (isOfflineReady) SafeGreen else WarningAmber
                )
            }

            if (playableUri != null) {
                key(playableUri) {
                    InlineVideoSurface(
                        uri = playableUri,
                        isPlaying = isPlaying,
                        onTogglePlay = { isPlaying = !isPlaying },
                        onPlaybackCompleted = { isPlaying = false },
                        onProgressUpdate = { pos, dur ->
                            positionMs = pos
                            durationMs = dur
                        },
                        onFullscreenClick = { isFullscreen = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(210.dp)
                            .clip(RoundedCornerShape(14.dp))
                    )
                }
            } else {
                // Honest offline/unlinked video placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VideocamOff,
                            contentDescription = "ویدئو در دسترس نیست",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(34.dp)
                        )
                        Text(
                            text = when {
                                mediaAsset?.streamUrl.isNullOrBlank() ->
                                    "فایل ویدئویی رسمی هنوز متصل نشده است (لینک ساختگی قرار داده نشده)."
                                !isOnline ->
                                    "اینترنت قطع است و فایل آفلاین دانلود نشده است."
                                else ->
                                    "برای مشاهده، ویدئو را دانلود یا فایل محلی انتخاب کنید."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (mediaAsset != null) {
                Text(
                    text = mediaAsset.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "منبع: ${mediaAsset.sourceAttribution}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (localFile != null) {
                    OutlinedButton(
                        onClick = {
                            isPlaying = false
                            onDeleteOfflineVideo(mediaAsset)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("حذف فایل آفلاین از حافظه")
                    }
                } else if (bundledVideoResId == null) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (!mediaAsset.streamUrl.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = { onDownloadClick(mediaAsset) },
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Filled.CloudDownload, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("دانلود آفلاین")
                            }
                        }
                        OutlinedButton(
                            onClick = { videoPicker.launch(arrayOf("video/mp4", "video/*")) },
                            modifier = Modifier
                                .weight(1f)
                                .minimumInteractiveComponentSize()
                                .testTag("video_import_local_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Filled.VideoFile, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("بارگذاری ویدئوی محلی")
                        }
                    }
                }
            }
        }
    }

    if (isFullscreen && playableUri != null) {
        Dialog(
            onDismissRequest = { isFullscreen = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
            ) {
                key(playableUri) {
                    InlineVideoSurface(
                        uri = playableUri,
                        isPlaying = isPlaying,
                        onTogglePlay = { isPlaying = !isPlaying },
                        onPlaybackCompleted = { isPlaying = false },
                        onProgressUpdate = { pos, dur ->
                            positionMs = pos
                            durationMs = dur
                        },
                        onFullscreenClick = { isFullscreen = false },
                        isFullscreenMode = true,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
private fun InlineVideoSurface(
    uri: Uri,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onPlaybackCompleted: () -> Unit,
    onProgressUpdate: (Int, Int) -> Unit,
    onFullscreenClick: () -> Unit,
    isFullscreenMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    var videoViewRef by remember { mutableStateOf<VideoView?>(null) }
    var currentPos by remember { mutableIntStateOf(0) }
    var totalDur by remember { mutableIntStateOf(1) }

    LaunchedEffect(isPlaying, videoViewRef) {
        val vv = videoViewRef ?: return@LaunchedEffect
        if (isPlaying) vv.start() else if (vv.isPlaying) vv.pause()
        while (isPlaying) {
            val pos = runCatching { vv.currentPosition }.getOrDefault(0)
            val dur = runCatching { vv.duration }.getOrDefault(1).coerceAtLeast(1)
            currentPos = pos
            totalDur = dur
            onProgressUpdate(pos, dur)
            delay(350)
        }
    }

    DisposableEffect(uri) {
        onDispose {
            runCatching { videoViewRef?.stopPlayback() }
        }
    }

    Box(modifier = modifier.background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                VideoView(ctx).apply {
                    setVideoURI(uri)
                    setOnPreparedListener { mp ->
                        totalDur = mp.duration.coerceAtLeast(1)
                        seekTo(1)
                    }
                    setOnCompletionListener {
                        currentPos = 0
                        onPlaybackCompleted()
                    }
                    videoViewRef = this
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.Black.copy(alpha = 0.55f))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "توقف" else "پخش",
                    tint = Color.White
                )
            }
            Slider(
                value = (currentPos.toFloat() / totalDur.coerceAtLeast(1)).coerceIn(0f, 1f),
                onValueChange = { frac ->
                    val target = (frac * totalDur).toInt()
                    currentPos = target
                    runCatching { videoViewRef?.seekTo(target) }
                },
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = onFullscreenClick,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Icon(
                    imageVector = if (isFullscreenMode) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                    contentDescription = if (isFullscreenMode) "خروج از تمام‌صفحه" else "تمام‌صفحه",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(38.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

