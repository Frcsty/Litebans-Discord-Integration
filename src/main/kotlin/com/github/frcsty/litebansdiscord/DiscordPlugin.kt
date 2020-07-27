package com.github.frcsty.litebansdiscord

import com.github.frcsty.litebansdiscord.discord.Discord
import org.bukkit.plugin.java.JavaPlugin

class DiscordPlugin : JavaPlugin() {

    val prefix = "-"

    override fun onEnable() {
        saveDefaultConfig()

        Discord(this)
    }

    override fun onDisable() {
        reloadConfig()
    }
}