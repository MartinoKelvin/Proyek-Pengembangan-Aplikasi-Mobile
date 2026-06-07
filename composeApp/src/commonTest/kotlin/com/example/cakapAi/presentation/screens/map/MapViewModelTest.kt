package com.example.cakapAi.presentation.screens.map

import com.example.cakapAi.domain.model.LevelProgress
import com.example.cakapAi.domain.model.QuizHistory
import com.example.cakapAi.domain.model.Vocabulary
import com.example.cakapAi.domain.repository.LearningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeLearningRepository : LearningRepository {
    val levelsFlow = MutableStateFlow<List<LevelProgress>>(emptyList())

    override fun getAllLevels(): Flow<List<LevelProgress>> = levelsFlow

    override fun getLevelById(levelId: Long): Flow<LevelProgress?> = levelsFlow.map { list ->
        list.find { it.id.toLong() == levelId }
    }

    override suspend fun completeLevel(levelId: Long, score: Int, accuracy: Double) {
        levelsFlow.update { list ->
            list.map {
                if (it.id.toLong() == levelId) {
                    it.copy(isCompleted = true, highScore = maxOf(it.highScore, score))
                } else it
            }
        }
    }

    override suspend fun unlockNextLevel(levelId: Long) {
        levelsFlow.update { list ->
            list.map {
                if (it.id.toLong() == levelId) {
                    it.copy(isUnlocked = true)
                } else it
            }
        }
    }

    override suspend fun insertQuizHistory(
        levelId: Long,
        score: Int,
        totalQuestions: Int,
        accuracy: Double,
        isPassed: Boolean
    ) {
        // No-op for now
    }

    override fun getQuizHistory(levelId: Long): Flow<List<QuizHistory>> {
        return MutableStateFlow(emptyList())
    }

    override fun getVocabularies(levelId: Long): Flow<List<Vocabulary>> {
        return MutableStateFlow(emptyList())
    }

    override suspend fun initializeLevelsIfEmpty() {
        if (levelsFlow.value.isEmpty()) {
            levelsFlow.value = listOf(
                LevelProgress(1, "Dasar 1", "Salam", "LISTENING", true, false, 0, 0),
                LevelProgress(2, "Dasar 2", "Angka", "SPEAKING", false, false, 0, 0)
            )
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class MapViewModelTest {

    private lateinit var repository: FakeLearningRepository
    private lateinit var viewModel: MapViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeLearningRepository()
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_shouldBeLoadingOrEmpty() = runTest {
        viewModel = MapViewModel(repository)
        val state = viewModel.uiState.first()
        assertTrue(state is MapUiState.Loading || state is MapUiState.Empty)
    }

    @Test
    fun loadLevels_success_shouldShowData() = runTest {
        repository.levelsFlow.value = listOf(
            LevelProgress(1, "Test 1", "Sub 1", "LISTENING", true, false, 0, 0)
        )
        viewModel = MapViewModel(repository)
        advanceUntilIdle() // Process flows

        val state = viewModel.uiState.value
        assertTrue(state is MapUiState.Success)
        assertEquals(1, (state as MapUiState.Success).levels.size)
    }

    @Test
    fun loadLevels_empty_shouldShowEmptyState() = runTest {
        repository.levelsFlow.value = emptyList()
        // Prevent initializeLevelsIfEmpty from polluting by redefining the behaviour or just check after clear
        repository.levelsFlow.value = emptyList()
        viewModel = MapViewModel(repository)
        
        // Wait for init to populate data
        advanceUntilIdle()
        
        // But let's override it back to empty to test the map logic
        repository.levelsFlow.value = emptyList()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state is MapUiState.Empty)
    }

    @Test
    fun savePracticeResult_success_shouldUnlockNextLevel() = runTest {
        repository.levelsFlow.value = listOf(
            LevelProgress(1, "Test 1", "Sub 1", "LISTENING", true, false, 0, 0),
            LevelProgress(2, "Test 2", "Sub 2", "SPEAKING", false, false, 0, 0)
        )
        viewModel = MapViewModel(repository)
        
        viewModel.savePracticeResult(1, 100, true)
        advanceUntilIdle()

        val levels = repository.levelsFlow.value
        assertTrue(levels[0].isCompleted)
        assertTrue(levels[1].isUnlocked)
    }

    @Test
    fun savePracticeResult_fail_shouldNotUnlockNextLevel() = runTest {
        repository.levelsFlow.value = listOf(
            LevelProgress(1, "Test 1", "Sub 1", "LISTENING", true, false, 0, 0),
            LevelProgress(2, "Test 2", "Sub 2", "SPEAKING", false, false, 0, 0)
        )
        viewModel = MapViewModel(repository)
        
        viewModel.savePracticeResult(1, 50, false)
        advanceUntilIdle()

        val levels = repository.levelsFlow.value
        assertTrue(!levels[0].isCompleted)
        assertTrue(!levels[1].isUnlocked)
    }
}
