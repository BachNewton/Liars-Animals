package com.example.animals

import kotlin.random.Random

enum class CardType {
    Dog, Cat, Elephant, Horse, Mouse, Snake;

    fun getEmoji(): String {
        return when (this) {
            Dog -> "\uD83D\uDC15"
            Cat -> "\uD83D\uDC08"
            Elephant -> "\uD83D\uDC18"
            Horse -> "\uD83D\uDC0E"
            Mouse -> "\uD83D\uDC01"
            Snake -> "\uD83D\uDC0D"
        }
    }

    companion object {
        @ExperimentalStdlibApi
        fun getRandom(seed: Int, animalTypes: Int): CardType {
            return values().slice(0 until animalTypes).random(Random(seed))
        }
    }
}