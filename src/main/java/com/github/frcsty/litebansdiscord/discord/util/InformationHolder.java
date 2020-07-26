package com.github.frcsty.litebansdiscord.discord.util;

import java.sql.Timestamp;

public final class InformationHolder {

    private final String reason;
    private final String punisher;
    private final long time;
    private final long until;
    private final long banId;
    private final boolean active;
    private final String ip;
    private final Timestamp removed;
    private final String serverScope;
    private final String serverOrigin;
    private final boolean silent;
    private final boolean ipBan;

    public InformationHolder(final String reason, final String punisher,
                             final long time, final long until, final long banId,
                             final boolean active, final String ip, final Timestamp removed,
                             final String serverScope, final String serverOrigin, final boolean silent,
                             final boolean ipBan) {
        this.reason = reason;
        this.punisher = punisher;
        this.time = time;
        this.until = until;
        this.banId = banId;
        this.active = active;
        this.ip = ip;
        this.removed = removed;
        this.serverScope = serverScope;
        this.serverOrigin = serverOrigin;
        this.silent = silent;
        this.ipBan = ipBan;
    }

    public String getReason() {
        return this.reason;
    }

    public String getPunisher() {
        return this.punisher;
    }

    public long getTime() {
        return this.time;
    }

    public long getUntil() {
        return this.until;
    }

    public long getBanId() {
        return this.banId;
    }

    public boolean isActive() {
        return this.active;
    }

    public String getIp() {
        return this.ip;
    }

    public Timestamp getRemovedDate() {
        return this.removed;
    }

    public String getServerScope() {
        return this.serverScope;
    }

    public String getServerOrigin() {
        return this.serverOrigin;
    }

    public boolean isSilent() {
        return this.silent;
    }

    public boolean isIpBan() {
        return this.ipBan;
    }

}
