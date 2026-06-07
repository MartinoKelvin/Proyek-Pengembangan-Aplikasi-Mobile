package com.example.cakapAi.presentation.screens.tutor

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium AI Tutor conversational learning screen with dynamic support for Light and Dark modes.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITutorScreen(
    onNavigateBack: () -> Unit,
    viewModel: AITutorViewModel = koinViewModel()
) {
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = if (isLight) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f) else MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val skyAccent = MaterialTheme.colorScheme.secondary

    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant else Color.LightGray
    val borderStrokeColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else MaterialTheme.colorScheme.outline
    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer else Color(0xFF012B1E)

    var inputText by remember { mutableStateOf("") }
    
    val chatHistory by viewModel.chatHistory.collectAsStateWithLifecycle()
    val isAITyping by viewModel.isAITyping.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "AI Tutor", 
                        color = textPrimary,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = textPrimary)
                    }
                },
                windowInsets = WindowInsets(top = 0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = textPrimary,
                    navigationIconContentColor = textPrimary
                )
            )
        },
        modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(gradientStart, backgroundColor)
            )
        )
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Chat History Area
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                reverseLayout = false
            ) {
                item {
                    Box(modifier = Modifier.padding(top = 8.dp))
                }
                
                items(chatHistory) { message ->
                    ChatBubble(
                        message = message,
                        emeraldAccent = emeraldAccent,
                        skyAccent = skyAccent,
                        cardColor = cardColor
                    )
                }
                
                if (isAITyping) {
                    item {
                        TypingIndicatorBubble(
                            cardColor = cardColor,
                            skyAccent = skyAccent
                        )
                    }
                }
                
                item {
                    Box(modifier = Modifier.padding(bottom = 8.dp))
                }
            }

            // Input Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, borderStrokeColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Ketik pesan...", color = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary
                        ),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isAITyping) {
                                viewModel.sendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .background(if (inputText.isNotBlank()) emeraldAccent else if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.2f) else Color.Gray, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Kirim",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    emeraldAccent: Color,
    skyAccent: Color,
    cardColor: Color
) {
    val isUser = message.isUser
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    
    // Bubble alignment
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    // Bubble colors
    val bubbleColor = if (isUser) {
        if (isLight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else skyAccent.copy(alpha = 0.2f)
    } else {
        cardColor
    }
    
    val borderColor = if (isUser) {
        if (isLight) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else skyAccent.copy(alpha = 0.5f)
    } else {
        if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.08f)
    }
    
    // Bubble shape
    val shape = if (isUser) {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 16.dp, bottomEnd = 4.dp)
    } else {
        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp)
    }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = alignment
    ) {
        Card(
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = bubbleColor),
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                if (!isUser && message.isFeedback) {
                    Text(
                        text = "AI Feedback",
                        style = MaterialTheme.typography.labelSmall,
                        color = emeraldAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                } else if (!isUser) {
                    Text(
                        text = "AI Tutor",
                        style = MaterialTheme.typography.labelSmall,
                        color = skyAccent,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }

                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textPrimary
                )
            }
        }
    }
}

@Composable
fun TypingIndicatorBubble(
    cardColor: Color,
    skyAccent: Color
) {
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val borderStrokeColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.08f)

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, borderStrokeColor),
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "AI Tutor",
                    style = MaterialTheme.typography.labelSmall,
                    color = skyAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                TypingIndicator()
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val dotColor = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.6f)

    val circles = listOf(
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) },
        remember { Animatable(initialValue = 0f) }
    )

    circles.forEachIndexed { index, animatable ->
        LaunchedEffect(key1 = animatable) {
            delay(index * 150L)
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 600),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Row(
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        circles.forEach { animatable ->
            Box(
                modifier = Modifier
                    .offset(y = (-8 * animatable.value).dp)
                    .size(8.dp)
                    .background(dotColor, CircleShape)
            )
        }
    }
}