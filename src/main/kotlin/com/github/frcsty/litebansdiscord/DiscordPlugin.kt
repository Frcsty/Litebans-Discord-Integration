package com.github.frcsty.litebansdiscord

import com.github.frcsty.litebansdiscord.discord.Discord
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

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

fun log(text: String) {
    val plugin = JavaPlugin.getProvidingPlugin(DiscordPlugin::class.java)

    plugin.logger.log(Level.INFO, text)
}