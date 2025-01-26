package org.croxie.teamBorder

import org.bukkit.plugin.java.JavaPlugin

class TeamBorder : JavaPlugin() {

    override fun onEnable() {
        logger.info("Enabling...")

        saveDefaultConfig();

        val worldBorderManager = WorldBorderManager(this)
        worldBorderManager.setupWorldBorder();
        logger.info("Plugin loaded successfully!")
    }

    override fun onDisable() {
        logger.info("Disabling...")
    }
}
