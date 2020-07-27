package com.github.frcsty.litebansdiscord.discord.command;

import com.github.frcsty.litebansdiscord.DiscordPlugin;
import com.github.frcsty.litebansdiscord.discord.util.HistoryHolder;
import com.github.frcsty.litebansdiscord.discord.util.Utilities;
import me.mattstudios.mfjda.annotations.Command;
import me.mattstudios.mfjda.annotations.Default;
import me.mattstudios.mfjda.annotations.Prefix;
import me.mattstudios.mfjda.base.CommandBase;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.util.List;

@Prefix("-")
@Command("iphistory")
public final class IpHistoryCommand extends CommandBase {

    private final DiscordPlugin plugin;

    public IpHistoryCommand(final DiscordPlugin plugin) {
        this.plugin = plugin;
    }

    @Default
    public void ipHistoryCommand() {
        final Message message = getMessage();
        final MessageChannel channel = message.getChannel();
        final User user = message.getAuthor();

        if (Utilities.isNotUser(user, message)) return;
        if (message.getMember() == null) return;
        if (Utilities.hasMissingPermission(message.getMember(), plugin.getConfig().getString("settings.requiredRoleId"))) {
            channel.sendMessage("You do not have the required permission for this!").queue();
            return;
        }

        final String[] args = message.getContentRaw().split(" ");
        if (args.length == 1) {
            channel.sendMessage("You did not specify a user to check!").queue();
            return;
        }

        final OfflinePlayer player = Bukkit.getOfflinePlayer(args[1]);
        final List<HistoryHolder> holders = Utilities.getUserIPHistory(player);
        if (holders.size() == 0) {
            channel.sendMessage("User " + player.getName() + " has no logic history.").queue();
            return;
        }

        channel.sendMessage(getFormattedEmbed(player, holders).build()).queue();
        holders.clear();
    }

    private EmbedBuilder getFormattedEmbed(final OfflinePlayer player, final List<HistoryHolder> holders) {
        final EmbedBuilder embedBuilder = new EmbedBuilder().setColor(Color.CYAN);
        final StringBuilder builder = new StringBuilder();
        embedBuilder.setTitle("Login history for " + player.getName() + " (Limit: " + holders.size() + "):");

        holders.forEach(holder -> builder.append(getFormattedIPHistoryInformation(holder)));
        embedBuilder.setDescription(builder.toString());
        return embedBuilder;
    }

    private StringBuilder getFormattedIPHistoryInformation(final HistoryHolder holder) {
        final StringBuilder builder = new StringBuilder();

        builder.append(" - [" + holder.getDate() + "] " + holder.getName() + ": " + holder.getIp() + "\n");

        return builder;
    }
}
