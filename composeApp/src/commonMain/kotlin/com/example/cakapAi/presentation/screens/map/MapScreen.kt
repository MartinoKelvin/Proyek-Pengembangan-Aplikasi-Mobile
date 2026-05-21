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
    val xOffsetFactor: Float // S-curve sine-offset (-0.4f to 0.4f)
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
    onNavigateToAITutor: () -> Unit
) {
    // Generate mock levels data matching the interactive map
    val levels = remember {
        listOf(
            PathLevel(1, 0, LevelType.LISTENING, "Level 1 - Basic Greeting", "Sapaan dasar dan perkenalan diri", LevelStatus.COMPLETED, 0.0f),
            PathLevel(2, 1, LevelType.READING, "Level 2 - Daily Vocabulary", "Kosakata sehari-hari yang sering digunakan", LevelStatus.UNLOCKED, 0.25f),
            PathLevel(3, 2, LevelType.GAMING, "Level 3 - Simple Grammar", "Struktur kalimat dan tata bahasa dasar", LevelStatus.LOCKED, 0.38f),
            PathLevel(4, 3, LevelType.SPEAKING, "Level 4 - Conversation", "Latihan percakapan interaktif pendek", LevelStatus.LOCKED, 0.15f),
            PathLevel(5, 4, LevelType.VIDEO, "Level 5 - Speaking Practice", "Latihan pengucapan kata dan kalimat", LevelStatus.LOCKED, -0.2f)
        )
    }

    var selectedLevel by remember { mutableStateOf<PathLevel?>(null) }

    // Curated rich color palette representing a deep ocean voyage map
    val backgroundColor = Color(0xFF071224) // Deep navy ocean
    val cardColor = Color(0xFF0F1A30).copy(alpha = 0.95f)
    val emeraldAccent = Color(0xFF10B981)
    val skyAccent = Color(0xFF0EA5E9)
    val goldAccent = Color(0xFFF59E0B)

    val completedLevels = remember(levels) { levels.count { it.status == LevelStatus.COMPLETED } }
    val totalLevels = remember(levels) { levels.size }

    // Unified scrollable measurements
    val stepHeight = 156.dp
    val topPadding = 60.dp
    val bottomPadding = 140.dp
    val totalHeight = topPadding + bottomPadding + (stepHeight * levels.size)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1B35), backgroundColor)
                )
            )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // Premium Header Section showing actual level progress and shortcuts
            HeaderPanel(
                completedCount = completedLevels,
                totalCount = totalLevels,
                onNavigateToDictionary = onNavigateToDictionary,
                onNavigateToAITutor = onNavigateToAITutor
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
        AnimatedVisibility(
            visible = selectedLevel != null,
            enter = slideInVertically(initialOffsetY = { it / 2 }) + fadeIn(),
            exit = slideOutVertically(targetOffsetY = { it / 2 }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            selectedLevel?.let { level ->
                LevelPopupDetails(
                    level = level,
                    cardColor = cardColor,
                    emeraldAccent = emeraldAccent,
                    onStart = {
                        onNavigateToQuiz(level.id)
                        selectedLevel = null
                    },
                    onClose = { selectedLevel = null }
                )
            }
        }
    }
}

@Composable
fun HeaderPanel(
    completedCount: Int,
    totalCount: Int,
    onNavigateToDictionary: () -> Unit,
    onNavigateToAITutor: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1520).copy(alpha = 0.95f)),
        shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
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
                    color = Color(0xFF10B981),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "Peta Perjalanan Belajar",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
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
                        color = Color(0xFF10B981),
                        trackColor = Color(0xFF1E293B),
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp))
                    )
                    Text(
                        text = "$completedCount/$totalCount Selesai",
                        color = Color.LightGray,
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
                // Dictionary shortcut
                IconButton(
                    onClick = onNavigateToDictionary,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF1E293B), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = "Kamus",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // AI Tutor shortcut
                IconButton(
                    onClick = onNavigateToAITutor,
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFF1E293B), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "AI Tutor",
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

            // Sleek glassmorphic card for Level title
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (level.status == LevelStatus.LOCKED) {
                        Color(0xFF1E293B).copy(alpha = 0.5f)
                    } else {
                        Color(0xFF0F172A).copy(alpha = 0.85f)
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isCurrentlyActive) emeraldAccent.copy(alpha = 0.6f) else Color.White.copy(alpha = 0.08f)
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
                    color = if (level.status == LevelStatus.LOCKED) Color.Gray else Color.White,
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

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 1. Draw Ocean Background Waves at repeating scroll intervals
        val waveColor = Color(0xFF38BDF8).copy(alpha = 0.04f)
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
        drawCircle(
            color = Color.White.copy(alpha = 0.05f),
            radius = compassRadius,
            center = compassCenter,
            style = Stroke(width = 1.5f)
        )
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
            start = Offset(compassCenter.x - compassRadius - 8f, compassCenter.y),
            end = Offset(compassCenter.x + compassRadius + 8f, compassCenter.y),
            strokeWidth = 1.5f
        )
        drawLine(
            color = Color.White.copy(alpha = 0.05f),
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
            val islandColor = when (level.status) {
                LevelStatus.COMPLETED -> Color(0xFF0F3124) // Soft green island
                LevelStatus.UNLOCKED -> Color(0xFF162D4A) // Soft blue island
                LevelStatus.LOCKED -> Color(0xFF1B2330) // Soft dark slate island
            }
            val islandBorderColor = when (level.status) {
                LevelStatus.COMPLETED -> Color(0xFF10B981).copy(alpha = 0.25f)
                LevelStatus.UNLOCKED -> Color(0xFF0EA5E9).copy(alpha = 0.25f)
                LevelStatus.LOCKED -> Color(0xFF475569).copy(alpha = 0.08f)
            }

            // Draw multi-layered organic island shadows & landmass
            drawCircle(
                color = islandColor.copy(alpha = 0.5f),
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
            for (angle in 0..360 step 45) {
                val radians = Math.toRadians(angle.toDouble())
                val sandOffset = Offset(
                    (pt.x + Math.cos(radians) * 45.dp.toPx()).toFloat(),
                    (pt.y + Math.sin(radians) * 45.dp.toPx()).toFloat()
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.08f),
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