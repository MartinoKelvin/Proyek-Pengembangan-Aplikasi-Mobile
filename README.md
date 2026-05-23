# 🗣️ CakapAi — Sprint 2 Core Features

![CI](https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

CakapAi adalah aplikasi pembelajaran bahasa berbasis **Kotlin Multiplatform** dan **Compose Multiplatform**. Pada Sprint 2, fokus utama pengembangan adalah implementasi **UI screens**, **navigation**, **data layer**, dan **CRUD operations** sesuai rubrik penilaian Sprint 2.

---

## 👥 Anggota Tim

| Nama | NIM | Tanggung Jawab |
|---|---:|---|
| Martino Kelvin | 123140165 | UI screens, navigation, CRUD vocabulary, dokumentasi README |
| Louis Hutabarat | 123140052 | Data layer, repository, SQLDelight, testing dan code review |

---

## 🔗 Link Penting

| Kebutuhan | Link |
|---|---|
| Video Demo Sprint 2 | [Tonton Video Demo](https://drive.google.com/file/d/1lD-ec7jRO_Pe9p0mKM1v0-Cksyp8qYLk/view?usp=drive_link) |

> Catatan video demo: video berdurasi ±1 menit berisi alur membuka aplikasi, berpindah screen, mencoba fitur CRUD vocab, search/filter vocab, dan navigasi kuis sampai result.

---

## 📌 Ringkasan Sprint 2

Sprint 2 berfokus pada fitur inti aplikasi:

- Implementasi minimal 3 screen aktif.
- Navigasi antar screen dengan argument.
- Data layer menggunakan repository pattern.
- Local storage menggunakan SQLDelight.
- CRUD vocabulary pada fitur Dictionary / Translator.
- Search dan filter pada daftar kosakata tersimpan.
- Struktur kode rapi dengan Clean Architecture dan MVVM.

---

## ✅ Checklist Rubrik Sprint 2

| Komponen Rubrik | Bobot | Status | Implementasi di Project |
|---|---:|:---:|---|
| UI Screens | 25% | ✅ | Tersedia Map, Quiz, Result, Dictionary, dan AI Tutor screen dengan Compose Material 3. |
| Navigation | 20% | ✅ | Menggunakan `AppNavHost`, typed route, bottom navigation, argument `levelId`, dan back handling. |
| Data Layer | 25% | ✅ | Menggunakan domain repository interface, repository implementation, SQLDelight database, mapper, dan Koin DI. |
| CRUD Operations | 20% | ✅ | CRUD kosakata tersimpan pada fitur Dictionary / Vocabulary. |
| Code Quality | 10% | ✅ | Struktur folder clean architecture, separation of concerns, ViewModel, StateFlow, dan CI workflow. |
| Search & Filter | Bonus / Penguat | ✅ | Search bar pada daftar vocab tersimpan untuk memfilter `sourceText` dan `translatedText`. |

---

## ✨ Fitur Utama

### 1. Map Screen / Learning Path

Map Screen menjadi halaman awal aplikasi. Screen ini menampilkan daftar level pembelajaran bahasa, status level yang terbuka, level terkunci, dan progres belajar pengguna.

**File terkait:**

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/map/MapScreen.kt
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/map/MapViewModel.kt
```

**Fitur:**

- Menampilkan level pembelajaran.
- Membaca progress level dari local database.
- Menggunakan `MapUiState` untuk state `Loading`, `Success`, `Empty`, dan `Error`.
- Navigasi ke Quiz Screen dengan membawa `levelId`.

---

### 2. Quiz Screen

Quiz Screen digunakan untuk mengerjakan soal berdasarkan level yang dipilih dari Map Screen.

**File terkait:**

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/quiz/QuizScreen.kt
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/quiz/QuizViewModel.kt
```

**Fitur:**

- Menampilkan soal berdasarkan `levelId`.
- Menampilkan pilihan jawaban.
- Validasi jawaban benar atau salah.
- Sistem nyawa / lives.
- Menghitung skor dan jumlah jawaban benar.
- Navigasi ke Result Screen setelah kuis selesai.

---

### 3. Result Screen

Result Screen menampilkan hasil pengerjaan kuis pengguna.

**File terkait:**

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/result/ResultScreen.kt
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/result/ResultViewModel.kt
```

**Fitur:**

- Menampilkan skor.
- Menampilkan total soal.
- Menampilkan akurasi.
- Menampilkan status lulus atau belum lulus.
- Tombol kembali ke Map.
- Tombol mengulang kuis.

---

### 4. Dictionary / Vocabulary Screen

Dictionary Screen digunakan sebagai fitur translator sederhana sekaligus tempat menyimpan kosakata penting.

**File terkait:**

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/dictionary/DictionaryScreen.kt
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/dictionary/DictionaryViewModel.kt
```

**Fitur:**

- Input teks kosakata.
- Output terjemahan sederhana.
- Menyimpan kosakata ke database lokal.
- Menampilkan daftar kosakata tersimpan.
- Search/filter kosakata tersimpan.
- Edit kosakata tersimpan.
- Delete kosakata tersimpan.

---

### 5. AI Tutor Screen

AI Tutor Screen menyediakan tampilan chat interaktif untuk latihan bahasa.

**File terkait:**

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/screens/tutor/AITutorScreen.kt
composeApp/src/commonMain/kotlin/com/example/cakapAi/data/repository/AIRepositoryImpl.kt
composeApp/src/commonMain/kotlin/com/example/cakapAi/data/remote/api/GeminiService.kt
```

**Fitur:**

- Tampilan chat antara user dan tutor.
- Input pesan dari user.
- Bubble chat untuk user dan AI.
- Indikator AI sedang mengetik.
- Struktur repository AI sudah tersedia untuk integrasi Gemini API.

---

## 🧩 CRUD Operations pada Vocabulary

CRUD diterapkan pada fitur **Dictionary / Vocabulary** menggunakan tabel `SavedVocabularyEntity` di SQLDelight.

### Create

User dapat menambahkan kosakata baru melalui tombol tambah atau menyimpan hasil input translator.

```kotlin
fun addVocab(sourceLang: String, targetLang: String, sourceText: String, translatedText: String) {
    viewModelScope.launch {
        repository.insertSavedVocab(
            SavedVocab(
                sourceLang = sourceLang,
                targetLang = targetLang,
                sourceText = sourceText,
                translatedText = translatedText
            )
        )
    }
}
```

### Read

Data kosakata tersimpan dibaca secara reactive dari repository menggunakan `Flow` dan dikonversi menjadi `StateFlow`.

```kotlin
val savedVocabs: StateFlow<List<SavedVocab>> = repository.getAllSavedVocabs()
    .stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
```

### Update

User dapat mengedit kosakata yang sudah tersimpan melalui dialog edit.

```kotlin
fun updateVocab(vocab: SavedVocab, newSourceText: String, newTranslatedText: String) {
    viewModelScope.launch {
        repository.updateSavedVocab(
            vocab.copy(
                sourceText = newSourceText,
                translatedText = newTranslatedText
            )
        )
    }
}
```

### Delete

User dapat menghapus kosakata dari daftar vocabulary tersimpan.

```kotlin
fun deleteVocab(vocab: SavedVocab) {
    viewModelScope.launch {
        repository.deleteSavedVocab(vocab.id)
    }
}
```

---

## 🔍 Search dan Filter Vocabulary

Search/filter diterapkan pada daftar kosakata tersimpan di Dictionary Screen.

```kotlin
val filteredVocabs = savedVocabs.filter {
    it.sourceText.contains(searchQuery, ignoreCase = true) ||
    it.translatedText.contains(searchQuery, ignoreCase = true)
}
```

Dengan fitur ini, pengguna dapat mencari kosakata berdasarkan:

- kata asli / `sourceText`
- hasil terjemahan / `translatedText`

Jika tidak ada data yang cocok, aplikasi menampilkan pesan kosong seperti:

```text
Tidak ada kosakata yang cocok.
```

---

## 🧱 Data Layer

Project menggunakan pendekatan **Clean Architecture** dengan pemisahan layer:

```text
Presentation Layer
Screen + ViewModel + UiState

Domain Layer
Model + Repository Interface

Data Layer
Repository Implementation + SQLDelight + Mapper + Remote Service
```

### Struktur Data Layer

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/data/
├── local/
│   ├── LearningMapper.kt
│   └── datastore/
├── remote/
│   ├── api/
│   │   └── GeminiService.kt
│   └── dto/
│       └── GeminiDto.kt
└── repository/
    ├── AIRepositoryImpl.kt
    ├── DictionaryRepositoryImpl.kt
    └── LearningRepositoryImpl.kt
```

### Repository Interface

Repository interface berada pada domain layer agar presentation layer tidak bergantung langsung pada detail database.

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/domain/repository/
├── AIRepository.kt
├── DictionaryRepository.kt
└── LearningRepository.kt
```

### SQLDelight Local Storage

Database lokal berada pada file:

```text
composeApp/src/commonMain/sqldelight/com/example/cakapAi/data/local/CakapAi.sq
```

Tabel utama yang digunakan:

| Tabel | Fungsi |
|---|---|
| `LevelProgressEntity` | Menyimpan progres level belajar. |
| `OfflineVocabularyEntity` | Menyimpan vocabulary offline per level. |
| `QuizHistoryEntity` | Menyimpan riwayat pengerjaan kuis. |
| `SavedVocabularyEntity` | Menyimpan vocabulary dari Dictionary / Translator. |

Query CRUD vocabulary:

```sql
getAllSavedVocabs:
SELECT * FROM SavedVocabularyEntity
ORDER BY created_at DESC;

insertSavedVocab:
INSERT OR REPLACE INTO SavedVocabularyEntity (source_lang, target_lang, source_text, translated_text, created_at)
VALUES (?, ?, ?, ?, ?);

deleteSavedVocab:
DELETE FROM SavedVocabularyEntity
WHERE id = ?;

updateSavedVocab:
UPDATE SavedVocabularyEntity
SET source_text = ?, translated_text = ?
WHERE id = ?;
```

---

## 🧭 Navigation

Navigasi menggunakan `NavHost` dan typed route berbasis Kotlin Serialization.

**File terkait:**

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/navigation/Routes.kt
composeApp/src/commonMain/kotlin/com/example/cakapAi/presentation/navigation/AppNavHost.kt
```

### Route yang tersedia

```kotlin
sealed interface Route {
    data object Map : Route
    data class Quiz(val levelId: Int) : Route
    data class Result(
        val levelId: Int,
        val score: Int,
        val totalQuestion: Int,
        val accuracy: Int,
        val isPassed: Boolean
    ) : Route
    data object Dictionary : Route
    data object AITutor : Route
}
```

### Alur Navigasi

```text
Map Screen
   └── Quiz Screen(levelId)
          └── Result Screen(levelId, score, totalQuestion, accuracy, isPassed)

Bottom Navigation
   ├── Peta / Map
   ├── Kuis / Quiz
   ├── Kamus / Dictionary
   └── AI Tutor
```

### Argument Passing

Argument `levelId` dikirim dari Map Screen ke Quiz Screen:

```kotlin
navigationActions.navigateToQuiz(levelId)
```

Result Screen menerima beberapa argument:

```kotlin
Route.Result(
    levelId = levelId,
    score = score,
    totalQuestion = totalQuestion,
    accuracy = accuracy,
    isPassed = isPassed
)
```

---

## 🖥️ UI Screens

| Screen | Status | Deskripsi |
|---|:---:|---|
| Map Screen | ✅ | Halaman utama yang menampilkan peta level pembelajaran. |
| Quiz Screen | ✅ | Halaman kuis berdasarkan level. |
| Result Screen | ✅ | Halaman hasil kuis. |
| Dictionary Screen | ✅ | Halaman translator dan vocabulary CRUD. |
| AI Tutor Screen | ✅ | Halaman chat tutor untuk latihan bahasa. |

### Placeholder Screenshot

> Tambahkan screenshot setelah aplikasi dijalankan di emulator/device.

#### Map Screen

`ISI_SCREENSHOT_MAP_DI_SINI`

#### Quiz Screen

`ISI_SCREENSHOT_QUIZ_DI_SINI`

#### Result Screen

`ISI_SCREENSHOT_RESULT_DI_SINI`

#### Dictionary / Vocabulary Screen

`ISI_SCREENSHOT_DICTIONARY_DI_SINI`

#### AI Tutor Screen

`ISI_SCREENSHOT_AI_TUTOR_DI_SINI`

---

## 🧪 State Management

Project menggunakan `ViewModel`, `StateFlow`, dan sealed interface untuk mengelola state UI.

Contoh pada `MapViewModel`:

```kotlin
sealed interface MapUiState {
    data object Loading : MapUiState
    data class Success(val levels: List<LevelProgress>) : MapUiState
    data object Empty : MapUiState
    data class Error(val message: String) : MapUiState
}
```

Contoh pada `QuizViewModel`:

```kotlin
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
```

---

## 🛠️ Tech Stack

| Teknologi | Kegunaan |
|---|---|
| Kotlin Multiplatform | Shared code untuk Android dan iOS. |
| Compose Multiplatform | UI deklaratif lintas platform. |
| Material 3 | Komponen UI modern. |
| Navigation Compose | Navigasi antar screen. |
| SQLDelight | Local database dan query type-safe. |
| Koin | Dependency Injection. |
| Kotlin Coroutines & Flow | Async process dan reactive data. |
| Ktor Client | Persiapan komunikasi API / Gemini service. |
| GitHub Actions | Continuous Integration. |

---

## 📂 Struktur Project

```text
composeApp/src/commonMain/kotlin/com/example/cakapAi/
├── App.kt
├── core/
│   ├── di/
│   │   └── AppModule.kt
│   ├── network/
│   └── util/
├── data/
│   ├── local/
│   ├── remote/
│   └── repository/
├── domain/
│   ├── model/
│   └── repository/
└── presentation/
    ├── components/
    ├── navigation/
    ├── screens/
    │   ├── dictionary/
    │   ├── map/
    │   ├── quiz/
    │   ├── result/
    │   └── tutor/
    └── theme/
```

---

## 🚀 Cara Menjalankan Project

### 1. Clone repository

```bash
git clone ISI_LINK_REPOSITORY_DI_SINI
cd Proyek-Pengembangan-Aplikasi-Mobile
```

### 2. Buka di Android Studio

- Buka Android Studio.
- Pilih **File > Open**.
- Pilih folder project.
- Tunggu proses Gradle Sync selesai.

### 3. Jalankan aplikasi

Pilih konfigurasi:

```text
composeApp
```

Lalu jalankan ke emulator atau device Android.

### 4. Build lewat terminal

```bash
./gradlew build
```

Untuk Windows PowerShell:

```powershell
.\gradlew.bat build
```

---

## 🧪 Testing dan CI

Project memiliki workflow CI di:

```text
.github/workflows/ci.yml
```

Target code quality Sprint 2:

- Project dapat di-build.
- Struktur folder konsisten.
- Repository dan ViewModel dipisahkan.
- Tidak ada dead end pada navigasi utama.
- CRUD vocabulary dapat diuji lewat aplikasi.

---

## 🎥 Skenario Video Demo Sprint 2

Gunakan alur ini untuk video demo ±1 menit:

1. Buka aplikasi dan tampilkan Map Screen.
2. Klik salah satu level, masuk ke Quiz Screen.
3. Jawab beberapa soal sampai masuk Result Screen.
4. Kembali ke Map Screen.
5. Buka Dictionary / Kamus dari bottom navigation.
6. Tambahkan vocabulary baru.
7. Tampilkan data vocabulary yang tersimpan.
8. Gunakan search/filter untuk mencari vocab.
9. Edit salah satu vocab.
10. Hapus salah satu vocab.
11. Buka AI Tutor Screen untuk menunjukkan screen tambahan.

---

## 📊 Bukti Pemenuhan Rubrik

### UI Screens — 25%

Aplikasi memiliki lebih dari 3 screen aktif:

- Map Screen
- Quiz Screen
- Result Screen
- Dictionary Screen
- AI Tutor Screen

### Navigation — 20%

Aplikasi menggunakan:

- `AppNavHost`
- typed route
- bottom navigation
- argument passing `levelId`
- back handling ke Map Screen

### Data Layer — 25%

Aplikasi memiliki:

- repository interface di domain layer
- repository implementation di data layer
- SQLDelight local database
- mapper entity ke domain
- Koin dependency injection

### CRUD Operations — 20%

CRUD diterapkan pada fitur Vocabulary:

| Operasi | Implementasi |
|---|---|
| Create | `insertSavedVocab()` |
| Read | `getAllSavedVocabs()` |
| Update | `updateSavedVocab()` |
| Delete | `deleteSavedVocab()` |

### Code Quality — 10%

Code quality ditunjukkan melalui:

- folder clean architecture
- pemisahan screen dan ViewModel
- penggunaan repository pattern
- penggunaan `Flow` dan `StateFlow`
- dependency injection dengan Koin
- CI workflow

---

## 📝 Catatan Pengembangan

- Fitur CRUD utama Sprint 2 difokuskan pada vocabulary tersimpan di Dictionary Screen.
- Search/filter vocab sudah tersedia pada daftar vocab tersimpan.
- AI Tutor UI sudah tersedia, sedangkan integrasi Gemini dapat dilanjutkan pada Sprint berikutnya.
- Data progress level dan vocabulary menggunakan SQLDelight sebagai local storage.

---

## 📄 Lisensi

Project ini dibuat untuk memenuhi tugas mata kuliah **Pengembangan Aplikasi Mobile**.

Program Studi Teknik Informatika  
Institut Teknologi Sumatera  
Tahun Akademik 2025/2026
