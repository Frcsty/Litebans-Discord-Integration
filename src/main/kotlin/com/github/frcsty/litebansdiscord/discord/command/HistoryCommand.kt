package com.github.frcsty.litebansdiscord.discord.command

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.util.InformationHolder
import com.github.frcsty.litebansdiscord.discord.util.getUserBans
import com.github.frcsty.litebansdiscord.discord.util.isNotMember
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.awt.Color
import java.util.*

class HistoryCommand(private val plugin: DiscordPlugin) : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val start = System.currentTimeMillis()
        val message = event.message
        val content = message.contentRaw
        if (content.startsWith("${plugin.prefix}history").not() && !content.startsWith(event.guild.selfMember.asMention)) {
            return
        }

        val channel = event.channel
        val user = event.author

        if (user.isNotMember(message)) return
        /*
        if (message.member.hasMissingPermission(plugin.config.getString("settings.requiredRoleId"))) {
            channel.sendMessage("You do not have the required permission for this!").queue()
            return
        }
        */

        val args = content.split(" ").toTypedArray()
        if (args.size == 1) {
            channel.sendMessage("You did not specify a user to check!").queue()
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val holders = player.getUserBans()
        val fromPosition = if (args.size < 3) 0 else Integer.valueOf(args[2])
        val toPosition = if (args.size < 4) fromPosition + 5 else Integer.valueOf(args[3])
        if (holders.isEmpty()) {
            channel.sendMessage("User ${player.name} has no history.").queue()
            return
        }
        message.delete().queue()
        channel.sendMessage(getFormattedEmbed(holders, player, fromPosition, toPosition, start).build()).queue()
    }

    private fun getFormattedEmbed(holders: List<InformationHolder>, player: OfflinePlayer, from: Int, to: Int, start: Long): EmbedBuilder {
        val embedBuilder = EmbedBuilder().setColor(Color.CYAN)
        val builder = StringBuilder()
        embedBuilder.setTitle("History for ${player.name} (Limit: ${holders.size}):")

        var i = from
        while (i < to) {
            if (holders.size < i + 1) continue
            val holder = holders[i]
            builder.append(getFormattedHistoryInformation(holder, i))
            i++
        }
        embedBuilder.setDescription(builder.toString())
        embedBuilder.setFooter("Executed in ${System.currentTimeMillis() - start}ms", null)
        return embedBuilder
    }

    private fun getFormattedHistoryInformation(holder: InformationHolder, position: Int): StringBuilder {
        val time = Date(holder.time)
        val expiry = Date(holder.until)
        val active = StringUtils.capitalize(holder.isActive.toString())

        val builder = StringBuilder()
        builder.append("```\n")
        builder.append("[${position + 1}] Time: $time\n")
        builder.append(" - Reason: ${holder.reason}\n")
        builder.append(" - Banned By: ${holder.punisher}\n")
        if (holder.isActive) {
            builder.append(" - Expires: $expiry\n")
        }
        builder.append(" - Active: $active\n")
        builder.append("```")
        return builder
    }

}