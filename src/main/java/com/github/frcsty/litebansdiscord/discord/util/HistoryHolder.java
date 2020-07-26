package com.github.frcsty.litebansdiscord.discord.util;

import java.sql.Timestamp;

public final class HistoryHolder {

    private final long id;
    private final Timestamp date;
    private final String name;
    private final String uuid;
    private final String ip;

    public HistoryHolder(final long id, final Timestamp date, final String name, final String uuid, final String ip) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.uuid = uuid;
        this.ip = ip;
    }

    public long getId() { return this.id; }
    public Timestamp getDate() { return this.date; }
    public String getName() { return this.name; }
    public String getUuid() { return this.uuid; }
    public String getIp() { return this.ip; }
}
