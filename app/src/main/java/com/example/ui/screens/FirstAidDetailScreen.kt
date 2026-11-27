package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.OfflineMediaEntity
import com.example.data.model.CrisisType
import com.example.data.model.QuizTopic
import com.example.data.model.TrainingLesson
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.LessonVideoPlayerCard
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.TrainingUiState

@Composable
fun FirstAidDetailScreen(
    lesson: TrainingLesson,
    uiState: TrainingUiState,
    onDownloadVideo: (OfflineMediaEntity) -> Unit,
    onImportLocalVideo: (String, Uri) -> Unit,
    onDeleteOfflineVideo: (OfflineMediaEntity) -> Unit,
    onDismissBanner: () -> Unit,
    onStartQuiz: (QuizTopic) -> Unit,
    onSelectAnswer: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onCloseQuiz: () -> Unit,
    onOpenCrisisGuide: (CrisisType) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediaAsset = uiState.mediaMap[lesson.mediaId]
    val relatedQuizTopic = uiState.quizTopics.firstOrNull { it.id == lesson.relatedQuizTopicId }
    val quizProgress = relatedQuizTopic?.let { uiState.progressMap[it.id] }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = lesson.title,
                onBackClick = {
                    if (uiState.activeQuizTopic != null) onCloseQuiz() else onBack()
                },
                isOnline = uiState.isOnline
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            if (uiState.activeQuizTopic != null) {
                QuizSessionView(
                    uiState = uiState,
                    onSelectAnswer = onSelectAnswer,
                    onNextQuestion = onNextQuestion,
                    onCloseQuiz = onCloseQuiz
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    uiState.mediaBannerMessage?.let { msg ->
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.bodySmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                    TextButton(onClick = onDismissBanner) {
                                        Text("بستن")
                                    }
                                }
                            }
                        }
                    }

                    // Header summary card
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    StatusBadge(
                                        text = lesson.category,
                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                    Text(
                                        text = "${lesson.durationMinutes} دقیقه مطالعه",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = lesson.title,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = lesson.summary,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    // Video player & offline download/import card
                    item {
                        LessonVideoPlayerCard(
                            mediaAsset = mediaAsset,
                            isOnline = uiState.isOnline,
                            onDownloadClick = onDownloadVideo,
                            onImportLocalVideo = { uri ->
                                onImportLocalVideo(lesson.mediaId, uri)
                            },
                            onDeleteOfflineVideo = onDeleteOfflineVideo
                        )
                    }

                    // Step-by-step instructions
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = "مراحل اقدام ایمن (گام‌به‌گام)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                lesson.steps.forEachIndexed { idx, step ->
                                    Row(verticalAlignment = Alignment.Top) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .clip(CircleShape)
                                                .background(MaterialTheme.colorScheme.primaryContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${idx + 1}",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = step,
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Warning box
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
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = "هشدار ایمنی",
                                    tint = WarningAmber,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "هشدار مهم ایمنی",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = WarningAmber
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = lesson.warningNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // End-of-lesson quiz CTA
                    if (relatedQuizTopic != null) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f))
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Filled.Quiz,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "آزمونک پایان درس",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (quizProgress != null) {
                                            StatusBadge(
                                                text = "بهترین: ${quizProgress.bestScore}/${quizProgress.totalQuestions}",
                                                containerColor = SafeGreenSoft,
                                                contentColor = SafeGreen,
                                                icon = Icons.Filled.CheckCircle
                                            )
                                        }
                                    }
                                    Text(
                                        text = relatedQuizTopic.subtitle,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Button(
                                        onClick = { onStartQuiz(relatedQuizTopic) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("lesson_start_quiz_button"),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text(if (quizProgress == null) "شروع آزمون پایان درس" else "تکرار آزمون درس")
                                    }
                                }
                            }
                        }
                    }

                    // Source & Related Guide
                    item {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "منبع علمی: ${lesson.sourceNote}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = "آخرین بازبینی محتوا: ${lesson.lastReviewedDate}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                lesson.relatedCrisis?.let { crisis ->
                                    OutlinedButton(
                                        onClick = { onOpenCrisisGuide(crisis) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .minimumInteractiveComponentSize(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Filled.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("مشاهده راهنمای بحران ${crisis.titleFa}")
                                    }
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }
            }
        }
    }
}
