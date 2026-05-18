package com.example.cakapAi.presentation.screens.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuizScreen(
    levelId: Int,
    onNavigateBack: () -> Unit,
    onFinishQuiz: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Quiz Level $levelId",
            style = MaterialTheme.typography.headlineMedium
        )

        Text("Health")

        LinearProgressIndicator(
            progress = { 0.7f },
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = "What is the meaning of 'Good Morning'?",
            style = MaterialTheme.typography.titleLarge
        )

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selamat malam")
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selamat pagi")
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selamat tinggal")
        }

        Button(
            onClick = onFinishQuiz,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Selesai Quiz")
        }

        OutlinedButton(
            onClick = onNavigateBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kembali")
        }
    }
}