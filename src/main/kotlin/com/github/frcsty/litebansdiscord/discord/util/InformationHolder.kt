package com.github.frcsty.litebansdiscord.discord.util

import java.sql.Timestamp

class InformationHolder(val reason: String, val punisher: String,
                        val time: Long, val until: Long, val banId: Long,
                        val isActive: Boolean, val ip: String, val removedDate: Timestamp,
                        val serverScope: String, val serverOrigin: String, val isSilent: Boolean,
                        val isIpBan: Boolean
)