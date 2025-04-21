package org.croxie.teamBorder

import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin

class TeamBorder : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()

        val worldBorderManager = WorldBorderManager(this)
        worldBorderManager.setupWorldBorder()
        logger.info("Plugin loaded successfully!")
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        val playerManager = PlayerManager()
        val teamState = playerManager.checkTeam(player)

        if (teamState == null) {
            val teamManager = TeamManager(this)
            val teamName = teamManager.checkEmpty()

            if (teamName == null) {
                val team = teamManager.createTeam()

                teamManager.addPlayer(player, team)
                player.sendMessage("You have been added to the team: $team")
            } else {
                teamManager.addPlayer(player, teamName)
                player.sendMessage("You have been added to the team: $teamName")
            }
        }
    }
}
