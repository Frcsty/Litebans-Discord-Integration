package com.github.frcsty.litebansdiscordjava.command.util;

import com.github.frcsty.litebansdiscordjava.command.holder.HistoryHolder;
import com.github.frcsty.litebansdiscordjava.command.holder.InformationHolder;
import litebans.api.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class InformationUtil {

    private static final Database DATABASE = Database.get();

    public static List<InformationHolder> getUserBans(final UUID userID) {
        final List<InformationHolder> holders = new ArrayList<>();
        final String query = "SELECT * FROM {bans} WHERE uuid=?;";

        try (final PreparedStatement statement = DATABASE.prepareStatement(query)) {
            statement.setString(1, userID.toString());

            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final String reason = result.getString("reason");
                final String bannedByName = result.getString("banned_by_name");
                final long time = result.getLong("time");
                final long until = result.getLong("until");
                final boolean active = result.getBoolean("active");
                final String serverScope = result.getString("server_scope");
                final String serverOrigin = result.getString("server_origin");
                final boolean silent = result.getBoolean("silent");
                final boolean ipBan = result.getBoolean("ipban");

                holders.add(
                        new InformationHolder(
                                reason == null ? "Unknown" : reason,
                                bannedByName == null ? "Unknown" : bannedByName,
                                time,
                                until,
                                active,
                                serverScope == null ? "Unknown" : serverScope,
                                serverOrigin == null ? "Unknown" : serverOrigin,
                                silent,
                                ipBan
                        )
                );
            }

            result.close();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return holders;
    }

    public static List<HistoryHolder> getUserIPHistory(final UUID userID) {
        final List<HistoryHolder> holders = new ArrayList<>();
        final String query = "SELECT * FROM {history} WHERE uuid=?;";

        try (final PreparedStatement statement = DATABASE.prepareStatement(query)) {
            statement.setString(1, userID.toString());

            final ResultSet result = statement.executeQuery();
            while (result.next()) {
                final long id = result.getLong("id");
                final Timestamp date = result.getTimestamp("date");
                final String name = result.getString("name");
                final String uuidString = result.getString("uuid");

                String ip = result.getString("ip");
                if (ip.equalsIgnoreCase("#")) ip = "Unknown";

                holders.add(
                        new HistoryHolder(
                                id,
                                date,
                                name == null ? "Unknown" : name,
                                uuidString == null ? "Unknown" : uuidString,
                                ip
                        )
                );
            }

            result.close();
        } catch (final SQLException ex) {
            ex.printStackTrace();
        }

        return holders;
    }

}
