package com.github.frcsty.litebansdiscordjava.command;

import com.github.frcsty.litebansdiscordjava.command.holder.InformationHolder;
import com.github.frcsty.litebansdiscordjava.command.util.InformationUtil;
import com.github.frcsty.litebansdiscordjava.util.Task;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class UserHistoryCommand extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        Task.async(() -> {
            final long start = System.currentTimeMillis();
            final TextChannel channel = event.getChannel();
            final Message message = event.getMessage();
            final String content = message.getContentRaw();

            if (!content.startsWith("-history")) {
                return;
            }

            final String[] arguments = content.split(" ");
            if (arguments.length <= 1) {
                channel.sendMessage(
                        "The entered command requires a 'Minecraft User' (Name or UUID) parameter!"
                ).queue();
                return;
            }

            final String argument = arguments[1];
            UUID userIdentifier;
            OfflinePlayer player;

            if (argument.length() == 36) {
                userIdentifier = UUID.fromString(argument);

                player = Bukkit.getOfflinePlayer(userIdentifier);
            } else {
                player = Bukkit.getOfflinePlayer(argument);

                userIdentifier = player.getUniqueId();
            }

            final List<InformationHolder> holders = InformationUtil.getUserBans(userIdentifier);
            if (holders.isEmpty()) {
                channel.sendMessage(
                        "The specified user ('" + player.getName() + "') does not have any history!"
                ).queue();
                return;
            }

            final int fromPosition = arguments.length < 3 ? 0 : Integer.parseInt(arguments[2]);
            final int toPosition = arguments.length < 4 ? fromPosition + 5 : Integer.parseInt(arguments[3]);

            channel.sendMessage(getFormattedEmbedBuilder(holders, player, fromPosition, toPosition, start).build()).queue();
        });
    }

    private EmbedBuilder getFormattedEmbedBuilder(final List<InformationHolder> holders, final OfflinePlayer player, final int from, final int to, final long start) {
        final EmbedBuilder builder = new EmbedBuilder().setColor(Color.CYAN);
        final StringBuilder stringBuilder = new StringBuilder();

        builder.setTitle("History for " + player.getName() + " (Limit: " + holders.size() + "):");
        for (int index = from; index <= to; index++) {
            if (holders.size() < index + 1) continue;

            final InformationHolder holder = holders.get(index);
            stringBuilder.append(getFormattedInformation(holder, index));
        }

        builder.setDescription(stringBuilder.toString());
        builder.setFooter("Executed in " + (System.currentTimeMillis() - start) + "ms");

        return builder;
    }

    private StringBuilder getFormattedInformation(final InformationHolder holder, final int position) {
        final StringBuilder builder = new StringBuilder();

        final Date time = new Date(holder.time);
        final Date until = new Date(holder.until);

        final String active = StringUtils.capitalize(String.valueOf(holder.isActive));

        builder.append("```\n");
        builder.append("[").append((position + 1)).append("] Time: ").append(time).append("\n");
        builder.append(" - Reason: ").append(holder.reason).append("\n");
        builder.append(" - Banned By: ").append(holder.punisher).append("\n");

        if (holder.isActive) {
            builder.append(" - Expires: ").append(until).append("\n");
        }

        builder.append(" - Active: ").append(active).append("\n");
        builder.append("```");

        return builder;
    }

}
