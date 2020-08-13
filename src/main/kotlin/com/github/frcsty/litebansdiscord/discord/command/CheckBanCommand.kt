package com.github.frcsty.litebansdiscord.discord.command

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.util.InformationHolder
import com.github.frcsty.litebansdiscord.discord.util.getOfflineUser
import com.github.frcsty.litebansdiscord.discord.util.getUserBans
import com.github.frcsty.litebansdiscord.discord.util.isNotMember
import litebans.api.Database
import net.dv8tion.jda.core.EmbedBuilder
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter
import org.bukkit.OfflinePlayer
import java.awt.Color
import java.util.*

class CheckBanCommand(private val plugin: DiscordPlugin) : ListenerAdapter() {

    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        val start = System.currentTimeMillis()
        val message = event.message
        val content = message.contentRaw
        if (content.startsWith("${plugin.prefix}checkban").not() && !content.startsWith(event.guild.selfMember.asMention)) {
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

        val player = getOfflineUser(args[1])
        if (player == null) {
            channel.sendMessage("Specified user does not exist! (User: ${args[1]})")
            return
        }
        val isBanned = Database.get().isPlayerBanned(player.uniqueId, null)
        if (!isBanned) {
            channel.sendMessage("User ${player.name} is not banned!").queue()
            return
        }
        val holders = player.getUserBans()
        var activeHolder: InformationHolder? = null
        for (holder in holders) {
            if (holder.isActive) {
                activeHolder = holder
                break
            }
        }
        if (activeHolder == null) {
            channel.sendMessage("Punishment data for user ${player.name} is incomplete or missing!").queue()
            return
        }

        channel.sendMessage(getFormattedEmbed(player, activeHolder, start).build()).queue()
    }

    private fun getFormattedEmbed(player: OfflinePlayer, holder: InformationHolder, start: Long): EmbedBuilder {
        val embedBuilder = EmbedBuilder().setColor(Color.CYAN)
        embedBuilder.setTitle("Target ${player.name} is banned:")
        embedBuilder.setDescription(getFormattedCheckBanInformation(holder))
        embedBuilder.setFooter("Executed in ${System.currentTimeMillis() - start}ms", null)
        return embedBuilder
    }

    private fun getFormattedCheckBanInformation(holder: InformationHolder): StringBuilder {
        val builder = StringBuilder()
        val time = Date(holder.time)
        val until = Date(holder.until)

        builder.append(" - Banned By: ${holder.punisher}\n")
        builder.append(" - Reason: ${holder.reason}\n")
        builder.append(" - Banned On: $time\n")
        builder.append(" - Banned Until: $until\n")
        builder.append(" - Banned On Server: ${holder.serverOrigin}, Server Scope: ${holder.serverScope}\n")
        builder.append(" - IP Ban: ${holder.isIpBan}, Silent: ${holder.isSilent}\n")
        return builder
    }

}