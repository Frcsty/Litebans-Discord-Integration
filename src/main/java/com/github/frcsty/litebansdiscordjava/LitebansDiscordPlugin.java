package com.github.frcsty.litebansdiscordjava;

import com.github.frcsty.litebansdiscordjava.discord.DiscordProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class LitebansDiscordPlugin extends JavaPlugin {

    private final DiscordProvider discordProvider = new DiscordProvider(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        reloadConfig();
    }

}
