package it.danielebonaldo.filamentdemo.models

data class ItemToLoad(
    val name: String,
    val assetModel: String,
    val topDownView: Boolean,
    val printTimeMin: Int
)

val allItems = listOf(
    ItemToLoad(
        "VESA Support",
        "vesa_support",
        false,
        165
    ),
    ItemToLoad(
        "DevFest Milano Logo",
        "devfest_milano_flip",
        false,
        124
    ),
    ItemToLoad(
        "Valentine Rose",
        "valentine_rose",
        true,
        110
    ),
    ItemToLoad(
        "Custom Screw",
        "screw",
        false,
        37
    ),
    ItemToLoad(
        "Spiderman wall art",
        "spiderman",
        true,
        80
    ),
)
