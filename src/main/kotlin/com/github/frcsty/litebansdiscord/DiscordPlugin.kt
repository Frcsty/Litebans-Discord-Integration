package com.github.frcsty.litebansdiscord

import com.github.frcsty.litebansdiscord.discord.Discord
import org.bukkit.plugin.java.JavaPlugin

class DiscordPlugin : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()

        Discord(this)
    }

    override fun onDisable() {
        reloadConfig()
    }
}