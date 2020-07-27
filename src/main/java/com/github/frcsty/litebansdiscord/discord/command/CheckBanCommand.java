package com.github.frcsty.litebansdiscord.discord.command;

import com.github.frcsty.litebansdiscord.DiscordPlugin;
import com.github.frcsty.litebansdiscord.discord.util.InformationHolder;
import com.github.frcsty.litebansdiscord.discord.util.Utilities;
import litebans.api.Database;
import me.mattstudios.mfjda.annotations.Command;
import me.mattstudios.mfjda.annotations.Default;
import me.mattstudios.mfjda.base.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Date;
import java.util.List;

@Command("-checkban")
public final class CheckBanCommand extends CommandBase {

    private final DiscordPlugin plugin;
    public CheckBanCommand(final DiscordPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void checkBanCommand() {
        final Message message = getMessage();
        final MessageChannel channel = message.getChannel();
        final User user = message.getAuthor();

        if (Utilities.isNotUser(user, message)) return;
        if (message.getMember() == null) return;
        if (Utilities.hasMissingPermission(message.getMember(), plugin.getConfig().getString("settings.requiredRoleId"))) {
            channel.sendMessage("You do not have the required permission for this!").queue();
            return;
        }

        final String[] args = message.getContentRaw().split(" ");
        if (args.length == 1) {
            channel.sendMessage("You did not specify a user to check!").queue();
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        final boolean isBanned = Database.get().isPlayerBanned(player.getUniqueId(), null);
        if (!isBanned) {
            channel.sendMessage("User " + player.getName() + " is not banned!").queue();
            return;
        }

        final List<InformationHolder> holders = Utilities.getUserBans(player);
        InformationHolder activeHolder = null;
        for (final InformationHolder holder : holders) {
            if (holder.isActive()) {
                activeHolder = holder;
                break;
            }
        }

        if (activeHolder == null) {
            channel.sendMessage("Punishment data for user " + player.getName() + " is incomplete or missing!").queue();
            return;
        }

        channel.sendMessage(getFormattedEmbed(player, activeHolder).build()).queue();
    }

    private EmbedBuilder getFormattedEmbed(final OfflinePlayer player, final InformationHolder holder) {
        final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        embedBuilder.setTitle("Target " + player.getName() + " is banned:");
        embedBuilder.setDescription(getFormattedCheckBanInformation(holder));

        return embedBuilder;
    }

    private StringBuilder getFormattedCheckBanInformation(final InformationHolder holder) {
        final StringBuilder builder = new StringBuilder();

        builder.append(" - Banned By: " + holder.getPunisher() + "\n");
        builder.append(" - Reason: " + holder.getReason() + "\n");
        builder.append(" - Banned On: " + new Date(holder.getTime()) + "\n");
        builder.append(" - Banned Until: " + new Date(holder.getUntil()) + "\n");
        builder.append(" - Banned On Server: " + holder.getServerOrigin() + ", Server Scope: " + holder.getServerScope() + "\n");
        builder.append(" - IP Ban: " + holder.isIpBan() + ", Silent: " + holder.isSilent() + "\n");

        return builder;
    }
}
