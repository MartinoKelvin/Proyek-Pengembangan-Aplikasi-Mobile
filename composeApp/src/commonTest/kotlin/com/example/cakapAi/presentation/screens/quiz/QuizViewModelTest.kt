package com.example.cakapAi.presentation.screens.quiz

import com.example.cakapAi.presentation.screens.map.FakeLearningRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    private lateinit var repository: FakeLearningRepository
    private lateinit var viewModel: QuizViewModel
    private val testDispatcher = StandardTestDispatcher()

    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeLearningRepository()
        viewModel = QuizViewModel(levelId = 1, repository = repository)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_shouldBeSuccessWithQuestions() = runTest {
        val state = viewModel.uiState.first()
        assertTrue(state is QuizUiState.Success)
        
        state as QuizUiState.Success
        assertEquals(5, state.questions.size)
        assertEquals(0, state.currentQuestionIndex)
        assertEquals(3, state.lives)
        assertEquals(0, state.correctCount)
        assertFalse(state.isFinished)
    }

    @Test
    fun selectAnswer_shouldUpdateSelectedAnswer() = runTest {
        viewModel.selectAnswer("Selamat pagi")
        val state = viewModel.uiState.first() as QuizUiState.Success
        
        assertEquals("Selamat pagi", state.selectedAnswer)
        assertFalse(state.isAnswerChecked)
    }

    @Test
    fun checkAnswer_correct_shouldIncreaseCorrectCountAndKeepLives() = runTest {
        viewModel.selectAnswer("Selamat pagi") // The correct answer for level 1 question 1
        viewModel.checkAnswer()
        
        val state = viewModel.uiState.first() as QuizUiState.Success
        assertTrue(state.isAnswerChecked)
        assertEquals(1, state.correctCount)
        assertEquals(3, state.lives)
    }

    @Test
    fun checkAnswer_incorrect_shouldDecreaseLives() = runTest {
        viewModel.selectAnswer("Selamat malam") // Incorrect answer
        viewModel.checkAnswer()
        
        val state = viewModel.uiState.first() as QuizUiState.Success
        assertTrue(state.isAnswerChecked)
        assertEquals(0, state.correctCount)
        assertEquals(2, state.lives)
    }

    @Test
    fun nextQuestion_shouldAdvanceToNextQuestion() = runTest {
        viewModel.selectAnswer("Selamat pagi")
        viewModel.checkAnswer()
        viewModel.nextQuestion()
        
        val state = viewModel.uiState.first() as QuizUiState.Success
        assertEquals(1, state.currentQuestionIndex)
        assertEquals(null, state.selectedAnswer)
        assertFalse(state.isAnswerChecked)
    }

    @Test
    fun nextQuestion_whenFinished_shouldFinishQuiz() = runTest {
        // Complete 5 questions
        repeat(5) {
            val state = viewModel.uiState.value as QuizUiState.Success
            val currentQ = state.questions[state.currentQuestionIndex]
            viewModel.selectAnswer(currentQ.correctAnswer)
            viewModel.checkAnswer()
            viewModel.nextQuestion()
        }
        
        val finalState = viewModel.uiState.first() as QuizUiState.Success
        assertTrue(finalState.isFinished)
    }
    
    @Test
    fun getQuestionsForLevel_shouldReturnCorrectQuestions() {
        val level1 = getQuestionsForLevel(1)
        assertEquals(5, level1.size)
        assertEquals("Selamat pagi", level1[0].correctAnswer)

        val levelUnknown = getQuestionsForLevel(99)
        assertEquals(5, levelUnknown.size)
        assertEquals("Ya", levelUnknown[0].correctAnswer)
    }
}
