package com.example.cakapAi.presentation.screens.map

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class LevelItem(
    val id: Int,
    val title: String,
    val isUnlocked: Boolean
)

@Composable
fun MapScreen(
    onNavigateToQuiz: (Int) -> Unit,
    onNavigateToDictionary: () -> Unit,
    onNavigateToAITutor: () -> Unit
) {
    val levels = listOf(
        LevelItem(1, "Level 1 - Basic Greeting", true),
        LevelItem(2, "Level 2 - Daily Vocabulary", true),
        LevelItem(3, "Level 3 - Simple Grammar", false),
        LevelItem(4, "Level 4 - Conversation", false),
        LevelItem(5, "Level 5 - Speaking Practice", false)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "CakapAI",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Peta perjalanan belajar bahasa asing",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            levels.forEach { level ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (level.isUnlocked) {
                            MaterialTheme.colorScheme.primaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = level.title,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text = if (level.isUnlocked) {
                                    "Terbuka"
                                } else {
                                    "Terkunci"
                                },
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        if (level.isUnlocked) {
                            Button(
                                onClick = {
                                    onNavigateToQuiz(level.id)
                                }
                            ) {
                                Text("Mulai")
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked"
                            )
                        }
                    }
                }
            }
        }
    }
}