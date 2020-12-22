package com.github.frcsty.litebansdiscordjava.command.holder;

import java.sql.Timestamp;

public final class HistoryHolder {

    public final long entryID;
    public final Timestamp entryRegisteredDate;

    public final String entryUserName;
    public final String entryUserUUID;

    public final String entryUserIP;

    public HistoryHolder(final long id, final Timestamp date, final String name, final String uuid, final String ip) {
        this.entryID = id;
        this.entryRegisteredDate = date;
        this.entryUserName = name;
        this.entryUserUUID = uuid;
        this.entryUserIP = ip;
    }

}
