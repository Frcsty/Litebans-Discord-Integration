package com.github.frcsty.litebansdiscord.discord.command

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.util.HistoryHolder
import com.github.frcsty.litebansdiscord.discord.util.getUserIPHistory
import com.github.frcsty.litebansdiscord.discord.util.hasMissingPermission
import com.github.frcsty.litebansdiscord.discord.util.isNotMember
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.awt.Color
import java.util.function.Consumer

class IpHistoryCommand(private val plugin: DiscordPlugin) : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val message = event.message
        val content = message.contentRaw
        if (content.startsWith("${plugin.prefix}iphistory").not() && !content.startsWith(event.guild.selfMember.asMention)) {
            return
        }

        val channel = event.channel
        val user = event.author

        if (user.isNotMember(message)) return
        if (message.member.hasMissingPermission(plugin.config.getString("settings.requiredRoleId"))) {
            channel.sendMessage("You do not have the required permission for this!").queue()
            return
        }

        val args = content.split(" ").toTypedArray()
        if (args.size == 1) {
            channel.sendMessage("You did not specify a user to check!").queue()
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val holders = player.getUserIPHistory()
        if (holders.isEmpty()) {
            channel.sendMessage("User ${player.name} has no logic history.").queue()
            return
        }

        channel.sendMessage(getFormattedEmbed(player, holders).build()).queue()
    }

    private fun getFormattedEmbed(player: OfflinePlayer, holders: List<HistoryHolder>): EmbedBuilder {
        val embedBuilder = EmbedBuilder().setColor(Color.CYAN)
        val builder = StringBuilder()
        embedBuilder.setTitle("Login history for ${player.name} (Limit: ${holders.size}):")
        holders.forEach(Consumer { holder: HistoryHolder -> builder.append(getFormattedIPHistoryInformation(holder)) })
        embedBuilder.setDescription(builder.toString())
        return embedBuilder
    }

    private fun getFormattedIPHistoryInformation(holder: HistoryHolder): StringBuilder {
        val builder = StringBuilder()
        builder.append(" - [${holder.date}] ${holder.name}: ${holder.ip}\n")
        return builder
    }

}