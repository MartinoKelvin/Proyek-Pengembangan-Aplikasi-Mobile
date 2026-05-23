package com.example.cakapAi.presentation.screens.quiz

import androidx.lifecycle.ViewModel
import com.example.cakapAi.domain.repository.LearningRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Representation of a Quiz Question used by the Presentation layer.
 */
data class QuizQuestion(
    val id: Int,
    val type: String, // e.g. "MENDENGARKAN", "KOSAKATA", "TATA BAHASA", "PERCAKAPAN", "PENGUCAPAN"
    val prompt: String,
    val options: List<String>,
    val correctAnswer: String
)

/**
 * UI State representing all possible presentation states of the Quiz Screen.
 */
sealed interface QuizUiState {
    data object Loading : QuizUiState
    data class Success(
        val questions: List<QuizQuestion>,
        val currentQuestionIndex: Int,
        val selectedAnswer: String?,
        val isAnswerChecked: Boolean,
        val lives: Int,
        val correctCount: Int,
        val isFinished: Boolean
    ) : QuizUiState
    data class Error(val message: String) : QuizUiState
}

/**
 * ViewModel managing the active Quiz Screen state, controlling question navigation,
 * selected choice validation, live heart calculations, and overall completion logic.
 */
class QuizViewModel(
    private val levelId: Int,
    private val repository: LearningRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<QuizUiState>(QuizUiState.Loading)
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    init {
        loadQuestions()
    }

    /**
     * Loads the curated set of distinct questions dynamically based on the passed level ID.
     */
    fun loadQuestions() {
        _uiState.value = QuizUiState.Loading
        try {
            val questions = getQuestionsForLevel(levelId)
            _uiState.value = QuizUiState.Success(
                questions = questions,
                currentQuestionIndex = 0,
                selectedAnswer = null,
                isAnswerChecked = false,
                lives = 3,
                correctCount = 0,
                isFinished = false
            )
        } catch (e: Exception) {
            _uiState.value = QuizUiState.Error(e.message ?: "Terjadi kesalahan saat memuat materi kuis")
        }
    }

    /**
     * Updates the selected choice in the active question.
     */
    fun selectAnswer(answer: String) {
        val state = _uiState.value
        if (state is QuizUiState.Success && !state.isAnswerChecked) {
            _uiState.update {
                state.copy(selectedAnswer = answer)
            }
        }
    }

    /**
     * Validates the selected answer against the correct answer and recalculates remaining heart lives.
     */
    fun checkAnswer() {
        val state = _uiState.value
        if (state is QuizUiState.Success && !state.isAnswerChecked && state.selectedAnswer != null) {
            val currentQuestion = state.questions.getOrNull(state.currentQuestionIndex)
            if (currentQuestion != null) {
                val isCorrect = state.selectedAnswer == currentQuestion.correctAnswer
                _uiState.update {
                    state.copy(
                        isAnswerChecked = true,
                        correctCount = if (isCorrect) state.correctCount + 1 else state.correctCount,
                        lives = if (isCorrect) state.lives else state.lives - 1
                    )
                }
            }
        }
    }

    /**
     * Transitions to the next question in sequence, or completes the quiz if no questions are left.
     */
    fun nextQuestion() {
        val state = _uiState.value
        if (state is QuizUiState.Success && state.isAnswerChecked) {
            if (state.currentQuestionIndex + 1 < state.questions.size && state.lives > 0) {
                _uiState.update {
                    state.copy(
                        currentQuestionIndex = state.currentQuestionIndex + 1,
                        selectedAnswer = null,
                        isAnswerChecked = false
                    )
                }
            } else {
                _uiState.update {
                    state.copy(isFinished = true)
                }
            }
        }
    }
}

/**
 * Returns a static, curated set of 5 distinct questions per level
 * to match the CakapAi foreign language curriculum in the README.
 */
