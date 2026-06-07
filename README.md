# CakapAI - Sprint 4 Polish & Testing

![CI](https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile/actions/workflows/ci.yml/badge.svg)

## Sprint 4 Features
* Bug Fixes
* UI Polish
* Unit Testing
* UI Testing
* Coverage Report

## Bug Fixes
* Fixed navigation issue
* Fixed crash when opening profile
* Fixed state not updating
* Fixed search filtering issue

## UI Improvements
* Splash Screen refinement
* Consistent spacing
* Better typography
* Improved card layout
* Better dark mode support
* Improved icon consistency

## Testing Strategy

### Repository Tests
* Insert note
* Update note
* Delete note
* Search note
* Error handling

### ViewModel Tests
* Initial state
* Loading state
* Success state
* Empty state
* Error state

### UI Tests
* Home Screen
* Add Note Flow
* Vocabulary Search

## Menjalankan Test & Coverage

Proyek ini telah dilengkapi dengan Unit Test dan UI Test untuk memenuhi rubrik Sprint 4. Kover dikonfigurasi untuk mengecualikan UI classes agar dapat berfokus pada Business Logic (ViewModel, Repository, Model, DTO).

Untuk menjalankan seluruh test dan melakukan *generate coverage report* di Windows, jalankan perintah berikut di terminal (PowerShell/CMD):

```powershell
.\gradlew test koverHtmlReport
```

Atau jika menggunakan Git Bash / Mac / Linux:
```bash
./gradlew test koverHtmlReport
```

## Coverage Report
Setelah perintah di atas selesai, laporan coverage Kover dalam format HTML akan tersedia di path berikut:
`composeApp/build/reports/kover/html/index.html`

**Status Coverage Saat Ini (Kover Report):**
- **Line Coverage:** 62.5%
- **Instruction Coverage:** 52.7%
- Target rubrik **50%+ Overall Coverage** telah tercapai! ✅

### Screenshot Coverage
*(Harap ambil screenshot halaman `index.html` yang terbuka di browser dan simpan di folder `docs/` dengan nama `coverage_screenshot.png` untuk memenuhi rubrik)*

[Coverage Screenshot]
![1780849311303](image/README/1780849311303.png)

