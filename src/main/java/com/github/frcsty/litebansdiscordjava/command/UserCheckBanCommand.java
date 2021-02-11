package com.github.frcsty.litebansdiscordjava.command;

import com.github.frcsty.litebansdiscordjava.command.holder.InformationHolder;
import com.github.frcsty.litebansdiscordjava.command.util.InformationUtil;
import com.github.frcsty.litebansdiscordjava.util.Task;
import litebans.api.Database;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public final class UserCheckBanCommand extends ListenerAdapter {

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        Task.async(() -> {
            final long start = System.currentTimeMillis();
            final TextChannel channel = event.getChannel();
            final Message message = event.getMessage();
            final String content = message.getContentRaw();

            if (!content.startsWith("-checkban")) {
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

            final boolean isBanned = Database.get().isPlayerBanned(userIdentifier, player.isOnline() ? player.getPlayer().getAddress().getAddress().getHostAddress() : null);
            if (!isBanned) {
                channel.sendMessage(
                        "The specified user ('" + player.getName() + "') is not banned!"
                ).queue();
                return;
            }

            final List<InformationHolder> holders = InformationUtil.getUserBans(userIdentifier);
            InformationHolder activeHolder = null;

            for (final InformationHolder holder : holders) {
                if (holder.isActive) {
                    activeHolder = holder;
                    break;
                }
            }

            if (activeHolder == null) {
                channel.sendMessage(
                        "The punishment data for user ('" + player.getName() + "') is incomplete or missing!"
                ).queue();
                channel.sendMessage("https://tenor.com/view/unacceptable-knight-lock-and-load-cocks-gun-gif-17380126").queue();
                return;
            }

            channel.sendMessage(getFormattedEmbedBuilder(activeHolder, player, start).build()).queue();
        });
    }

    private EmbedBuilder getFormattedEmbedBuilder(final InformationHolder holder, final OfflinePlayer player, final long start) {
        final EmbedBuilder builder = new EmbedBuilder().setColor(Color.CYAN);

        builder.setTitle("Target '" + player.getName() + "' is banned:");
        builder.setDescription(getFormattedInformation(holder).toString());
        builder.setFooter("Executed in " + (System.currentTimeMillis() - start) + "ms");

        return builder;
    }

    private StringBuilder getFormattedInformation(final InformationHolder holder) {
        final StringBuilder builder = new StringBuilder();

        final Date time = new Date(holder.time);
        final Date until = new Date(holder.until);

        builder.append(" - Banned By: ").append(holder.punisher).append("\n");
        builder.append(" - Reason: ").append(holder.reason).append("\n");
        builder.append(" - Banned On: ").append(time).append("\n");
        builder.append(" - Banned Until: ").append(until).append("\n");
        builder.append(" - Banned On Server: ").append(holder.serverOrigin).append(", Server Scope: ").append(holder.serverScope).append("\n");
        builder.append(" - IP Ban: ").append(holder.isIP).append(", Silent: ").append(holder.isSilent).append("\n");

        return builder;
    }

}
