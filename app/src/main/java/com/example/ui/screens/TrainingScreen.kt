package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.CrisisType
import com.example.data.model.QuizTopic
import com.example.data.model.TrainingLesson
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberSoft
import com.example.ui.viewmodel.TrainingUiState

@Composable
fun TrainingScreen(
    uiState: TrainingUiState,
    isOnline: Boolean,
    onStartQuiz: (QuizTopic) -> Unit,
    onSelectAnswer: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onCloseQuiz: () -> Unit,
    onOpenCrisisGuide: (CrisisType) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by rememberSaveable { mutableIntStateOf(0) }
    var expandedLessonId by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "آموزش و سنجش آمادگی",
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Image(
                            painter = painterResource(id = R.drawable.img_first_aid),
                            contentDescription = "تصویر آموزش کمک‌های اولیه",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .clip(RoundedCornerShape(22.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = activeTab == 0,
                                onClick = { activeTab = 0 },
                                leadingIcon = { Icon(Icons.Filled.School, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("درس‌های کمک‌های اولیه") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("training_lessons_tab")
                            )
                            FilterChip(
                                selected = activeTab == 1,
                                onClick = { activeTab = 1 },
                                leadingIcon = { Icon(Icons.Filled.Quiz, contentDescription = null, modifier = Modifier.size(16.dp)) },
                                label = { Text("آزمون‌های کوتاه") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("training_quiz_tab")
                            )
                        }
                    }

                    if (activeTab == 0) {
                        items(uiState.lessons, key = { it.id }) { lesson ->
                            LessonCard(
                                lesson = lesson,
                                isExpanded = expandedLessonId == lesson.id,
                                onToggleExpand = {
                                    expandedLessonId = if (expandedLessonId == lesson.id) null else lesson.id
                                },
                                onOpenCrisisGuide = onOpenCrisisGuide
                            )
                        }
                    } else {
                        items(uiState.quizTopics, key = { it.id }) { topic ->
                            val progress = uiState.progressMap[topic.id]
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(18.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
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
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = topic.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = topic.subtitle,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                        if (progress != null) {
                                            StatusBadge(
                                                text = "بهترین: ${progress.bestScore}/${progress.totalQuestions}",
                                                containerColor = SafeGreenSoft,
                                                contentColor = SafeGreen,
                                                icon = Icons.Filled.CheckCircle
                                            )
                                        }
                                    }
                                    Button(
                                        onClick = { onStartQuiz(topic) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp)
                                            .testTag("start_quiz_${topic.id}"),
                                        shape = RoundedCornerShape(14.dp)
                                    ) {
                                        Text(if (progress == null) "شروع آزمون" else "تکرار آزمون")
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

@Composable
private fun LessonCard(
    lesson: TrainingLesson,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onOpenCrisisGuide: (CrisisType) -> Unit
) {
    Card(
        onClick = onToggleExpand,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = lesson.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = lesson.summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (isExpanded) "بستن درس" else "باز کردن درس"
                )
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    lesson.steps.forEachIndexed { idx, step ->
                        Row(verticalAlignment = Alignment.Top) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = step,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Surface(
                        color = WarningAmberSoft,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                Icons.Filled.Warning,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = lesson.warningNote,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    Text(
                        text = "منبع: ${lesson.sourceNote}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    lesson.relatedCrisis?.let { related ->
                        OutlinedButton(
                            onClick = { onOpenCrisisGuide(related) },
                            modifier = Modifier.minimumInteractiveComponentSize(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("پیشنهاد آموزشی: مشاهده راهنمای ${related.titleFa}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuizSessionView(
    uiState: TrainingUiState,
    onSelectAnswer: (Int) -> Unit,
    onNextQuestion: () -> Unit,
    onCloseQuiz: () -> Unit
) {
    val topic = uiState.activeQuizTopic ?: return
    val questions = uiState.activeQuestions

    Column(
        modifier = Modifier
            .widthIn(max = 640.dp)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (uiState.isQuizFinished) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        Icons.Filled.CheckCircle,
                        contentDescription = "پایان آزمون",
                        tint = SafeGreen,
                        modifier = Modifier.size(56.dp)
                    )
                    Text(
                        text = "نتیجه ${topic.title}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "امتیاز شما: ${uiState.correctAnswersCount} از ${questions.size}",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = if (uiState.correctAnswersCount == questions.size) {
                            "عالی! آمادگی بسیار خوبی در این موضوع دارید."
                        } else {
                            "مرور درس‌های مرتبط به شما کمک می‌کند امتیاز کامل بگیرید."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Button(
                        onClick = onCloseQuiz,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("quiz_finish_close_button"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("بازگشت به آموزش‌ها")
                    }
                }
            }
        } else {
            val q = questions.getOrNull(uiState.currentQuestionIndex) ?: return
            Text(
                text = "${topic.title} • پرسش ${uiState.currentQuestionIndex + 1} از ${questions.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            LinearProgressIndicator(
                progress = { (uiState.currentQuestionIndex + 1).toFloat() / questions.size.coerceAtLeast(1) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(50))
            )
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = q.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    q.options.forEachIndexed { idx, opt ->
                        val selected = uiState.selectedOptionIndex
                        val bg = when {
                            selected == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                            idx == q.correctIndex -> SafeGreenSoft
                            idx == selected -> EmergencyRedSoft
                            else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        }
                        val borderColor = when {
                            selected == null -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            idx == q.correctIndex -> SafeGreen
                            idx == selected -> EmergencyRed
                            else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                        }
                        Surface(
                            onClick = { onSelectAnswer(idx) },
                            color = bg,
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, borderColor),
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("quiz_option_$idx")
                        ) {
                            Text(
                                text = opt,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }

                    if (uiState.selectedOptionIndex != null) {
                        Surface(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                text = q.explanation,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Button(
                            onClick = onNextQuestion,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("quiz_next_button"),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text(
                                if (uiState.currentQuestionIndex + 1 >= questions.size) "مشاهده نتیجه" else "سؤال بعدی"
                            )
                        }
                    }
                }
            }
            OutlinedButton(
                onClick = onCloseQuiz,
                modifier = Modifier.minimumInteractiveComponentSize()
            ) {
                Text("انصراف از آزمون")
            }
        }
    }
}
