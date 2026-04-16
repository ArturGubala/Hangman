package com.hangman.app.game.domain

class GameEngine {

    val maxAttempts: Int = 6

    private val wordBank: List<Pair<String, String>> = listOf(
        // Animals
        "ELEPHANT" to "Animals",
        "GIRAFFE" to "Animals",
        "PENGUIN" to "Animals",
        "CROCODILE" to "Animals",
        "BUTTERFLY" to "Animals",
        "KANGAROO" to "Animals",
        "CHEETAH" to "Animals",
        "DOLPHIN" to "Animals",
        "FLAMINGO" to "Animals",
        "PORCUPINE" to "Animals",
        // Countries
        "BRAZIL" to "Countries",
        "AUSTRALIA" to "Countries",
        "PORTUGAL" to "Countries",
        "SWEDEN" to "Countries",
        "ARGENTINA" to "Countries",
        "THAILAND" to "Countries",
        "ETHIOPIA" to "Countries",
        "COLOMBIA" to "Countries",
        "NORWAY" to "Countries",
        "VIETNAM" to "Countries",
        // Technology
        "KEYBOARD" to "Technology",
        "ALGORITHM" to "Technology",
        "DATABASE" to "Technology",
        "COMPILER" to "Technology",
        "FRAMEWORK" to "Technology",
        "INTERFACE" to "Technology",
        "NETWORK" to "Technology",
        "PROTOCOL" to "Technology",
        "PROCESSOR" to "Technology",
        "SOFTWARE" to "Technology",
        // Food
        "AVOCADO" to "Food",
        "BROCCOLI" to "Food",
        "CINNAMON" to "Food",
        "PRETZEL" to "Food",
        "SPAGHETTI" to "Food",
        "BLUEBERRY" to "Food",
        "MUSHROOM" to "Food",
        "PINEAPPLE" to "Food",
        "RASPBERRY" to "Food",
        "CHOCOLATE" to "Food",
        // Sports
        "VOLLEYBALL" to "Sports",
        "BADMINTON" to "Sports",
        "GYMNASTICS" to "Sports",
        "WRESTLING" to "Sports",
        "ARCHERY" to "Sports",
        "FENCING" to "Sports",
        "KAYAKING" to "Sports",
        "TRIATHLON" to "Sports",
        "SKATEBOARD" to "Sports",
        "SNOWBOARD" to "Sports"
    )

    fun getRandomWord(): Pair<String, String> = wordBank.random()

    fun displayWord(word: String, guessedLetters: Set<Char>): String {
        return word.map { letter ->
            if (letter in guessedLetters) letter else '_'
        }.joinToString(" ")
    }

    fun isWon(word: String, guessedLetters: Set<Char>): Boolean {
        return word.all { it in guessedLetters }
    }

    fun wrongGuessCount(word: String, guessedLetters: Set<Char>): Int {
        return guessedLetters.count { it !in word }
    }
}
