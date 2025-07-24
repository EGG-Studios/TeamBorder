package org.croxie.teamBorder

import org.bukkit.Bukkit
import org.bukkit.Sound
import java.util.LinkedList
import java.util.Queue
import kotlin.math.abs

class WorldBorderManager(private val plugin: TeamBorder) {
    private val shrinkQueue: Queue<Unit> = LinkedList()
    private var isShrinking = false
    private var isExpanding = false

    fun setupWorldBorder() {
        val worldBorderSize = plugin.config.getInt("world-border.start")
        val isWorldBorderEnabled = plugin.config.getBoolean("world-border.set")
        val worlds = Bukkit.getWorlds()

        plugin.logger.warning("in setupWorldBorder, worldBorderSize: $worldBorderSize, isWorldBorderEnabled: $isWorldBorderEnabled")
        plugin.logger.info("in setupWorldBorder, worlds: ${worlds.size}")

        if (!isWorldBorderEnabled) {
            for (world in worlds) {
                if (world != null) {
                    plugin.logger.warning("setting world border for ${world.name} to $worldBorderSize")
                    world.worldBorder.setCenter(0.0, 0.0)
                    world.worldBorder.size = worldBorderSize.toDouble()
                }
            }
            plugin.config["world-border.set"] = true
            plugin.saveConfig()
        }
    }

    fun expandBorder() {
        val endWorldBorderSize = plugin.config.getInt("world-border.end")
        val worlds = Bukkit.getWorlds()

        for (world in worlds) {
            if (world != null) {
                val currentWorldBorderSize = world.worldBorder.size
                if (currentWorldBorderSize < endWorldBorderSize) {
                    isExpanding = true
                    val newSize = (currentWorldBorderSize + 16.0).coerceAtMost(endWorldBorderSize.toDouble())
                    world.worldBorder.setSize(newSize, 10L)
                }
            }
        }
        Bukkit.getScheduler().runTaskLater(plugin, Runnable {
            isExpanding = false
            val allExpanded = worlds.all { it != null && it.worldBorder.size >= endWorldBorderSize }
            if (allExpanded) {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                    player.sendMessage("Congratulations! The world border has reached its maximum size.")
                }
            }
        }, 11L)
    }

    fun queueShrinkBorder() {
        shrinkQueue.add(Unit)
        processQueue()
    }

    private fun processQueue() {
        if (isShrinking || isExpanding || shrinkQueue.isEmpty()) return
        isShrinking = true
        shrinkQueue.poll()
        shrinkBorder {
            isShrinking = false
            processQueue()
        }
    }

    private fun waitForBorderSize(target: Double, onDone: () -> Unit) {
        val world = Bukkit.getWorlds()[0]
        if (world != null && abs(world.worldBorder.size - target) < 0.1) {
            onDone()
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                waitForBorderSize(target, onDone)
            }, 2L)
        }
    }

    private fun shrinkBorder(onComplete: () -> Unit) {
        val worlds = Bukkit.getWorlds()

        for (world in worlds) {
            if (world != null) {
                val currentWorldBorderSize = world.worldBorder.size
                if (currentWorldBorderSize > 16) {
                    val prediction = currentWorldBorderSize - 16
                    if (prediction < 16) {
                        world.worldBorder.size = 16.0
                        onComplete()
                    } else {
                        world.worldBorder.setSize(prediction, 10L)
                        waitForBorderSize(prediction) { onComplete() }
                    }
                }
            }
        }
    }
}