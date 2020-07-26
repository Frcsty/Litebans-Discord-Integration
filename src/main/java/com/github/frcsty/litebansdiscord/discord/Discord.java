package com.github.frcsty.litebansdiscord.discord;

import com.github.frcsty.litebansdiscord.LiteBansDiscord;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import org.bukkit.configuration.file.FileConfiguration;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;

public final class Discord {

    private final LiteBansDiscord plugin;
    private final FileConfiguration config;
    private JDA jda;

    public Discord(final LiteBansDiscord plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();

        setupDiscord();
    }

    private void setupDiscord() {
        if (jda != null) {
            return;
        }

        if (startBot()) {
            jda.addEventListener(new DiscordListener(plugin));
        }
    }

    private boolean startBot() {
        try {
            final JDABuilder builder = new JDABuilder(AccountType.BOT);
            builder.setToken(config.getString("settings.token"));
            builder.setStatus(OnlineStatus.ONLINE);
            jda = builder.build().awaitReady();
        } catch (final LoginException | InterruptedException ex) {
            plugin.getLogger().log(Level.WARNING, "Discord bot was unable to start! Please verify the bot token is correct.");
            return false;
        }
        return true;
    }

}
