package org.croxie.teamBorder

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class TeamBorder : JavaPlugin(), Listener {
    private val damagedPlayers = mutableSetOf<UUID>()

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
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
                player.sendMessage("You have been added to: $team")
            } else {
                teamManager.addPlayer(player, teamName)
                player.sendMessage("You have been added to: $teamName")
            }
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            if (damagedPlayers.contains(player.uniqueId)) return

            val team = PlayerManager().checkTeam(player)
            val onlineTeammates = team?.entries
                ?.mapNotNull { Bukkit.getPlayer(it) }
                ?.filter { it.uniqueId != player.uniqueId }

            onlineTeammates?.forEach { teammate ->
                damagedPlayers.add(teammate.uniqueId)
                teammate.damage(event.damage)
                damagedPlayers.remove(teammate.uniqueId)
            }
        }
    }
}
