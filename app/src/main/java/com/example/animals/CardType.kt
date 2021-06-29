package com.example.animals

import kotlin.random.Random

enum class CardType {
    Dog, Cat, Elephant, Horse, Mouse;

    fun getEmoji(): String {
        return when (this) {
            Dog -> "\uD83D\uDC15"
            Cat -> "\uD83D\uDC08"
            Elephant -> "\uD83D\uDC18"
            Horse -> "\uD83D\uDC0E"
            Mouse -> "\uD83D\uDC01"
        }
    }

    companion object {
        @ExperimentalStdlibApi
        fun getRandom(seed: Int): CardType {
            return values().random(Random(seed))
        }
    }
}