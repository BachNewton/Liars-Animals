package com.example.animals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animals.GameState.*
import com.example.animals.ui.theme.AnimalsTheme
import java.util.*
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    @ExperimentalStdlibApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AnimalsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.padding(15.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Liar's Animals", fontWeight = FontWeight.Bold, fontSize = 30.sp)

                        var gameState by rememberSaveable { mutableStateOf(SeedSelect) }
                        val deck = rememberSaveable { mutableListOf<CardType>() }
                        val knownCards = rememberSaveable { mutableListOf<CardType>() }
                        var deckSizeSliderSaved by rememberSaveable { mutableStateOf(5f) }
                        var revealSizeSliderSaved by rememberSaveable { mutableStateOf(3f) }
                        var animalTypesSliderSaved by rememberSaveable { mutableStateOf(5f) }

                        when (gameState) {
                            SeedSelect -> DrawSeedSelect(
                                deckSizeSliderSaved,
                                revealSizeSliderSaved,
                                animalTypesSliderSaved,
                                fun (seed: String, deckSize: Int, revealSize: Int, animalTypes: Int) {
                                    deckSizeSliderSaved = deckSize.toFloat()
                                    revealSizeSliderSaved = revealSize.toFloat()
                                    animalTypesSliderSaved = animalTypes.toFloat()

                                    val generatedDeck = generateDeckFromSeed(seed, deckSize, animalTypes)
                                    deck.clear()
                                    deck.addAll(generatedDeck)
                                    knownCards.clear()
                                    knownCards.addAll(getKnownCards(generatedDeck, revealSize))
                                    gameState = KnownCards
                                }
                            )

                            KnownCards -> DrawKnownCards(Collections.unmodifiableList(knownCards), deck.size - knownCards.size) {
                                gameState = DeckReveal
                            }

                            DeckReveal -> DrawDeck(Collections.unmodifiableList(deck)) {
                                gameState = SeedSelect
                            }
                        }
                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    private fun generateDeckFromSeed(seedWord: String, deckSize: Int, animalTypes: Int): List<CardType> {
        val seedAsNumber = seedWord.hashCode()

        val deck = mutableListOf<CardType>()
        for (i in 0 until deckSize) {
            deck.add(CardType.getRandom(seedAsNumber + i, animalTypes))
        }

        return Collections.unmodifiableList(deck).sortedBy { card -> card.name }
    }

    private fun getKnownCards(deck: List<CardType>, revealSize: Int): List<CardType> {
        return deck.shuffled().slice(0 until revealSize).sortedBy { card -> card.name }
    }

    @Composable
    private fun DrawSeedSelect(
        deckSizeInit: Float,
        revealSizeInit: Float,
        animalTypesInit: Float,
        generateCallback: (String, Int, Int, Int) -> Unit
    ) {
        var deckSeed by rememberSaveable { mutableStateOf("") }

        OutlinedTextField(
            value = deckSeed,
            onValueChange = { deckSeed = it },
            label = { Text("Deck Seed") }
        )

        Spacer(Modifier.size(15.dp))

        var deckSizeSlider by rememberSaveable { mutableStateOf(deckSizeInit) }
        var revealSizeSlider by rememberSaveable { mutableStateOf(revealSizeInit) }
        var animalTypesSlider by rememberSaveable { mutableStateOf(animalTypesInit) }

        Text("Deck Size: ${deckSizeSlider.roundToInt()}")
        Slider(
            value = deckSizeSlider,
            onValueChange = { newValue ->
                revealSizeSlider = revealSizeSlider.coerceAtMost(deckSizeSlider - 1)
                animalTypesSlider = animalTypesSlider.coerceAtMost(deckSizeSlider)
                deckSizeSlider = newValue
            },
            valueRange = 2f..25f,
            steps = 22
        )

        Text("Reveal Size: ${revealSizeSlider.roundToInt()}")
        Slider(
            value = revealSizeSlider,
            onValueChange = { newValue -> revealSizeSlider = newValue },
            valueRange = 1f..(deckSizeSlider - 1),
            steps = (deckSizeSlider.roundToInt() - 3).coerceAtLeast(0)
        )

        Text("Animal Types: ${animalTypesSlider.roundToInt()}")
        Slider(
            value = animalTypesSlider,
            onValueChange = { newValue -> animalTypesSlider = newValue },
            valueRange = 2f..(6f.coerceAtMost(deckSizeSlider)),
            steps = (6f.coerceAtMost(deckSizeSlider).roundToInt() - 3).coerceAtLeast(0)
        )

        Spacer(Modifier.size(15.dp))

        Button(onClick = {
            generateCallback(
                deckSeed,
                deckSizeSlider.roundToInt(),
                revealSizeSlider.roundToInt(),
                animalTypesSlider.roundToInt()
            )
         }, enabled = deckSeed.isNotBlank()) {
            Text("Generate!")
        }
    }

    @Composable
    private fun DrawKnownCards(knownCards: List<CardType>, hidden: Int, revealCallback: () -> Unit) {
        Column(
            Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in knownCards.indices step 3) {
                Row {
                    if (i + 0 < knownCards.size) AnimalCard(knownCards[i + 0])
                    if (i + 1 < knownCards.size) AnimalCard(knownCards[i + 1])
                    if (i + 2 < knownCards.size) AnimalCard(knownCards[i + 2])
                }
            }

            Text("There are $hidden cards hidden from you")

            Button(onClick = { revealCallback() }) {
                Text("Reveal the Deck!")
            }
        }
    }

    @Composable
    private fun DrawDeck(deck: List<CardType>, newGameCallback: () -> Unit) {
        Column(
            Modifier.verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            for (i in deck.indices step 3) {
                Row {
                    if (i + 0 < deck.size) AnimalCard(deck[i + 0])
                    if (i + 1 < deck.size) AnimalCard(deck[i + 1])
                    if (i + 2 < deck.size) AnimalCard(deck[i + 2])
                }
            }

            Button(onClick = { newGameCallback() }) {
                Text("New Game")
            }
        }
    }

    @Composable
    private fun AnimalCard(type: CardType) {
        Card(
            modifier = Modifier
                .padding(5.dp)
                .border(2.dp, color = Color.Black)
                .width(100.dp)
                .height(150.dp),
            backgroundColor = Color.LightGray
        ) {
            Column(
                modifier = Modifier.padding(5.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = type.name)
                Text(text = type.getEmoji(), fontSize = 40.sp)
            }
        }
    }
}