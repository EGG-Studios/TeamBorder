package org.croxie.teamBorder;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.configuration.file.FileConfiguration;

public class WorldBorderManager {

    private final TeamBorder plugin;

    public WorldBorderManager(TeamBorder plugin) {
        this.plugin = plugin;
    }

    public void setupWorldBorder() {
        int worldBorderSize = plugin.getConfig().getInt("world-border.start");
        boolean isWorldBorderEnabled = plugin.getConfig().getBoolean("world-border.set");

        if (!isWorldBorderEnabled) {
           World world = Bukkit.getWorlds().get(0);
           if (world != null) {
               world.getWorldBorder().setCenter(0, 0);
               world.getWorldBorder().setSize(worldBorderSize);

               plugin.getConfig().set("world-border.set", true);
               plugin.saveConfig();
           }
        }
    }
}
