package com.github.frcsty.litebansdiscord.discord.command

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.util.InformationHolder
import com.github.frcsty.litebansdiscord.discord.util.getUserBans
import com.github.frcsty.litebansdiscord.discord.util.hasMissingPermission
import com.github.frcsty.litebansdiscord.discord.util.isNotMember
import me.mattstudios.mfjda.annotations.Command
import me.mattstudios.mfjda.annotations.Default
import me.mattstudios.mfjda.base.CommandBase
import net.dv8tion.jda.api.EmbedBuilder
import org.apache.commons.lang.StringUtils
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.awt.Color
import java.util.*

@Command("history")
class HistoryCommand(private val plugin: DiscordPlugin) : CommandBase() {

    @Default
    fun historyCommand() {
        println("Ran history command")
        val message = message
        val channel = message.channel
        val user = message.author

        if (user.isNotMember(message)) return
        if (message.member.hasMissingPermission(plugin.config.getString("settings.requiredRoleId"))) {
            channel.sendMessage("You do not have the required permission for this!").queue()
            return
        }

        val args = message.contentRaw.split(" ").toTypedArray()
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
        channel.sendMessage(getFormattedEmbed(holders, player, fromPosition, toPosition).build()).queue()
    }

    private fun getFormattedEmbed(holders: List<InformationHolder>, player: OfflinePlayer, from: Int, to: Int): EmbedBuilder {
        val embedBuilder = EmbedBuilder().setColor(Color.CYAN)
        val builder = StringBuilder()
        embedBuilder.setTitle("History for ${player.name} (Limit: ${holders.size}):")
        for (i in from until to) {
            if (holders.size < i + 1) continue
            val holder = holders[i]
            builder.append(getFormattedHistoryInformation(holder, i))
        }
        embedBuilder.setDescription(builder.toString())
        return embedBuilder
    }

    private fun getFormattedHistoryInformation(holder: InformationHolder, position: Int): StringBuilder {
        val position = position + 1
        val time = Date(holder.time)
        val expiry = Date(holder.until)
        val active = StringUtils.capitalize(holder.isActive.toString())

        val builder = StringBuilder()
        builder.append("```\n")
        builder.append("[$position] Time: $time\n")
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