package com.github.frcsty.litebansdiscord.discord;

import com.github.frcsty.litebansdiscord.DiscordPlugin;
import com.github.frcsty.litebansdiscord.discord.command.CheckBanCommand;
import com.github.frcsty.litebansdiscord.discord.command.HistoryCommand;
import com.github.frcsty.litebansdiscord.discord.command.IpHistoryCommand;
import me.mattstudios.mfjda.base.CommandManager;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import org.bukkit.configuration.file.FileConfiguration;

import javax.security.auth.login.LoginException;
import java.util.Arrays;
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

        if (startBot()) {
            final CommandManager commandManager = new CommandManager(jda);

            commandManager.register(Arrays.asList(
                    new CheckBanCommand(plugin),
                    new HistoryCommand(plugin),
                    new IpHistoryCommand(plugin)
            ));
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
