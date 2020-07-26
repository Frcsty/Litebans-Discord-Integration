package com.github.frcsty.litebansdiscord.discord;

import com.github.frcsty.litebansdiscord.LiteBansDiscord;
import com.github.frcsty.litebansdiscord.discord.util.HistoryHolder;
import com.github.frcsty.litebansdiscord.discord.util.InformationHolder;
import litebans.api.Database;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class DiscordListener extends ListenerAdapter {

    private static final String PREFIX = "-";
    private final LiteBansDiscord plugin;

    DiscordListener(final LiteBansDiscord plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onGuildMessageReceived(final GuildMessageReceivedEvent event) {
        if (event.getMessage().isWebhookMessage() || event.getAuthor().isBot()) {
            return;
        }

        final String content = event.getMessage().getContentRaw();
        if (!content.startsWith(PREFIX)) {
            return;
        }

        final TextChannel channel = event.getChannel();
        if (!hasPermission(event.getMember())) {
            channel.sendMessage("You do not have permission for this!").queue();
            return;
        }

        if (content.startsWith(PREFIX + "checkban")) {
            checkBan(content, channel);
        } else if (content.startsWith(PREFIX + "history")) {
            history(content, channel);
        } else if (content.startsWith(PREFIX + "iphistory")) {
            ipHistory(content, channel);
        }
    }

    private boolean hasPermission(final Member member) {
        if (member.hasPermission(Permission.MANAGE_SERVER)) return true;

        final Role requiredRole = member.getGuild().getRoleById(plugin.getConfig().getString("settings.requiredRoleId"));
        return requiredRole != null && member.getRoles().stream().anyMatch(role -> role.getPosition() >= requiredRole.getPosition());
    }

    private void ipHistory(final String content, final TextChannel channel) {
        final String[] args = content.split(" ");
        if (args.length == 1) {
            channel.sendMessage(":x: Please specify a user to check!").queue();
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        final List<HistoryHolder> holders = getUserIPHistory(player);
        if (holders.size() == 0) {
            channel.sendMessage("User " + player.getName() + " has no login history!").queue();
            return;
        }

        embedBuilder.setTitle("Login history for " + player.getName() + " (Limit: 10):");
        final StringBuilder builder = new StringBuilder();
        holders.forEach(login -> builder.append(getFormattedIPHistoryInformation(login)));
        embedBuilder.setDescription(builder.toString());
        channel.sendMessage(embedBuilder.build()).queue();
    }

    private void checkBan(final String content, final TextChannel channel) {
        final String[] args = content.split(" ");
        if (args.length == 1) {
            channel.sendMessage(":x: Please specify a user to check!").queue();
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        final boolean isBanned = Database.get().isPlayerBanned(player.getUniqueId(), null);
        if (isBanned) {
            final List<InformationHolder> holders = getUserBans(player);
            InformationHolder active = null;
            for (final InformationHolder holder : holders) {
                if (holder.isActive()) active = holder;
            }

            if (active == null) {
                channel.sendMessage("Ban data for user " + player.getName() + " in incomplete!").queue();
                return;
            }

            final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
            embedBuilder.setTitle("Target " + player.getName() + " is banned:");

            System.out.println(active.getIp());
            embedBuilder.setDescription(getFormatterCheckBanInformation(active));
            channel.sendMessage(embedBuilder.build()).queue();
        } else {
            channel.sendMessage("User " + player.getName() + " is not banned!").queue();
        }
    }

    private void history(final String content, final TextChannel channel) {
        final String[] args = content.split(" ");
        if (args.length == 1) {
            channel.sendMessage(":x: Please specify a user to check!").queue();
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        final List<InformationHolder> holders = getUserBans(player);
        final int amount = args.length < 3 ? 1 : Integer.valueOf(args[2]);

        if (holders.size() == 0) {
            channel.sendMessage("User " + player.getName() + " has no history.").queue();
            return;
        }

        final StringBuilder builder = new StringBuilder();
        final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        embedBuilder.setTitle("History for " + player.getName() + " (Limit: " + holders.size() + "):");

        for (int i = 0; i < amount; i++) {
            final InformationHolder holder = holders.get(i);

            if (holder == null) {
                continue;
            }

            builder.append(getFormattedHistoryInformation(holder));
        }

        embedBuilder.setDescription(builder.toString());
        channel.sendMessage(embedBuilder.build()).queue();
        holders.clear();
    }

    private StringBuilder getFormatterCheckBanInformation(final InformationHolder holder) {
        final StringBuilder builder = new StringBuilder();

        builder.append(" - Banned By: " + holder.getPunisher() + "\n");
        builder.append(" - Reason: " + holder.getReason() + "\n");
        builder.append(" - Banned On: " + new Date(holder.getTime()) + "\n");
        builder.append(" - Banned Until: " + new Date(holder.getUntil()) + "\n");
        builder.append(" - Banned On Server: " + holder.getServerOrigin() + ", Server Scope: " + holder.getServerScope() + "\n");
        builder.append(" - IP Ban: " + holder.isIpBan() + ", Silent: " + holder.isSilent() + "\n");

        return builder;
    }

    private StringBuilder getFormattedHistoryInformation(final InformationHolder holder) {
        final StringBuilder builder = new StringBuilder();

        builder.append("```\n");
        builder.append("Time: " + new Date(holder.getTime()) + "\n");
        builder.append(" - Reason: " + holder.getReason() + "\n");
        builder.append(" - Banned By: " + holder.getPunisher() + "\n");

        if (holder.isActive()) {
            builder.append(" - Expires: " + new Date(holder.getUntil()) + "\n");
        }

        builder.append(" - Active: " + StringUtils.capitalize(String.valueOf(holder.isActive())) + "\n");
        builder.append("```");

        return builder;
    }

    private StringBuilder getFormattedIPHistoryInformation(final HistoryHolder holder) {
        final StringBuilder builder = new StringBuilder();

        builder.append(" - [" + holder.getDate() + "] " + holder.getName() + ": " + holder.getIp());

        return builder;
    }

    private List<InformationHolder> getUserBans(final OfflinePlayer player) {
        final List<InformationHolder> holders = new ArrayList<>();
        final String uuid = player.getUniqueId().toString();
        final String query = "SELECT * FROM {bans} WHERE uuid=?";

        try (final PreparedStatement statement = Database.get().prepareStatement(query)) {
            statement.setString(1, uuid);

            try (final ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    final long id = result.getLong("id");
                    final String ip = result.getString("ip");
                    final Timestamp removedByDate = result.getTimestamp("removed_by_date");
                    final String reason = result.getString("reason");
                    final String bannedByName = result.getString("banned_by_name");
                    final long time = result.getLong("time");
                    final long until = result.getLong("until");
                    final boolean active = result.getBoolean("active");
                    final String serverScope = result.getString("server_scope");
                    final String serverOrigin = result.getString("server_origin");
                    final boolean silent = result.getBoolean("silent");
                    final boolean ipBan = result.getBoolean("ipban");

                    holders.add(new InformationHolder(reason, bannedByName, time, until, id, active, ip, removedByDate, serverScope, serverOrigin, silent, ipBan));
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return holders;
    }

    private List<HistoryHolder> getUserIPHistory(final OfflinePlayer player) {
        final List<HistoryHolder> holders = new ArrayList<>();
        final String uuid = player.getUniqueId().toString();
        final String query = "SELECT * from {history} WHERE uuid=?";

        try (final PreparedStatement statement = Database.get().prepareStatement(query)) {
            statement.setString(1, uuid);
            try (final ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    final long id = result.getLong("id");
                    final Timestamp date = result.getTimestamp("date");
                    final String name = result.getString("name");
                    final String uuidString = result.getString("uuid");
                    final String ip = result.getString("ip");

                    holders.add(new HistoryHolder(id, date, name, uuidString, ip));
                }
            }
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return holders;
    }
}
