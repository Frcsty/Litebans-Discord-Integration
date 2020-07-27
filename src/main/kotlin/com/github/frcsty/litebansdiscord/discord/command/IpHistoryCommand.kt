package com.github.frcsty.litebansdiscord.discord.command

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.util.HistoryHolder
import com.github.frcsty.litebansdiscord.discord.util.Utilities.getUserIPHistory
import com.github.frcsty.litebansdiscord.discord.util.Utilities.hasMissingPermission
import com.github.frcsty.litebansdiscord.discord.util.Utilities.isNotUser
import me.mattstudios.mfjda.annotations.Command
import me.mattstudios.mfjda.annotations.Default
import me.mattstudios.mfjda.base.CommandBase
import net.dv8tion.jda.api.EmbedBuilder
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.awt.Color
import java.util.function.Consumer

@Command("iphistory")
class IpHistoryCommand(private val plugin: DiscordPlugin) : CommandBase() {

    @Default
    fun ipHistoryCommand() {
        val message = message
        val channel = message.channel
        val user = message.author

        if (isNotUser(user, message)) return
        if (message.member == null) return
        if (hasMissingPermission(message.member!!, plugin.config.getString("settings.requiredRoleId"))) {
            channel.sendMessage("You do not have the required permission for this!").queue()
            return
        }

        val args = message.contentRaw.split(" ").toTypedArray()
        if (args.size == 1) {
            channel.sendMessage("You did not specify a user to check!").queue()
            return
        }

        val player = Bukkit.getOfflinePlayer(args[1])
        val holders = getUserIPHistory(player)
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