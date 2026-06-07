package com.example.cakapAi.presentation.screens.map

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.sin
import org.koin.compose.viewmodel.koinViewModel
import com.example.cakapAi.presentation.screens.map.components.LevelPracticeDetailSheet
import com.example.cakapAi.presentation.screens.practice.PracticeSessionOverlay
import com.example.cakapAi.core.util.BackHandler

/**
 * Data class representing a Level Step in the Learning Path Map.
 */
data class PathLevel(
    val id: Int,
    val index: Int,
    val type: LevelType,
    val title: String,
    val subtitle: String,
    val status: LevelStatus,
    val xOffsetFactor: Float, // S-curve sine-offset (-0.4f to 0.4f)
    val highScore: Int = 0
)

enum class LevelType {
    LISTENING, SPEAKING, REWARD, VIDEO, READING, GAMING
}

enum class LevelStatus {
    LOCKED, UNLOCKED, COMPLETED
}

/**
 * Elegant Learning Path Map Screen designed for Jetpack Compose / Compose Multiplatform.
 * Features a beautifully unified scrollable map layout where islands, routes, waves,
 * and level buttons scroll together perfectly as a single coordinate-locked path.
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MapScreen(
    onNavigateToQuiz: (Int) -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToAITutor: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: MapViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = MaterialTheme.colorScheme.background
    val cardColor = MaterialTheme.colorScheme.surface
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val skyAccent = MaterialTheme.colorScheme.secondary
    val goldAccent = Color(0xFFF59E0B)

    // Dynamic theme-based text and border colors to support Light Mode perfectly
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val textTertiary = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
    val borderStrokeColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    val borderStrokeColorAlpha08 = MaterialTheme.colorScheme.outline.copy(alpha = 0.08f)
    val borderStrokeColorAlpha12 = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)

    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f) else Color(0xFF012B1E)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("HomeScreen")
            .background(
                Brush.verticalGradient(
                    colors = listOf(gradientStart, backgroundColor)
                )
            )
    ) {
        when (val state = uiState) {
            is MapUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = emeraldAccent,
                        strokeWidth = 4.dp
                    )
                }
            }
            is MapUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Error",
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Gagal Memuat Peta",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                color = Color.White.copy(alpha = 0.7f),
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
            is MapUiState.Empty -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Empty",
                            tint = Color.White.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Belum ada materi belajar",
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            is MapUiState.Success -> {
                // Map db LevelProgress to path visual model to retain S-curve rendering
                val levels = remember(state.levels) {
                    state.levels.map { progress ->
                        PathLevel(
                            id = progress.id,
                            index = progress.id - 1,
                            type = try {
                                LevelType.valueOf(progress.levelType.uppercase())
                            } catch (e: Exception) {
                                LevelType.LISTENING
                            },
                            title = progress.title,
                            subtitle = progress.subtitle,
                            status = when {
                                progress.isCompleted -> LevelStatus.COMPLETED
                                progress.isUnlocked -> LevelStatus.UNLOCKED
                                else -> LevelStatus.LOCKED
                            },
                            xOffsetFactor = sin((progress.id.toFloat() - 1f) * 1.3f) * 0.35f,
                            highScore = progress.highScore
                        )
                    }
                }

                var selectedLevel by remember { mutableStateOf<PathLevel?>(null) }
                var selectedPracticeLevel by remember { mutableStateOf<PathLevel?>(null) }
                var isPracticeSessionOpen by remember { mutableStateOf(false) }
                var showExitConfirmDialog by remember { mutableStateOf(false) }

                val completedLevels = remember(levels) { levels.count { it.status == LevelStatus.COMPLETED } }
                val totalLevels = remember(levels) { levels.size }

                // Unified scrollable measurements
                val stepHeight = 156.dp
                val topPadding = 60.dp
                val bottomPadding = 140.dp
                val totalHeight = topPadding + bottomPadding + (stepHeight * levels.size)

                Column(modifier = Modifier.fillMaxSize()) {
                    // Premium Header Section showing actual level progress and shortcuts
                    HeaderPanel(
                        completedCount = completedLevels,
                        totalCount = totalLevels,
                        onNavigateToProfile = onNavigateToProfile
                    )

                    // Scrollable Map Area where path lines, islands, and buttons scroll in perfect sync
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    ) {
                        val scrollState = rememberScrollState()

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                        ) {
                            // Unified Sea Map Backdrop (archipelago landmasses, waves, and paths scrolling together)
                            ConnectionLinesBackdrop(
                                levels = levels,
                                stepHeight = stepHeight,
                                topPadding = topPadding,
                                pathColor = emeraldAccent,
                                dashedColor = Color.White.copy(alpha = 0.35f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(totalHeight)
                            )

                            // Interactive Level Nodes placed at the exact same scrollable coordinates
                            levels.forEach { item ->
                                val yOffset = topPadding + (stepHeight * item.index)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .offset(y = yOffset)
                                ) {
                                    LevelNodeItem(
                                        level = item,
                                        emeraldAccent = emeraldAccent,
                                        skyAccent = skyAccent,
                                        goldAccent = goldAccent,
                                        isCurrentlyActive = (selectedLevel?.id == item.id),
                                        onClick = {
                                            if (item.status != LevelStatus.LOCKED) {
                                                selectedLevel = if (selectedLevel?.id == item.id) null else item
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Popup Details Overlay when clicking an unlocked Node
                if (selectedLevel != null) {
                    LevelPracticeDetailSheet(
                        level = selectedLevel!!,
                        onDismiss = { selectedLevel = null },
                        onStartPractice = {
                            selectedPracticeLevel = selectedLevel
                            isPracticeSessionOpen = true
                            selectedLevel = null
                        }
                    )
                }

                BackHandler(enabled = isPracticeSessionOpen) {
                    showExitConfirmDialog = true
                }

                if (isPracticeSessionOpen && selectedPracticeLevel != null) {
                    PracticeSessionOverlay(
                        level = selectedPracticeLevel!!,
                        onClose = {
                            showExitConfirmDialog = true
                        },
                        onCompleted = { isSuccess, score ->
                            isPracticeSessionOpen = false
                            if (selectedPracticeLevel != null) {
                                viewModel.savePracticeResult(selectedPracticeLevel!!.id, score, isSuccess)
                            }
                            selectedPracticeLevel = null
                        }
                    )
                }

                if (showExitConfirmDialog) {
                    AlertDialog(
                        onDismissRequest = { showExitConfirmDialog = false },
                        containerColor = cardColor,
                        titleContentColor = textPrimary,
                        textContentColor = textPrimary,
                        title = { Text(text = "Keluar dari kuis?", fontWeight = FontWeight.Bold) },
                        text = { Text(text = "Progress pengerjaan saat ini akan hilang. Apakah kamu yakin ingin keluar?") },
                        confirmButton = {
                            Button(
                                onClick = {
                                    showExitConfirmDialog = false
                                    isPracticeSessionOpen = false
                                    selectedPracticeLevel = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Keluar", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        },
                        dismissButton = {
                            TextButton(
                                onClick = { showExitConfirmDialog = false }
                            ) {
                                Text(
                                    text = "Batal",
                                    color = textSecondary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HeaderPanel(
    completedCount: Int,
    totalCount: Int,
    onNavigateToProfile: () -> Unit
) {
    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val cardColor = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val borderStrokeColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
    val buttonBackground = MaterialTheme.colorScheme.surfaceVariant
    val buttonIconTint = MaterialTheme.colorScheme.onSurfaceVariant
    val progressTrack = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        border = BorderStroke(1.dp, borderStrokeColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left details with CakapAi title and Level Progress
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "CAKAPAI",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Peta Perjalanan Belajar",
                    style = MaterialTheme.typography.titleMedium,
                    color = textPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Progress Bar of Completed Levels
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val progressValue = completedCount.toFloat() / totalCount.toFloat()
                    LinearProgressIndicator(
                        progress = { progressValue },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = progressTrack,
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = "$completedCount/$totalCount Selesai",
                        color = textSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Quick Actions (Shortcuts to Dictionary & AI Tutor)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Profile shortcut
                IconButton(
                    onClick = onNavigateToProfile,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("ProfileButton")
                        .background(Color(0xFF10B981), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun LevelNodeItem(
    level: PathLevel,
    emeraldAccent: Color,
    skyAccent: Color,
    goldAccent: Color,
    isCurrentlyActive: Boolean,
    onClick: () -> Unit
) {
    val xOffsetDp = (level.xOffsetFactor * 160).dp

    // Color theme mapping for the 3D button
    val baseColor = when (level.status) {
        LevelStatus.COMPLETED -> Color(0xFF10B981) // Emerald 500
        LevelStatus.UNLOCKED -> Color(0xFF0EA5E9) // Sky 500
        LevelStatus.LOCKED -> Color(0xFF334155) // Slate 600
    }

    val rimColor = when (level.status) {
        LevelStatus.COMPLETED -> Color(0xFF047857) // Emerald 700
        LevelStatus.UNLOCKED -> Color(0xFF0369A1) // Sky 700
        LevelStatus.LOCKED -> Color(0xFF1E293B) // Slate 800
    }

    val iconColor = when (level.status) {
        LevelStatus.COMPLETED -> Color.White
        LevelStatus.UNLOCKED -> Color.White
        LevelStatus.LOCKED -> Color(0xFF64748B) // Slate 500
    }

    // Modern and unique game icons
    val nodeIcon = when (level.type) {
        LevelType.LISTENING -> Icons.Default.VolumeUp
        LevelType.SPEAKING -> Icons.Default.KeyboardVoice
        LevelType.REWARD -> Icons.Default.Star
        LevelType.VIDEO -> Icons.Default.Videocam
        LevelType.READING -> Icons.Default.MenuBook
        LevelType.GAMING -> Icons.Default.SportsEsports
    }

    // Active level glow pulse animation
    val isPulsing = level.status == LevelStatus.UNLOCKED
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 1.0f,
            targetValue = 1.35f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = EaseOutQuad),
                repeatMode = RepeatMode.Restart
            ),
            label = "scale"
        )
    } else {
        remember { mutableStateOf(1.0f) }
    }
    
    val pulseAlpha by if (isPulsing) {
        infiniteTransition.animateFloat(
            initialValue = 0.5f,
            targetValue = 0.0f,
            animationSpec = infiniteRepeatable(
                animation = tween(1600, easing = EaseOutQuad),
                repeatMode = RepeatMode.Restart
            ),
            label = "alpha"
        )
    } else {
        remember { mutableStateOf(0.0f) }
    }

    // Bounce interaction scale on click selection
    val animatedScale by animateFloatAsState(
        targetValue = if (isCurrentlyActive) 1.15f else 1.0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(x = xOffsetDp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Pulse Glowing Ring behind current level
                if (isPulsing) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .graphicsLayer {
                                scaleX = pulseScale
                                scaleY = pulseScale
                                alpha = pulseAlpha
                            }
                            .background(baseColor.copy(alpha = 0.4f), CircleShape)
                    )
                }

                // 3D tactile button stack
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .graphicsLayer {
                            scaleX = animatedScale
                            scaleY = animatedScale
                        }
                        .clip(CircleShape)
                        .clickable { onClick() },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    // Shadow / Rim layer (produces 3D depth)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(rimColor, CircleShape)
                    )

                    // Button face layer (raised by 6.dp)
                    Box(
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .size(68.dp)
                            .shadow(
                                elevation = if (isCurrentlyActive) 12.dp else 4.dp,
                                shape = CircleShape
                            )
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(baseColor, baseColor.copy(alpha = 0.9f))
                                ),
                                shape = CircleShape
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = nodeIcon,
                            contentDescription = level.title,
                            tint = iconColor,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val isLight = MaterialTheme.colorScheme.background.red > 0.5f
            val labelContainerColor = if (isLight) {
                MaterialTheme.colorScheme.surface
            } else {
                if (level.status == LevelStatus.LOCKED) {
                    Color(0xFF1E293B).copy(alpha = 0.5f)
                } else {
                    Color(0xFF0F172A).copy(alpha = 0.85f)
                }
            }

            val labelTextColor = if (isLight) {
                if (level.status == LevelStatus.LOCKED) {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                } else {
                    MaterialTheme.colorScheme.onSurface
                }
            } else {
                if (level.status == LevelStatus.LOCKED) {
                    Color.Gray
                } else {
                    Color.White
                }
            }

            val labelBorderColor = if (isLight) {
                if (isCurrentlyActive) emeraldAccent else MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            } else {
                if (isCurrentlyActive) emeraldAccent.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
            }

            // Sleek glassmorphic card for Level title
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = labelContainerColor
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = labelBorderColor
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick() }
            ) {
                Text(
                    text = level.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = labelTextColor,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * Draws a beautiful archipelago map integrated into the scroll: islands (landmasses), 
 * wave ripples, sand shoreline grains, and the sea route connecting everything.
 * All shapes are drawn relative to the level nodes, scrolling perfectly together!
 */
