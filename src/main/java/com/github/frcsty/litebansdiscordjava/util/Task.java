package com.github.frcsty.litebansdiscordjava.util;

import com.github.frcsty.litebansdiscordjava.LitebansDiscordPlugin;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public final class Task {

    private static final LitebansDiscordPlugin PLUGIN = JavaPlugin.getPlugin(LitebansDiscordPlugin.class);
    private static final BukkitScheduler SCHEDULER = Bukkit.getScheduler();

    public static void async(final Runnable runnable) {
        SCHEDULER.scheduleAsyncDelayedTask(PLUGIN, runnable);
    }

}
