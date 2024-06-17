package net.strokkur.strokkchat;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.luckperms.api.LuckPerms;
import net.strokkur.strokkchat.command.CmdStrokkChat;
import net.strokkur.strokkchat.config.CPConfig;
import net.strokkur.strokkchat.config.GeneralConfig;
import net.strokkur.strokkchat.listener.ChatListener;
import net.strokkur.strokkchat.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class StrokkChat extends JavaPlugin {

    public static StrokkChat plugin;

    public static LuckPerms luckPerms;
    public static boolean placeholderAPI;

    private static final ChatListener chatListener = new ChatListener();

    @Override
    public void onLoad() {
        final CommandAPIBukkitConfig cfg = new CommandAPIBukkitConfig(this);
        cfg.setNamespace("strokkchat");
        cfg.shouldHookPaperReload(true);
        cfg.skipReloadDatapacks(true);
        CommandAPI.onLoad(cfg);
    }

    @Override
    public void onEnable() {
        // Get LuckPerms API reference if available
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();

        }

        // Get whether PlaceholderAPI is installed
        placeholderAPI = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;

        // Enable CommandAPI
        CommandAPI.onEnable();

        // Load config files
        new GeneralConfig("Config.yml");
        new CPConfig("CustomPlaceholders.yml");

        // Register strokkchat command
        CmdStrokkChat.getCommand().register();

        // Register chat event for the first time
        reloadChatEvent(GeneralConfig.priority());

        // Send successfully enabled message :)
        log("Successfully enabled StrokkChat!");
    }

    @Override
    public void onDisable() {
        log("Successfully disabled StrokkChat! Bye bye...");
    }

    private final PluginManager pluginManager = Bukkit.getPluginManager();

    public void reloadChatEvent(EventPriority priority) {
        HandlerList.unregisterAll(chatListener);
        chatEvent(priority);
    }

    private void chatEvent(EventPriority priority) {
        pluginManager.registerEvent(AsyncChatEvent.class, chatListener, priority, (ll, event) ->
                ((ChatListener) ll).onMessage((AsyncChatEvent) event), this);
    }

    public static void log(String message, TagResolver... tagResolvers) {
        Bukkit.getConsoleSender().sendMessage(TextUtil.parse("<dark_gray>[<gold>Strokk</gold><yellow>Chat</yellow>] </dark_gray><message>",
                combineTagResolvers(Placeholder.parsed("message", message), tagResolvers)
        ));
    }

    public static void logWarning(String message, TagResolver... tagResolvers) {
        Bukkit.getConsoleSender()
                .sendMessage(TextUtil.parse("<dark_gray>[<gold>Strokk</gold><yellow>Chat</yellow>] [<yellow>WARNING</yellow>] </dark_gray><message>",
                        combineTagResolvers(Placeholder.parsed("message", message), tagResolvers)
                ));
    }

    public static void logError(String message, TagResolver... tagResolvers) {
        Bukkit.getConsoleSender().sendMessage(TextUtil.parse("<dark_gray>[<gold>Strokk</gold><yellow>Chat</yellow>] [<red>ERROR</red>] </dark_gray><message>",
                combineTagResolvers(Placeholder.parsed("message", message), tagResolvers)
        ));
    }

    private static TagResolver @NotNull [] combineTagResolvers(TagResolver single, TagResolver... multiple) {
        List<TagResolver> resolvers = new ArrayList<>(List.of(multiple));
        resolvers.add(single);
        return resolvers.toArray(new TagResolver[0]);
    }


}
