package org.croxie.teamBorder

import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.scoreboard.Team

class PlayerManager {
    // Manages the player, like checking if they're in a team, etc
    fun checkTeam(player: OfflinePlayer): Team? {
        return Bukkit.getScoreboardManager().mainScoreboard.getPlayerTeam(player)
    }
}