@Composable
fun ConnectionLinesBackdrop(
    levels: List<PathLevel>,
    stepHeight: Dp,
    topPadding: Dp,
    pathColor: Color,
    dashedColor: Color,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val stepHeightPx = with(density) { stepHeight.toPx() }
    val topPaddingPx = with(density) { topPadding.toPx() }

    val colorScheme = MaterialTheme.colorScheme
    val isLight = colorScheme.background.red > 0.5f
    val primaryColor = colorScheme.primary
    val surfaceColor = colorScheme.surface
    val surfaceVariantColor = colorScheme.surfaceVariant
    val outlineColor = colorScheme.outline

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 1. Draw Ocean Background Waves at repeating scroll intervals
        val waveColor = if (isLight) {
            primaryColor.copy(alpha = 0.08f)
        } else {
            Color(0xFF38BDF8).copy(alpha = 0.04f)
        }
        for (yOffset in 300..height.toInt() step 500) {
            drawCircle(
                color = waveColor,
                radius = 120.dp.toPx(),
                center = Offset(width * 0.15f, yOffset.toFloat()),
                style = Stroke(width = 2f)
            )
            drawCircle(
                color = waveColor,
                radius = 140.dp.toPx(),
                center = Offset(width * 0.15f, yOffset.toFloat()),
                style = Stroke(width = 1.5f)
            )
        }

        // 2. Draw a beautiful stylized Vintage Compass Rose at top right
        val compassCenter = Offset(width * 0.82f, topPaddingPx + 40.dp.toPx())
        val compassRadius = 30.dp.toPx()
        val compassColor = if (isLight) {
            primaryColor.copy(alpha = 0.12f)
        } else {
            Color.White.copy(alpha = 0.05f)
        }
        drawCircle(
            color = compassColor,
            radius = compassRadius,
            center = compassCenter,
            style = Stroke(width = 1.5f)
        )
        drawLine(
            color = compassColor,
            start = Offset(compassCenter.x - compassRadius - 8f, compassCenter.y),
            end = Offset(compassCenter.x + compassRadius + 8f, compassCenter.y),
            strokeWidth = 1.5f
        )
        drawLine(
            color = compassColor,
            start = Offset(compassCenter.x, compassCenter.y - compassRadius - 8f),
            end = Offset(compassCenter.x, compassCenter.y + compassRadius + 8f),
            strokeWidth = 1.5f
        )

        // 3. Generate island point coordinates (adding 38.dp to align with center of 76.dp node buttons)
        val points = mutableListOf<Offset>()
        levels.forEach { level ->
            val py = topPaddingPx + (level.index * stepHeightPx) + (38.dp.toPx())
            val px = (width / 2) + (level.xOffsetFactor * 160.dp.toPx())
            points.add(Offset(px, py))
        }

        // 4. Draw beautiful "Islands" (Pulau) under each level node coordinate
        levels.forEachIndexed { i, level ->
            val pt = points[i]
            val islandColor = if (isLight) {
                when (level.status) {
                    LevelStatus.COMPLETED -> Color(0xFFE6F4EA) // Soft green island
                    LevelStatus.UNLOCKED -> Color(0xFFE8F0FE) // Soft blue island
                    LevelStatus.LOCKED -> surfaceVariantColor.copy(alpha = 0.5f) // Soft gray island
                }
            } else {
                when (level.status) {
                    LevelStatus.COMPLETED -> Color(0xFF0F3124) // Soft green island
                    LevelStatus.UNLOCKED -> Color(0xFF162D4A) // Soft blue island
                    LevelStatus.LOCKED -> Color(0xFF1B2330) // Soft dark slate island
                }
            }

            val islandBorderColor = if (isLight) {
                when (level.status) {
                    LevelStatus.COMPLETED -> Color(0xFF10B981).copy(alpha = 0.4f)
                    LevelStatus.UNLOCKED -> Color(0xFF0EA5E9).copy(alpha = 0.4f)
                    LevelStatus.LOCKED -> outlineColor.copy(alpha = 0.15f)
                }
            } else {
                when (level.status) {
                    LevelStatus.COMPLETED -> Color(0xFF10B981).copy(alpha = 0.25f)
                    LevelStatus.UNLOCKED -> Color(0xFF0EA5E9).copy(alpha = 0.25f)
                    LevelStatus.LOCKED -> Color(0xFF475569).copy(alpha = 0.08f)
                }
            }

            // Draw multi-layered organic island shadows & landmass
            drawCircle(
                color = if (isLight) surfaceColor else islandColor.copy(alpha = 0.5f),
                radius = 64.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = islandColor,
                radius = 52.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = islandBorderColor,
                radius = 52.dp.toPx(),
                center = pt,
                style = Stroke(width = 2f)
            )

            // Small decorative dots representing shoreline sands
            val sandColor = if (isLight) {
                primaryColor.copy(alpha = 0.25f)
            } else {
                Color.White.copy(alpha = 0.08f)
            }
            for (angle in 0..360 step 45) {
                val radians = Math.toRadians(angle.toDouble())
                val sandOffset = Offset(
                    (pt.x + Math.cos(radians) * 45.dp.toPx()).toFloat(),
                    (pt.y + Math.sin(radians) * 45.dp.toPx()).toFloat()
                )
                drawCircle(
                    color = sandColor,
                    radius = 1.5f,
                    center = sandOffset
                )
            }
        }

        // 5. Draw the sea shipping lane pathway connecting islands
        if (points.size > 1) {
            val routePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (idx in 1 until points.size) {
                    val prev = points[idx - 1]
                    val curr = points[idx]
                    // Bezier logic for gorgeous smooth curves
                    cubicTo(
                        x1 = prev.x, y1 = (prev.y + curr.y) / 2,
                        x2 = curr.x, y2 = (prev.y + curr.y) / 2,
                        x3 = curr.x, y3 = curr.y
                    )
                }
            }

            // Path Gradient from green to cyan
            val pathBrush = Brush.verticalGradient(
                colors = listOf(Color(0xFF10B981), Color(0xFF0EA5E9))
            )

            // Draw glowing under-bridge path
            drawPath(
                path = routePath,
                brush = pathBrush,
                alpha = 0.12f,
                style = Stroke(width = 12f)
            )

            // Draw center dashed pathway representing navigation route
            drawPath(
                path = routePath,
                color = dashedColor,
                style = Stroke(
                    width = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(14f, 14f), 0f)
                )
            )
        }
    }
}

@Composable
fun LevelPopupDetails(
    level: PathLevel,
    cardColor: Color,
    emeraldAccent: Color,
    onStart: () -> Unit,
    onClose: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(300.dp)
            .shadow(24.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(containerColor = cardColor),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Chip / Label
                Surface(
                    color = emeraldAccent.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = level.type.name,
                        color = emeraldAccent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Close Button
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Chapter Title
            Text(
                text = level.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Subtitle Description
            Text(
                text = level.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray.copy(alpha = 0.8f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Play / Start Button
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text(
                    text = "MULAI KUIS",
                    color = Color(0xFF030712),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}