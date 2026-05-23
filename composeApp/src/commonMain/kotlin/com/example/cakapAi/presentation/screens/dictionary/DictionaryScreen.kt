package com.example.cakapAi.presentation.screens.dictionary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cakapAi.domain.model.SavedVocab
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryScreen(
    onNavigateBack: () -> Unit,
    viewModel: DictionaryViewModel = koinViewModel()
) {
    var sourceText by remember { mutableStateOf("") }
    var sourceLang by remember { mutableStateOf("Inggris") }
    var targetLang by remember { mutableStateOf("Indonesia") }

    // State for CRUD from Database
    val savedVocabs by viewModel.savedVocabs.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var editingVocab by remember { mutableStateOf<SavedVocab?>(null) }
    var editSourceText by remember { mutableStateOf("") }
    var editTranslatedText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    // Placeholder logic for translation
    val translatedText = if (sourceText.isNotBlank()) "Terjemahan: $sourceText" else ""

    // Map & Quiz color palette
    val backgroundColor = Color(0xFF071224)
    val cardColor = Color(0xFF0F1A30).copy(alpha = 0.95f)
    val emeraldAccent = Color(0xFF10B981)
    val skyAccent = Color(0xFF0EA5E9)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Translator", 
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingVocab = null
                    editSourceText = ""
                    editTranslatedText = ""
                    showDialog = true 
                },
                containerColor = emeraldAccent,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kosakata")
            }
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sourceLang,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = skyAccent,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            val temp = sourceLang
                            sourceLang = targetLang
                            targetLang = temp
                            sourceText = "" // clear text when swapping languages
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap Languages",
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    }

                    Text(
                        text = targetLang,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = skyAccent,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End
                    )
                }
            }

            // Input Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 150.dp)
                ) {
                    TextField(
                        value = sourceText,
                        onValueChange = { sourceText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false),
                        placeholder = { Text("Masukkan teks", color = Color.Gray) },
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
                        textStyle = MaterialTheme.typography.bodyLarge
                    )

                    if (sourceText.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(onClick = { sourceText = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Hapus teks",
                                    tint = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }

            // Output Card (translation result)
            if (translatedText.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.8f)), // Slight variation for output
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, emeraldAccent.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .heightIn(min = 100.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = targetLang,
                                style = MaterialTheme.typography.labelMedium,
                                color = emeraldAccent
                            )
                            IconButton(
                                onClick = {
                                    viewModel.addVocab(
                                        sourceLang = sourceLang,
                                        targetLang = targetLang,
                                        sourceText = sourceText,
                                        translatedText = translatedText
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Simpan Kosakata",
                                    tint = emeraldAccent
                                )
                            }
                        }
                        Text(
                            text = translatedText,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                }
            }
            
            // Saved Vocabularies
            if (savedVocabs.isNotEmpty() || searchQuery.isNotEmpty()) {
                val filteredVocabs = savedVocabs.filter {
                    it.sourceText.contains(searchQuery, ignoreCase = true) || 
                    it.translatedText.contains(searchQuery, ignoreCase = true)
                }

                Text(
                    text = "Kosakata Tersimpan",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    placeholder = { Text("Cari kosakata...", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedIndicatorColor = emeraldAccent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = emeraldAccent
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                if (filteredVocabs.isEmpty()) {
                    Text(
                        text = "Tidak ada kosakata yang cocok.",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    filteredVocabs.forEach { vocab ->
                        Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = cardColor),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = vocab.sourceText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = vocab.translatedText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = emeraldAccent,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "${vocab.sourceLang} → ${vocab.targetLang}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    editingVocab = vocab
                                    editSourceText = vocab.sourceText
                                    editTranslatedText = vocab.translatedText
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = skyAccent)
                                }
                                IconButton(onClick = {
                                    viewModel.deleteVocab(vocab)
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }
            }
            }
            
            // Add padding to ensure content isn't hidden behind FAB
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.heightIn(80.dp))
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                containerColor = cardColor,
                titleContentColor = Color.White,
                textContentColor = Color.White,
                title = {
                    Text(text = if (editingVocab == null) "Tambah Kosakata" else "Edit Kosakata")
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = editSourceText,
                            onValueChange = { editSourceText = it },
                            label = { Text("Bahasa Inggris") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = emeraldAccent,
                                unfocusedIndicatorColor = emeraldAccent.copy(alpha = 0.5f),
                                focusedLabelColor = emeraldAccent,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = emeraldAccent
                            )
                        )
                        TextField(
                            value = editTranslatedText,
                            onValueChange = { editTranslatedText = it },
                            label = { Text("Terjemahan") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedIndicatorColor = emeraldAccent,
                                unfocusedIndicatorColor = emeraldAccent.copy(alpha = 0.5f),
                                focusedLabelColor = emeraldAccent,
                                unfocusedLabelColor = Color.Gray,
                                cursorColor = emeraldAccent
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (editSourceText.isNotBlank() && editTranslatedText.isNotBlank()) {
                                if (editingVocab == null) {
                                    viewModel.addVocab(
                                        sourceLang = sourceLang,
                                        targetLang = targetLang,
                                        sourceText = editSourceText,
                                        translatedText = editTranslatedText
                                    )
                                } else {
                                    viewModel.updateVocab(
                                        vocab = editingVocab!!,
                                        newSourceText = editSourceText,
                                        newTranslatedText = editTranslatedText
                                    )
                                }
                                showDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent)
                    ) {
                        Text("Simpan")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Batal", color = Color.Gray)
                    }
                }
            )
        }
    }
}