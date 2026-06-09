package com.example.data.model

data class WordPuzzle(
    val id: String,
    val word: String,
    val hint: String,
    val category: String,
    val difficulty: String
)

object GameData {
    val puzzlePool = listOf(
        // Easy (3-4 Letters)
        WordPuzzle("p_01", "MOON", "Earth's glowing companion in the night sky", "Space", "Easy"),
        WordPuzzle("p_02", "BIRD", "A feather-covered singer soaring between branches", "Nature", "Easy"),
        WordPuzzle("p_03", "CAMP", "Pitch a tent under starry wild canopies", "Travel", "Easy"),
        WordPuzzle("p_04", "STAR", "A burning spark in the infinite velvet of cosmic dark", "Space", "Easy"),
        WordPuzzle("p_05", "CAKE", "Sweet, frosted layers marking solar orbits", "Food", "Easy"),
        WordPuzzle("p_06", "WIND", "Invisible currents shaking trees and cooling fields", "Nature", "Easy"),
        WordPuzzle("p_07", "BOAT", "Glides along water paths, carried by wood and wind", "Travel", "Easy"),
        WordPuzzle("p_08", "FIRE", "A crackling, glowing heart of warmth and amber shadows", "Nature", "Easy"),
        WordPuzzle("p_09", "BOOK", "Folds of paper housing empires and silent dreams", "Culture", "Easy"),
        WordPuzzle("p_10", "RAIN", "Whispering skies dropping crystals on thirsty soil", "Nature", "Easy"),

        // Medium (5-6 Letters)
        WordPuzzle("p_11", "CAMERA", "Freezes light to trap memories in silver and pixels", "Technology", "Medium"),
        WordPuzzle("p_12", "FOREST", "A dense fortress of moss, tall wood, and wild eyes", "Nature", "Medium"),
        WordPuzzle("p_13", "COFFEE", "Warm, dark fuel ground from roasted tropical cherries", "Food", "Medium"),
        WordPuzzle("p_14", "PLANET", "A spherical traveler dancing around an stellar furnace", "Space", "Medium"),
        WordPuzzle("p_15", "SUMMER", "Bright sunrays, sandy skin, and lengthy warm light", "Nature", "Medium"),
        WordPuzzle("p_16", "DESERT", "A golden ocean of dust, silence, and scorching heat", "Travel", "Medium"),
        WordPuzzle("p_17", "ROCKET", "Iron tubes powered by fire breaking free of gravity", "Space", "Medium"),
        WordPuzzle("p_18", "PUZZLE", "Jumbled tiles waiting for alignment in a unified image", "Culture", "Medium"),
        WordPuzzle("p_19", "CATCH", "To meet a flying orbit with fingers or leather mitts", "Sport", "Medium"),
        WordPuzzle("p_20", "OCEAN", "A massive world of blue tides, dark depths, and salt", "Nature", "Medium"),

        // Hard (7-8 Letters)
        WordPuzzle("p_21", "VOLCANO", "A tectonic chimney breathing brimstone and melted quartz", "Nature", "Hard"),
        WordPuzzle("p_22", "COMET", "A dirty snowball with a glowing tail racing past stars", "Space", "Hard"),
        WordPuzzle("p_23", "JOURNEY", "An epic path walked across valleys or inner spaces", "Travel", "Hard"),
        WordPuzzle("p_24", "COMPASS", "A spinning needle seeking the earth's silent magnetic pole", "Travel", "Hard"),
        WordPuzzle("p_25", "HISTORY", "The long tail of records we leave in the mud of time", "Culture", "Hard"),
        WordPuzzle("p_26", "ASTRONAUT", "A brave soul floating in suits between silent voids", "Space", "Hard"),
        WordPuzzle("p_27", "MOUNTAIN", "Giant stone wrinkles rising to kiss the storm fronts", "Nature", "Hard"),
        WordPuzzle("p_28", "SANDWICH", "Two dough-baked protective slices framing general goods", "Food", "Hard"),
        WordPuzzle("p_29", "TREASURE", "Buried wooden coffers holding sparkling gold fortunes", "Travel", "Hard"),
        WordPuzzle("p_30", "FESTIVAL", "A colorful gathering of singers, lanterns, and loud joy", "Culture", "Hard")
    )

    // Gets 3 daily puzzles based on day offset of YYYY-MM-DD
    fun getDailyPuzzles(dateString: String): List<WordPuzzle> {
        val hash = dateString.hashCode().let { if (it < 0) -it else it }
        val easyList = puzzlePool.filter { it.difficulty == "Easy" }
        val mediumList = puzzlePool.filter { it.difficulty == "Medium" }
        val hardList = puzzlePool.filter { it.difficulty == "Hard" }

        val p1 = easyList[hash % easyList.size]
        val p2 = mediumList[(hash + 1) % mediumList.size]
        val p3 = hardList[(hash + 2) % hardList.size]

        return listOf(p1, p2, p3)
    }
}
