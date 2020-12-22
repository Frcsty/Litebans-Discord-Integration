package com.github.frcsty.litebansdiscordjava.command.holder;

public final class InformationHolder {

    public final String reason;
    public final String punisher;

    public final long time;
    public final long until;
    public final boolean isActive;

    public final String serverScope;
    public final String serverOrigin;

    public final boolean isSilent;
    public final boolean isIP;

    public InformationHolder(final String reason, final String punisher, final long time, final long until, final boolean isActive,
                             final String scope, final String origin, final boolean silent, final boolean ip) {
        this.reason = reason;
        this.punisher = punisher;
        this.time = time;
        this.until = until;
        this.isActive = isActive;
        this.serverScope = scope;
        this.serverOrigin = origin;
        this.isSilent = silent;
        this.isIP = ip;
    }

}
