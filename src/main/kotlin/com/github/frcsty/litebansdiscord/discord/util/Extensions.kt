package com.github.frcsty.litebansdiscord.discord.util

import litebans.api.Database
import net.dv8tion.jda.core.Permission
import net.dv8tion.jda.core.entities.Member
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.Role
import net.dv8tion.jda.core.entities.User
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import java.sql.SQLException
import java.util.*

fun User.isNotMember(message: Message): Boolean {
    return this.isBot || message.isWebhookMessage
}

fun Member?.hasMissingPermission(roleId: String?): Boolean {
    if (this == null) return true
    if (roleId == null) return true
    if (this.hasPermission(Permission.MANAGE_SERVER)) return false
    val requiredRole = this.guild.getRoleById(roleId)

    return requiredRole == null || this.roles.stream().anyMatch { role: Role -> role.position <= requiredRole.position }
}

fun getOfflineUser(name: String): OfflinePlayer? {
    var resultPlayer: OfflinePlayer? = null
    for (player in Bukkit.getOfflinePlayers()) {
        if (player.name.equals(name, ignoreCase = true)) {
            resultPlayer = player
            break
        }
    }

    return resultPlayer
}

fun OfflinePlayer.getUserBans(): List<InformationHolder> {
    val holders: MutableList<InformationHolder> = ArrayList()
    val uuid = this.uniqueId.toString()
    val query = "SELECT * FROM {bans} WHERE uuid=?"
    try {
        Database.get().prepareStatement(query).use { statement ->
            statement.setString(1, uuid)
            statement.executeQuery().use { result ->
                while (result.next()) {
                    val reason = result.getString("reason")?: "Unknown"
                    val bannedByName = result.getString("banned_by_name")?: "Unknown"
                    val time = result.getLong("time")
                    val until = result.getLong("until")
                    val active = result.getBoolean("active")
                    val serverScope = result.getString("server_scope")?: "Unknown"
                    val serverOrigin = result.getString("server_origin")?: "Unknown"
                    val silent = result.getBoolean("silent")
                    val ipBan = result.getBoolean("ipban")
                    holders.add(InformationHolder(reason, bannedByName, time, until, active, serverScope, serverOrigin, silent, ipBan))
                }
            }
        }
    } catch (ex: SQLException) {
        ex.printStackTrace()
    }
    return holders
}

fun OfflinePlayer.getUserIPHistory(): List<HistoryHolder> {
    val holders: MutableList<HistoryHolder> = ArrayList()
    val uuid = this.uniqueId.toString()
    val query = "SELECT * from {history} WHERE uuid=?"
    try {
        Database.get().prepareStatement(query).use { statement ->
            statement.setString(1, uuid)
            statement.executeQuery().use { result ->
                while (result.next()) {
                    val id = result.getLong("id")
                    val date = result.getTimestamp("date")
                    val name = result.getString("name")?: "Unknown"
                    val uuidString = result.getString("uuid")?: "Unknown"
                    var ip = result.getString("ip")
                    if (ip.equals("#", ignoreCase = false)) ip = "Unknown"
                    holders.add(HistoryHolder(id, date, name, uuidString, ip))
                }
            }
        }
    } catch (ex: SQLException) {
        ex.printStackTrace()
    }
    return holders
}