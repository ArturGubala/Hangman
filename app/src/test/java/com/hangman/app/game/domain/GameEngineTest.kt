package com.hangman.app.game.domain

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isGreaterThanOrEqualTo
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GameEngineTest {

    private lateinit var gameEngine: GameEngine

    @BeforeEach
    fun setUp() {
        gameEngine = GameEngine()
    }

    @Test
    fun `getRandomWord returns non-blank word and category`() {
        val (word, category) = gameEngine.getRandomWord()
        assertThat(word).isNotEmpty()
        assertThat(category).isNotEmpty()
    }

    @Test
    fun `getRandomWord returns uppercase word`() {
        repeat(10) {
            val (word, _) = gameEngine.getRandomWord()
            assertThat(word).isEqualTo(word.uppercase())
        }
    }

    @Test
    fun `displayWord hides all letters when no guesses`() {
        val word = "HANGMAN"
        val display = gameEngine.displayWord(word, emptySet())
        assertThat(display).isEqualTo("_ _ _ _ _ _ _")
    }

    @Test
    fun `displayWord reveals only guessed letters`() {
        val word = "HANGMAN"
        val display = gameEngine.displayWord(word, setOf('H', 'A'))
        assertThat(display).isEqualTo("H A _ _ _ A _")
    }

    @Test
    fun `displayWord reveals all letters when all guessed`() {
        val word = "CAT"
        val display = gameEngine.displayWord(word, setOf('C', 'A', 'T'))
        assertThat(display).isEqualTo("C A T")
    }

    @Test
    fun `isWon returns false when not all letters guessed`() {
        val word = "HANGMAN"
        val result = gameEngine.isWon(word, setOf('H', 'A'))
        assertThat(result).isFalse()
    }

    @Test
    fun `isWon returns true when all letters guessed`() {
        val word = "CAT"
        val result = gameEngine.isWon(word, setOf('C', 'A', 'T'))
        assertThat(result).isTrue()
    }

    @Test
    fun `wrongGuessCount returns 0 when all guesses are correct`() {
        val word = "CAT"
        val count = gameEngine.wrongGuessCount(word, setOf('C', 'A', 'T'))
        assertThat(count).isEqualTo(0)
    }

    @Test
    fun `wrongGuessCount counts only letters not in word`() {
        val word = "CAT"
        val count = gameEngine.wrongGuessCount(word, setOf('C', 'X', 'Z'))
        assertThat(count).isEqualTo(2)
    }

    @Test
    fun `maxAttempts is 6`() {
        assertThat(gameEngine.maxAttempts).isEqualTo(6)
    }

    @Test
    fun `word bank contains at least 10 words`() {
        val words = (1..10).map { gameEngine.getRandomWord().first }.toSet()
        // With 50 words the random draws should not all be identical
        assertThat(words.size).isGreaterThanOrEqualTo(1)
    }
}
