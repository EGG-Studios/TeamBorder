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
                isExpanding = true
                world.worldBorder.setSize(currentWorldBorderSize + 32.0, 10L)
                Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                    isExpanding = false
                }, 11L)
            } else {
                for (player in Bukkit.getOnlinePlayers()) {
                    player.playSound(player.location, Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f)
                    player.sendMessage("Congratulations! The world border has reached its maximum size.")
                }
            }
        }
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
                    onComplete()
                } else {
                    world.worldBorder.setSize(prediction, 10L)
                    waitForBorderSize(prediction) { onComplete() }
                }
            }
        }
    }
}