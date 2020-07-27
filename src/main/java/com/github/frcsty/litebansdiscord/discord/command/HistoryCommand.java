package com.github.frcsty.litebansdiscord.discord.command;

import com.github.frcsty.litebansdiscord.DiscordPlugin;
import com.github.frcsty.litebansdiscord.discord.util.InformationHolder;
import com.github.frcsty.litebansdiscord.discord.util.Utilities;
import me.mattstudios.mfjda.annotations.Command;
import me.mattstudios.mfjda.annotations.Default;
import me.mattstudios.mfjda.annotations.Prefix;
import me.mattstudios.mfjda.base.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Date;
import java.util.List;

@Prefix("-")
@Command("history")
public final class HistoryCommand extends CommandBase {

    private final DiscordPlugin plugin;

    public HistoryCommand(final DiscordPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void historyCommand() {
        System.out.println("Ran history command");
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
        final List<InformationHolder> holders = Utilities.getUserBans(player);
        final int fromPosition = args.length < 3 ? 0 : Integer.valueOf(args[2]);
        final int toPosition = args.length < 4 ? fromPosition + 5 : Integer.valueOf(args[3]);

        if (holders.size() == 0) {
            channel.sendMessage("User " + player.getName() + " has no history.").queue();
            return;
        }

        channel.sendMessage(getFormattedEmbed(holders, player, fromPosition, toPosition).build()).queue();
        holders.clear();
    }

    private EmbedBuilder getFormattedEmbed(final List<InformationHolder> holders, final OfflinePlayer player, final int from, final int to) {
        final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        final StringBuilder builder = new StringBuilder();
        embedBuilder.setTitle("History for " + player.getName() + " (Limit: " + holders.size() + "):");

        for (int i = from; i < to; i++) {
            if (holders.size() < i + 1) continue;
            final InformationHolder holder = holders.get(i);

            if (holder == null) continue;
            builder.append(getFormattedHistoryInformation(holder, i));
        }

        embedBuilder.setDescription(builder.toString());
        return embedBuilder;
    }

    private StringBuilder getFormattedHistoryInformation(final InformationHolder holder, final int position) {
        final StringBuilder builder = new StringBuilder();

        builder.append("```\n");
        builder.append("[" + (position + 1) + "] Time: " + new Date(holder.getTime()) + "\n");
        builder.append(" - Reason: " + holder.getReason() + "\n");
        builder.append(" - Banned By: " + holder.getPunisher() + "\n");

        if (holder.isActive()) {
            builder.append(" - Expires: " + new Date(holder.getUntil()) + "\n");
        }

        builder.append(" - Active: " + StringUtils.capitalize(String.valueOf(holder.isActive())) + "\n");
        builder.append("```");

        return builder;
    }
}
