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
        6 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Apa terjemahan dari 'Father'?", listOf("Ibu", "Paman", "Ayah"), "Ayah"),
            QuizQuestion(2, "KOSAKATA", "Bagaimana menerjemahkan kata 'Sister'?", listOf("Saudara perempuan", "Saudara laki-laki", "Sepupu"), "Saudara perempuan"),
            QuizQuestion(3, "KOSAKATA", "Siapakah 'Grandfather' itu?", listOf("Nenek", "Ayah", "Kakek"), "Kakek"),
            QuizQuestion(4, "KOSAKATA", "Kata 'Uncle' memiliki arti...", listOf("Bibi", "Paman", "Keponakan"), "Paman"),
            QuizQuestion(5, "PERCAKAPAN", "Terjemahkan kalimat 'This is my friend.'", listOf("Ini teman saya.", "Ini keluarga saya.", "Ini guru saya."), "Ini teman saya.")
        )
        7 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Berapakah angka 'Twelve'?", listOf("2", "12", "20"), "12"),
            QuizQuestion(2, "KOSAKATA", "Berapakah hasil dari 'Ten plus five'?", listOf("Fifteen", "Fifty", "Five"), "Fifteen"),
            QuizQuestion(3, "PERCAKAPAN", "A: 'What time is it?'\nB: 'It is eight ___.'", listOf("o'clock", "hours", "minutes"), "o'clock"),
            QuizQuestion(4, "MEMAHAMI", "Bagaimana menerjemahkan waktu 'Half past seven'?", listOf("Jam 7 lewat 15 menit", "Jam 7 lewat 30 menit", "Jam 8 kurang 15 menit"), "Jam 7 lewat 30 menit"),
            QuizQuestion(5, "KOSAKATA", "Bahasa Inggris dari angka '50' adalah...", listOf("Fifteen", "Five", "Fifty"), "Fifty")
        )
        8 -> listOf(
            QuizQuestion(1, "TATA BAHASA", "Lengkapi kalimat: 'He ___ to school every day.'", listOf("go", "goes", "going"), "goes"),
            QuizQuestion(2, "TATA BAHASA", "Lengkapi kalimat negatif: 'They ___ not like spicy food.'", listOf("do", "does", "is"), "do"),
            QuizQuestion(3, "TATA BAHASA", "Lengkapi kalimat tanya: '___ she speak English?'", listOf("Do", "Does", "Is"), "Does"),
            QuizQuestion(4, "TATA BAHASA", "Pilih kalimat yang benar:", listOf("She write a letter.", "She writes a letter.", "She writing a letter."), "She writes a letter."),
            QuizQuestion(5, "TATA BAHASA", "Lengkapi kalimat: 'We ___ happy today.'", listOf("am", "is", "are"), "are")
        )
        9 -> listOf(
            QuizQuestion(1, "PERCAKAPAN", "Bagaimana memesan sopan: 'I ___ like a cup of tea, please.'", listOf("will", "would", "want"), "would"),
            QuizQuestion(2, "PERCAKAPAN", "Apa arti ungkapan 'Can I have the bill, please?'", listOf("Bolehkah saya meminta menunya?", "Bisakah saya meminta struk/tagihannya?", "Di mana toiletnya?"), "Bisakah saya meminta struk/tagihannya?"),
            QuizQuestion(3, "KOSAKATA", "Kata 'Fried rice' memiliki arti...", listOf("Mie goreng", "Nasi goreng", "Ayam goreng"), "Nasi goreng"),
            QuizQuestion(4, "PERCAKAPAN", "Pelayan: 'Are you ready to order?'\nPelanggan: 'Yes, ___.'", listOf("I would like a steak", "I want to pay", "Where is the food"), "I would like a steak"),
            QuizQuestion(5, "KOSAKATA", "Terjemahkan kata 'Water'!", listOf("Air", "Jus", "Teh"), "Air")
        )
        10 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Apa arti kata 'Meeting'?", listOf("Pekerjaan", "Rapat / Pertemuan", "Gaji"), "Rapat / Pertemuan"),
            QuizQuestion(2, "KOSAKATA", "Kata benda 'Desk' berarti...", listOf("Kursi", "Meja tulis", "Lemari"), "Meja tulis"),
            QuizQuestion(3, "PERCAKAPAN", "Terjemahkan kalimat: 'Please check your email.'", listOf("Silakan periksa email Anda.", "Tolong kirim surat ini.", "Mari kita rapat sekarang."), "Silakan periksa email Anda."),
            QuizQuestion(4, "KOSAKATA", "Apa arti dari istilah 'Deadline'?", listOf("Jam kerja", "Target penjualan", "Batas waktu pengumpulan"), "Batas waktu pengumpulan"),
            QuizQuestion(5, "KOSAKATA", "Kata 'Manager' diterjemahkan menjadi...", listOf("Karyawan", "Manajer / Pengelola", "Direktur"), "Manajer / Pengelola")
        )
        11 -> listOf(
            QuizQuestion(1, "PERCAKAPAN", "Bagaimana menanyakan jalan ke stasiun bus?", listOf("Excuse me, how do I get to the bus station?", "Where is my bus?", "What time does the bus leave?"), "Excuse me, how do I get to the bus station?"),
            QuizQuestion(2, "PERCAKAPAN", "Apa arti petunjuk jalan 'Turn right'?", listOf("Belok kiri", "Belok kanan", "Jalan lurus"), "Belok kanan"),
            QuizQuestion(3, "PERCAKAPAN", "Arti kata 'Go straight ahead' adalah...", listOf("Belok di pertigaan", "Jalan lurus terus ke depan", "Putar balik arah"), "Jalan lurus terus ke depan"),
            QuizQuestion(4, "PERCAKAPAN", "Bagaimana menerjemahkan 'Between the library and the bank'?", listOf("Di depan perpustakaan dan bank", "Di sebelah perpustakaan dan bank", "Di antara perpustakaan dan bank"), "Di antara perpustakaan dan bank"),
            QuizQuestion(5, "KOSAKATA", "Kata 'Behind' berarti...", listOf("Di depan", "Di samping", "Di belakang"), "Di belakang")
        )
        12 -> listOf(
            QuizQuestion(1, "TATA BAHASA", "Lengkapi kalimat: 'I am ___ a book right now.'", listOf("read", "reading", "reads"), "reading"),
            QuizQuestion(2, "TATA BAHASA", "Lengkapi kalimat: 'They ___ playing soccer in the field.'", listOf("is", "am", "are"), "are"),
            QuizQuestion(3, "TATA BAHASA", "Lengkapi kalimat: 'She is ___ (menangis) because she is sad.'", listOf("crying", "cry", "cried"), "crying"),
            QuizQuestion(4, "TATA BAHASA", "Bentuk tanya yang benar dari 'He is working' adalah...", listOf("Is he working?", "Does he working?", "He is working?"), "Is he working?"),
            QuizQuestion(5, "TATA BAHASA", "Lengkapi kalimat: 'We are ___ for the train.'", listOf("waiting", "wait", "waited"), "waiting")
        )
        13 -> listOf(
            QuizQuestion(1, "PERCAKAPAN", "Bagaimana menanyakan harga barang?", listOf("How much does it cost?", "Where is the shop?", "Do you have money?"), "How much does it cost?"),
            QuizQuestion(2, "PERCAKAPAN", "Apa arti kalimat 'Can I try this on?'", listOf("Bolehkah saya membelinya?", "Bisakah saya mencobanya (pakaian)?", "Apakah ada diskon?"), "Bisakah saya mencobanya (pakaian)?"),
            QuizQuestion(3, "KOSAKATA", "Kata 'Discount' memiliki arti...", listOf("Biaya tambahan", "Diskon / Potongan harga", "Kembalian uang"), "Diskon / Potongan harga"),
            QuizQuestion(4, "PERCAKAPAN", "Di mana kita membayar belanjaan di toko?", listOf("Fitting room", "Cashier", "Entrance"), "Cashier"),
            QuizQuestion(5, "PERCAKAPAN", "Jika harga barang terlalu tinggi, kita bisa menyebutnya...", listOf("Too cheap", "Too expensive", "Fair price"), "Too expensive")
        )
        14 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Apa bahasa Inggris dari perasaan 'Bahagia'?", listOf("Sad", "Angry", "Happy"), "Happy"),
            QuizQuestion(2, "KOSAKATA", "Jika Anda kurang tidur dan butuh istirahat, Anda merasa...", listOf("Tired", "Excited", "Scared"), "Tired"),
            QuizQuestion(3, "PERCAKAPAN", "Terjemahkan kalimat: 'I feel nervous about the exam.'", listOf("Saya merasa gugup tentang ujian ini.", "Saya merasa senang tentang ujian ini.", "Saya malas ikut ujian ini."), "Saya merasa gugup tentang ujian ini."),
            QuizQuestion(4, "KOSAKATA", "Kata 'Angry' memiliki arti...", listOf("Sedih", "Marah", "Takut"), "Marah"),
            QuizQuestion(5, "KOSAKATA", "Apa arti kata perasaan 'Bored'?", listOf("Bosan", "Capek", "Semangat"), "Bosan")
        )
        15 -> listOf(
            QuizQuestion(1, "TATA BAHASA", "Bentuk lampau (Verb 2) dari 'go' adalah...", listOf("goes", "went", "gone"), "went"),
            QuizQuestion(2, "TATA BAHASA", "Lengkapi kalimat lampau: 'Yesterday, I ___ a good movie.'", listOf("watch", "watched", "watching"), "watched"),
            QuizQuestion(3, "TATA BAHASA", "Bentuk negatif lampau: 'We ___ not go to the museum last week.'", listOf("did", "do", "does"), "did"),
            QuizQuestion(4, "TATA BAHASA", "Lengkapi kalimat: 'They ___ happy yesterday.'", listOf("was", "were", "are"), "were"),
            QuizQuestion(5, "TATA BAHASA", "Bentuk lampau (Verb 2) dari 'buy' adalah...", listOf("bought", "buyed", "buys"), "bought")
        )
        16 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Apa arti kata 'Headache'?", listOf("Sakit kepala", "Sakit perut", "Demam"), "Sakit kepala"),
            QuizQuestion(2, "PERCAKAPAN", "Bagaimana mendoakan orang sakit dalam bahasa Inggris?", listOf("Happy birthday!", "Congratulations!", "Get well soon!"), "Get well soon!"),
            QuizQuestion(3, "KOSAKATA", "Apa terjemahan dari kata 'Medicine'?", listOf("Resep", "Obat", "Plester"), "Obat"),
            QuizQuestion(4, "KOSAKATA", "Arti kata 'Fever' adalah...", listOf("Batuk", "Pilek", "Demam"), "Demam"),
            QuizQuestion(5, "PERCAKAPAN", "Terjemahkan kalimat: 'You should rest and drink water.'", listOf("Kamu harus berjalan-jalan di luar.", "Kamu sebaiknya beristirahat dan minum air.", "Kamu harus segera minum obat."), "Kamu sebaiknya beristirahat dan minum air.")
        )
        17 -> listOf(
            QuizQuestion(1, "TATA BAHASA", "Lengkapi kalimat rencana: 'I ___ call you tomorrow.'", listOf("will", "going to", "am"), "will"),
            QuizQuestion(2, "TATA BAHASA", "Lengkapi kalimat: 'She is ___ to study tonight.'", listOf("will", "go", "going"), "going"),
            QuizQuestion(3, "KOSAKATA", "Apa arti keterangan waktu 'Next week'?", listOf("Minggu ini", "Minggu lalu", "Minggu depan"), "Minggu depan"),
            QuizQuestion(4, "PERCAKAPAN", "Terjemahkan kalimat: 'We will visit Japan next year.'", listOf("Kami akan mengunjungi Jepang tahun depan.", "Kami mengunjungi Jepang tahun lalu.", "Kami ingin pergi ke Jepang."), "Kami akan mengunjungi Jepang tahun depan."),
            QuizQuestion(5, "TATA BAHASA", "Lengkapi kalimat rencana: 'They ___ going to buy a car.'", listOf("is", "am", "are"), "are")
        )
        18 -> listOf(
            QuizQuestion(1, "KOSAKATA", "Apa arti kata cuaca 'Sunny'?", listOf("Cerah", "Hujan", "Berangin"), "Cerah"),
            QuizQuestion(2, "KOSAKATA", "Kata 'Rainy season' memiliki arti...", listOf("Musim kemarau", "Musim hujan", "Musim gugur"), "Musim hujan"),
            QuizQuestion(3, "KOSAKATA", "Apa arti kata cuaca 'Windy'?", listOf("Berangin", "Mendung", "Cerah"), "Berangin"),
            QuizQuestion(4, "KOSAKATA", "Nama musim 'Summer' berarti...", listOf("Musim dingin", "Musim panas", "Musim semi"), "Musim panas"),
            QuizQuestion(5, "PERCAKAPAN", "Terjemahkan kalimat: 'It is freezing cold in the winter.'", listOf("Cuaca sangat berangin di musim gugur.", "Di luar sangat panas di musim kemarau.", "Suhu sangat dingin membeku di musim dingin."), "Suhu sangat dingin membeku di musim dingin.")
        )
        19 -> listOf(
            QuizQuestion(1, "PERCAKAPAN", "Bagaimana meminta tolong secara sopan?", listOf("Help me now!", "Could you please help me?", "Must you help me?"), "Could you please help me?"),
            QuizQuestion(2, "PERCAKAPAN", "Apa arti dari 'May I borrow your pen?'", listOf("Bolehkah saya meminjam penamu?", "Haruskah saya membeli pena ini?", "Bolehkah saya menggunakan pena ini?"), "Bolehkah saya meminjam penamu?"),
            QuizQuestion(3, "TATA BAHASA", "Kata 'Should' biasanya digunakan untuk...", listOf("Memberikan saran", "Membuat larangan keras", "Menyatakan kepastian"), "Memberikan saran"),
            QuizQuestion(4, "PERCAKAPAN", "Terjemahkan kalimat: 'Would you like some tea?'", listOf("Bolehkah saya minum teh?", "Apakah Anda ingin teh?", "Mengapa Anda minum teh?"), "Apakah Anda ingin teh?"),
            QuizQuestion(5, "TATA BAHASA", "Lengkapi kalimat larangan: 'You ___ not park here.'", listOf("must", "can", "should"), "must")
        )
        20 -> listOf(
            QuizQuestion(1, "MEMAHAMI", "Apa arti dari idiom 'A piece of cake'?", listOf("Kue manis", "Sangat mudah", "Membagi makanan"), "Sangat mudah"),
            QuizQuestion(2, "MEMAHAMI", "Jika seseorang berkata 'I am under the weather', artinya dia sedang...", listOf("Sakit / kurang sehat", "Sangat bahagia", "Kepayahan karena cuaca"), "Sakit / kurang sehat"),
            QuizQuestion(3, "MEMAHAMI", "Kapan kita mengucapkan idiom 'Break a leg!'?", listOf("Saat terjadi kecelakaan", "Sebelum orang tampil atau ujian", "Saat pesta dimulai"), "Sebelum orang tampil atau ujian"),
            QuizQuestion(4, "MEMAHAMI", "Apa arti dari ungkapan 'Let's call it a day'?", listOf("Mari kita sudahi pekerjaan hari ini", "Mari kita hubungi dia sekarang", "Selamat berlibur"), "Mari kita sudahi pekerjaan hari ini"),
            QuizQuestion(5, "MEMAHAMI", "Apa maksud peribahasa 'Better late than never'?", listOf("Selalu datang tepat waktu", "Jangan pernah terlambat", "Lebih baik terlambat daripada tidak sama sekali"), "Lebih baik terlambat daripada tidak sama sekali")
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
