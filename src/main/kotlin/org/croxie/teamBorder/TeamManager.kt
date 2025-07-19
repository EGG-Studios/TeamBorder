package org.croxie.teamBorder

import org.bukkit.Bukkit
import org.bukkit.entity.Player


class TeamManager(private val plugin: TeamBorder) {
    fun checkEmpty(): String? {
        val teams = Bukkit.getScoreboardManager().mainScoreboard.teams

        for (team in teams) {
            if (team.size < plugin.config.getInt("group-size")) {
                return team.name
            }
        }
        return null
    }

    fun createTeam(): String {
        val adjectives = arrayOf(
            "Happy", "Sad", "Angry", "Brave", "Calm", "Eager", "Fancy", "Gentle", "Harsh", "Itchy",
            "Jolly", "Kind", "Lazy", "Moody", "Neat", "Odd", "Proud", "Quick", "Rich", "Silly",
            "Tame", "Ugly", "Vast", "Wild", "Young", "Zealous", "Ancient", "Bright", "Clever", "Dull",
            "Elegant", "Fierce", "Graceful", "Hungry", "Innocent", "Jumpy", "Keen", "Lively", "Mighty", "Narrow",
            "Open", "Polite", "Quiet", "Rare", "Shiny", "Tough", "Unusual", "Vivid", "Weird", "Yummy",
            "Zany", "Adorable", "Bitter", "Chilly", "Dirty", "Excited", "Famous", "Giant", "Hot", "Icy",
            "Jealous", "Klutzy", "Lucky", "Messy", "Nasty", "Obvious", "Plain", "Quirky", "Rude", "Sharp",
            "Tasty", "Uneven", "Vast", "Witty", "Zesty", "Agile", "Bashful", "Clumsy", "Delightful", "Energetic",
            "Fragile", "Grumpy", "Helpful", "Itchy", "Jagged", "Kooky", "Loud", "Massive", "Nifty", "Obedient",
            "Panicky", "Quaint", "Reckless", "Scary", "Tiny", "Upbeat", "Victorious", "Wicked", "Yearly", "Zonal"
        )
        val nouns = arrayOf(
            "Apple", "Ball", "Cat", "Dog", "Egg", "Fox", "Garden", "House", "Island", "Jacket",
            "Kite", "Lion", "Mountain", "Notebook", "Ocean", "Pencil", "Queen", "River", "Star", "Tree",
            "Umbrella", "Violin", "Whale", "Xylophone", "Yacht", "Zebra", "Artist", "Bridge", "Castle", "Doctor",
            "Engine", "Forest", "Guitar", "Helmet", "Igloo", "Jungle", "Kitchen", "Lamp", "Monkey", "Nest",
            "Orange", "Pillow", "Quilt", "Robot", "School", "Train", "Uniform", "Village", "Window", "Xerox",
            "Yogurt", "Seal", "Airplane", "Basket", "Camera", "Desk", "Eagle", "Fan", "Glove", "Hill",
            "Ink", "Jewel", "Key", "Leaf", "Market", "Net", "Oven", "Plate", "Queen", "Road",
            "Spoon", "Table", "Unicorn", "Vase", "Wheel", "Xenon", "Yard", "Zone", "Actor", "Bottle",
            "Chicken", "Drum", "Engineers", "Feather", "Gift", "Horse", "Iceberg", "Jar", "Knife", "Lake",
            "Magazine", "Needle", "Office", "Pizza", "Quartz", "Ring", "Stone", "Tunnel", "Visitor", "Wagon"
        )

        val teamName = adjectives.random() + nouns.random()
        Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam(teamName)
        return teamName
    }

    fun addPlayer(player: Player, teamName: String) {
        val team = Bukkit.getScoreboardManager().mainScoreboard.getTeam(teamName)
        team?.addPlayer(player)
    }
}