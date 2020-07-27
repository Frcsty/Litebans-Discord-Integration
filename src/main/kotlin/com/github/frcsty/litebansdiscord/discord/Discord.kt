package com.github.frcsty.litebansdiscord.discord

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.command.CheckBanCommand
import com.github.frcsty.litebansdiscord.discord.command.HistoryCommand
import com.github.frcsty.litebansdiscord.discord.command.IpHistoryCommand
import net.dv8tion.jda.core.JDA
import net.dv8tion.jda.core.JDABuilder
import net.dv8tion.jda.core.OnlineStatus
import java.util.logging.Level
import javax.security.auth.login.LoginException

class Discord(private val plugin: DiscordPlugin) {

    private val jda: JDA? = startBot()

    private fun setupDiscord() {
        if (jda == null) return

        jda.addEventListener(IpHistoryCommand(plugin))
        jda.addEventListener(HistoryCommand(plugin))
        jda.addEventListener(CheckBanCommand(plugin))
    }

    private fun startBot(): JDA? {
        return try {
            JDABuilder().setToken(plugin.config.getString("settings.token"))
                    .setStatus(OnlineStatus.ONLINE)
                    .build().awaitReady()
        } catch (ex: LoginException) {
            plugin.logger.log(Level.WARNING, "Discord bot was unable to start! Please verify the bot token is correct.")
            plugin.pluginLoader.disablePlugin(plugin)
            return null
        } catch (ex: InterruptedException) {
            plugin.logger.log(Level.WARNING, "Discord bot was unable to start! Please verify the bot token is correct.")
            plugin.pluginLoader.disablePlugin(plugin)
            return null
        }
    }

    init {
        setupDiscord()
    }
}