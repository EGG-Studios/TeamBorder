package org.croxie.teamBorder

import org.bukkit.Bukkit
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.*

class TeamBorder : JavaPlugin(), Listener {
    private val damagedPlayers = mutableSetOf<UUID>()
    private val deadTeams = mutableSetOf<String>()
    private val worldBorderManager = WorldBorderManager(this)

    private var lastDay: Long = 0L

    override fun onEnable() {
        server.pluginManager.registerEvents(this, this)
        server.scheduler.runTaskTimer(this, Runnable {
            checkDay()
        }, 0L, 1L)

        saveDefaultConfig()

        worldBorderManager.setupWorldBorder()
        logger.info("Plugin loaded successfully!")
    }

    private fun checkDay() {
        val world = Bukkit.getWorlds()[0]
        val currentDay = world.fullTime / 24000

        if (currentDay != lastDay) {
            val players = Bukkit.getOnlinePlayers()
            for (player in players) {
                player.playSound(player.location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f)
                player.sendMessage("A day without people dying?! Your reward is a world border expansion!")
            }

            lastDay = currentDay
            worldBorderManager.expandBorder()
        }
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

    @EventHandler
    fun onPlayerDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val team = PlayerManager().checkTeam(player) ?: return

        deadTeams.add(team.name)
        worldBorderManager.shrinkBorder()
    }
}
