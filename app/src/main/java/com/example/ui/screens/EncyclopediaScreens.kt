package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.QuizQuestion
import com.example.data.model.ScienceCategory
import com.example.data.model.ScienceTopic
import com.example.data.model.WhyQuestion
import com.example.ui.components.AnimatedScienceDiagramCard
import com.example.ui.components.HamyarTopBar
import com.example.ui.components.StatusBadge
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.EmergencyRedSoft
import com.example.ui.theme.SafeGreen
import com.example.ui.theme.SafeGreenSoft
import com.example.ui.viewmodel.ScienceUiState

@Composable
fun EncyclopediaScreen(
    uiState: ScienceUiState,
    isOnline: Boolean,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (ScienceCategory?) -> Unit,
    onOpenTopic: (String) -> Unit,
    onOpenWhyItHappens: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "دانشنامه علم و بحران",
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
                // Featured entry to "Why It Happens?"
                item {
                    Card(
                        onClick = onOpenWhyItHappens,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("open_why_it_happens_card")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.HelpOutline,
                                contentDescription = "چرا این اتفاق می‌افتد",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(34.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "چرا این اتفاق می‌افتد؟",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = "پاسخ علمی به ۷ پرسش واقعی درباره زلزله، آتش‌سوزی، برق، سیل و CO",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                    }
                }

                // Search bar
                item {
                    OutlinedTextField(
                        value = uiState.searchQuery,
                        onValueChange = onSearchChange,
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        label = { Text("جست‌وجو در موضوعات علمی و علوم متوسطه اول") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("encyclopedia_search_input")
                    )
                }

                // Category chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = uiState.selectedCategory == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("همه موضوعات") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            ),
                            modifier = Modifier.minimumInteractiveComponentSize()
                        )
                        ScienceCategory.entries.forEach { cat ->
                            FilterChip(
                                selected = uiState.selectedCategory == cat,
                                onClick = { onSelectCategory(cat) },
                                label = { Text(cat.titleFa) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                                ),
                                modifier = Modifier.minimumInteractiveComponentSize()
                            )
                        }
                    }
                }

                items(uiState.topics, key = { it.id }) { topic ->
                    val progress = uiState.progressMap[topic.quizTopicId]
                    Card(
                        onClick = { onOpenTopic(topic.id) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("science_topic_card_${topic.id}")
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StatusBadge(
                                    text = topic.category.titleFa,
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                    icon = Icons.Filled.Science
                                )
                                if (progress != null) {
                                    StatusBadge(
                                        text = "آزمونک ✓",
                                        containerColor = SafeGreenSoft,
                                        contentColor = SafeGreen,
                                        icon = Icons.Filled.CheckCircle
                                    )
                                }
                            }
                            Text(
                                text = topic.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = topic.gradeInfo,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = topic.scientificDefinition,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
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
fun EncyclopediaDetailScreen(
    topic: ScienceTopic,
    questions: List<QuizQuestion>,
    selectedAnswers: Map<String, Int>,
    isOnline: Boolean,
    onAnswerQuiz: (QuizQuestion, Int) -> Unit,
    onResetQuiz: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = topic.title,
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
                // Section 1: Scientific Definition
                item {
                    EncyclopediaInfoSectionCard(
                        badgeText = "۱. تعریف علمی",
                        title = topic.title,
                        body = topic.scientificDefinition
                    )
                }

                // Section 2: Simple Explanation
                item {
                    EncyclopediaInfoSectionCard(
                        badgeText = "۲. توضیح ساده و روان",
                        title = "به زبان ساده چه اتفاقی می‌افتد؟",
                        body = topic.simpleExplanation
                    )
                }

                // Section 3: Middle School Science Connection
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatusBadge(
                                text = "۳. ارتباط با علوم متوسطه اول",
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                icon = Icons.Filled.MenuBook
                            )
                            Text(
                                text = topic.gradeInfo,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = topic.chapterAlignment,
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = topic.textbookConnection,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Section 4: Crisis Application
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SafeGreenSoft),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(1.dp, SafeGreen.copy(alpha = 0.35f))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Shield, contentDescription = null, tint = SafeGreen)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "۴. کاربرد در مدیریت بحران و ایمنی",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = SafeGreen
                                )
                            }
                            Text(
                                text = topic.crisisApplication,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Section 5: Animated Diagram
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "۵. تصویر یا نمودار مفهومی متحرک",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        AnimatedScienceDiagramCard(diagramType = topic.diagramType)
                    }
                }

                // Section 6: Key Points
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
                            Text(
                                text = "۶. نکات کلیدی",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            topic.keyPoints.forEach { pt ->
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(text = pt, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }

                // Section 7: Interactive Inline Quiz
                if (questions.isNotEmpty()) {
                    item {
                        Text(
                            text = "۷. آزمونک تعاملی این موضوع",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    items(questions, key = { it.id }) { q ->
                        InlineScienceQuizCard(
                            question = q,
                            selectedIndex = selectedAnswers[q.id],
                            onSelect = { idx -> onAnswerQuiz(q, idx) },
                            onReset = { onResetQuiz(q.id) }
                        )
                    }
                }

                item {
                    Text(
                        text = "${topic.sourceNote} • آخرین بازبینی: ${topic.lastReviewedDate}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun WhyItHappensScreen(
    uiState: ScienceUiState,
    isOnline: Boolean,
    onAnswerQuiz: (QuizQuestion, Int) -> Unit,
    onResetQuiz: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expandedId by rememberSaveable { mutableStateOf<String?>(uiState.whyQuestions.firstOrNull()?.id) }

    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            HamyarTopBar(
                title = "چرا این اتفاق می‌افتد؟",
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
                items(uiState.whyQuestions, key = { it.id }) { item ->
                    val expanded = expandedId == item.id
                    WhyQuestionCard(
                        item = item,
                        expanded = expanded,
                        selectedAnswer = uiState.selectedQuizAnswers[item.quizQuestion.id],
                        onToggle = {
                            expandedId = if (expanded) null else item.id
                        },
                        onAnswer = { idx -> onAnswerQuiz(item.quizQuestion, idx) },
                        onReset = { onResetQuiz(item.quizQuestion.id) }
                    )
                }
                item { Spacer(modifier = Modifier.height(12.dp)) }
            }
        }
    }
}

@Composable
private fun WhyQuestionCard(
    item: WhyQuestion,
    expanded: Boolean,
    selectedAnswer: Int?,
    onToggle: () -> Unit,
    onAnswer: (Int) -> Unit,
    onReset: () -> Unit
) {
    Card(
        onClick = onToggle,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)),
        modifier = Modifier.testTag("why_card_${item.id}")
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
                Text(
                    text = item.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "بستن" else "باز کردن"
                )
            }
            Text(
                text = item.shortAnswer,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = item.scientificExplanation,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = SafeGreenSoft,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "نکته ایمنی: ${item.crisisTakeaway}",
                            style = MaterialTheme.typography.bodySmall,
                            color = SafeGreen,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                    AnimatedScienceDiagramCard(diagramType = item.diagramType)
                    InlineScienceQuizCard(
                        question = item.quizQuestion,
                        selectedIndex = selectedAnswer,
                        onSelect = onAnswer,
                        onReset = onReset
                    )
                }
            }
        }
    }
}

@Composable
private fun EncyclopediaInfoSectionCard(
    badgeText: String,
    title: String,
    body: String
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StatusBadge(
                text = badgeText,
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun InlineScienceQuizCard(
    question: QuizQuestion,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    onReset: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Quiz, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("پرسش علمی", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
                if (selectedIndex != null) {
                    TextButton(onClick = onReset) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تلاش مجدد")
                    }
                }
            }
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            question.options.forEachIndexed { idx, opt ->
                val bg = when {
                    selectedIndex == null -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
                    idx == question.correctIndex -> SafeGreenSoft
                    idx == selectedIndex -> EmergencyRedSoft
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                }
                val border = when {
                    selectedIndex == null -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    idx == question.correctIndex -> SafeGreen
                    idx == selectedIndex -> EmergencyRed
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                }
                Surface(
                    onClick = { onSelect(idx) },
                    color = bg,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, border),
                    modifier = Modifier
                        .fillMaxWidth()
                        .minimumInteractiveComponentSize()
                        .testTag("inline_quiz_${question.id}_$idx")
                ) {
                    Text(
                        text = opt,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            if (selectedIndex != null) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = question.explanation,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}
