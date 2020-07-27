package com.github.frcsty.litebansdiscord.discord

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.command.CheckBanCommand
import com.github.frcsty.litebansdiscord.discord.command.HistoryCommand
import com.github.frcsty.litebansdiscord.discord.command.IpHistoryCommand
import me.mattstudios.mfjda.base.CommandManager
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.utils.cache.CacheFlag
import java.util.logging.Level
import javax.security.auth.login.LoginException

class Discord(private val plugin: DiscordPlugin) {

    private var jda: JDA? = null

    private fun setupDiscord() {
        if (jda != null) {
            return
        }
        startBot()

        val commandManager = CommandManager(jda, "-")

        commandManager.register(listOf(
                CheckBanCommand(plugin),
                HistoryCommand(plugin),
                IpHistoryCommand(plugin)
        ))
    }

    private fun startBot() {
        try {
            jda = JDABuilder.create(plugin.config.getString("settings.token"), emptyList())
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                    .setStatus(OnlineStatus.ONLINE)
                    .build().awaitReady()
        } catch (ex: LoginException) {
            plugin.logger.log(Level.WARNING, "Discord bot was unable to start! Please verify the bot token is correct.")
            plugin.pluginLoader.disablePlugin(plugin)
        } catch (ex: InterruptedException) {
            plugin.logger.log(Level.WARNING, "Discord bot was unable to start! Please verify the bot token is correct.")
            plugin.pluginLoader.disablePlugin(plugin)
        }
    }

    init {
        setupDiscord()
    }
}