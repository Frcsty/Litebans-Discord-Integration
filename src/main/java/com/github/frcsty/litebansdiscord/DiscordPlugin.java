package com.github.frcsty.litebansdiscord;

import com.github.frcsty.litebansdiscord.discord.Discord;
import org.bukkit.plugin.java.JavaPlugin;

public final class DiscordPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        new Discord(this);
    }

    @Override
    public void onDisable() {
        reloadConfig();
    }

}
