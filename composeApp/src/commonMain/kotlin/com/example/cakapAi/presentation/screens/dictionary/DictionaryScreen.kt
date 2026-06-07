package com.example.cakapAi.presentation.screens.dictionary

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.testTag
import com.example.cakapAi.domain.model.SavedVocab
import org.koin.compose.viewmodel.koinViewModel

/**
 * Premium Vocabulary & Translator screen with dynamic support for Light and Dark modes.
 */
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

    val translationResult by viewModel.translationResult.collectAsStateWithLifecycle()
    val isTranslating by viewModel.isTranslating.collectAsStateWithLifecycle()

    LaunchedEffect(sourceText, sourceLang, targetLang) {
        if (sourceText.isNotBlank()) {
            kotlinx.coroutines.delay(800) // Debounce
            viewModel.translate(sourceLang, targetLang, sourceText)
        }
    }

    val isLight = MaterialTheme.colorScheme.background.red > 0.5f
    val backgroundColor = if (isLight) Color(0xFFF0F4F8) else Color(0xFF071224)
    val cardColor = if (isLight) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f) else Color(0xFF0F1A30).copy(alpha = 0.95f)
    val emeraldAccent = MaterialTheme.colorScheme.primary
    val skyAccent = MaterialTheme.colorScheme.secondary

    val textPrimary = if (isLight) MaterialTheme.colorScheme.onBackground else Color.White
    val textSecondary = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant else Color.LightGray
    val textSecondaryColor = if (isLight) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f) else Color.Gray
    val borderStrokeColor = if (isLight) MaterialTheme.colorScheme.outline.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.08f)
    val gradientStart = if (isLight) MaterialTheme.colorScheme.primaryContainer else Color(0xFF0A1B35)
    val outputCardColor = if (isLight) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f) else Color(0xFF1E293B).copy(alpha = 0.8f)
    val textFieldContainer = if (isLight) MaterialTheme.colorScheme.surfaceVariant else Color(0xFF1E293B)

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Translator & Kamus", 
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    editingVocab = null
                    editSourceText = ""
                    editTranslatedText = ""
                    showDialog = true 
                },
                modifier = Modifier.testTag("AddVocabularyButton"),
                containerColor = emeraldAccent,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Kosakata")
            }
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Language Selector Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = cardColor),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, borderStrokeColor)
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
                            sourceText = ""
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Swap Languages",
                            tint = textSecondary
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
                border = BorderStroke(1.dp, borderStrokeColor)
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
                        placeholder = { Text("Masukkan teks", color = textSecondaryColor) },
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
                                    tint = textSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Output Card (translation result)
            if (translationResult.isNotEmpty() || isTranslating) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = outputCardColor),
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
                                    if (translationResult.isNotEmpty()) {
                                        viewModel.addVocab(
                                            sourceLang = sourceLang,
                                            targetLang = targetLang,
                                            sourceText = sourceText,
                                            translatedText = translationResult
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.BookmarkBorder,
                                    contentDescription = "Simpan Kosakata",
                                    tint = emeraldAccent
                                )
                            }
                        }
                        if (isTranslating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp).align(Alignment.CenterHorizontally),
                                color = emeraldAccent,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = translationResult,
                                style = MaterialTheme.typography.bodyLarge,
                                color = textPrimary
                            )
                        }
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
                    color = textPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                )

                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).testTag("SearchField"),
                    placeholder = { Text("Cari kosakata...", color = textSecondaryColor) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = textSecondaryColor)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = textSecondaryColor)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = cardColor,
                        unfocusedContainerColor = cardColor,
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
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
                        color = textSecondaryColor,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp).fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    filteredVocabs.forEach { vocab ->
                        Card(
                            modifier = Modifier.fillMaxWidth().testTag("VocabularyItem"),
                            colors = CardDefaults.cardColors(containerColor = cardColor),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, borderStrokeColor)
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
                                        color = textPrimary,
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
                                        color = textSecondaryColor,
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
            } else {
                // Empty Vocabulary State
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No vocabulary available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = textSecondaryColor
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { 
                            editingVocab = null
                            editSourceText = ""
                            editTranslatedText = ""
                            showDialog = true 
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = emeraldAccent)
                    ) {
                        Text("Tambah Kosakata")
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
                titleContentColor = textPrimary,
                textContentColor = textPrimary,
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
                                focusedContainerColor = textFieldContainer,
                                unfocusedContainerColor = textFieldContainer,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedIndicatorColor = emeraldAccent,
                                unfocusedIndicatorColor = emeraldAccent.copy(alpha = 0.5f),
                                focusedLabelColor = emeraldAccent,
                                unfocusedLabelColor = textSecondaryColor,
                                cursorColor = emeraldAccent
                            )
                        )
                        TextField(
                            value = editTranslatedText,
                            onValueChange = { editTranslatedText = it },
                            label = { Text("Terjemahan") },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = textFieldContainer,
                                unfocusedContainerColor = textFieldContainer,
                                focusedTextColor = textPrimary,
                                unfocusedTextColor = textPrimary,
                                focusedIndicatorColor = emeraldAccent,
                                unfocusedIndicatorColor = emeraldAccent.copy(alpha = 0.5f),
                                focusedLabelColor = emeraldAccent,
                                unfocusedLabelColor = textSecondaryColor,
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
                        Text("Batal", color = textSecondaryColor)
                    }
                }
            )
        }
    }
}