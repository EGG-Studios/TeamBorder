package org.croxie.teamBorder

import org.bukkit.Bukkit
import org.bukkit.Sound

class WorldBorderManager(private val plugin: TeamBorder) {
    fun setupWorldBorder() {
        val worldBorderSize = plugin.config.getInt("world-border.start")
        val isWorldBorderEnabled = plugin.config.getBoolean("world-border.set")

        if (!isWorldBorderEnabled) {
            val world = Bukkit.getWorlds()[0]
            if (world != null) {
                world.worldBorder.setCenter(0.0, 0.0)
                world.worldBorder.size = worldBorderSize.toDouble()

                plugin.config["world-border.set"] = true
                plugin.saveConfig()
            }
        }
    }

    fun expandBorder() {
        val endWorldBorderSize = plugin.config.getInt("world-border.end")

        val world = Bukkit.getWorlds()[0]
        if (world != null) {
            val currentWorldBorderSize = world.worldBorder.size
            if (currentWorldBorderSize < endWorldBorderSize) {
                world.worldBorder.setSize(currentWorldBorderSize + 32.0, 10L)
            } else {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                    player.sendMessage("Congratulations! The world border has reached its maximum size.")
                }
            }
        }
    }

    fun shrinkBorder() {
        val world = Bukkit.getWorlds()[0]
        if (world != null) {
            val currentWorldBorderSize = world.worldBorder.size
            if (currentWorldBorderSize > 32) {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.playSound(player.location, Sound.ENTITY_EVOKER_PREPARE_ATTACK, 1f, 1f)
                    player.sendMessage("Someone died... The world border is shrinking!")
                }

                val prediction = currentWorldBorderSize - 32
                if (prediction < 32) {
                    world.worldBorder.size = 32.0
                } else {
                    world.worldBorder.setSize(prediction, 10L)
                }
            }
        }
    }
}