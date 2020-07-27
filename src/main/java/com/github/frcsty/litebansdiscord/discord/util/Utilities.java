package com.github.frcsty.litebansdiscord.discord.util;

import litebans.api.Database;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.OfflinePlayer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public final class Utilities {

    public static boolean isNotUser(final User user, final Message message) {
        return user.isBot() || message.isWebhookMessage();
    }

    public static boolean hasMissingPermission(final Member member, final String roleId) {
        if (member.hasPermission(Permission.MANAGE_SERVER)) return true;

        final Role requiredRole = member.getGuild().getRoleById(roleId);
        return requiredRole != null && member.getRoles().stream().anyMatch(role -> role.getPosition() >= requiredRole.getPosition());
    }

    public static List<InformationHolder> getUserBans(final OfflinePlayer player) {
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

    public static List<HistoryHolder> getUserIPHistory(final OfflinePlayer player) {
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
