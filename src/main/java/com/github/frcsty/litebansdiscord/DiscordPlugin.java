package com.github.frcsty.litebansdiscord;

import com.github.frcsty.litebansdiscord.discord.Discord;
import com.github.frcsty.litebansdiscord.discord.command.CheckBanCommand;
import com.github.frcsty.litebansdiscord.discord.command.HistoryCommand;
import com.github.frcsty.litebansdiscord.discord.command.IpHistoryCommand;
import me.mattstudios.mfjda.base.CommandManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public final class DiscordPlugin extends JavaPlugin {

    private final Discord discord = new Discord(this);

    @Override
    public void onEnable() {
        saveDefaultConfig();

        final CommandManager commandManager = new CommandManager(discord.getJda());

        commandManager.register(Arrays.asList(
                new CheckBanCommand(this),
                new HistoryCommand(this),
                new IpHistoryCommand(this)
        ));
    }

    @Override
    public void onDisable() {
        reloadConfig();
    }

}