## Demo Video
[Link Video Demo Sprint 4](#)

---

## Deskripsi Aplikasi

CakapAI adalah aplikasi pembelajaran bahasa berbasis AI yang dirancang untuk membantu pengguna belajar bahasa melalui fitur dictionary, vocabulary, practice quiz, speaking practice, dan AI Tutor. Aplikasi ini menggunakan Gemini API untuk mendukung pembelajaran interaktif serta SQLDelight untuk penyimpanan lokal agar aplikasi tetap bisa digunakan secara offline.

---


## Sprint 3 Overview

Pada Sprint 3, aplikasi CakapAI difokuskan pada implementasi *advanced features* untuk melengkapi fungsionalitas utama, yaitu:
- Search/filter functionality
- API integration (Gemini API)
- Offline support (SQLDelight)
- Additional screen (Profile & Settings)
- Bonus feature (Splash screen, dark mode, animasi, speaking practice)
- UI polish & Splash screen branding

---

## Checklist Rubrik Sprint 3

| Komponen Rubrik | Bobot | Status | Implementasi |
|---|---:|:---:|---|
| Search / Filter | 25% | ✅ | Search vocabulary pada Dictionary Screen |
| API / Enhanced Local | 25% | ✅ | Gemini API untuk AI Tutor, Dictionary, dan Practice |
| Offline Support | 20% | ✅ | SQLDelight local database dan fallback questions |
| Additional Screen | 15% | ✅ | Profile Screen dan Settings Screen |
| Bonus Feature | 15% | ✅ | Dark mode, splash screen, animasi, speaking practice, dan text-to-speech |

---

## Detail Implementasi Sprint 3

### 1. Search / Filter

Fitur search diterapkan pada Dictionary Screen. Pengguna dapat mencari vocabulary yang tersimpan berdasarkan teks asli maupun hasil terjemahan. Search berjalan secara responsif dan langsung saat pengguna mengetik, dilengkapi tombol clear, serta empty state ketika tidak ada hasil yang cocok.

### 2. API Integration / Enhanced Local

Aplikasi menggunakan Gemini API sebagai fitur advanced untuk mendukung pembelajaran berbasis AI. Integrasi API digunakan pada fitur AI Tutor, Practice, dan bantuan pembelajaran lainnya. HTTP request dikelola menggunakan Ktor Client melalui HttpClientFactory dan konfigurasi API dikelola secara aman. Jika request API gagal, aplikasi tetap memberikan fallback agar pengguna masih dapat melanjutkan latihan.

### 3. Offline Support

Aplikasi mendukung penggunaan offline melalui penyimpanan lokal menggunakan SQLDelight. Data seperti vocabulary, progress level, quiz history, dan saved vocabulary tetap dapat diakses tanpa koneksi internet. Pada fitur Practice, aplikasi juga menyediakan offline fallback questions sehingga pengguna tetap dapat mengerjakan latihan meskipun koneksi internet tidak tersedia atau API gagal dipanggil.

### 4. Additional Screen

Sprint 3 menambahkan dan menyempurnakan screen tambahan berupa Profile Screen dan Settings Screen. Profile Screen menampilkan informasi pengguna, level, progress, dan statistik pembelajaran. Settings Screen digunakan untuk pengaturan aplikasi seperti dark mode dan preferensi tampilan.

### 5. Bonus Features

Aplikasi memiliki beberapa bonus feature untuk meningkatkan pengalaman pengguna, yaitu:
- Dark mode support
- Splash screen branding menggunakan logo CakapAI
- Animasi pada tampilan UI aplikasi
- Speaking practice menggunakan microphone
- Text-to-speech untuk mendukung latihan speaking
- Sound dan vibration feedback

---

## Sprint 3 Update — Splash Screen Branding

Pada Sprint 3, aplikasi CakapAI ditambahkan screen awal berupa Splash Screen untuk memperkuat identitas aplikasi.

### Fitur yang Ditambahkan
- Splash Screen saat aplikasi pertama kali dibuka
- Menampilkan logo CakapAI
- Menampilkan nama aplikasi dan tagline
- Animasi logo sederhana
- Loading indicator
- Transisi otomatis ke halaman utama
- Support light mode dan dark mode

### Asset Logo
Logo yang digunakan berada di:
```text
composeApp/src/commonMain/composeResources/drawable/logo_cakapai.png
```

### Alur Aplikasi
`App Open → Splash Screen → Main Screen`

### Tujuan
Fitur ini dibuat agar aplikasi terlihat lebih profesional, memiliki identitas visual yang jelas, dan memberikan pengalaman awal yang lebih baik kepada pengguna.

---

## Core Features dari Sprint 2 yang Tetap Berjalan

Fitur inti dari Sprint 2 tetap dipertahankan dan masih berjalan pada Sprint 3, yaitu:
- Navigation antar screen
- Bottom navigation
- Dictionary / vocabulary management
- CRUD vocabulary
- Local database menggunakan SQLDelight
- UI state seperti loading, success, empty, dan error
- Quiz / Practice Screen
- Map / Home Screen
- Repository pattern

---

## Teknologi yang Digunakan

- Kotlin Multiplatform
- Compose Multiplatform
- Material 3
- SQLDelight
- DataStore
- Ktor Client
- Gemini API
- Kotlinx Serialization
- Coroutines
- StateFlow
- Navigation Compose

---

## Struktur Project

```text
composeApp/
├── src/
│   ├── commonMain/
│   │   ├── kotlin/
│   │   │   └── com/example/cakapAi/
│   │   │       ├── core/
│   │   │       ├── data/
│   │   │       ├── domain/
│   │   │       └── presentation/
│   │   ├── composeResources/
│   │   │   └── drawable/
│   │   │       └── logo_cakapai.png
│   │   └── sqldelight/
│   └── androidMain/
│       ├── kotlin/
│       └── res/
```

---

## Cara Menjalankan Project

Clone repository:
```bash
git clone https://github.com/MartinoKelvin/Proyek-Pengembangan-Aplikasi-Mobile.git
cd Proyek-Pengembangan-Aplikasi-Mobile
```

Jalankan build untuk Windows:
```powershell
gradlew.bat :composeApp:assembleDebug
```

Untuk Mac/Linux:
```bash
./gradlew :composeApp:assembleDebug
```

Buka project di Android Studio, tunggu Gradle Sync selesai, lalu jalankan pada emulator atau perangkat Android (run `composeApp`).

---

## Konfigurasi Gemini API Key

Aplikasi menggunakan Gemini API untuk fitur AI Tutor dan Practice. Pastikan API key sudah dikonfigurasi melalui properti environment atau `local.properties`. 

Contoh penambahan di `local.properties`:
```properties
GEMINI_API_KEY=YOUR_API_KEY_HERE
```
Untuk keamanan, jangan commit API key asli ke repository publik. Aplikasi akan otomatis menarik API Key ini pada saat build.

---

## Video Demo Sprint 3

Link video demo:
```text
https://drive.google.com/drive/folders/1UU3RQus6D0go0HLjzZQivif-fUOKpKLf?usp=sharing
```
Demo Sprint 3 menampilkan:
- Splash Screen logo
- Search vocabulary
- AI Tutor / Gemini API
- Practice / Quiz
- Offline fallback
- Profile Screen
- Settings Screen
- Dark mode
- Speaking practice

---

## Status Build / CI

Status build:
```text
✅ Build berhasil / menunggu pengecekan lokal
```

Perintah build lokal:
```bash
gradlew.bat :composeApp:assembleDebug
```
Workflow CI di GitHub Actions (`ci.yml`) juga memastikan *code quality* dan integritas *build* secara berkala.

---

## Catatan Pengembangan

- Pada Sprint 3, arsitektur yang sudah ada disempurnakan dengan *error handling* untuk interaksi jaringan (Ktor) dan *graceful degradation* (fallback to local db) ketika offline.
- Tampilan dan fungsionalitas tambahan dititikberatkan pada kualitas pengalaman pengguna, dibuktikan dengan hadirnya UI animasi, *dark mode*, dan navigasi splash.

---

## Kesimpulan

Project CakapAI sudah memenuhi rubrik penilaian **Sprint 3: Advanced Features** karena memiliki:
- Search/filter functionality yang responsif
- Gemini API integration yang berfungsi
- Offline support dengan *fallback* yang aman
- Profile dan Settings Screen yang matang
- Bonus feature seperti dark mode, splash screen branding, animasi UI, speaking practice, dan text-to-speech
- Core features Sprint 2 yang secara utuh dipertahankan dan tetap berjalan dengan baik.
