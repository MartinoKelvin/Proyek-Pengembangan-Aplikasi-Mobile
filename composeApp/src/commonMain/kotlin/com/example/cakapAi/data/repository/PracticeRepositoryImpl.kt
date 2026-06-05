package com.example.cakapAi.data.repository

import com.example.cakapAi.data.remote.api.GeminiService
import com.example.cakapAi.domain.model.PracticeQuestion
import com.example.cakapAi.domain.model.PracticeQuestionType
import com.example.cakapAi.domain.repository.PracticeRepository
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString

class PracticeRepositoryImpl(
    private val geminiService: GeminiService
) : PracticeRepository {

    private val json = Json { ignoreUnknownKeys = true }

    override suspend fun generatePracticeQuestions(
        levelId: Int,
        levelTitle: String,
        levelType: String
    ): Result<List<PracticeQuestion>> = runCatching {
        val prompt = """
            Kamu adalah guru bahasa Inggris profesional. Buat 5 soal latihan bahasa Inggris berdasarkan data berikut:
            - Level ID: $levelId
            - Tema Level: $levelTitle
            - Fokus Latihan: $levelType

            ATURAN WAJIB:
            1. Berikan campuran dari 3 tipe soal: "MULTIPLE_CHOICE", "FILL_BLANK", dan "SPEAKING".
            2. Untuk soal "FILL_BLANK", WAJIB berikan tepat 3 kata pengecoh beserta kata yang benar di dalam array "options".
            3. Untuk soal "SPEAKING", "options" boleh kosong [], namun "prompt" harus berisi kalimat yang harus diucapkan user.
            4. "explanation" harus berisi penjelasan singkat dalam bahasa Indonesia mengapa jawaban tersebut benar.
            5. JAWABAN HANYA BOLEH BERUPA JSON ARRAY murni. JANGAN gunakan tag markdown seperti ```json atau teks pembuka/penutup lainnya.

            Format JSON yang diharapkan:
            [
              {
                "id": "unik-id",
                "levelId": $levelId,
                "type": "MULTIPLE_CHOICE",
                "instruction": "Pilih terjemahan yang tepat",
                "prompt": "What is the meaning of 'book'?",
                "options": ["Buku", "Meja", "Kursi", "Pintu"],
                "correctAnswer": "Buku",
                "explanation": "'Book' adalah kata benda dalam bahasa Inggris yang berarti 'Buku'."
              }
            ]
        """.trimIndent()

        val response = geminiService.generateContent(prompt).getOrThrow()
        
        // Clean markdown backticks if any
        val cleanedJson = response.replace("```json", "").replace("```", "").trim()
        
        json.decodeFromString<List<PracticeQuestion>>(cleanedJson)
    }

    override suspend fun getOfflineQuestions(levelId: Int): List<PracticeQuestion> {
        return when (levelId) {
            1 -> listOf(
                PracticeQuestion(
                    id = "lvl1-q1",
                    levelId = 1,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Apa arti dari ungkapan sapaan 'Good Morning'?",
                    options = listOf("Selamat pagi", "Selamat malam", "Selamat sore", "Selamat tinggal"),
                    correctAnswer = "Selamat pagi",
                    explanation = "'Good morning' diterjemahkan sebagai 'Selamat pagi'."
                ),
                PracticeQuestion(
                    id = "lvl1-q2",
                    levelId = 1,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Bagaimana menerjemahkan kalimat 'Nice to meet you' ke bahasa Indonesia?",
                    options = listOf("Senang bertemu denganmu", "Apa kabarmu hari ini", "Selamat tinggal teman", "Permisi sebentar"),
                    correctAnswer = "Senang bertemu denganmu",
                    explanation = "'Nice to meet you' berarti 'Senang bertemu denganmu'."
                ),
                PracticeQuestion(
                    id = "lvl1-q3",
                    levelId = 1,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Bagaimana cara mengucapkan 'Terima kasih' dalam bahasa Inggris?",
                    options = listOf("Thank you", "You are welcome", "Excuse me", "Sorry"),
                    correctAnswer = "Thank you",
                    explanation = "'Thank you' berarti 'Terima kasih'."
                ),
                PracticeQuestion(
                    id = "lvl1-q4",
                    levelId = 1,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Apa arti dari ungkapan perpisahan 'Goodbye'?",
                    options = listOf("Selamat tinggal", "Halo", "Selamat datang", "Sampai jumpa besok"),
                    correctAnswer = "Selamat tinggal",
                    explanation = "'Goodbye' berarti 'Selamat tinggal'."
                ),
                PracticeQuestion(
                    id = "lvl1-q5",
                    levelId = 1,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Terjemahkan pertanyaan sederhana 'How are you?'",
                    options = listOf("Apa kabar?", "Di mana rumahmu?", "Siapa namamu?", "Berapa umurmu?"),
                    correctAnswer = "Apa kabar?",
                    explanation = "'How are you?' digunakan untuk menanyakan kabar."
                ),
                PracticeQuestion(
                    id = "lvl1-q6",
                    levelId = 1,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "Hello, my name ___ John.",
                    options = listOf("is", "am", "are", "be"),
                    correctAnswer = "is",
                    explanation = "Gunakan to-be 'is' untuk subjek tunggal orang ketiga 'my name'."
                ),
                PracticeQuestion(
                    id = "lvl1-q7",
                    levelId = 1,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "Nice to ___ you.",
                    options = listOf("meet", "meeting", "met", "meets"),
                    correctAnswer = "meet",
                    explanation = "Setelah 'to' gunakan infinitive verb 'meet'."
                ),
                PracticeQuestion(
                    id = "lvl1-q8",
                    levelId = 1,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat sapaan berikut",
                    prompt = "Good morning, how are you today?",
                    correctAnswer = "Good morning, how are you today?",
                    explanation = "Latihan mengucapkan sapaan pagi secara lengkap."
                ),
                PracticeQuestion(
                    id = "lvl1-q9",
                    levelId = 1,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "Nice to meet you too.",
                    correctAnswer = "Nice to meet you too.",
                    explanation = "Latihan membalas sapaan perkenalan."
                ),
                PracticeQuestion(
                    id = "lvl1-q10",
                    levelId = 1,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "___ morning, teacher!",
                    options = listOf("Good", "Nice", "Hello", "How"),
                    correctAnswer = "Good",
                    explanation = "Ungkapan yang tepat untuk menyapa guru di pagi hari adalah 'Good morning'."
                )
            )
            2 -> listOf(
                PracticeQuestion(
                    id = "lvl2-q1",
                    levelId = 2,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Apa arti kata buah-buahan 'Apple' dalam bahasa Indonesia?",
                    options = listOf("Apel", "Nanas", "Jeruk", "Pisang"),
                    correctAnswer = "Apel",
                    explanation = "'Apple' berarti 'Apel'."
                ),
                PracticeQuestion(
                    id = "lvl2-q2",
                    levelId = 2,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Kata benda 'Book' memiliki arti...",
                    options = listOf("Buku", "Meja", "Pena", "Tas"),
                    correctAnswer = "Buku",
                    explanation = "'Book' diterjemahkan sebagai 'Buku'."
                ),
                PracticeQuestion(
                    id = "lvl2-q3",
                    levelId = 2,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Terjemahkan kata kebutuhan pokok 'Water'!",
                    options = listOf("Air", "Makanan", "Udara", "Api"),
                    correctAnswer = "Air",
                    explanation = "'Water' berarti 'Air'."
                ),
                PracticeQuestion(
                    id = "lvl2-q4",
                    levelId = 2,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Binatang peliharaan 'Cat' memiliki arti...",
                    options = listOf("Kucing", "Anjing", "Kelinci", "Burung"),
                    correctAnswer = "Kucing",
                    explanation = "'Cat' adalah 'Kucing'."
                ),
                PracticeQuestion(
                    id = "lvl2-q5",
                    levelId = 2,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Tempat belajar 'School' diterjemahkan menjadi...",
                    options = listOf("Sekolah", "Taman", "Rumah Sakit", "Pasar"),
                    correctAnswer = "Sekolah",
                    explanation = "'School' adalah tempat belajar yang berarti 'Sekolah'."
                ),
                PracticeQuestion(
                    id = "lvl2-q6",
                    levelId = 2,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "I write my lesson with a ___.",
                    options = listOf("pen", "car", "apple", "dog"),
                    correctAnswer = "pen",
                    explanation = "Alat tulis yang tepat adalah 'pen' (pulpen)."
                ),
                PracticeQuestion(
                    id = "lvl2-q7",
                    levelId = 2,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "The color of milk is ___.",
                    options = listOf("white", "black", "red", "yellow"),
                    correctAnswer = "white",
                    explanation = "Warna susu pada umumnya adalah 'white' (putih)."
                ),
                PracticeQuestion(
                    id = "lvl2-q8",
                    levelId = 2,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "I eat an apple everyday.",
                    correctAnswer = "I eat an apple everyday.",
                    explanation = "Latihan pengucapan kosakata buah."
                ),
                PracticeQuestion(
                    id = "lvl2-q9",
                    levelId = 2,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "This is my new book.",
                    correctAnswer = "This is my new book.",
                    explanation = "Latihan pengucapan kosakata benda sekolah."
                ),
                PracticeQuestion(
                    id = "lvl2-q10",
                    levelId = 2,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Kata benda 'Dog' memiliki arti...",
                    options = listOf("Anjing", "Kelinci", "Kuda", "Kambing"),
                    correctAnswer = "Anjing",
                    explanation = "'Dog' berarti 'Anjing'."
                )
            )
            3 -> listOf(
                PracticeQuestion(
                    id = "lvl3-q1",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat pronoun berikut",
                    prompt = "I ___ a student.",
                    options = listOf("am", "is", "are", "be"),
                    correctAnswer = "am",
                    explanation = "Untuk subjek orang pertama tunggal 'I', to-be yang tepat adalah 'am'."
                ),
                PracticeQuestion(
                    id = "lvl3-q2",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat pronoun berikut",
                    prompt = "___ is reading a book.",
                    options = listOf("She", "They", "We", "I"),
                    correctAnswer = "She",
                    explanation = "Karena to-be nya 'is', maka subjek yang tepat adalah 'She' (orang ketiga tunggal)."
                ),
                PracticeQuestion(
                    id = "lvl3-q3",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi present tense berikut",
                    prompt = "They ___ football everyday.",
                    options = listOf("play", "plays", "playing", "played"),
                    correctAnswer = "play",
                    explanation = "Subjek jamak 'They' menggunakan kata kerja bentuk dasar tanpa akhiran 's/es' yaitu 'play'."
                ),
                PracticeQuestion(
                    id = "lvl3-q4",
                    levelId = 3,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih bentuk lampau (past tense)",
                    prompt = "Bentuk lampau dari kata kerja 'go' adalah...",
                    options = listOf("went", "gone", "goes", "going"),
                    correctAnswer = "went",
                    explanation = "Kata kerja 'go' tidak beraturan (irregular verb), bentuk lampau ke-2 nya adalah 'went'."
                ),
                PracticeQuestion(
                    id = "lvl3-q5",
                    levelId = 3,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih bentuk jamak (plural)",
                    prompt = "Bentuk jamak (plural) dari kata 'child' adalah...",
                    options = listOf("children", "childs", "childrens", "childes"),
                    correctAnswer = "children",
                    explanation = "Plural dari 'child' adalah tidak beraturan yaitu 'children'."
                ),
                PracticeQuestion(
                    id = "lvl3-q6",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "She ___ to school yesterday.",
                    options = listOf("went", "go", "goes", "going"),
                    correctAnswer = "went",
                    explanation = "Karena ada keterangan waktu lampau 'yesterday', gunakan verb-2 'went'."
                ),
                PracticeQuestion(
                    id = "lvl3-q7",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "He ___ a new car.",
                    options = listOf("has", "have", "having", "had"),
                    correctAnswer = "has",
                    explanation = "Gunakan auxiliary 'has' untuk subjek tunggal 'He' pada present tense."
                ),
                PracticeQuestion(
                    id = "lvl3-q8",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat tanya berikut",
                    prompt = "___ you speak English?",
                    options = listOf("Do", "Does", "Are", "Is"),
                    correctAnswer = "Do",
                    explanation = "Subjek 'you' menggunakan kata bantu tanya 'Do'."
                ),
                PracticeQuestion(
                    id = "lvl3-q9",
                    levelId = 3,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat tata bahasa berikut",
                    prompt = "She reads a book in the library.",
                    correctAnswer = "She reads a book in the library.",
                    explanation = "Latihan pengucapan kalimat berstruktur Present Tense."
                ),
                PracticeQuestion(
                    id = "lvl3-q10",
                    levelId = 3,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "We are learning English together.",
                    correctAnswer = "We are learning English together.",
                    explanation = "Latihan pengucapan Present Continuous Tense."
                )
            )
            4 -> listOf(
                PracticeQuestion(
                    id = "lvl4-q1",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "A: 'How is the weather today?'\nB: 'It is ___.' (Cerah)",
                    options = listOf("sunny", "rainy", "snowy", "windy"),
                    correctAnswer = "sunny",
                    explanation = "'Sunny' berarti cuaca cerah/terang matahari."
                ),
                PracticeQuestion(
                    id = "lvl4-q2",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Bagaimana menanyakan arah 'Where is the restroom?' dalam bahasa Indonesia?",
                    options = listOf("Di mana toiletnya?", "Kapan stasiun dibuka?", "Berapa harga tiket ini?", "Ke mana jalan ke pasar?"),
                    correctAnswer = "Di mana toiletnya?",
                    explanation = "'Restroom' berarti toilet/kamar kecil."
                ),
                PracticeQuestion(
                    id = "lvl4-q3",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Terjemahkan ungkapan permohonan 'Can you help me?'",
                    options = listOf("Bisakah Anda membantu saya?", "Apakah Anda tahu jalan ini?", "Permisi, jam berapa sekarang?", "Kapan ini selesai?"),
                    correctAnswer = "Bisakah Anda membantu saya?",
                    explanation = "'Can you help me?' berarti meminta bantuan."
                ),
                PracticeQuestion(
                    id = "lvl4-q4",
                    levelId = 4,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi percakapan berikut",
                    prompt = "A: 'What time is it?'\nB: 'It is five ___.'",
                    options = listOf("o'clock", "hours", "minutes", "seconds"),
                    correctAnswer = "o'clock",
                    explanation = "Gunakan 'o'clock' untuk menyatakan jam pas/tepat."
                ),
                PracticeQuestion(
                    id = "lvl4-q5",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Bagaimana menanyakan harga barang 'How much is this?'",
                    options = listOf("Berapa harganya?", "Di mana ini?", "Kapan ini selesai?", "Siapa ini?"),
                    correctAnswer = "Berapa harganya?",
                    explanation = "'How much' digunakan untuk menanyakan harga barang."
                ),
                PracticeQuestion(
                    id = "lvl4-q6",
                    levelId = 4,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi percakapan berikut",
                    prompt = "A: 'How do you do?'\nB: '___ do you do?'",
                    options = listOf("How", "Who", "What", "Nice"),
                    correctAnswer = "How",
                    explanation = "Sapaan formal pertama kali 'How do you do' dibalas dengan 'How do you do'."
                ),
                PracticeQuestion(
                    id = "lvl4-q7",
                    levelId = 4,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi percakapan berikut",
                    prompt = "A: 'Thank you!'\nB: 'You are ___.'",
                    options = listOf("welcome", "thanks", "well", "fine"),
                    correctAnswer = "welcome",
                    explanation = "Balasan umum dari terima kasih adalah 'You are welcome'."
                ),
                PracticeQuestion(
                    id = "lvl4-q8",
                    levelId = 4,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat percakapan berikut",
                    prompt = "Can you tell me where the station is?",
                    correctAnswer = "Can you tell me where the station is?",
                    explanation = "Latihan percakapan menanyakan arah ke stasiun."
                ),
                PracticeQuestion(
                    id = "lvl4-q9",
                    levelId = 4,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "How much does this shirt cost?",
                    correctAnswer = "How much does this shirt cost?",
                    explanation = "Latihan percakapan berbelanja pakaian."
                ),
                PracticeQuestion(
                    id = "lvl4-q10",
                    levelId = 4,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat penawaran berikut",
                    prompt = "Would you like some tea?",
                    correctAnswer = "Would you like some tea?",
                    explanation = "Latihan percakapan menawarkan minuman dengan sopan."
                )
            )
            5 -> listOf(
                PracticeQuestion(
                    id = "lvl5-q1",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Bagaimana mengucap sopan 'Excuse me' untuk permisi?",
                    options = listOf("Permisi", "Maaf", "Halo", "Selamat tinggal"),
                    correctAnswer = "Permisi",
                    explanation = "'Excuse me' paling tepat diterjemahkan sebagai 'Permisi'."
                ),
                PracticeQuestion(
                    id = "lvl5-q2",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Bagaimana membalas terima kasih dengan ramah 'No problem'?",
                    options = listOf("Tidak masalah", "Sama-sama", "Tentu saja", "Sangat bagus"),
                    correctAnswer = "Tidak masalah",
                    explanation = "'No problem' berarti 'Tidak masalah/Tidak apa-apa'."
                ),
                PracticeQuestion(
                    id = "lvl5-q3",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Ungkapan selamat 'Congratulations!' diterjemahkan menjadi...",
                    options = listOf("Selamat!", "Semoga berhasil!", "Hati-hati!", "Terima kasih!"),
                    correctAnswer = "Selamat!",
                    explanation = "'Congratulations!' digunakan untuk memberi selamat."
                ),
                PracticeQuestion(
                    id = "lvl5-q4",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Kata penyambutan hangat 'Welcome' berarti...",
                    options = listOf("Selamat datang", "Selamat tinggal", "Sampai jumpa", "Permisi"),
                    correctAnswer = "Selamat datang",
                    explanation = "'Welcome' memiliki arti penyambutan atau 'Selamat datang'."
                ),
                PracticeQuestion(
                    id = "lvl5-q5",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Ungkapan doa baik 'Have a nice day!' berarti...",
                    options = listOf("Semoga hari Anda menyenangkan!", "Semoga sukses selalu!", "Selamat beristirahat!", "Hati-hati di jalan!"),
                    correctAnswer = "Semoga hari Anda menyenangkan!",
                    explanation = "'Have a nice day!' mendoakan agar hari yang dilalui menyenangkan."
                ),
                PracticeQuestion(
                    id = "lvl5-q6",
                    levelId = 5,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "___ me, where is the exit?",
                    options = listOf("Excuse", "Sorry", "Please", "Pardon"),
                    correctAnswer = "Excuse",
                    explanation = "Gunakan 'Excuse me' untuk permisi bertanya secara sopan."
                ),
                PracticeQuestion(
                    id = "lvl5-q7",
                    levelId = 5,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat percakapan berikut",
                    prompt = "Excuse me, could you speak slower?",
                    correctAnswer = "Excuse me, could you speak slower?",
                    explanation = "Latihan berbicara meminta lawan bicara melambatkan ucapan."
                ),
                PracticeQuestion(
                    id = "lvl5-q8",
                    levelId = 5,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan ucapan selamat berikut",
                    prompt = "Congratulations on your graduation!",
                    correctAnswer = "Congratulations on your graduation!",
                    explanation = "Latihan pengucapan selamat kelulusan."
                ),
                PracticeQuestion(
                    id = "lvl5-q9",
                    levelId = 5,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan doa keselamatan berikut",
                    prompt = "Have a safe and nice trip!",
                    correctAnswer = "Have a safe and nice trip!",
                    explanation = "Latihan mendoakan perjalanan seseorang agar selamat."
                ),
                PracticeQuestion(
                    id = "lvl5-q10",
                    levelId = 5,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat apresiasi berikut",
                    prompt = "Thank you for your warm welcome.",
                    correctAnswer = "Thank you for your warm welcome.",
                    explanation = "Latihan pengucapan terima kasih atas sambutan."
                )
            )
            else -> listOf(
                PracticeQuestion(
                    id = "gen-q1",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti idiom yang tepat",
                    prompt = "Apa arti dari ungkapan idiom 'To make a long story short'?",
                    options = listOf("Mempersingkat cerita", "Memperpanjang masalah", "Membaca buku cerita", "Berbohong kepada orang lain"),
                    correctAnswer = "Mempersingkat cerita",
                    explanation = "'To make a long story short' digunakan untuk langsung ke inti masalah/mempersingkat cerita."
                ),
                PracticeQuestion(
                    id = "gen-q2",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti idiom yang tepat",
                    prompt = "Jika seseorang berkata 'I am feeling under the weather', artinya dia sedang...",
                    options = listOf("Kurang enak badan", "Sangat bahagia", "Kepanasan karena cuaca", "Ingin pergi keluar"),
                    correctAnswer = "Kurang enak badan",
                    explanation = "'Under the weather' adalah ungkapan idiom untuk merasa sakit atau kurang enak badan."
                ),
                PracticeQuestion(
                    id = "gen-q3",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih maksud ungkapan yang tepat",
                    prompt = "Kapan Anda biasanya mengucapkan 'Break a leg!' kepada teman?",
                    options = listOf("Sebelum mereka tampil/ujian", "Saat mereka mendapat musibah", "Ketika mereka sedang tidur", "Saat merayakan ulang tahun"),
                    correctAnswer = "Sebelum mereka tampil/ujian",
                    explanation = "'Break a leg!' adalah idiom bahasa Inggris yang berarti 'Semoga sukses!' (Good luck!)."
                ),
                PracticeQuestion(
                    id = "gen-q4",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti idiom yang tepat",
                    prompt = "Apa arti ungkapan 'A piece of cake'?",
                    options = listOf("Sangat mudah", "Kue yang manis", "Membagi makanan", "Masalah yang rumit"),
                    correctAnswer = "Sangat mudah",
                    explanation = "'A piece of cake' menggambarkan suatu pekerjaan yang sangat mudah dilakukan."
                ),
                PracticeQuestion(
                    id = "gen-q5",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat pengandaian (conditional sentence) berikut",
                    prompt = "If I ___ you, I would study harder.",
                    options = listOf("were", "was", "am", "be"),
                    correctAnswer = "were",
                    explanation = "Dalam conditional sentence type 2, subjek 'I' menggunakan 'were' untuk pengandaian yang tidak nyata."
                ),
                PracticeQuestion(
                    id = "gen-q6",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat Past Perfect berikut",
                    prompt = "By the time we arrived, the movie ___ already started.",
                    options = listOf("had", "has", "have", "was"),
                    correctAnswer = "had",
                    explanation = "Gunakan 'had' karena kejadian film dimulai terjadi sebelum kejadian tiba (arrived) di masa lampau."
                ),
                PracticeQuestion(
                    id = "gen-q7",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat gerund berikut",
                    prompt = "She is looking forward to ___ her grandparents.",
                    options = listOf("visiting", "visit", "visited", "visits"),
                    correctAnswer = "visiting",
                    explanation = "Frasa 'look forward to' harus diikuti oleh Verb-ing (Gerund) / kata benda."
                ),
                PracticeQuestion(
                    id = "gen-q8",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi preposisi waktu berikut",
                    prompt = "I have been living here ___ three years.",
                    options = listOf("for", "since", "during", "ago"),
                    correctAnswer = "for",
                    explanation = "Gunakan 'for' untuk menunjukkan durasi waktu (selama tiga tahun) pada Present Perfect."
                ),
                PracticeQuestion(
                    id = "gen-q9",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pepatah bahasa Inggris berikut",
                    prompt = "Actions speak louder than words.",
                    correctAnswer = "Actions speak louder than words.",
                    explanation = "Pepatah ini berarti perbuatan lebih nyata pembuktiannya daripada sekadar kata-kata."
                ),
                PracticeQuestion(
                    id = "gen-q10",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pepatah berikut dengan lancar",
                    prompt = "Practice makes perfect.",
                    correctAnswer = "Practice makes perfect.",
                    explanation = "Arti: Latihan terus-menerus akan membuat kita menjadi mahir/sempurna."
                ),
                PracticeQuestion(
                    id = "gen-q11",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih antonim kata yang tepat",
                    prompt = "Apa lawan kata (opposite) dari 'generous' (dermawan)?",
                    options = listOf("Stingy", "Kind", "Polite", "Rich"),
                    correctAnswer = "Stingy",
                    explanation = "Generous berarti dermawan, antonimnya adalah 'Stingy' yang berarti pelit kikir."
                ),
                PracticeQuestion(
                    id = "gen-q12",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan kosakata yang tepat",
                    prompt = "Apa arti dari kata kerja 'postpone'?",
                    options = listOf("Menunda", "Melanjutkan", "Membatalkan", "Mempercepat"),
                    correctAnswer = "Menunda",
                    explanation = "'Postpone' berarti menjadwalkan ulang ke waktu berikutnya atau 'Menunda'."
                ),
                PracticeQuestion(
                    id = "gen-q13",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih sinonim kata yang tepat",
                    prompt = "Manakah kata yang merupakan sinonim dari 'accurate'?",
                    options = listOf("Precise", "Vague", "Wrong", "Slow"),
                    correctAnswer = "Precise",
                    explanation = "'Accurate' berarti akurat/tepat, sinonimnya yang cocok adalah 'Precise' (presisi/tepat)."
                ),
                PracticeQuestion(
                    id = "gen-q14",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat relatif (relative pronoun) berikut",
                    prompt = "He is the man ___ car was stolen yesterday.",
                    options = listOf("whose", "who", "whom", "which"),
                    correctAnswer = "whose",
                    explanation = "Gunakan 'whose' untuk menyatakan kepemilikan (pria yang mobilnya dicuri kemarin)."
                ),
                PracticeQuestion(
                    id = "gen-q15",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kesesuaian subjek & verba berikut",
                    prompt = "Neither of the options ___ correct.",
                    options = listOf("is", "are", "were", "be"),
                    correctAnswer = "is",
                    explanation = "Subjek 'Neither of...' dianggap tunggal (singular), sehingga kata bantu yang tepat adalah 'is'."
                ),
                PracticeQuestion(
                    id = "gen-q16",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat inversi berikut",
                    prompt = "Hardly had I opened the door ___ the phone rang.",
                    options = listOf("when", "than", "then", "that"),
                    correctAnswer = "when",
                    explanation = "Struktur inversi 'Hardly had... when...' dipasangkan bersama untuk menyatakan kejadian berurutan cepat."
                ),
                PracticeQuestion(
                    id = "gen-q17",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan permintaan sopan berikut",
                    prompt = "Could you please turn down the volume?",
                    correctAnswer = "Could you please turn down the volume?",
                    explanation = "Meminta seseorang mengecilkan volume suara/musik dengan sopan."
                ),
                PracticeQuestion(
                    id = "gen-q18",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat formal berikut",
                    prompt = "I would highly appreciate your feedback.",
                    correctAnswer = "I would highly appreciate your feedback.",
                    explanation = "Latihan menyatakan apresiasi atas masukan/feedback secara formal."
                ),
                PracticeQuestion(
                    id = "gen-q19",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti idiom yang tepat",
                    prompt = "Apa makna dari frasa idiom 'Break the ice'?",
                    options = listOf("Mencairkan suasana", "Memecahkan es batu", "Memulai perselisihan", "Mengakhiri pembicaraan"),
                    correctAnswer = "Mencairkan suasana",
                    explanation = "'Break the ice' berarti melakukan sesuatu untuk memulai obrolan/mencairkan suasana kaku."
                ),
                PracticeQuestion(
                    id = "gen-q20",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat had better berikut",
                    prompt = "You had better ___ home now.",
                    options = listOf("go", "to go", "going", "went"),
                    correctAnswer = "go",
                    explanation = "Frasa 'had better' diikuti oleh bare infinitive (kata kerja dasar tanpa 'to') yaitu 'go'."
                ),
                PracticeQuestion(
                    id = "gen-q21",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata penghubung berikut",
                    prompt = "She is not only smart but ___ kind.",
                    options = listOf("also", "too", "either", "as well"),
                    correctAnswer = "also",
                    explanation = "Pasangan kata hubung korelatif untuk 'not only' adalah 'but also'."
                ),
                PracticeQuestion(
                    id = "gen-q22",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat motivasi berikut",
                    prompt = "It is never too late to learn something new.",
                    correctAnswer = "It is never too late to learn something new.",
                    explanation = "Arti: Tidak pernah ada kata terlambat untuk mempelajari hal baru."
                ),
                PracticeQuestion(
                    id = "gen-q23",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti idiom yang tepat",
                    prompt = "Apa arti dari ungkapan idiom 'Let's call it a day'?",
                    options = listOf("Mari kita sudahi hari ini", "Mari kita mulai bekerja", "Ayo hubungi dia sekarang", "Selamat pagi semuanya"),
                    correctAnswer = "Mari kita sudahi hari ini",
                    explanation = "'Let's call it a day' diucapkan untuk menyatakan berhenti melakukan suatu aktivitas pekerjaan."
                ),
                PracticeQuestion(
                    id = "gen-q24",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat tenses berikut",
                    prompt = "He is very tired because he ___ all day.",
                    options = listOf("has been working", "works", "is working", "worked"),
                    correctAnswer = "has been working",
                    explanation = "Gunakan Present Perfect Continuous 'has been working' untuk menyatakan sebab rasa lelah yang berlangsung seharian."
                ),
                PracticeQuestion(
                    id = "gen-q25",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pepatah berikut",
                    prompt = "Better late than never.",
                    correctAnswer = "Better late than never.",
                    explanation = "Pepatah ini berarti lebih baik terlambat daripada tidak sama sekali."
                )
            )
        }
    }
}
