package com.github.frcsty.litebansdiscord.discord.util

import litebans.api.Database
import net.dv8tion.jda.api.Permission
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.entities.Role
import net.dv8tion.jda.api.entities.User
import org.bukkit.OfflinePlayer
import java.sql.SQLException
import java.util.*

object Utilities {
    @JvmStatic
    fun isNotUser(user: User, message: Message): Boolean {
        return user.isBot || message.isWebhookMessage
    }

    @JvmStatic
    fun hasMissingPermission(member: Member, roleId: String?): Boolean {
        if (member.hasPermission(Permission.MANAGE_SERVER)) return true
        val requiredRole = member.guild.getRoleById(roleId!!)
        return requiredRole != null && member.roles.stream().anyMatch { role: Role -> role.position >= requiredRole.position }
    }

    @JvmStatic
    fun getUserBans(player: OfflinePlayer): List<InformationHolder> {
        val holders: MutableList<InformationHolder> = ArrayList()
        val uuid = player.uniqueId.toString()
        val query = "SELECT * FROM {bans} WHERE uuid=?"
        try {
            Database.get().prepareStatement(query).use { statement ->
                statement.setString(1, uuid)
                statement.executeQuery().use { result ->
                    while (result.next()) {
                        val id = result.getLong("id")
                        val ip = result.getString("ip")
                        val removedByDate = result.getTimestamp("removed_by_date")
                        val reason = result.getString("reason")
                        val bannedByName = result.getString("banned_by_name")
                        val time = result.getLong("time")
                        val until = result.getLong("until")
                        val active = result.getBoolean("active")
                        val serverScope = result.getString("server_scope")
                        val serverOrigin = result.getString("server_origin")
                        val silent = result.getBoolean("silent")
                        val ipBan = result.getBoolean("ipban")
                        holders.add(InformationHolder(reason, bannedByName, time, until, id, active, ip, removedByDate, serverScope, serverOrigin, silent, ipBan))
                    }
                }
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }
        return holders
    }

    @JvmStatic
    fun getUserIPHistory(player: OfflinePlayer): List<HistoryHolder> {
        val holders: MutableList<HistoryHolder> = ArrayList()
        val uuid = player.uniqueId.toString()
        val query = "SELECT * from {history} WHERE uuid=?"
        try {
            Database.get().prepareStatement(query).use { statement ->
                statement.setString(1, uuid)
                statement.executeQuery().use { result ->
                    while (result.next()) {
                        val id = result.getLong("id")
                        val date = result.getTimestamp("date")
                        val name = result.getString("name")
                        val uuidString = result.getString("uuid")
                        val ip = result.getString("ip")
                        holders.add(HistoryHolder(id, date, name, uuidString, ip))
                    }
                }
            }
        } catch (ex: SQLException) {
            ex.printStackTrace()
        }
        return holders
    }
}