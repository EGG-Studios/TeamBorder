package org.croxie.teamBorder

import org.bukkit.Bukkit

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
}