fun getQuestionsForLevel(levelId: Int): List<QuizQuestion> {
    return when (levelId) {
        1 -> listOf(
            QuizQuestion(1, "MENDENGARKAN", "Apa arti dari ungkapan sapaan 'Good Morning'?", listOf("Selamat malam", "Selamat pagi", "Selamat sore"), "Selamat pagi"),
            QuizQuestion(2, "MEMAHAMI", "Bagaimana menerjemahkan kalimat 'Nice to meet you' ke bahasa Indonesia?", listOf("Senang bertemu denganmu", "Apa kabarmu hari ini", "Selamat tinggal teman"), "Senang bertemu denganmu"),
            QuizQuestion(3, "MENDENGARKAN", "Bagaimana cara mengucapkan 'Terima kasih' dalam bahasa Inggris?", listOf("You are welcome", "Excuse me", "Thank you"), "Thank you"),
            QuizQuestion(4, "MEMAHAMI", "Apa arti dari ungkapan perpisahan 'Goodbye'?", listOf("Halo", "Selamat datang", "Selamat tinggal"), "Selamat tinggal"),
            QuizQuestion(5, "MENDENGARKAN", "Terjemahkan pertanyaan sederhana 'How are you?'", listOf("Apa kabar?", "Di mana rumahmu?", "Siapa namamu?"), "Apa kabar?")
        )
        2 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Apa arti kata buah-buahan 'Apple' dalam bahasa Indonesia?", listOf("Nanas", "Jeruk", "Apel"), "Apel"),
            QuizQuestion(2, "KOSAKATA", "Kata benda 'Book' memiliki arti...", listOf("Buku", "Meja", "Pena"), "Buku"),
            QuizQuestion(3, "KOSAKATA", "Terjemahkan kata kebutuhan pokok 'Water'!", listOf("Makanan", "Air", "Udara"), "Air"),
            QuizQuestion(4, "KOSAKATA", "Binatang peliharaan 'Cat' memiliki arti...", listOf("Anjing", "Kelinci", "Kucing"), "Kucing"),
            QuizQuestion(5, "KOSAKATA", "Tempat belajar 'School' diterjemahkan menjadi...", listOf("Taman", "Sekolah", "Rumah Sakit"), "Sekolah")
        )
        3 -> listOf(
            QuizQuestion(1, "TATA BAHASA", "Lengkapi kalimat pronoun berikut: 'I ___ a student.'", listOf("is", "am", "are"), "am"),
            QuizQuestion(2, "TATA BAHASA", "Pilih kata ganti orang yang tepat: '___ is reading a book.' (Merujuk ke dia perempuan)", listOf("He", "She", "They"), "She"),
            QuizQuestion(3, "TATA BAHASA", "Lengkapi present tense: 'They ___ football everyday.'", listOf("play", "plays", "playing"), "play"),
            QuizQuestion(4, "TATA BAHASA", "Bentuk lampau (past tense) dari kata kerja 'go' adalah...", listOf("went", "gone", "goes"), "went"),
            QuizQuestion(5, "TATA BAHASA", "Bentuk jamak (plural) dari kata 'child' adalah...", listOf("childs", "childrens", "children"), "children")
        )
        4 -> listOf(
            QuizQuestion(1, "PERCAKAPAN", "A: 'How is the weather today?'\nB: 'It is ___.' (Cerah)", listOf("sunny", "rainy", "snowy"), "sunny"),
            QuizQuestion(2, "PERCAKAPAN", "Bagaimana menanyakan arah 'Where is the restroom?' dalam bahasa Indonesia?", listOf("Di mana toiletnya?", "Kapan stasiun dibuka?", "Berapa harga tiket ini?"), "Di mana toiletnya?"),
            QuizQuestion(3, "PERCAKAPAN", "Terjemahkan ungkapan permohonan 'Can you help me?'", listOf("Bisakah Anda membantu saya?", "Apakah Anda tahu jalan ini?", "Permisi, jam berapa sekarang?"), "Bisakah Anda membantu saya?"),
            QuizQuestion(4, "PERCAKAPAN", "A: 'What time is it?'\nB: 'It is five ___.'", listOf("hours", "o'clock", "minutes"), "o'clock"),
            QuizQuestion(5, "PERCAKAPAN", "Bagaimana menanyakan harga barang 'How much is this?'", listOf("Di mana ini?", "Berapa harganya?", "Kapan ini selesai?"), "Berapa harganya?")
        )
        5 -> listOf(
            QuizQuestion(1, "PENGUCAPAN", "Bagaimana mengucap sopan 'Excuse me' untuk permisi?", listOf("Permisi", "Maaf", "Halo"), "Permisi"),
            QuizQuestion(2, "PENGUCAPAN", "Bagaimana membalas terima kasih dengan ramah 'No problem'?", listOf("Sama-sama", "Tidak masalah", "Tentu saja"), "Tidak masalah"),
            QuizQuestion(3, "PENGUCAPAN", "Ungkapan selamat 'Congratulations!' diterjemahkan menjadi...", listOf("Selamat!", "Semoga berhasil!", "Hati-hati!"), "Selamat!"),
            QuizQuestion(4, "PENGUCAPAN", "Kata penyambutan hangat 'Welcome' berarti...", listOf("Selamat tinggal", "Selamat datang", "Sampai jumpa"), "Selamat datang"),
            QuizQuestion(5, "PENGUCAPAN", "Ungkapan doa baik 'Have a nice day!' berarti...", listOf("Semoga sukses selalu!", "Selamat beristirahat!", "Semoga hari Anda menyenangkan!"), "Semoga hari Anda menyenangkan!")
        )
        else -> listOf(
            QuizQuestion(1, "UMUM", "Apa terjemahan dari kata 'Yes'?", listOf("Ya", "Tidak", "Mungkin"), "Ya"),
            QuizQuestion(2, "UMUM", "Apa terjemahan dari kata 'No'?", listOf("Ya", "Tidak", "Mungkin"), "Tidak"),
            QuizQuestion(3, "UMUM", "Apa terjemahan dari kata 'Hello'?", listOf("Halo", "Selamat tinggal", "Maaf"), "Halo"),
            QuizQuestion(4, "UMUM", "Apa terjemahan dari kata 'Thank you'?", listOf("Terima kasih", "Sama-sama", "Maaf"), "Terima kasih"),
            QuizQuestion(5, "UMUM", "Apa terjemahan dari kata 'Please'?", listOf("Tolong", "Silakan", "Maaf"), "Silakan")
        )
    }
}
