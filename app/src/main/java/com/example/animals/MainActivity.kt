package com.example.animals

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animals.GameState.*
import com.example.animals.ui.theme.AnimalsTheme

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
                        Text(text = "Animal Game", fontWeight = FontWeight.Bold, fontSize = 30.sp)

                        var gameState by rememberSaveable { mutableStateOf(SeedSelect) }
                        val deck = rememberSaveable { mutableListOf<CardType>() }
                        val knownCards = rememberSaveable { mutableListOf<CardType>() }
//                        var seedWord by remember { mutableStateOf(DEFAULT_SEED_WORD) }

                        when (gameState) {
                            SeedSelect -> DrawSeedSelect(fun (seed: String) {
//                                seedWord = seed
                                val generatedDeck = generateDeckFromSeed(seed)
                                deck.clear()
                                deck.addAll(generatedDeck)
                                knownCards.clear()
                                knownCards.addAll(getKnownCards(generatedDeck))
                                gameState = KnownCards
                            })

                            KnownCards -> DrawKnownCards(knownCards) {
                                gameState = DeckReveal
                            }

                            DeckReveal -> DrawDeck(deck) {
                                gameState = SeedSelect
                            }
                        }
                    }
                }
            }
        }
    }

    @ExperimentalStdlibApi
    private fun generateDeckFromSeed(seedWord: String): List<CardType> {
        val seedAsNumber = seedWord.hashCode()
//        val seedAsNumber = join("", seedWord.map { it.code.toString() }).toLong()

        return listOf(
            CardType.getRandom(seedAsNumber + 0),
            CardType.getRandom(seedAsNumber + 1),
            CardType.getRandom(seedAsNumber + 2),
            CardType.getRandom(seedAsNumber + 3),
            CardType.getRandom(seedAsNumber + 4)
        )
    }

    private fun getKnownCards(deck: List<CardType>): List<CardType> {
        val deckShuffled = deck.shuffled()

        return listOf(
            deckShuffled[0],
            deckShuffled[1],
            deckShuffled[2]
        )
    }

    @Composable
    private fun DrawSeedSelect(generateCallback: (seedWord: String) -> Unit) {
        var deckSeed by rememberSaveable { mutableStateOf("") }

        OutlinedTextField(
            value = deckSeed,
            onValueChange = { deckSeed = it },
            label = { Text("Deck Seed") }
        )

        Spacer(Modifier.size(5.dp))

        Button(onClick = { generateCallback(deckSeed) }, enabled = deckSeed.isNotBlank()) {
            Text("Generate!")
        }
    }

    @Composable
    private fun DrawKnownCards(knownCards: MutableList<CardType>, revealCallback: () -> Unit) {
        Row {
            AnimalCard(type = knownCards[0])
            AnimalCard(type = knownCards[1])
            AnimalCard(type = knownCards[2])
        }
        
        Text("There are two cards hidden from you")

        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
            Button(onClick = { revealCallback() }) {
                Text("Reveal the Deck!")
            }
        }
    }

    @Composable
    private fun DrawDeck(deck: MutableList<CardType>, newGameCallback: () -> Unit) {
        when (LocalConfiguration.current.orientation) {
            Configuration.ORIENTATION_PORTRAIT -> {
                Row {
                    AnimalCard(type = deck[0])
                    AnimalCard(type = deck[1])
                    AnimalCard(type = deck[2])
                }
                Row {
                    AnimalCard(type = deck[3])
                    AnimalCard(type = deck[4])
                }
            }

            else -> {
                Row {
                    AnimalCard(type = deck[0])
                    AnimalCard(type = deck[1])
                    AnimalCard(type = deck[2])
                    AnimalCard(type = deck[3])
                    AnimalCard(type = deck[4])
                }
            }
        }

        Column(Modifier.fillMaxHeight(), verticalArrangement = Arrangement.Bottom) {
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