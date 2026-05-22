package com.example.cakapAi.presentation.screens.tutor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(
    val isUser: Boolean,
    val text: String,
    val isFeedback: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITutorScreen(
    onNavigateBack: () -> Unit
) {
    val backgroundColor = Color(0xFF071224)
    val cardColor = Color(0xFF0F1A30).copy(alpha = 0.95f)
    val emeraldAccent = Color(0xFF10B981)
    val skyAccent = Color(0xFF0EA5E9)

    var inputText by remember { mutableStateOf("") }
    var isAITyping by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    
    val chatHistory = remember {
        mutableStateListOf(
            ChatMessage(
                isUser = false, 
                text = "Halo! Aku AI Tutor kamu. Coba ketik kalimat dalam bahasa Inggris dan aku akan memberikan feedback mengenai grammar-mu!"
            )
        )
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "AI Tutor", 
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali", tint = Color.White)
                    }
                },
                windowInsets = WindowInsets(top = 0.dp),
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        modifier = Modifier.background(
            Brush.verticalGradient(
                colors = listOf(Color(0xFF0A1B35), backgroundColor)
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
                    // Spacer at top
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
                    // Spacer at bottom
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
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
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
                        placeholder = { Text("Ketik pesan...", color = Color.Gray) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        maxLines = 3
                    )

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank() && !isAITyping) {
                                val textToSend = inputText
                                chatHistory.add(ChatMessage(isUser = true, text = textToSend))
                                inputText = ""
                                isAITyping = true
                                
                                coroutineScope.launch {
                                    delay(2000) // Simulasi AI sedang berpikir
                                    isAITyping = false
                                    // Mock AI response
                                    chatHistory.add(
                                        ChatMessage(
                                            isUser = false, 
                                            text = "Feedback untuk: \"$textToSend\"\n\nGrammar kamu sudah lumayan bagus! Namun akan lebih natural jika dikatakan seperti ini:\n\n\"(Koreksi yang disarankan AI)\"",
                                            isFeedback = true
                                        )
                                    )
                                }
                            }
                        },
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .background(if (inputText.isNotBlank()) emeraldAccent else Color.Gray, CircleShape)
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
    
    // Bubble alignment
    val alignment = if (isUser) Alignment.CenterEnd else Alignment.CenterStart
    
    // Bubble colors
    val bubbleColor = if (isUser) skyAccent.copy(alpha = 0.2f) else cardColor
    val borderColor = if (isUser) skyAccent.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.08f)
    
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
                    color = Color.White
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
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 4.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = cardColor),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
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
                    .background(Color.White.copy(alpha = 0.6f), CircleShape)
            )
        }
    }
}