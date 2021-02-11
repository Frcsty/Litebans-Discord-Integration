package com.github.frcsty.litebansdiscordjava.command;

import com.github.frcsty.litebansdiscordjava.command.holder.HistoryHolder;
import com.github.frcsty.litebansdiscordjava.command.util.InformationUtil;
import com.github.frcsty.litebansdiscordjava.util.Task;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class UserIPHistoryCommand extends ListenerAdapter {

    private final Role requiredRole;

    public UserIPHistoryCommand(final Role requiredRole) {
        this.requiredRole = requiredRole;
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        Task.async(() -> {
            final long start = System.currentTimeMillis();
            final TextChannel channel = event.getChannel();
            final Message message = event.getMessage();
            final String content = message.getContentRaw();

            if (!content.startsWith("-iphistory")) {
                return;
            }

            final Member member = event.getMember();
            if (member == null) return;
            if (member.getUser().isBot()) return;

            if (!hasRequiredRole(member)) {
                channel.sendMessage(
                        "You do not have permission to execute this command!"
                ).queue();
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

            final List<HistoryHolder> holders = InformationUtil.getUserIPHistory(userIdentifier);
            if (holders.isEmpty()) {
                channel.sendMessage(
                        "The specified user ('" + player.getName() + "') does not have any IP history!"
                ).queue();
                return;
            }

            channel.sendMessage(getFormattedEmbedBuilder(holders, player, start).build()).queue();
        });
    }

    private boolean hasRequiredRole(final Member user) {
        final Optional<Role> hasRole = user.getRoles().stream().filter(role -> role.getPosition() <= requiredRole.getPosition()).findAny();

        return hasRole.isPresent();
    }

    private EmbedBuilder getFormattedEmbedBuilder(final List<HistoryHolder> holders, final OfflinePlayer player, final long start) {
        final EmbedBuilder builder = new EmbedBuilder().setColor(Color.CYAN);
        final StringBuilder stringBuilder = new StringBuilder();

        builder.setTitle("Login history user User '" + player.getName() + "' (Limit: " + holders.size() + "):");
        holders.forEach(holder -> stringBuilder.append(getFormattedInformation(holder)));

        builder.setDescription(stringBuilder.toString());
        builder.setFooter("Executed in " + (System.currentTimeMillis() - start) + "ms");

        return builder;
    }

    private StringBuilder getFormattedInformation(final HistoryHolder holder) {
        final StringBuilder builder = new StringBuilder();

        builder.append(" - [").append(holder.entryRegisteredDate).append("] ").append(holder.entryUserName).append(": ").append(holder.entryUserIP).append("\n");
        return builder;
    }

}
