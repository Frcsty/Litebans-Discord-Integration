package com.github.frcsty.litebansdiscord.discord;

import com.github.frcsty.litebansdiscord.DiscordPlugin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.configuration.file.FileConfiguration;

import javax.security.auth.login.LoginException;
import java.util.Collections;
import java.util.logging.Level;

public final class Discord {

    private final DiscordPlugin plugin;
    private final FileConfiguration config;
    private JDA jda;

    public Discord(final DiscordPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        setupDiscord();
    }

    private void setupDiscord() {
        if (jda != null) {
            return;
        }

        startBot();
    }

    private void startBot() {
        try {
            jda = JDABuilder.create(config.getString("settings.token"), Collections.emptyList())
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.EMOTE, CacheFlag.CLIENT_STATUS)
                    .setStatus(OnlineStatus.ONLINE)
                    .build().awaitReady();
        } catch (final LoginException | InterruptedException ex) {
            plugin.getLogger().log(Level.WARNING, "Discord bot was unable to start! Please verify the bot token is correct.");
            plugin.getPluginLoader().disablePlugin(plugin);
        }
    }

    public JDA getJda() { return this.jda; }
}
