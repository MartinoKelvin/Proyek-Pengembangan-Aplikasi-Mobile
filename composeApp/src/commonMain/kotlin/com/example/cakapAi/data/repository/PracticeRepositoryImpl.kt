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
            - Level ID: ${'$'}levelId
            - Tema Level: ${'$'}levelTitle
            - Fokus Latihan: ${'$'}levelType

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
                "levelId": ${'$'}levelId,
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
                    prompt = "Kata benda 'House' berarti...",
                    options = listOf("Rumah", "Sekolah", "Taman", "Kantor"),
                    correctAnswer = "Rumah",
                    explanation = "'House' diterjemahkan sebagai 'Rumah'."
                ),
                PracticeQuestion(
                    id = "lvl2-q6",
                    levelId = 2,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "I sit on a ___.",
                    options = listOf("chair", "water", "apple", "cat"),
                    correctAnswer = "chair",
                    explanation = "'Chair' (kursi) adalah tempat duduk yang tepat."
                ),
                PracticeQuestion(
                    id = "lvl2-q7",
                    levelId = 2,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "He drives a new ___.",
                    options = listOf("car", "book", "chair", "table"),
                    correctAnswer = "car",
                    explanation = "Kata benda transportasi yang bisa dikendarai (drives) adalah 'car' (mobil)."
                ),
                PracticeQuestion(
                    id = "lvl2-q8",
                    levelId = 2,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat deskriptif berikut",
                    prompt = "This is a blue pen.",
                    correctAnswer = "This is a blue pen.",
                    explanation = "Latihan mengucapkan kalimat deskripsi benda sederhana."
                ),
                PracticeQuestion(
                    id = "lvl2-q9",
                    levelId = 2,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat deskriptif berikut",
                    prompt = "The cat is sleeping on the table.",
                    correctAnswer = "The cat is sleeping on the table.",
                    explanation = "Latihan melafalkan posisi benda dengan preposition 'on'."
                ),
                PracticeQuestion(
                    id = "lvl2-q10",
                    levelId = 2,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "I read a ___ every night.",
                    options = listOf("book", "apple", "car", "water"),
                    correctAnswer = "book",
                    explanation = "Benda yang dibaca (read) adalah 'book' (buku)."
                )
            )
            3 -> listOf(
                PracticeQuestion(
                    id = "lvl3-q1",
                    levelId = 3,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih to-be yang tepat",
                    prompt = "Pilih to-be yang tepat: 'I ___ a student.'",
                    options = listOf("am", "is", "are", "be"),
                    correctAnswer = "am",
                    explanation = "Subjek 'I' berpasangan dengan to-be 'am' di present tense."
                ),
                PracticeQuestion(
                    id = "lvl3-q2",
                    levelId = 3,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih to-be yang tepat",
                    prompt = "Pilih to-be untuk 'She': 'She ___ a doctor.'",
                    options = listOf("is", "am", "are", "was"),
                    correctAnswer = "is",
                    explanation = "Subjek tunggal 'She' berpasangan dengan to-be 'is'."
                ),
                PracticeQuestion(
                    id = "lvl3-q3",
                    levelId = 3,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih to-be yang tepat",
                    prompt = "Pilih to-be untuk 'They': 'They ___ my friends.'",
                    options = listOf("are", "is", "am", "be"),
                    correctAnswer = "are",
                    explanation = "Subjek jamak 'They' berpasangan dengan to-be 'are'."
                ),
                PracticeQuestion(
                    id = "lvl3-q4",
                    levelId = 3,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kata ganti yang tepat",
                    prompt = "Pilih kata ganti (pronoun) untuk laki-laki tunggal:",
                    options = listOf("He", "She", "It", "They"),
                    correctAnswer = "He",
                    explanation = "'He' digunakan untuk menggantikan orang ketiga laki-laki tunggal."
                ),
                PracticeQuestion(
                    id = "lvl3-q5",
                    levelId = 3,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Kata ganti 'We' memiliki arti...",
                    options = listOf("Kita / Kami", "Mereka", "Kamu", "Dia perempuan"),
                    correctAnswer = "Kita / Kami",
                    explanation = "'We' adalah kata ganti orang pertama jamak yang berarti 'Kita' atau 'Kami'."
                ),
                PracticeQuestion(
                    id = "lvl3-q6",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "You ___ very kind.",
                    options = listOf("are", "is", "am", "be"),
                    correctAnswer = "are",
                    explanation = "Subjek 'You' berpasangan dengan to-be 'are'."
                ),
                PracticeQuestion(
                    id = "lvl3-q7",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "It ___ a beautiful day.",
                    options = listOf("is", "are", "am", "be"),
                    correctAnswer = "is",
                    explanation = "Subjek 'It' menggunakan to-be 'is'."
                ),
                PracticeQuestion(
                    id = "lvl3-q8",
                    levelId = 3,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "He is my English teacher.",
                    correctAnswer = "He is my English teacher.",
                    explanation = "Latihan melafalkan to-be untuk subjek tunggal 'He'."
                ),
                PracticeQuestion(
                    id = "lvl3-q9",
                    levelId = 3,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "We are learning together.",
                    correctAnswer = "We are learning together.",
                    explanation = "Latihan melafalkan to-be untuk subjek jamak 'We'."
                ),
                PracticeQuestion(
                    id = "lvl3-q10",
                    levelId = 3,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "They ___ playing in the garden.",
                    options = listOf("are", "is", "am", "be"),
                    correctAnswer = "are",
                    explanation = "Subjek 'They' berpasangan dengan to-be 'are'."
                )
            )
            4 -> listOf(
                PracticeQuestion(
                    id = "lvl4-q1",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih pertanyaan yang tepat",
                    prompt = "Bagaimana cara menanyakan nama seseorang dalam bahasa Inggris?",
                    options = listOf("What is your name?", "How are you?", "Where do you live?", "Who is that?"),
                    correctAnswer = "What is your name?",
                    explanation = "'What is your name?' digunakan untuk menanyakan nama seseorang."
                ),
                PracticeQuestion(
                    id = "lvl4-q2",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Apa arti dari pertanyaan 'How old are you?'?",
                    options = listOf("Berapa umurmu?", "Apa kabarmu?", "Di mana kamu berada?", "Siapa orang tuamu?"),
                    correctAnswer = "Berapa umurmu?",
                    explanation = "'How old are you?' menanyakan usia atau umur."
                ),
                PracticeQuestion(
                    id = "lvl4-q3",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti pertanyaan yang tepat",
                    prompt = "Pertanyaan 'Where are you from?' menanyakan tentang...",
                    options = listOf("Asal negara/daerah", "Pekerjaan", "Hobi", "Alamat rumah sekarang"),
                    correctAnswer = "Asal negara/daerah",
                    explanation = "'Where are you from?' digunakan untuk menanyakan daerah asal."
                ),
                PracticeQuestion(
                    id = "lvl4-q4",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih respon yang tepat",
                    prompt = "Jawaban yang paling tepat untuk sapaan formal 'How do you do?' adalah...",
                    options = listOf("How do you do?", "I am fine, thank you", "Nice to meet you", "Goodbye"),
                    correctAnswer = "How do you do?",
                    explanation = "Sapaan formal 'How do you do?' dijawab kembali dengan ungkapan yang sama."
                ),
                PracticeQuestion(
                    id = "lvl4-q5",
                    levelId = 4,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Menerjemahkan kalimat 'I live in Jakarta' ke bahasa Indonesia:",
                    options = listOf("Saya tinggal di Jakarta", "Saya pergi ke Jakarta", "Saya suka Jakarta", "Saya bekerja di Jakarta"),
                    correctAnswer = "Saya tinggal di Jakarta",
                    explanation = "'Live in' berarti tinggal di."
                ),
                PracticeQuestion(
                    id = "lvl4-q6",
                    levelId = 4,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat percakapan berikut",
                    prompt = "What ___ your phone number?",
                    options = listOf("is", "are", "am", "do"),
                    correctAnswer = "is",
                    explanation = "Gunakan 'is' karena 'your phone number' adalah kata benda tunggal."
                ),
                PracticeQuestion(
                    id = "lvl4-q7",
                    levelId = 4,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat percakapan berikut",
                    prompt = "I am ___ Indonesia.",
                    options = listOf("from", "to", "in", "at"),
                    correctAnswer = "from",
                    explanation = "Preposisi 'from' digunakan untuk menunjukkan asal tempat lahir/negara."
                ),
                PracticeQuestion(
                    id = "lvl4-q8",
                    levelId = 4,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pertanyaan berikut",
                    prompt = "Where do you live?",
                    correctAnswer = "Where do you live?",
                    explanation = "Latihan melafalkan pertanyaan tempat tinggal."
                ),
                PracticeQuestion(
                    id = "lvl4-q9",
                    levelId = 4,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan perkenalan hobi berikut",
                    prompt = "My hobby is playing soccer.",
                    correctAnswer = "My hobby is playing soccer.",
                    explanation = "Latihan melafalkan hobi sepak bola."
                ),
                PracticeQuestion(
                    id = "lvl4-q10",
                    levelId = 4,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat percakapan berikut",
                    prompt = "Nice to meet you. ___ to meet you too.",
                    options = listOf("Nice", "Good", "Fine", "Glad"),
                    correctAnswer = "Nice",
                    explanation = "Balasan sapaan 'Nice to meet you' yang lengkap adalah 'Nice to meet you too'."
                )
            )
            5 -> listOf(
                PracticeQuestion(
                    id = "lvl5-q1",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Ungkapan doa baik 'Have a nice day!' berarti...",
                    options = listOf("Semoga hari Anda menyenangkan!", "Semoga sukses selalu!", "Selamat beristirahat!", "Hati-hati di jalan!"),
                    correctAnswer = "Semoga hari Anda menyenangkan!",
                    explanation = "'Have a nice day!' mendoakan agar hari yang dilalui menyenangkan."
                ),
                PracticeQuestion(
                    id = "lvl5-q2",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih fungsi ungkapan yang tepat",
                    prompt = "Kapan kita biasanya mengucapkan kata 'Excuse me'?",
                    options = listOf("Ketika ingin meminta perhatian/permisi", "Ketika melakukan kesalahan besar", "Saat berpamitan pulang", "Saat memberi selamat"),
                    correctAnswer = "Ketika ingin meminta perhatian/permisi",
                    explanation = "'Excuse me' (Permisi) diucapkan untuk menarik perhatian secara sopan."
                ),
                PracticeQuestion(
                    id = "lvl5-q3",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih balasan yang tepat",
                    prompt = "Balasan sopan dari ucapan terima kasih 'Thank you' adalah...",
                    options = listOf("You are welcome", "No problem", "Never mind", "Excuse me"),
                    correctAnswer = "You are welcome",
                    explanation = "'You are welcome' diterjemahkan sebagai 'Sama-sama'."
                ),
                PracticeQuestion(
                    id = "lvl5-q4",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Apa arti dari kalimat permintaan maaf 'I am sorry'?",
                    options = listOf("Saya minta maaf", "Saya sangat lelah", "Saya senang sekali", "Permisi sebentar"),
                    correctAnswer = "Saya minta maaf",
                    explanation = "'I am sorry' digunakan untuk meminta maaf."
                ),
                PracticeQuestion(
                    id = "lvl5-q5",
                    levelId = 5,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih fungsi ungkapan yang tepat",
                    prompt = "Ucapan 'No problem' paling cocok digunakan untuk...",
                    options = listOf("Membalas ucapan terima kasih / maaf", "Memulai percakapan baru", "Menolak ajakan makan siang", "Menyapa guru di kelas"),
                    correctAnswer = "Membalas ucapan terima kasih / maaf",
                    explanation = "'No problem' berarti 'Tidak masalah', biasa digunakan sebagai respon santai."
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
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "Thank you ___ your help.",
                    options = listOf("for", "to", "with", "at"),
                    correctAnswer = "for",
                    explanation = "Preposisi setelah 'Thank you' untuk menunjukkan alasan bantuan adalah 'for'."
                ),
                PracticeQuestion(
                    id = "lvl5-q8",
                    levelId = 5,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat percakapan berikut",
                    prompt = "Excuse me, could you speak slower?",
                    correctAnswer = "Excuse me, could you speak slower?",
                    explanation = "Latihan berbicara meminta lawan bicara melambatkan ucapan."
                ),
                PracticeQuestion(
                    id = "lvl5-q9",
                    levelId = 5,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan ucapan selamat berikut",
                    prompt = "Congratulations on your graduation!",
                    correctAnswer = "Congratulations on your graduation!",
                    explanation = "Latihan pengucapan selamat kelulusan."
                ),
                PracticeQuestion(
                    id = "lvl5-q10",
                    levelId = 5,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan doa keselamatan berikut",
                    prompt = "Have a safe and nice trip!",
                    correctAnswer = "Have a safe and nice trip!",
                    explanation = "Latihan mendoakan perjalanan seseorang agar selamat."
                )
            )
            6 -> listOf(
                PracticeQuestion(
                    id = "lvl6-q1",
                    levelId = 6,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata keluarga yang tepat",
                    prompt = "Apa arti kata keluarga 'Father'?",
                    options = listOf("Ayah", "Ibu", "Paman", "Kakek"),
                    correctAnswer = "Ayah",
                    explanation = "'Father' adalah sebutan untuk Ayah."
                ),
                PracticeQuestion(
                    id = "lvl6-q2",
                    levelId = 6,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata keluarga yang tepat",
                    prompt = "Siapakah saudara perempuan dari ayah atau ibu kita dalam bahasa Inggris?",
                    options = listOf("Aunt", "Uncle", "Sister", "Cousin"),
                    correctAnswer = "Aunt",
                    explanation = "Saudara perempuan ayah/ibu disebut 'Aunt' (Bibi/Tante)."
                ),
                PracticeQuestion(
                    id = "lvl6-q3",
                    levelId = 6,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata yang tepat",
                    prompt = "Kata 'Brother' memiliki arti...",
                    options = listOf("Saudara laki-laki", "Saudara perempuan", "Teman bermain", "Keponakan laki-laki"),
                    correctAnswer = "Saudara laki-laki",
                    explanation = "'Brother' berarti saudara kandung laki-laki."
                ),
                PracticeQuestion(
                    id = "lvl6-q4",
                    levelId = 6,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata yang tepat",
                    prompt = "Siapakah 'Grandfather'?",
                    options = listOf("Kakek", "Nenek", "Ayah", "Paman"),
                    correctAnswer = "Kakek",
                    explanation = "'Grandfather' berarti Kakek."
                ),
                PracticeQuestion(
                    id = "lvl6-q5",
                    levelId = 6,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan dari ungkapan sepupu",
                    prompt = "Terjemahan dari ungkapan 'My best friend' adalah...",
                    options = listOf("Teman terbaik saya", "Keluarga dekat saya", "Saudara sepupu saya", "Tetangga rumah saya"),
                    correctAnswer = "Teman terbaik saya",
                    explanation = "'Best friend' diterjemahkan sebagai teman baik/terbaik."
                ),
                PracticeQuestion(
                    id = "lvl6-q6",
                    levelId = 6,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "My mother has a sister. She is my ___.",
                    options = listOf("aunt", "uncle", "mother", "sister"),
                    correctAnswer = "aunt",
                    explanation = "Adik atau kakak perempuan ibu disebut 'aunt' (tante)."
                ),
                PracticeQuestion(
                    id = "lvl6-q7",
                    levelId = 6,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "This is a photo of my ___.",
                    options = listOf("family", "friend", "school", "house"),
                    correctAnswer = "family",
                    explanation = "Kumpulan anggota keluarga inti di dalam foto disebut 'family'."
                ),
                PracticeQuestion(
                    id = "lvl6-q8",
                    levelId = 6,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat cinta keluarga berikut",
                    prompt = "I love my father and mother.",
                    correctAnswer = "I love my father and mother.",
                    explanation = "Latihan melafalkan ungkapan kasih sayang kepada orang tua."
                ),
                PracticeQuestion(
                    id = "lvl6-q9",
                    levelId = 6,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut",
                    prompt = "He has two brothers and one sister.",
                    correctAnswer = "He has two brothers and one sister.",
                    explanation = "Latihan menyatakan jumlah saudara kandung."
                ),
                PracticeQuestion(
                    id = "lvl6-q10",
                    levelId = 6,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat berikut",
                    prompt = "My father's brother is my ___.",
                    options = listOf("uncle", "aunt", "grandfather", "brother"),
                    correctAnswer = "uncle",
                    explanation = "Saudara laki-laki ayah disebut 'uncle' (paman)."
                )
            )
            7 -> listOf(
                PracticeQuestion(
                    id = "lvl7-q1",
                    levelId = 7,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih penulisan angka yang tepat",
                    prompt = "Bagaimana cara menulis angka 15 dalam bahasa Inggris?",
                    options = listOf("Fifteen", "Fifty", "Five", "Fiveteen"),
                    correctAnswer = "Fifteen",
                    explanation = "Angka 15 ditulis 'Fifteen'."
                ),
                PracticeQuestion(
                    id = "lvl7-q2",
                    levelId = 7,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti pertanyaan yang tepat",
                    prompt = "Apa arti dari kalimat tanya 'What time is it?'?",
                    options = listOf("Jam berapa sekarang?", "Hari apa sekarang?", "Kapan kamu berangkat?", "Berapa harganya?"),
                    correctAnswer = "Jam berapa sekarang?",
                    explanation = "'What time is it?' adalah cara umum menanyakan waktu."
                ),
                PracticeQuestion(
                    id = "lvl7-q3",
                    levelId = 7,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih penunjuk waktu yang tepat",
                    prompt = "Pernyataan 'It is half past seven' menunjukkan pukul...",
                    options = listOf("07.30", "07.15", "08.30", "06.30"),
                    correctAnswer = "07.30",
                    explanation = "'Half past' menunjukkan lewat 30 menit. Jadi 'half past seven' adalah 07.30."
                ),
                PracticeQuestion(
                    id = "lvl7-q4",
                    levelId = 7,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih angka yang tepat",
                    prompt = "Berapakah nilai angka dari kata 'Fifty-five'?",
                    options = listOf("55", "15", "50", "505"),
                    correctAnswer = "55",
                    explanation = "'Fifty-five' adalah lima puluh lima (55)."
                ),
                PracticeQuestion(
                    id = "lvl7-q5",
                    levelId = 7,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih waktu yang tepat",
                    prompt = "Keterangan waktu 'Eight o'clock' menunjukkan...",
                    options = listOf("Tepat jam 8", "Jam 8 lewat 8 menit", "Jam 7 lewat 45 menit", "Jam 8 kurang 8 menit"),
                    correctAnswer = "Tepat jam 8",
                    explanation = "'O'clock' digunakan untuk menunjukkan waktu yang tepat/pas."
                ),
                PracticeQuestion(
                    id = "lvl7-q6",
                    levelId = 7,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi matematika dasar berikut",
                    prompt = "Ten plus ten is ___.",
                    options = listOf("twenty", "thirty", "fifty", "ten"),
                    correctAnswer = "twenty",
                    explanation = "10 + 10 = 20 (twenty)."
                ),
                PracticeQuestion(
                    id = "lvl7-q7",
                    levelId = 7,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat penunjuk waktu",
                    prompt = "It's a quarter ___ nine (pukul 08.45).",
                    options = listOf("to", "past", "at", "on"),
                    correctAnswer = "to",
                    explanation = "08.45 berarti 15 menit menuju jam 9, diucapkan 'quarter to nine'."
                ),
                PracticeQuestion(
                    id = "lvl7-q8",
                    levelId = 7,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat waktu berikut",
                    prompt = "The train arrives at nine o'clock.",
                    correctAnswer = "The train arrives at nine o'clock.",
                    explanation = "Latihan melafalkan jadwal kedatangan kereta."
                ),
                PracticeQuestion(
                    id = "lvl7-q9",
                    levelId = 7,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan rutinitas pagi berikut",
                    prompt = "I wake up at five in the morning.",
                    correctAnswer = "I wake up at five in the morning.",
                    explanation = "Latihan menyatakan jam bangun pagi."
                ),
                PracticeQuestion(
                    id = "lvl7-q10",
                    levelId = 7,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat jumlah hari berikut",
                    prompt = "There are ___ days in a week.",
                    options = listOf("seven", "six", "five", "twelve"),
                    correctAnswer = "seven",
                    explanation = "Ada 7 hari ('seven') dalam seminggu."
                )
            )
            8 -> listOf(
                PracticeQuestion(
                    id = "lvl8-q1",
                    levelId = 8,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat present tense yang benar",
                    prompt = "Manakah kalimat present tense yang benar untuk subjek 'She'?",
                    options = listOf("She drinks milk every morning.", "She drink milk every morning.", "She drinking milk every morning.", "She is drink milk every morning."),
                    correctAnswer = "She drinks milk every morning.",
                    explanation = "Untuk subjek 'She' kata kerja present tense ditambah akhiran '-s' (drinks)."
                ),
                PracticeQuestion(
                    id = "lvl8-q2",
                    levelId = 8,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Apa arti kalimat 'They play soccer every Sunday'?",
                    options = listOf("Mereka bermain sepak bola setiap hari Minggu.", "Mereka sedang bermain sepak bola hari ini.", "Mereka suka sepak bola di hari Minggu.", "Mereka ingin bermain sepak bola besok."),
                    correctAnswer = "Mereka bermain sepak bola setiap hari Minggu.",
                    explanation = "'Play soccer' berarti bermain sepak bola, dan 'every Sunday' berarti setiap hari Minggu."
                ),
                PracticeQuestion(
                    id = "lvl8-q3",
                    levelId = 8,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat negatif yang benar",
                    prompt = "Bentuk negatif dari kalimat present 'He likes apples' adalah...",
                    options = listOf("He does not like apples.", "He do not like apples.", "He is not like apples.", "He not likes apples."),
                    correctAnswer = "He does not like apples.",
                    explanation = "Gunakan auxiliary verb 'does not' untuk subjek tunggal 'He' dan kembalikan verb ke bentuk dasar 'like'."
                ),
                PracticeQuestion(
                    id = "lvl8-q4",
                    levelId = 8,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih auxiliary verb yang tepat",
                    prompt = "Kata tanya penolong untuk subjek 'You' dalam present tense adalah...",
                    options = listOf("Do", "Does", "Is", "Are"),
                    correctAnswer = "Do",
                    explanation = "Subjek 'You' berpasangan dengan auxiliary 'Do' untuk membuat kalimat tanya."
                ),
                PracticeQuestion(
                    id = "lvl8-q5",
                    levelId = 8,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Arti dari kalimat 'I study English at school' adalah...",
                    options = listOf("Saya belajar bahasa Inggris di sekolah", "Saya mengajar bahasa Inggris di sekolah", "Saya suka bahasa Inggris sekolah", "Saya membawa buku bahasa Inggris"),
                    correctAnswer = "Saya belajar bahasa Inggris di sekolah",
                    explanation = "'Study' berarti belajar, dan 'at school' berarti di sekolah."
                ),
                PracticeQuestion(
                    id = "lvl8-q6",
                    levelId = 8,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat present tense",
                    prompt = "My father ___ to work by bus.",
                    options = listOf("goes", "go", "going", "gone"),
                    correctAnswer = "goes",
                    explanation = "Subjek 'My father' tunggal, maka verb 'go' disesuaikan menjadi 'goes'."
                ),
                PracticeQuestion(
                    id = "lvl8-q7",
                    levelId = 8,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat negatif",
                    prompt = "We ___ not watch TV in the afternoon.",
                    options = listOf("do", "does", "are", "have"),
                    correctAnswer = "do",
                    explanation = "Subjek 'We' menggunakan auxiliary 'do' untuk kalimat negatif."
                ),
                PracticeQuestion(
                    id = "lvl8-q8",
                    levelId = 8,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat present tense berikut",
                    prompt = "My sister lives in New York.",
                    correctAnswer = "My sister lives in New York.",
                    explanation = "Latihan melafalkan verb 'lives' dengan akhiran s."
                ),
                PracticeQuestion(
                    id = "lvl8-q9",
                    levelId = 8,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat present tense berikut",
                    prompt = "He works at a bank downtown.",
                    correctAnswer = "He works at a bank downtown.",
                    explanation = "Latihan melafalkan pekerjaan dan tempat kerja."
                ),
                PracticeQuestion(
                    id = "lvl8-q10",
                    levelId = 8,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat tanya",
                    prompt = "___ she speak Japanese?",
                    options = listOf("Does", "Do", "Is", "Has"),
                    correctAnswer = "Does",
                    explanation = "Kalimat tanya present tense untuk subjek 'she' diawali dengan 'Does'."
                )
            )
            9 -> listOf(
                PracticeQuestion(
                    id = "lvl9-q1",
                    levelId = 9,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih ungkapan meminta menu yang sopan",
                    prompt = "Bagaimana cara meminta daftar menu makanan secara sopan di restoran?",
                    options = listOf("May I have the menu, please?", "Give me the menu!", "Where is the food list?", "I want to see what you eat."),
                    correctAnswer = "May I have the menu, please?",
                    explanation = "'May I have the menu, please?' adalah cara paling sopan meminta daftar menu."
                ),
                PracticeQuestion(
                    id = "lvl9-q2",
                    levelId = 9,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan pertanyaan pelayan",
                    prompt = "Apa arti pertanyaan pelayan 'Would you like to order?'?",
                    options = listOf("Apakah Anda ingin memesan?", "Apakah Anda suka makanan ini?", "Di mana Anda ingin duduk?", "Berapa banyak makanan yang Anda mau?"),
                    correctAnswer = "Apakah Anda ingin memesan?",
                    explanation = "'Would you like to order?' digunakan pelayan untuk menanyakan pesanan pelanggan."
                ),
                PracticeQuestion(
                    id = "lvl9-q3",
                    levelId = 9,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Terjemahkan kalimat 'I want a glass of water'!",
                    options = listOf("Saya ingin segelas air", "Saya mau sebotol air", "Saya memesan jus buah", "Saya butuh es teh"),
                    correctAnswer = "Saya ingin segelas air",
                    explanation = "'A glass of water' berarti segelas air putih."
                ),
                PracticeQuestion(
                    id = "lvl9-q4",
                    levelId = 9,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat meminta tagihan yang tepat",
                    prompt = "Bagaimana cara meminta tagihan pembayaran setelah makan?",
                    options = listOf("Can I have the bill, please?", "I want to pay now!", "Where is the cashier?", "How much is my eating?"),
                    correctAnswer = "Can I have the bill, please?",
                    explanation = "'Can I have the bill, please?' merupakan ungkapan sopan untuk meminta tagihan pembayaran."
                ),
                PracticeQuestion(
                    id = "lvl9-q5",
                    levelId = 9,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti menu makanan berikut",
                    prompt = "Menu makanan populer 'Fried rice' memiliki arti...",
                    options = listOf("Nasi goreng", "Ayam goreng", "Mie goreng", "Kentang goreng"),
                    correctAnswer = "Nasi goreng",
                    explanation = "'Fried rice' diterjemahkan sebagai nasi goreng."
                ),
                PracticeQuestion(
                    id = "lvl9-q6",
                    levelId = 9,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat memesan meja",
                    prompt = "I would like to ___ a table for two.",
                    options = listOf("book", "eat", "drink", "buy"),
                    correctAnswer = "book",
                    explanation = "'Book a table' berarti memesan/reservasi meja."
                ),
                PracticeQuestion(
                    id = "lvl9-q7",
                    levelId = 9,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi ungkapan berikut",
                    prompt = "Here is your food. ___ appetite!",
                    options = listOf("Bon", "Good", "Have", "Nice"),
                    correctAnswer = "Bon",
                    explanation = "Ungkapan bahasa Prancis yang diadopsi ke bahasa Inggris untuk mengucapkan selamat makan adalah 'Bon appetite'."
                ),
                PracticeQuestion(
                    id = "lvl9-q8",
                    levelId = 9,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pesanan makanan berikut",
                    prompt = "I would like to order chicken soup.",
                    correctAnswer = "I would like to order chicken soup.",
                    explanation = "Latihan memesan menu sup ayam."
                ),
                PracticeQuestion(
                    id = "lvl9-q9",
                    levelId = 9,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat ke kasir/pelayan berikut",
                    prompt = "Keep the change, thank you.",
                    correctAnswer = "Keep the change, thank you.",
                    explanation = "Ungkapan untuk memberikan uang kembalian sebagai tip: 'Ambil saja kembaliannya'."
                ),
                PracticeQuestion(
                    id = "lvl9-q10",
                    levelId = 9,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat tanya berikut",
                    prompt = "Is this table ___?",
                    options = listOf("free", "menu", "bill", "order"),
                    correctAnswer = "free",
                    explanation = "Bertanya apakah meja kosong/bisa ditempati: 'Is this table free?'."
                )
            )
            10 -> listOf(
                PracticeQuestion(
                    id = "lvl10-q1",
                    levelId = 10,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kosakata kantor yang tepat",
                    prompt = "Apa arti kata kerja operasional 'Meeting'?",
                    options = listOf("Rapat / Pertemuan", "Istirahat siang", "Perjalanan bisnis", "Evaluasi tahunan"),
                    correctAnswer = "Rapat / Pertemuan",
                    explanation = "'Meeting' adalah rapat atau pertemuan kerja."
                ),
                PracticeQuestion(
                    id = "lvl10-q2",
                    levelId = 10,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata jabatan berikut",
                    prompt = "Siapakah yang dimaksud dengan 'Manager'?",
                    options = listOf("Manajer / Pengelola", "Karyawan magang", "Pemilik saham", "Sekretaris direksi"),
                    correctAnswer = "Manajer / Pengelola",
                    explanation = "'Manager' adalah pimpinan unit atau pengelola tim."
                ),
                PracticeQuestion(
                    id = "lvl10-q3",
                    levelId = 10,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Arti dari aktivitas kantor 'Send an email' adalah...",
                    options = listOf("Mengirim email", "Menerima surat fisik", "Menghapus pesan lama", "Menyalin dokumen rapat"),
                    correctAnswer = "Mengirim email",
                    explanation = "'Send' berarti mengirim dan 'email' berarti surat elektronik."
                ),
                PracticeQuestion(
                    id = "lvl10-q4",
                    levelId = 10,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti istilah kerja berikut",
                    prompt = "Terjemahan istilah 'Deadline' dalam dunia kerja adalah...",
                    options = listOf("Batas waktu pengumpulan", "Jam masuk kantor", "Target omset", "Rencana kegiatan"),
                    correctAnswer = "Batas waktu pengumpulan",
                    explanation = "'Deadline' adalah tenggat waktu terakhir penyelesaian tugas."
                ),
                PracticeQuestion(
                    id = "lvl10-q5",
                    levelId = 10,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata finansial kantor",
                    prompt = "Arti kata 'Salary' adalah...",
                    options = listOf("Gaji", "Pajak", "Bonus liburan", "Uang makan"),
                    correctAnswer = "Gaji",
                    explanation = "'Salary' diterjemahkan sebagai gaji pokok pekerja."
                ),
                PracticeQuestion(
                    id = "lvl10-q6",
                    levelId = 10,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat kantor berikut",
                    prompt = "The manager is leading the ___ now.",
                    options = listOf("meeting", "desk", "coffee", "lunch"),
                    correctAnswer = "meeting",
                    explanation = "Pimpinan memimpin rapat ('meeting')."
                ),
                PracticeQuestion(
                    id = "lvl10-q7",
                    levelId = 10,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat kantor berikut",
                    prompt = "I have to finish my ___ before Friday.",
                    options = listOf("report", "lunch", "break", "leave"),
                    correctAnswer = "report",
                    explanation = "Dokumen yang harus diselesaikan di kantor adalah 'report' (laporan)."
                ),
                PracticeQuestion(
                    id = "lvl10-q8",
                    levelId = 10,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan instruksi kantor berikut",
                    prompt = "Please check your email for the project update.",
                    correctAnswer = "Please check your email for the project update.",
                    explanation = "Latihan mengucapkan permintaan memeriksa email perkembangan proyek."
                ),
                PracticeQuestion(
                    id = "lvl10-q9",
                    levelId = 10,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat diskusi kantor berikut",
                    prompt = "We need to discuss this schedule.",
                    correctAnswer = "We need to discuss this schedule.",
                    explanation = "Latihan melafalkan kebutuhan berdiskusi mengenai jadwal."
                ),
                PracticeQuestion(
                    id = "lvl10-q10",
                    levelId = 10,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat kantor berikut",
                    prompt = "She works at a multinational ___.",
                    options = listOf("company", "desk", "office", "report"),
                    correctAnswer = "company",
                    explanation = "Tempat bekerja skala multinasional disebut 'company' (perusahaan)."
                )
            )
            11 -> listOf(
                PracticeQuestion(
                    id = "lvl11-q1",
                    levelId = 11,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti petunjuk arah",
                    prompt = "Apa arti dari petunjuk arah 'Turn left'?",
                    options = listOf("Belok kiri", "Belok kanan", "Jalan terus", "Putar balik"),
                    correctAnswer = "Belok kiri",
                    explanation = "'Turn left' berarti belok ke arah kiri."
                ),
                PracticeQuestion(
                    id = "lvl11-q2",
                    levelId = 11,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih ungkapan arah yang tepat",
                    prompt = "Bagaimana cara mengatakan 'Jalan lurus terus' dalam bahasa Inggris?",
                    options = listOf("Go straight ahead", "Turn around", "Cross the bridge", "Go down stairs"),
                    correctAnswer = "Go straight ahead",
                    explanation = "'Go straight ahead' berarti berjalan lurus terus ke depan."
                ),
                PracticeQuestion(
                    id = "lvl11-q3",
                    levelId = 11,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan posisi yang tepat",
                    prompt = "Arti dari frasa posisi 'Next to the pharmacy' adalah...",
                    options = listOf("Di sebelah apotek", "Di belakang apotek", "Di seberang apotek", "Jauh dari apotek"),
                    correctAnswer = "Di sebelah apotek",
                    explanation = "'Next to' berarti di sebelah/di samping."
                ),
                PracticeQuestion(
                    id = "lvl11-q4",
                    levelId = 11,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan pertanyaan arah",
                    prompt = "Apa arti pertanyaan 'Where is the nearest bank?'?",
                    options = listOf("Di mana bank terdekat?", "Kapan bank buka?", "Apakah ini kantor bank?", "Di mana saya menaruh uang?"),
                    correctAnswer = "Di mana bank terdekat?",
                    explanation = "'Where is the nearest...?' digunakan untuk mencari lokasi terdekat."
                ),
                PracticeQuestion(
                    id = "lvl11-q5",
                    levelId = 11,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan posisi yang tepat",
                    prompt = "Arti posisi jalan 'Across the street' adalah...",
                    options = listOf("Di seberang jalan", "Di ujung jalan", "Di tengah jalan", "Di samping trotoar"),
                    correctAnswer = "Di seberang jalan",
                    explanation = "'Across the street' berarti menyeberangi jalan/di seberang jalan."
                ),
                PracticeQuestion(
                    id = "lvl11-q6",
                    levelId = 11,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata preposisi arah",
                    prompt = "The hospital is ___ the library and the post office.",
                    options = listOf("between", "on", "at", "behind"),
                    correctAnswer = "between",
                    explanation = "Gunakan 'between' untuk posisi di antara dua tempat."
                ),
                PracticeQuestion(
                    id = "lvl11-q7",
                    levelId = 11,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata preposisi arah",
                    prompt = "Go ___ this road until the traffic light.",
                    options = listOf("along", "at", "left", "behind"),
                    correctAnswer = "along",
                    explanation = "'Go along' berarti berjalan menyusuri jalan ini."
                ),
                PracticeQuestion(
                    id = "lvl11-q8",
                    levelId = 11,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pertanyaan arah berikut",
                    prompt = "Excuse me, how do I get to the bus station?",
                    correctAnswer = "Excuse me, how do I get to the bus station?",
                    explanation = "Latihan menanyakan arah ke terminal bus secara sopan."
                ),
                PracticeQuestion(
                    id = "lvl11-q9",
                    levelId = 11,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan penjelasan posisi berikut",
                    prompt = "The museum is behind the central park.",
                    correctAnswer = "The museum is behind the central park.",
                    explanation = "Latihan melafalkan posisi museum di belakang taman."
                ),
                PracticeQuestion(
                    id = "lvl11-q10",
                    levelId = 11,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata petunjuk arah",
                    prompt = "Turn ___ at the corner (belok kanan).",
                    options = listOf("right", "left", "straight", "along"),
                    correctAnswer = "right",
                    explanation = "'Right' berarti kanan, 'turn right' berarti belok kanan."
                )
            )
            12 -> listOf(
                PracticeQuestion(
                    id = "lvl12-q1",
                    levelId = 12,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat present continuous yang tepat",
                    prompt = "Manakah kalimat present continuous yang menyatakan kegiatan sedang berlangsung?",
                    options = listOf("I am reading a novel.", "I read a novel.", "I was read a novel.", "I will read a novel."),
                    correctAnswer = "I am reading a novel.",
                    explanation = "Format present continuous: Subject + to-be + verb-ing."
                ),
                PracticeQuestion(
                    id = "lvl12-q2",
                    levelId = 12,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Apa arti dari kalimat tanya 'What are you doing?'?",
                    options = listOf("Apa yang sedang kamu lakukan?", "Apa yang kamu makan?", "Ke mana kamu akan pergi?", "Mengapa kamu tertawa?"),
                    correctAnswer = "Apa yang sedang kamu lakukan?",
                    explanation = "'What are you doing?' menanyakan aktivitas yang sedang dikerjakan saat ini."
                ),
                PracticeQuestion(
                    id = "lvl12-q3",
                    levelId = 12,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih bentuk tenses yang tepat untuk 'He'",
                    prompt = "Bentuk tenses sedang berlangsung untuk subjek 'He' yang benar:",
                    options = listOf("He is writing a letter.", "He writing a letter.", "He are writing a letter.", "He writes a letter."),
                    correctAnswer = "He is writing a letter.",
                    explanation = "Subjek 'He' menggunakan to-be 'is' diikuti kata kerja berakhiran -ing."
                ),
                PracticeQuestion(
                    id = "lvl12-q4",
                    levelId = 12,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Terjemahkan kalimat 'We are studying English'!",
                    options = listOf("Kami sedang belajar bahasa Inggris", "Kami belajar bahasa Inggris kemarin", "Kami suka bahasa Inggris", "Kami ingin belajar bahasa Inggris"),
                    correctAnswer = "Kami sedang belajar bahasa Inggris",
                    explanation = "'Are studying' menunjukkan tindakan yang sedang dilakukan."
                ),
                PracticeQuestion(
                    id = "lvl12-q5",
                    levelId = 12,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat negatif yang tepat",
                    prompt = "Bentuk kalimat negatif dari 'They are playing' adalah...",
                    options = listOf("They are not playing.", "They not playing.", "They do not playing.", "They is not playing."),
                    correctAnswer = "They are not playing.",
                    explanation = "Tambahkan 'not' setelah to-be 'are'."
                ),
                PracticeQuestion(
                    id = "lvl12-q6",
                    levelId = 12,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi to-be kalimat berikut",
                    prompt = "She ___ listening to music right now.",
                    options = listOf("is", "am", "are", "be"),
                    correctAnswer = "is",
                    explanation = "Subjek 'She' membutuhkan to-be 'is' di present continuous."
                ),
                PracticeQuestion(
                    id = "lvl12-q7",
                    levelId = 12,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata kerja berimbuhan",
                    prompt = "Why are you ___? (sedang menangis)",
                    options = listOf("crying", "cry", "cried", "cries"),
                    correctAnswer = "crying",
                    explanation = "Kata kerja 'cry' diubah ke bentuk present continuous menjadi 'crying'."
                ),
                PracticeQuestion(
                    id = "lvl12-q8",
                    levelId = 12,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat sedang berlangsung berikut",
                    prompt = "We are waiting for the school bus.",
                    correctAnswer = "We are waiting for the school bus.",
                    explanation = "Latihan melafalkan tindakan sedang menunggu bus sekolah."
                ),
                PracticeQuestion(
                    id = "lvl12-q9",
                    levelId = 12,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat tanya sedang berlangsung berikut",
                    prompt = "What is he cooking in the kitchen?",
                    correctAnswer = "What is he cooking in the kitchen?",
                    explanation = "Latihan melafalkan pertanyaan tentang apa yang sedang dimasak."
                ),
                PracticeQuestion(
                    id = "lvl12-q10",
                    levelId = 12,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata kerja sedang berlangsung",
                    prompt = "Listen! The baby is ___.",
                    options = listOf("sleeping", "sleep", "sleeps", "slept"),
                    correctAnswer = "sleeping",
                    explanation = "Kata 'Listen!' menunjukkan bayi 'sedang tidur' (sleeping) saat ini."
                )
            )
            13 -> listOf(
                PracticeQuestion(
                    id = "lvl13-q1",
                    levelId = 13,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih pertanyaan harga yang tepat",
                    prompt = "Bagaimana cara menanyakan harga suatu pakaian di toko?",
                    options = listOf("How much does it cost?", "What is this price tag?", "Do you sell this?", "How many money I give?"),
                    correctAnswer = "How much does it cost?",
                    explanation = "'How much does it cost?' adalah pertanyaan umum dan sopan untuk menanyakan harga."
                ),
                PracticeQuestion(
                    id = "lvl13-q2",
                    levelId = 13,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan pertanyaan mencoba pakaian",
                    prompt = "Apa arti pertanyaan 'Can I try this on?'?",
                    options = listOf("Bisakah saya mencobanya (pakaian)?", "Bisakah saya membelinya?", "Bolehkah saya membawanya pulang?", "Apakah ini pakaian saya?"),
                    correctAnswer = "Bisakah saya mencobanya (pakaian)?",
                    explanation = "'Try on' digunakan untuk mencoba pakaian di kamar pas."
                ),
                PracticeQuestion(
                    id = "lvl13-q3",
                    levelId = 13,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata diskon",
                    prompt = "Arti kata belanja 'Discount' adalah...",
                    options = listOf("Diskon / Potongan harga", "Biaya tambahan", "Kembalian uang", "Kartu anggota"),
                    correctAnswer = "Diskon / Potongan harga",
                    explanation = "'Discount' adalah potongan harga dari harga normal."
                ),
                PracticeQuestion(
                    id = "lvl13-q4",
                    levelId = 13,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan kata bukti belanja",
                    prompt = "Terjemahkan kata benda transaksi 'Receipt'!",
                    options = listOf("Struk / Nota belanja", "Daftar belanjaan", "Kartu kredit", "Tas plastik"),
                    correctAnswer = "Struk / Nota belanja",
                    explanation = "'Receipt' (sering dibaca /rɪˈsiːt/) adalah struk bukti pembayaran."
                ),
                PracticeQuestion(
                    id = "lvl13-q5",
                    levelId = 13,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan rasa harga",
                    prompt = "Apa arti dari keluhan harga 'Too expensive'?",
                    options = listOf("Terlalu mahal", "Sangat murah", "Harga pas", "Barang palsu"),
                    correctAnswer = "Terlalu mahal",
                    explanation = "'Expensive' berarti mahal, 'too' memberikan penekanan 'terlalu'."
                ),
                PracticeQuestion(
                    id = "lvl13-q6",
                    levelId = 13,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata belanja berikut",
                    prompt = "Excuse me, do you have this shirt in a medium ___?",
                    options = listOf("size", "color", "price", "store"),
                    correctAnswer = "size",
                    explanation = "Medium (M) mengacu pada 'size' (ukuran) pakaian."
                ),
                PracticeQuestion(
                    id = "lvl13-q7",
                    levelId = 13,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi cara pembayaran berikut",
                    prompt = "I will pay by ___ card.",
                    options = listOf("credit", "cash", "discount", "bill"),
                    correctAnswer = "credit",
                    explanation = "Kartu yang digunakan berbelanja disebut 'credit card' (kartu kredit)."
                ),
                PracticeQuestion(
                    id = "lvl13-q8",
                    levelId = 13,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pertanyaan penawaran harga berikut",
                    prompt = "Do you offer any discount on this item?",
                    correctAnswer = "Do you offer any discount on this item?",
                    explanation = "Latihan melafalkan pertanyaan mengenai potongan harga barang."
                ),
                PracticeQuestion(
                    id = "lvl13-q9",
                    levelId = 13,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pertanyaan fasilitas toko berikut",
                    prompt = "Where is the fitting room, please?",
                    correctAnswer = "Where is the fitting room, please?",
                    explanation = "Latihan melafalkan lokasi kamar pas pakaian."
                ),
                PracticeQuestion(
                    id = "lvl13-q10",
                    levelId = 13,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat belanja berikut",
                    prompt = "The dress is very cheap, it's on ___.",
                    options = listOf("sale", "price", "size", "bill"),
                    correctAnswer = "sale",
                    explanation = "'On sale' berarti sedang dipromosikan dengan harga murah."
                )
            )
            14 -> listOf(
                PracticeQuestion(
                    id = "lvl14-q1",
                    levelId = 14,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata perasaan",
                    prompt = "Apa arti kata perasaan 'Happy'?",
                    options = listOf("Bahagia", "Sedih", "Marah", "Takut"),
                    correctAnswer = "Bahagia",
                    explanation = "'Happy' berarti bahagia atau senang."
                ),
                PracticeQuestion(
                    id = "lvl14-q2",
                    levelId = 14,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat perasaan yang tepat",
                    prompt = "Bagaimana cara mengungkapkan 'Saya lelah karena bekerja'?",
                    options = listOf("I am tired", "I am happy", "I am angry", "I am bored"),
                    correctAnswer = "I am tired",
                    explanation = "'Tired' berarti lelah atau letih."
                ),
                PracticeQuestion(
                    id = "lvl14-q3",
                    levelId = 14,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata emosi berikut",
                    prompt = "Arti kata emosi 'Angry' adalah...",
                    options = listOf("Marah", "Sedih", "Kecewa", "Takut"),
                    correctAnswer = "Marah",
                    explanation = "'Angry' diterjemahkan sebagai marah."
                ),
                PracticeQuestion(
                    id = "lvl14-q4",
                    levelId = 14,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan ekspresi antusias",
                    prompt = "Terjemahkan kalimat 'I am excited about the trip'!",
                    options = listOf("Saya sangat bersemangat tentang perjalanan ini", "Saya takut dengan perjalanan ini", "Saya malas ikut perjalanan ini", "Saya lupa tentang perjalanan ini"),
                    correctAnswer = "Saya sangat bersemangat tentang perjalanan ini",
                    explanation = "'Excited' berarti sangat bersemangat/antusias."
                ),
                PracticeQuestion(
                    id = "lvl14-q5",
                    levelId = 14,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata perasaan jenuh",
                    prompt = "Arti kata perasaan 'Bored' adalah...",
                    options = listOf("Bosan", "Capek", "Semangat", "Marah"),
                    correctAnswer = "Bosan",
                    explanation = "'Bored' berarti bosan atau jenuh."
                ),
                PracticeQuestion(
                    id = "lvl14-q6",
                    levelId = 14,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata perasaan berikut",
                    prompt = "He is ___ because his dog died.",
                    options = listOf("sad", "happy", "excited", "angry"),
                    correctAnswer = "sad",
                    explanation = "Anjing peliharaannya mati membuat dia merasa 'sad' (sedih)."
                ),
                PracticeQuestion(
                    id = "lvl14-q7",
                    levelId = 14,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata perasaan berikut",
                    prompt = "She fell asleep because she was very ___.",
                    options = listOf("tired", "bored", "happy", "angry"),
                    correctAnswer = "tired",
                    explanation = "Tertidur karena merasa sangat 'tired' (lelah)."
                ),
                PracticeQuestion(
                    id = "lvl14-q8",
                    levelId = 14,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat rasa senang berikut",
                    prompt = "I feel wonderful today, thank you!",
                    correctAnswer = "I feel wonderful today, thank you!",
                    explanation = "Latihan melafalkan rasa luar biasa hari ini."
                ),
                PracticeQuestion(
                    id = "lvl14-q9",
                    levelId = 14,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat penyemangat berikut",
                    prompt = "Don't be nervous about the test.",
                    correctAnswer = "Don't be nervous about the test.",
                    explanation = "Ungkapan agar tidak merasa gugup ('nervous') menghadapi ujian."
                ),
                PracticeQuestion(
                    id = "lvl14-q10",
                    levelId = 14,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata perasaan berikut",
                    prompt = "I am ___ of spiders (takut).",
                    options = listOf("scared", "happy", "sad", "tired"),
                    correctAnswer = "scared",
                    explanation = "Takut labu-laba diungkapkan dengan 'scared of'."
                )
            )
            15 -> listOf(
                PracticeQuestion(
                    id = "lvl15-q1",
                    levelId = 15,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih bentuk kata kerja lampau",
                    prompt = "Bentuk lampau (Verb 2) dari kata kerja 'go' adalah...",
                    options = listOf("went", "gone", "goes", "wenting"),
                    correctAnswer = "went",
                    explanation = "'Go' adalah irregular verb yang bentuk lampaunya adalah 'went'."
                ),
                PracticeQuestion(
                    id = "lvl15-q2",
                    levelId = 15,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat past tense yang benar",
                    prompt = "Pilih kalimat past tense yang menceritakan kejadian kemarin:",
                    options = listOf("Yesterday, I visited my uncle.", "Yesterday, I visit my uncle.", "Yesterday, I visiting my uncle.", "Yesterday, I am visit my uncle."),
                    correctAnswer = "Yesterday, I visited my uncle.",
                    explanation = "Menggunakan regular verb 2 'visited' untuk menerangkan aktivitas kemarin."
                ),
                PracticeQuestion(
                    id = "lvl15-q3",
                    levelId = 15,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih bentuk kata kerja lampau",
                    prompt = "Bentuk lampau (Verb 2) dari melihat 'see' adalah...",
                    options = listOf("saw", "seen", "sees", "sawed"),
                    correctAnswer = "saw",
                    explanation = "'See' berubah menjadi 'saw' dalam bentuk past tense."
                ),
                PracticeQuestion(
                    id = "lvl15-q4",
                    levelId = 15,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat negatif lampau yang benar",
                    prompt = "Bentuk negatif dari 'He watched a movie' adalah...",
                    options = listOf("He did not watch a movie.", "He not watched a movie.", "He does not watch a movie.", "He was not watch a movie."),
                    correctAnswer = "He did not watch a movie.",
                    explanation = "Gunakan did not + bare infinitive 'watch'."
                ),
                PracticeQuestion(
                    id = "lvl15-q5",
                    levelId = 15,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Terjemahan kalimat 'We were happy yesterday' adalah...",
                    options = listOf("Kami bahagia kemarin", "Kami bahagia hari ini", "Mereka sedih kemarin", "Kita harus bahagia kemarin"),
                    correctAnswer = "Kami bahagia kemarin",
                    explanation = "'Were' adalah to-be lampau untuk subjek 'We'."
                ),
                PracticeQuestion(
                    id = "lvl15-q6",
                    levelId = 15,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata kerja lampau berikut",
                    prompt = "I ___ a delicious dinner last night.",
                    options = listOf("had", "have", "has", "having"),
                    correctAnswer = "had",
                    explanation = "Bentuk lampau dari makan 'have dinner' adalah 'had dinner'."
                ),
                PracticeQuestion(
                    id = "lvl15-q7",
                    levelId = 15,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata bantu lampau negatif",
                    prompt = "They ___ not study together last Sunday.",
                    options = listOf("did", "do", "does", "were"),
                    correctAnswer = "did",
                    explanation = "Gunakan kata bantu 'did' untuk kalimat negatif past tense."
                ),
                PracticeQuestion(
                    id = "lvl15-q8",
                    levelId = 15,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat past tense berikut",
                    prompt = "I bought a new English textbook yesterday.",
                    correctAnswer = "I bought a new English textbook yesterday.",
                    explanation = "Latihan melafalkan kata kerja tidak beraturan 'bought' (membeli)."
                ),
                PracticeQuestion(
                    id = "lvl15-q9",
                    levelId = 15,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat lampau berikut",
                    prompt = "We went to the beach last weekend.",
                    correctAnswer = "We went to the beach last weekend.",
                    explanation = "Latihan melafalkan kegiatan pergi ke pantai akhir pekan lalu."
                ),
                PracticeQuestion(
                    id = "lvl15-q10",
                    levelId = 15,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi to-be lampau kalimat tanya",
                    prompt = "Where ___ you yesterday morning?",
                    options = listOf("were", "was", "are", "did"),
                    correctAnswer = "were",
                    explanation = "To-be lampau untuk subjek 'you' adalah 'were'."
                )
            )
            16 -> listOf(
                PracticeQuestion(
                    id = "lvl16-q1",
                    levelId = 16,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata penyakit",
                    prompt = "Apa arti kata penyakit kepala 'Headache'?",
                    options = listOf("Sakit kepala", "Sakit gigi", "Sakit perut", "Demam tinggi"),
                    correctAnswer = "Sakit kepala",
                    explanation = "'Headache' adalah rasa sakit di bagian kepala."
                ),
                PracticeQuestion(
                    id = "lvl16-q2",
                    levelId = 16,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih doa kesembuhan yang tepat",
                    prompt = "Bagaimana cara mendoakan teman yang sedang sakit?",
                    options = listOf("Get well soon!", "Congratulations!", "Happy birthday!", "Goodbye my friend!"),
                    correctAnswer = "Get well soon!",
                    explanation = "'Get well soon!' berarti 'Semoga cepat sembuh!'."
                ),
                PracticeQuestion(
                    id = "lvl16-q3",
                    levelId = 16,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata penyakit berikut",
                    prompt = "Arti kata kondisi badan 'Fever' adalah...",
                    options = listOf("Demam", "Batuk", "Pilek", "Luka bakar"),
                    correctAnswer = "Demam",
                    explanation = "'Fever' adalah kenaikan suhu tubuh atau demam."
                ),
                PracticeQuestion(
                    id = "lvl16-q4",
                    levelId = 16,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih profesi medis yang tepat",
                    prompt = "Siapakah profesi medis 'Doctor'?",
                    options = listOf("Dokter", "Perawat", "Apoteker", "Pasien"),
                    correctAnswer = "Dokter",
                    explanation = "'Doctor' berarti Dokter."
                ),
                PracticeQuestion(
                    id = "lvl16-q5",
                    levelId = 16,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan zat medis berikut",
                    prompt = "Arti kata 'Medicine' adalah...",
                    options = listOf("Obat", "Makanan sehat", "Plester luka", "Resep dokter"),
                    correctAnswer = "Obat",
                    explanation = "'Medicine' berarti obat untuk menyembuhkan penyakit."
                ),
                PracticeQuestion(
                    id = "lvl16-q6",
                    levelId = 16,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat medis berikut",
                    prompt = "I need to see a ___ because I am sick.",
                    options = listOf("doctor", "teacher", "manager", "friend"),
                    correctAnswer = "doctor",
                    explanation = "Saat sakit ('sick') kita perlu menemui dokter ('doctor')."
                ),
                PracticeQuestion(
                    id = "lvl16-q7",
                    levelId = 16,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata penyakit berikut",
                    prompt = "She has a sore ___ (sakit tenggorokan).",
                    options = listOf("throat", "head", "eye", "ear"),
                    correctAnswer = "throat",
                    explanation = "'Sore throat' adalah istilah untuk radang atau sakit tenggorokan."
                ),
                PracticeQuestion(
                    id = "lvl16-q8",
                    levelId = 16,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat sakit flu berikut",
                    prompt = "I have a terrible cold today.",
                    correctAnswer = "I have a terrible cold today.",
                    explanation = "Latihan melafalkan keluhan penyakit pilek/flu berat."
                ),
                PracticeQuestion(
                    id = "lvl16-q9",
                    levelId = 16,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan saran kesehatan berikut",
                    prompt = "You should drink more warm water and rest.",
                    correctAnswer = "You should drink more warm water and rest.",
                    explanation = "Latihan memberikan saran medis agar beristirahat."
                ),
                PracticeQuestion(
                    id = "lvl16-q10",
                    levelId = 16,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kalimat minum obat",
                    prompt = "Take this ___ three times a day.",
                    options = listOf("medicine", "water", "food", "tea"),
                    correctAnswer = "medicine",
                    explanation = "Benda yang diminum tiga kali sehari untuk kesembuhan adalah 'medicine' (obat)."
                )
            )
            17 -> listOf(
                PracticeQuestion(
                    id = "lvl17-q1",
                    levelId = 17,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat masa depan yang tepat",
                    prompt = "Pilih kalimat masa depan (future plans) yang benar menggunakan 'will':",
                    options = listOf("I will call you tomorrow.", "I will calling you tomorrow.", "I will called you tomorrow.", "I will to call you tomorrow."),
                    correctAnswer = "I will call you tomorrow.",
                    explanation = "Auxiliary 'will' wajib diikuti bare infinitive 'call'."
                ),
                PracticeQuestion(
                    id = "lvl17-q2",
                    levelId = 17,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih rencana masa depan yang tepat",
                    prompt = "Pilih rencana masa depan menggunakan 'going to' untuk 'She':",
                    options = listOf("She is going to study tonight.", "She are going to study tonight.", "She is going study tonight.", "She will going to study tonight."),
                    correctAnswer = "She is going to study tonight.",
                    explanation = "'She' berpasangan dengan 'is going to' untuk rencana masa depan."
                ),
                PracticeQuestion(
                    id = "lvl17-q3",
                    levelId = 17,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan keterangan waktu",
                    prompt = "Arti kata waktu 'Tomorrow' adalah...",
                    options = listOf("Besok", "Kemarin", "Hari ini", "Minggu depan"),
                    correctAnswer = "Besok",
                    explanation = "'Tomorrow' merujuk pada hari esok atau besok."
                ),
                PracticeQuestion(
                    id = "lvl17-q4",
                    levelId = 17,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan yang tepat",
                    prompt = "Terjemahkan kalimat 'We will visit Japan next year'!",
                    options = listOf("Kami akan mengunjungi Jepang tahun depan", "Kami mengunjungi Jepang tahun lalu", "Kami sedang di Jepang sekarang", "Kami ingin pergi ke Jepang bulan depan"),
                    correctAnswer = "Kami akan mengunjungi Jepang tahun depan",
                    explanation = "'Will visit' berarti akan mengunjungi, 'next year' adalah tahun depan."
                ),
                PracticeQuestion(
                    id = "lvl17-q5",
                    levelId = 17,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih kalimat negatif rencana yang tepat",
                    prompt = "Bentuk negatif dari 'He is going to play' adalah...",
                    options = listOf("He is not going to play.", "He not is going to play.", "He does not going to play.", "He will not going to play."),
                    correctAnswer = "He is not going to play.",
                    explanation = "Tambahkan 'not' setelah to-be 'is'."
                ),
                PracticeQuestion(
                    id = "lvl17-q6",
                    levelId = 17,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata bantu rencana",
                    prompt = "They ___ going to buy a new house next month.",
                    options = listOf("are", "is", "am", "will"),
                    correctAnswer = "are",
                    explanation = "Subjek 'They' membutuhkan to-be 'are' sebelum 'going to'."
                ),
                PracticeQuestion(
                    id = "lvl17-q7",
                    levelId = 17,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata bantu prediksi cuaca",
                    prompt = "I think it ___ rain tomorrow.",
                    options = listOf("will", "going", "is", "am"),
                    correctAnswer = "will",
                    explanation = "Gunakan 'will' untuk prediksi spontan masa depan."
                ),
                PracticeQuestion(
                    id = "lvl17-q8",
                    levelId = 17,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pertanyaan rencana akhir pekan berikut",
                    prompt = "What are you going to do this weekend?",
                    correctAnswer = "What are you going to do this weekend?",
                    explanation = "Latihan melafalkan pertanyaan tentang rencana akhir pekan."
                ),
                PracticeQuestion(
                    id = "lvl17-q9",
                    levelId = 17,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat rencana kerja berikut",
                    prompt = "I will start my new job next Monday.",
                    correctAnswer = "I will start my new job next Monday.",
                    explanation = "Latihan melafalkan rencana memulai pekerjaan baru."
                ),
                PracticeQuestion(
                    id = "lvl17-q10",
                    levelId = 17,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata rencana masa depan",
                    prompt = "We ___ have a party tomorrow night.",
                    options = listOf("will", "are", "going", "am"),
                    correctAnswer = "will",
                    explanation = "'We will' menunjukkan keputusan masa depan secara langsung."
                )
            )
            18 -> listOf(
                PracticeQuestion(
                    id = "lvl18-q1",
                    levelId = 18,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata cuaca",
                    prompt = "Apa arti kata cuaca 'Sunny'?",
                    options = listOf("Cerah / Berawan terang", "Mendung berair", "Sangat dingin", "Badai petir"),
                    correctAnswer = "Cerah / Berawan terang",
                    explanation = "'Sunny' berasal dari kata 'sun' (matahari) yang menunjukkan cuaca cerah."
                ),
                PracticeQuestion(
                    id = "lvl18-q2",
                    levelId = 18,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata musim",
                    prompt = "Apa arti musim 'Rainy season'?",
                    options = listOf("Musim hujan", "Musim kemarau", "Musim gugur", "Musim semi"),
                    correctAnswer = "Musim hujan",
                    explanation = "'Rainy season' adalah musim hujan."
                ),
                PracticeQuestion(
                    id = "lvl18-q3",
                    levelId = 18,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata kondisi cuaca",
                    prompt = "Arti kata cuaca 'Windy' adalah...",
                    options = listOf("Berangin", "Cerah", "Lembab", "Terselimuti kabut"),
                    correctAnswer = "Berangin",
                    explanation = "'Windy' berasal dari kata 'wind' (angin) yang berarti berangin."
                ),
                PracticeQuestion(
                    id = "lvl18-q4",
                    levelId = 18,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan cuaca dingin",
                    prompt = "Terjemahan kalimat 'It is snowing outside' adalah...",
                    options = listOf("Di luar sedang turun salju", "Di luar sedang hujan deras", "Cuaca di luar sangat berangin", "Di luar matahari bersinar terik"),
                    correctAnswer = "Di luar sedang turun salju",
                    explanation = "'Snowing' merujuk pada turunnya salju."
                ),
                PracticeQuestion(
                    id = "lvl18-q5",
                    levelId = 18,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti kata musim panas",
                    prompt = "Nama musim 'Summer' berarti...",
                    options = listOf("Musim panas", "Musim dingin", "Musim semi", "Musim gugur"),
                    correctAnswer = "Musim panas",
                    explanation = "'Summer' adalah musim panas."
                ),
                PracticeQuestion(
                    id = "lvl18-q6",
                    levelId = 18,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata cuaca kalimat berikut",
                    prompt = "Don't forget to bring an umbrella, it is ___.",
                    options = listOf("rainy", "sunny", "hot", "dry"),
                    correctAnswer = "rainy",
                    explanation = "Membawa payung karena cuaca sedang 'rainy' (hujan)."
                ),
                PracticeQuestion(
                    id = "lvl18-q7",
                    levelId = 18,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata rasa cuaca",
                    prompt = "In winter, the weather is very ___.",
                    options = listOf("cold", "hot", "warm", "sunny"),
                    correctAnswer = "cold",
                    explanation = "Saat musim dingin ('winter'), cuaca sangat 'cold' (dingin)."
                ),
                PracticeQuestion(
                    id = "lvl18-q8",
                    levelId = 18,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat cuaca berikut",
                    prompt = "The weather is beautiful and warm today.",
                    correctAnswer = "The weather is beautiful and warm today.",
                    explanation = "Latihan melafalkan cuaca yang indah dan hangat hari ini."
                ),
                PracticeQuestion(
                    id = "lvl18-q9",
                    levelId = 18,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat cuaca dingin berikut",
                    prompt = "It is freezing cold in the winter.",
                    correctAnswer = "It is freezing cold in the winter.",
                    explanation = "Latihan melafalkan dingin yang membekukan di musim dingin."
                ),
                PracticeQuestion(
                    id = "lvl18-q10",
                    levelId = 18,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi istilah musim gugur",
                    prompt = "Autumn is also called ___ season.",
                    options = listOf("fall", "summer", "winter", "spring"),
                    correctAnswer = "fall",
                    explanation = "Musim gugur dalam bahasa Inggris Amerika sering disebut 'fall'."
                )
            )
            19 -> listOf(
                PracticeQuestion(
                    id = "lvl19-q1",
                    levelId = 19,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih permohonan bantuan yang paling sopan",
                    prompt = "Bagaimana cara meminta bantuan seseorang secara sopan menggunakan modals?",
                    options = listOf("Could you please help me?", "Help me now!", "Must you help me?", "Should you do this for me?"),
                    correctAnswer = "Could you please help me?",
                    explanation = "'Could you please...' adalah ungkapan permohonan bantuan yang sangat sopan."
                ),
                PracticeQuestion(
                    id = "lvl19-q2",
                    levelId = 19,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan ijin meminjam pen",
                    prompt = "Apa arti dari kalimat tanya 'May I borrow your pen?'?",
                    options = listOf("Bolehkah saya meminjam penamu?", "Haruskah saya membeli pena ini?", "Bolehkah saya memberikan pena ini?", "Bisakah kamu menulis dengan penamu?"),
                    correctAnswer = "Bolehkah saya meminjam penamu?",
                    explanation = "'May I...' digunakan untuk meminta izin melakukan sesuatu secara sopan."
                ),
                PracticeQuestion(
                    id = "lvl19-q3",
                    levelId = 19,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih fungsi modal verb 'Should'",
                    prompt = "Penggunaan kata 'Should' biasanya bertujuan untuk...",
                    options = listOf("Memberikan saran/rekomendasi", "Membuat larangan mutlak", "Menunjukkan kepastian penuh", "Meminta izin masuk"),
                    correctAnswer = "Memberikan saran/rekomendasi",
                    explanation = "'Should' diterjemahkan sebagai 'seharusnya', digunakan untuk memberi nasihat/saran."
                ),
                PracticeQuestion(
                    id = "lvl19-q4",
                    levelId = 19,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan keharusan",
                    prompt = "Terjemahkan kalimat kewajiban 'You must study hard'!",
                    options = listOf("Kamu harus belajar keras", "Kamu boleh belajar keras", "Kamu sebaiknya belajar keras", "Kamu tidak perlu belajar keras"),
                    correctAnswer = "Kamu harus belajar keras",
                    explanation = "'Must' menyatakan keharusan atau kewajiban mutlak."
                ),
                PracticeQuestion(
                    id = "lvl19-q5",
                    levelId = 19,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih terjemahan tawaran minuman",
                    prompt = "Apa arti penawaran sopan 'Would you like some tea?'?",
                    options = listOf("Apakah Anda ingin teh?", "Bolehkah saya minum teh?", "Mengapa Anda minum teh?", "Apakah Anda membuat teh?"),
                    correctAnswer = "Apakah Anda ingin teh?",
                    explanation = "'Would you like...' adalah cara ramah menawarkan sesuatu."
                ),
                PracticeQuestion(
                    id = "lvl19-q6",
                    levelId = 19,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi modal kemampuan",
                    prompt = "___ you speak English?",
                    options = listOf("Can", "Must", "Should", "Would"),
                    correctAnswer = "Can",
                    explanation = "Gunakan 'Can' untuk menanyakan kemampuan (ability) seseorang."
                ),
                PracticeQuestion(
                    id = "lvl19-q7",
                    levelId = 19,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata larangan mutlak",
                    prompt = "You ___ not park here, it's illegal.",
                    options = listOf("must", "can", "should", "may"),
                    correctAnswer = "must",
                    explanation = "Larangan parkir karena melanggar hukum menggunakan modal 'must not' (dilarang keras)."
                ),
                PracticeQuestion(
                    id = "lvl19-q8",
                    levelId = 19,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan permintaan bantuan meja makan berikut",
                    prompt = "Could you pass me the salt, please?",
                    correctAnswer = "Could you pass me the salt, please?",
                    explanation = "Latihan melafalkan permintaan tolong melewatkan garam di meja makan."
                ),
                PracticeQuestion(
                    id = "lvl19-q9",
                    levelId = 19,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan permintaan sopan berikut",
                    prompt = "Would you mind opening the window?",
                    correctAnswer = "Would you mind opening the window?",
                    explanation = "Ungkapan sopan 'Apakah Anda keberatan membuka jendela?'."
                ),
                PracticeQuestion(
                    id = "lvl19-q10",
                    levelId = 19,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi izin kata kerja dasar",
                    prompt = "May I ___ in?",
                    options = listOf("come", "came", "coming", "comes"),
                    correctAnswer = "come",
                    explanation = "Setelah modal 'May' wajib diikuti kata kerja bentuk pertama 'come'."
                )
            )
            20 -> listOf(
                PracticeQuestion(
                    id = "lvl20-q1",
                    levelId = 20,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti ungkapan idiom",
                    prompt = "Apa arti dari ungkapan idiom 'A piece of cake'?",
                    options = listOf("Sangat mudah", "Kue manis yang enak", "Membagi makanan", "Masalah rumit"),
                    correctAnswer = "Sangat mudah",
                    explanation = "'A piece of cake' menggambarkan pekerjaan yang sangat mudah dilakukan."
                ),
                PracticeQuestion(
                    id = "lvl20-q2",
                    levelId = 20,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih maksud ungkapan idiom berikut",
                    prompt = "Jika seseorang berkata 'I am feeling under the weather', artinya dia sedang...",
                    options = listOf("Kurang enak badan / sakit", "Sangat gembira", "Kepanasan di luar rumah", "Ingin pergi berjalan-jalan"),
                    correctAnswer = "Kurang enak badan / sakit",
                    explanation = "'Under the weather' adalah idiom untuk merasa sakit atau kurang fit."
                ),
                PracticeQuestion(
                    id = "lvl20-q3",
                    levelId = 20,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih fungsi ucapan idiom berikut",
                    prompt = "Kapan Anda mengucapkan kalimat penyemangat 'Break a leg!'?",
                    options = listOf("Sebelum seseorang tampil atau ujian", "Saat terjadi musibah kecelakaan", "Ketika orang sedang tidur", "Saat pesta ulang tahun dimulai"),
                    correctAnswer = "Sebelum seseorang tampil atau ujian",
                    explanation = "'Break a leg!' adalah idiom yang berarti 'Semoga sukses!'."
                ),
                PracticeQuestion(
                    id = "lvl20-q4",
                    levelId = 20,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti ungkapan idiom berikut",
                    prompt = "Apa arti dari kalimat penutupan aktivitas 'Let's call it a day'?",
                    options = listOf("Mari kita sudahi pekerjaan hari ini", "Mari kita mulai bekerja kembali", "Ayo hubungi dia sekarang", "Selamat berlibur semuanya"),
                    correctAnswer = "Mari kita sudahi pekerjaan hari ini",
                    explanation = "'Let's call it a day' diucapkan untuk menyudahi aktivitas atau pekerjaan di hari tersebut."
                ),
                PracticeQuestion(
                    id = "lvl20-q5",
                    levelId = 20,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti peribahasa berikut",
                    prompt = "Peribahasa populer 'Better late than never' memiliki arti...",
                    options = listOf("Lebih baik terlambat daripada tidak sama sekali", "Terlambat adalah tanda malas", "Jangan pernah datang terlambat", "Selalu datang tepat waktu"),
                    correctAnswer = "Lebih baik terlambat daripada tidak sama sekali",
                    explanation = "Arti harfiah: lebih baik lambat/terlambat bertindak daripada tidak melakukan sama sekali."
                ),
                PracticeQuestion(
                    id = "lvl20-q6",
                    levelId = 20,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata ungkapan idiom",
                    prompt = "This math test is so easy, it's a piece of ___.",
                    options = listOf("cake", "bread", "apple", "sugar"),
                    correctAnswer = "cake",
                    explanation = "Pelengkap idiom 'a piece of cake' adalah kata 'cake'."
                ),
                PracticeQuestion(
                    id = "lvl20-q7",
                    levelId = 20,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata ungkapan idiom berikut",
                    prompt = "She cannot come because she is under the ___.",
                    options = listOf("weather", "rain", "sun", "cloud"),
                    correctAnswer = "weather",
                    explanation = "Pelengkap idiom 'under the weather' adalah kata 'weather'."
                ),
                PracticeQuestion(
                    id = "lvl20-q8",
                    levelId = 20,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan ungkapan idiom penutup hari berikut",
                    prompt = "Let's call it a day and go home.",
                    correctAnswer = "Let's call it a day and go home.",
                    explanation = "Latihan melafalkan ungkapan menyudahi pekerjaan untuk pulang ke rumah."
                ),
                PracticeQuestion(
                    id = "lvl20-q9",
                    levelId = 20,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan pepatah bijak berikut",
                    prompt = "It is never too late to learn.",
                    correctAnswer = "It is never too late to learn.",
                    explanation = "Melafalkan kalimat bijak: 'Tidak pernah ada kata terlambat untuk belajar'."
                ),
                PracticeQuestion(
                    id = "lvl20-q10",
                    levelId = 20,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi ungkapan idiom penyemangat",
                    prompt = "Good luck with your performance! Break a ___!",
                    options = listOf("leg", "arm", "hand", "head"),
                    correctAnswer = "leg",
                    explanation = "Lengkapi ucapan 'Break a leg!' sebagai idiom mendoakan keberuntungan."
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
                    instruction = "Pilih kata ganti kepemilikan yang tepat",
                    prompt = "This is my book, so this book is ___.",
                    options = listOf("mine", "yours", "his", "hers"),
                    correctAnswer = "mine",
                    explanation = "'Mine' adalah possessive pronoun untuk menunjukkan kepemilikan 'saya'."
                ),
                PracticeQuestion(
                    id = "gen-q3",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata sambung yang sesuai",
                    prompt = "I wanted to go for a walk, ___ it started to rain.",
                    options = listOf("but", "or", "and", "so"),
                    correctAnswer = "but",
                    explanation = "Gunakan kata hubung pertentangan 'but' (tetapi) karena situasi kontras."
                ),
                PracticeQuestion(
                    id = "gen-q4",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi auxiliary verb berikut",
                    prompt = "She ___ not like to drink coffee.",
                    options = listOf("does", "do", "is", "are"),
                    correctAnswer = "does",
                    explanation = "Untuk subjek tunggal 'she' pada present simple negatif, gunakan auxiliary verb 'does'."
                ),
                PracticeQuestion(
                    id = "gen-q5",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat sopan berikut",
                    prompt = "Could you please help me with this task?",
                    options = emptyList(),
                    correctAnswer = "Could you please help me with this task?",
                    explanation = "Latihan melafalkan permintaan tolong secara sopan menggunakan 'Could you'."
                ),
                PracticeQuestion(
                    id = "gen-q6",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti idiom penyemangat berikut",
                    prompt = "Apa arti dari ungkapan idiom 'Break a leg'?",
                    options = listOf("Semoga sukses", "Mengalami kecelakaan", "Bekerja sangat keras", "Beristirahat sejenak"),
                    correctAnswer = "Semoga sukses",
                    explanation = "'Break a leg' adalah idiom yang digunakan untuk mengucapkan semoga sukses sebelum pertunjukan/ujian."
                ),
                PracticeQuestion(
                    id = "gen-q7",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi kata depan (preposition) berikut",
                    prompt = "We have been friends ___ five years.",
                    options = listOf("for", "since", "during", "ago"),
                    correctAnswer = "for",
                    explanation = "Gunakan 'for' untuk menunjukkan durasi waktu ('selama lima tahun')."
                ),
                PracticeQuestion(
                    id = "gen-q8",
                    levelId = levelId,
                    type = PracticeQuestionType.FILL_BLANK,
                    instruction = "Lengkapi bentuk kata kerja yang sesuai",
                    prompt = "She is looking forward to ___ you.",
                    options = listOf("meeting", "meet", "met", "meets"),
                    correctAnswer = "meeting",
                    explanation = "Frasa 'look forward to' diikuti oleh verb-ing (gerund)."
                ),
                PracticeQuestion(
                    id = "gen-q9",
                    levelId = levelId,
                    type = PracticeQuestionType.MULTIPLE_CHOICE,
                    instruction = "Pilih arti idiom frekuensi berikut",
                    prompt = "Apa arti dari ungkapan idiom 'Once in a blue moon'?",
                    options = listOf("Sangat jarang terjadi", "Sering dilakukan", "Terjadi setiap malam", "Hanya saat bulan purnama"),
                    correctAnswer = "Sangat jarang terjadi",
                    explanation = "'Once in a blue moon' adalah idiom untuk menyatakan sesuatu yang sangat jarang terjadi."
                ),
                PracticeQuestion(
                    id = "gen-q10",
                    levelId = levelId,
                    type = PracticeQuestionType.SPEAKING,
                    instruction = "Ucapkan kalimat berikut dengan lancar",
                    prompt = "Please drive safely on your way home.",
                    options = emptyList(),
                    correctAnswer = "Please drive safely on your way home.",
                    explanation = "Latihan melafalkan ucapan hati-hati di jalan untuk orang lain."
                )
            )
        }
    }
}
