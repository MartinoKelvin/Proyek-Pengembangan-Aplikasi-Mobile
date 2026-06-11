# CakapAI

[![CI Build](https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)](https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml)
[![Kotlin Multiplatform](https://img.shields.io/badge/Kotlin-Multiplatform-blue.svg?style=flat&logo=kotlin)](https://kotlinlang.org/docs/multiplatform.html)
[![Compose Multiplatform](https://img.shields.io/badge/Compose-Multiplatform-purple.svg?style=flat&logo=jetpackcompose)](https://github.com/JetBrains/compose-multiplatform)

## Identitas Pengembang & Video Demo

*   **Daftar Pengembang:**
    *   **Martino Kelvin** (NIM: 123140165)
    *   **Louis Hutabarat** (NIM: 123140052)
*   **Link Video Demo Aplikasi (YouTube):** [Tonton Video Demo di YouTube](https://youtube.com/...)

---

**CakapAI** adalah aplikasi pembelajaran bahasa asing interaktif berbasis kecerdasan buatan (AI) yang dibangun menggunakan **Kotlin Multiplatform (KMP)** dan **Compose Multiplatform**. Aplikasi ini dirancang dengan pendekatan *offline-first* menggunakan **SQLDelight** untuk menyimpan kemajuan belajar secara lokal dan memanfaatkan kekuatan **Gemini API** untuk menghadirkan asisten tutor bahasa Inggris personal serta sistem penerjemah real-time.

---

## Daftar Fitur & Layar Aplikasi

CakapAI menawarkan pengalaman belajar gamifikasi terstruktur dengan navigasi modern dan responsif:

### 1. Splash Screen & Branding
*   Layar sambutan pertama saat aplikasi dibuka yang memperkuat identitas visual **CakapAI**.
*   Dilengkapi animasi logo, tagline, loading indicator, serta transisi otomatis ke halaman utama setelah inisialisasi selesai.
*   Mendukung pergantian tema warna secara mulus (*light/dark mode*).

### 2. Peta Perjalanan Belajar (Interactive Learning Map)
*   Peta interaktif dengan alur meliuk (S-curve) yang membagi pembelajaran ke dalam **4 Chapter (Total 20 Level)**:
    *   **Chapter 1: Foundations of English** (Level 1-5): Pengenalan diri, sapaan dasar, dan tata bahasa esensial.
    *   **Chapter 2: Daily Socialization & Work** (Level 6-10): Angka, waktu, aktivitas harian, dan komunikasi kerja.
    *   **Chapter 3: Getting Around & Tenses** (Level 11-15): Arah jalan, belanja, tenses masa lalu, dan emosi.
    *   **Chapter 4: Advanced Contexts & Expressions** (Level 16-20): Kesehatan, masa depan, cuaca, idiom, dan peribahasa.
*   Desain antarmuka premium dengan tombol 3D taktil, riak ombak animasi (*ocean waves backdrop*), ornamen kompas klasik, dan indikator mengapung **"KAMU"** yang menunjukkan posisi level aktif pengguna saat ini.
*   Dilengkapi progress bar dinamis di bagian header yang menunjukkan persentase level yang berhasil diselesaikan secara waktu nyata.

### 3. Detail Level & Progres
*   Setiap kali level yang tidak terkunci diklik, aplikasi akan menampilkan lembar detail latihan (*bottom sheet*) yang menunjukkan target pembelajaran, *high score* terbaik, dan akurasi pengerjaan Anda.
*   Menyediakan tombol cepat untuk langsung memulai sesi latihan.

### 4. Latihan Interaktif (Practice Session Overlay)
*   Sesi latihan tergamifikasi yang terdiri dari 5 soal acak dari bank soal per level dengan sistem **3 Nyawa (Hearts)**.
*   Mendukung tiga format pertanyaan:
    *   **Pilihan Ganda (Multiple Choice)**: Memilih opsi jawaban yang benar.
    *   **Isian Rumpang (Fill in the Blank)**: Melengkapi bagian kalimat yang kosong.
    *   **Latihan Pengucapan (Speaking Practice)**: Membaca teks bahasa Inggris keras-keras. Aplikasi mendengarkan dan mencocokkan ucapan Anda menggunakan teknologi *Speech Recognition*.
*   Dilengkapi efek suara responsif (*correct/incorrect answer audio effects*) dan umpan balik getaran (*vibration feedback*).

### 5. Hasil Akhir Latihan (Result Screen)
*   Menampilkan skor akhir pengerjaan, jumlah jawaban benar, tingkat akurasi (%), dan status kelulusan (lulus jika menyelesaikan kuis tanpa kehabisan nyawa).
*   Menyediakan tombol pintas untuk mengulang latihan (*retry*) atau kembali ke peta perjalanan.

### 6. Kuis Latihan Umum (General Quiz)
*   Tab khusus di bilah navigasi bawah untuk memulai sesi kuis acak dari topik kosa kata gabungan (*Listening, Speaking, Reading, Gaming*). Sangat berguna bagi pengguna untuk menantang diri sendiri dengan bank soal gabungan.

### 7. AI Tutor (CakapAI Chatbot)
*   Chatbot asisten personal interaktif yang ditenagai oleh **Gemini API**.
*   Pengguna dapat menulis kalimat bahasa Inggris sesuka mereka. AI Tutor akan membalas obrolan layaknya sahabat pena sekaligus menganalisis, mengoreksi struktur tata bahasa (*grammar*), dan memberikan saran penulisan yang lebih alami (*native-like*).

### 8. Kamus & Penerjemah (Dictionary Screen)
*   Penerjemah multibahasa (Indonesia <-> Inggris) bertenaga AI yang membersihkan output terjemahan secara rapi tanpa format markdown yang mengganggu.
*   Mendukung fitur **CRUD (Create, Read, Update, Delete)** Kosakata Tersimpan secara offline. Pengguna dapat menyimpan hasil terjemahan penting ke daftar kosakata lokal, memperbaruinya, atau menghapusnya.
*   Dilengkapi bilah pencarian (*search bar*) yang responsif dan penyaringan kosakata secara real-time.

### 9. Profil Pengguna (Profile Screen)
*   Menampilkan nama pengguna, email, tingkat level saat ini, serta statistik persentase kelulusan materi belajar.
*   Memiliki tata letak modern yang bersih dengan kartu level penuh (*full-width*) setelah pembersihan sistem EXP yang tidak digunakan.

### 10. Pengaturan Aplikasi (Settings Screen)
*   Memungkinkan pengguna mengaktifkan atau menonaktifkan Mode Gelap (Dark Mode) secara persisten untuk kenyamanan visual.

---

## Tumpukan Teknologi & Arsitektur

Aplikasi ini menggunakan pola arsitektur **Clean Architecture** yang terbagi ke dalam empat modul logika utama di bawah subdirektori `composeApp/src/commonMain/kotlin/com/example/cakapAi/`:

```text
cakapAi/
├── core/           # Konfigurasi DI, HttpClient, expect/actual platform-specific helpers
├── data/           # Repositori konkret, database SQLDelight, remote API, preferences (DataStore)
├── domain/         # Model data murni dan antarmuka (interface) repositori
└── presentation/   # Komponen Jetpack Compose UI, Navigation, dan ViewModel (StateFlow)
```

### Library & Framework Utama:
1.  **Kotlin Multiplatform (KMP)**: Berbagi kode logika bisnis tunggal (Common) untuk dijalankan di berbagai platform.
2.  **Compose Multiplatform**: Framework deklaratif modern untuk merancang antarmuka pengguna bersama.
3.  **SQLDelight**: Driver basis data SQL type-safe untuk penyimpanan lokal (*offline support*).
4.  **Ktor Client**: Library client HTTP untuk mengelola pemanggilan API Gemini secara asinkron.
5.  **Koin**: Dependency Injection (DI) yang ringan dan dioptimalkan untuk Kotlin Multiplatform.
6.  **Jetpack DataStore**: Pengganti SharedPreferences yang modern untuk penyimpanan konfigurasi persisten.
7.  **Kotlinx Coroutines & Flow**: Mengelola penanganan background thread dan manajemen state reaktif (`StateFlow`).
8.  **Navigation Compose**: Navigasi type-safe menggunakan Kotlin Serialization untuk mengarahkan pengguna antar layar.

---

## Fitur Lanjutan & Integrasi Platform (expect/actual)

CakapAI memanfaatkan pola `expect/actual` dari Kotlin Multiplatform untuk mengakses fitur hardware asli pada perangkat target secara mulus:

*   **Speech Recognition**: Mengakses mesin pengenal suara native platform (Android SpeechRecognizer API) untuk menganalisis akurasi pelafalan kata pengguna saat sesi *Speaking Practice*.
*   **Text To Speech (TTS)**: Menggunakan mesin suara bawaan perangkat untuk melafalkan teks bahasa Inggris dengan logat alami agar didengar oleh pengguna.
*   **Audio Feedback**: Menginisialisasi pemutar audio lokal untuk memutar sound effect kustom secara asinkron ketika jawaban pengguna terdeteksi benar atau salah.
*   **Database Driver**: Menginisialisasi `AndroidSqliteDriver` untuk Android dan driver sqlite pendukung di platform lain saat runtime.

---

## Strategi Pengujian (Unit & Integration Testing)

Aplikasi ini telah dilengkapi dengan cakupan pengujian yang kuat untuk memastikan keandalan logika bisnis. Semua pengujian lulus verifikasi kompilasi lokal:

*   **Repository Tests**: Menguji kebenaran query database SQLDelight (Notes/Vocabulary CRUD), skenario kegagalan jaringan, dan pengalihan ke bank soal fallback ketika offline.
*   **ViewModel Tests**: Menggunakan library **Turbine** untuk memantau emisi state perubahan data pada `StateFlow` di `AITutorViewModel`, `MapViewModel`, `QuizViewModel`, dan `DictionaryViewModel`.
*   **Mocking API**: Pemanggilan API jarak jauh Gemini disimulasikan menggunakan **Ktor MockEngine** agar pengujian tetap dapat dijalankan secara konsisten tanpa koneksi internet dan tidak menghabiskan kuota token API asli.

### Cara Menjalankan Pengujian Lokal:
Jalankan perintah berikut pada terminal di root proyek Anda untuk menjalankan seluruh rangkaian pengujian dan melihat status kelulusannya:

```bash
./gradlew testDebugUnitTest
```

---

## Penyiapan Awal & Cara Menjalankan Proyek

### 1. Prasyarat
*   Android Studio Jellyfish atau yang lebih baru.
*   JDK 17 or JDK 21 terinstal di sistem Anda.

### 2. Kloning Repositori
```bash
git clone https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
```

### 3. Konfigurasi Kunci API Gemini
Fitur AI Tutor dan Terjemahan membutuhkan kunci API Gemini gratis. Anda perlu menambahkannya ke file `local.properties` di root direktori proyek Anda agar dibaca secara otomatis saat kompilasi:

```properties
# file: local.properties
GEMINI_API_KEY=isi_dengan_kunci_api_gemini_anda
```

### 4. Build & Jalankan Aplikasi
*   Buka proyek menggunakan **Android Studio**.
*   Biarkan proses Gradle Sync berjalan hingga selesai.
*   Pilih target perangkat (Emulator Android atau perangkat fisik).
*   Klik tombol **Run** (Ikon Segitiga Hijau) untuk mengompilasi dan menginstal aplikasi ke perangkat Anda.
