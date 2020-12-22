package com.github.frcsty.litebansdiscordjava.discord;

import com.github.frcsty.litebansdiscordjava.LitebansDiscordPlugin;
import com.github.frcsty.litebansdiscordjava.command.UserCheckBanCommand;
import com.github.frcsty.litebansdiscordjava.command.UserHistoryCommand;
import com.github.frcsty.litebansdiscordjava.command.UserIPHistoryCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Role;

import javax.security.auth.login.LoginException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class DiscordProvider {

    private final LitebansDiscordPlugin plugin;
    private final Logger logger;

    public DiscordProvider(final LitebansDiscordPlugin plugin) {
        this.logger = plugin.getLogger();
        this.plugin = plugin;

        startBot();
    }

    private void startBot() {
        JDA jda = null;

        try {
            jda = JDABuilder.createDefault(
                    plugin.getConfig().getString("settings.bot-token")
            ).build().awaitReady();
        } catch (final InterruptedException | LoginException ex) {
            this.logger.log(Level.SEVERE, "Failed to build and start bot, ensure token is correct!");
        }

        if (jda == null)
            throw new RuntimeException("Failed to start bot!");

        final Role requiredRole = jda.getRoleById(plugin.getConfig().getLong("settings.required-role"));

        jda.addEventListener(
                new UserHistoryCommand(),
                new UserIPHistoryCommand(requiredRole),
                new UserCheckBanCommand()
        );
    }

}
