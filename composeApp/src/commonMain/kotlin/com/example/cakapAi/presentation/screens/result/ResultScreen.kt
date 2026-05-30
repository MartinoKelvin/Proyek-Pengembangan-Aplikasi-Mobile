package com.example.cakapAi.presentation.screens.result

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium, gamified Result Screen showing score achievements, accuracy rate,
 * and passed/failed visual rewards with high-fidelity glassmorphic card statistics
 * and vibrant color tones matching the deep ocean learning path theme.
 */
@Composable
fun ResultScreen(
    levelId: Int,
    score: Int,
    totalQuestion: Int,
    accuracy: Int,
    isPassed: Boolean,
    onBackToMap: () -> Unit,
    onRetryQuiz: () -> Unit,
    viewModel: ResultViewModel = koinViewModel()
) {
    // Save quiz result to database exactly once when ResultScreen is first composed
    LaunchedEffect(Unit) {
        viewModel.saveQuizResult(
            levelId = levelId.toLong(),
            score = score,
            totalQuestions = totalQuestion,
            accuracy = accuracy.toDouble(),
            isPassed = isPassed
        )
    }

    // Theme Colors matching MapScreen (fully dynamic)
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = if (isLight) Color(0xFFF0F4F8) else Color(0xFF071224)
    val cardColor = if (isLight) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f) else Color(0xFF0F1A30).copy(alpha = 0.9f)
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val redAccent = MaterialTheme.colorScheme.error
    val skyAccent = MaterialTheme.colorScheme.secondary
    val goldColor = Color(0xFFFBBF24)

    // Dynamic theme-based text and border colors to support Light Mode perfectly
    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant else Color.White.copy(alpha = 0.7f)
    val borderStrokeColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.08f)
    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer else Color(0xFF0B172E)

    // Animation entry states
    var startAnimations by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(150)
        startAnimations = true
    }

    val scale by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0.4f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )

    val fadeAlpha by animateFloatAsState(
        targetValue = if (startAnimations) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutQuad)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientStart, backgroundColor)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. TOP ILLUSTRATION & HEADER (🏆 Trophy / 🛡️ Restart Banner)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                        alpha = fadeAlpha
                    }
            ) {
                // Pulse Glowing Background behind the reward icon
                val pulseScale by rememberInfiniteTransition().animateFloat(
                    initialValue = 1f,
                    targetValue = 1.25f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500, easing = EaseInOutSine),
                        repeatMode = RepeatMode.Reverse
                    )
                )
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .graphicsLayer {
                            scaleX = pulseScale
                            scaleY = pulseScale
                        }
                        .background(
                            color = if (isPassed) emeraldAccent.copy(alpha = 0.15f) else redAccent.copy(alpha = 0.15f),
                            shape = CircleShape
                        )
                )

                // Main Reward Icon Base
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .shadow(16.dp, CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = if (isPassed) {
                                    listOf(emeraldAccent, Color(0xFF047857))
                                } else {
                                    listOf(redAccent, Color(0xFFB91C1C))
                                }
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isPassed) Icons.Default.EmojiEvents else Icons.Default.Refresh,
                        contentDescription = "Reward",
                        tint = if (isPassed) goldColor else Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Passed/Failed Text Titles
            Text(
                text = if (isPassed) "SELAMAT! KAMU LULUS" else "TETAP SEMANGAT!",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isPassed) emeraldAccent else redAccent,
                textAlign = TextAlign.Center,
                letterSpacing = 1.sp,
                modifier = Modifier.graphicsLayer { alpha = fadeAlpha }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle / Feedback Message
            val motivationalMessage = when {
                isPassed && score == 100 -> "Luar biasa! Skor sempurna! Anda menguasai bab ini dengan sangat baik."
                isPassed && score >= 80 -> "Hebat sekali! Anda memiliki pemahaman kosakata yang sangat kuat."
                isPassed -> "Kerja bagus! Anda berhasil lulus. Sedikit latihan lagi akan membuat Anda sempurna."
                else -> "Jangan berkecil hati! Kegagalan adalah awal dari keberhasilan. Ayo coba lagi untuk membuka level berikutnya!"
            }

            Text(
                text = motivationalMessage,
                fontSize = 14.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp,
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .graphicsLayer { alpha = fadeAlpha }
            )

            Spacer(modifier = Modifier.height(36.dp))

            // 2. STATS INFO ROW (Grid of 3 Glassmorphic Cards)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = fadeAlpha },
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Score Card
                StatCard(
                    title = "Skor",
                    value = score.toString(),
                    subValue = "/100",
                    accentColor = goldColor,
                    icon = Icons.Rounded.Star,
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )

                // Accuracy Card
                StatCard(
                    title = "Akurasi",
                    value = "$accuracy%",
                    subValue = "Tepat",
                    accentColor = skyAccent,
                    icon = Icons.Rounded.CheckCircle,
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )

                // Question Count Card
                StatCard(
                    title = "Total Soal",
                    value = totalQuestion.toString(),
                    subValue = "Soal",
                    accentColor = emeraldAccent,
                    icon = Icons.Rounded.Info,
                    modifier = Modifier.weight(1f),
                    cardColor = cardColor
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // 3. ACTION BUTTONS (Tactile 3D Buttons for Return / Continue)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { alpha = fadeAlpha },
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Primary Action Button (raised raised 3D design)
                val primaryButtonColor = if (isPassed) emeraldAccent else redAccent
                val primaryRimColor = if (isPassed) Color(0xFF047857) else Color(0xFF991B1B)
                val buttonText = if (isPassed) "LANJUTKAN BELAJAR" else "COBA LAGI"

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { 
                            if (isPassed) onBackToMap() else onRetryQuiz() 
                        },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Rim / Shadow Layer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .background(primaryRimColor, RoundedCornerShape(18.dp))
                    )

                    // Raised Button Face Layer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                            .padding(bottom = 6.dp)
                            .shadow(8.dp, RoundedCornerShape(18.dp))
                            .background(primaryButtonColor, RoundedCornerShape(18.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buttonText,
                            color = Color(0xFF030712),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            letterSpacing = 1.sp
                        )
                    }
                }

                // Secondary Outlined Glassmorphic Button
                if (!isPassed) {
                    OutlinedButton(
                        onClick = onBackToMap,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = textPrimary),
                        border = BorderStroke(1.dp, borderStrokeColor)
                    ) {
                        Text(
                            text = "KEMBALI KE PETA",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Reusable Glassmorphic Stats Card component.
 */
@Composable
fun StatCard(
    title: String,
    value: String,
    subValue: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    cardColor: Color
) {
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant else Color.Gray
    val textTertiary = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f) else Color.LightGray.copy(alpha = 0.6f)
    val borderStroke = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.08f)

    Card(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, borderStroke)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(accentColor.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Stat Value
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = value,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = textPrimary
                )
                if (subValue.isNotEmpty()) {
                    Text(
                        text = subValue,
                        fontSize = 11.sp,
                        color = textSecondary,
                        modifier = Modifier.padding(bottom = 3.dp, start = 1.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Label Title
            Text(
                text = title,
                fontSize = 11.sp,
                color = textTertiary,
                fontWeight = FontWeight.Bold
            )
        }
    }
}