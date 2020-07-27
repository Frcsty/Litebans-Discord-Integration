package com.github.frcsty.litebansdiscord.discord.command

import com.github.frcsty.litebansdiscord.DiscordPlugin
import com.github.frcsty.litebansdiscord.discord.util.InformationHolder
import com.github.frcsty.litebansdiscord.discord.util.getUserBans
import com.github.frcsty.litebansdiscord.discord.util.hasMissingPermission
import com.github.frcsty.litebansdiscord.discord.util.isNotMember
import litebans.api.Database
import me.mattstudios.mfjda.annotations.Command
import me.mattstudios.mfjda.annotations.Default
import me.mattstudios.mfjda.base.CommandBase
import net.dv8tion.jda.api.EmbedBuilder
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.awt.Color
import java.util.*

@Command("checkban")
class CheckBanCommand(private val plugin: DiscordPlugin) : CommandBase() {

    @Default
    fun checkBanCommand() {
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
        channel.sendMessage(getFormattedEmbed(player, activeHolder).build()).queue()
    }

    private fun getFormattedEmbed(player: OfflinePlayer, holder: InformationHolder): EmbedBuilder {
        val embedBuilder = EmbedBuilder().setColor(Color.CYAN)
        embedBuilder.setTitle("Target ${player.name} is banned:")
        embedBuilder.setDescription(getFormattedCheckBanInformation(holder))
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