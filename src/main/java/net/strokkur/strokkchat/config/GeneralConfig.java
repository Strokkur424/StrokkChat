package net.strokkur.strokkchat.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.strokkchat.StrokkChat;
import net.strokkur.strokkchat.util.AbstractConfigFile;
import net.strokkur.strokkchat.util.TextUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GeneralConfig extends AbstractConfigFile {

    private static GeneralConfig instance;
    private static YamlConfiguration cfg;

    private static HashMap<String, String> chatFormats = new HashMap<>();

    public GeneralConfig(String path) {
        super(path);
        instance = this;
    }

    @Override
    public void postReload() {
        cfg = super.cfg;
        reloadChatFormats();
    }

    private static @NotNull String get(final String key) {
        final String out = cfg.getString(key);
        if (out == null) {
            return "";
        }
        return out;
    }

    private static @NotNull List<String> getSubKeys(final @NotNull String key) {
        final List<String> out = new ArrayList<>();

        int depth = key.split("\\.").length;
        for (final String subKey : cfg.getKeys(true)) {

            // Check whether it starts with the key and has a deeper depth by one (direct child entry)
            if (subKey.startsWith(key) && subKey.split("\\.").length == depth + 1) {
                out.add(subKey);
            }
        }

        return out;
    }

    public static void reloadCfg() {
        instance.reload();
        StrokkChat.plugin.reloadChatEvent(priority());
    }

    private static void sendGenericMessage(@NotNull CommandSender sender, final String key) {
        sender.sendMessage(TextUtil.parse(get(key)));
    }

    private static void reloadChatFormats() {
        chatFormats.clear();

        for (final String subKey : cfg.getKeys(true)) {
            final String[] split = subKey.split("\\.");
            if (split.length != 2 || !split[0].equals("chat-formats")) {
                continue;
            }

            chatFormats.put(split[1], cfg.getString(subKey));
        }
    }

    public static EventPriority priority() {
        final String raw = cfg.getString("event-priority");

        if (raw == null) {
            StrokkChat.logWarning("event-priority @ Config.yml is not set. Defaulting to HIGH");
            return EventPriority.HIGH;
        }

        try {
            return EventPriority.valueOf(raw.toUpperCase());
        }
        catch (IllegalArgumentException ex) {
            StrokkChat.logWarning("event-priority @ Config.yml returns an invalid value: <input>. Defaulting to HIGH", Placeholder.unparsed("input", raw));
            return EventPriority.HIGH;
        }
    }

    public static int defaultDepth() {
        return cfg.getInt("placeholder-depth-limit");
    }

    public static String getCorrectFormat(final @NotNull Player player) {
        if (!player.hasPermission("strokkchat.chatformat")) {
            return cfg.getString("chat-format.default");
        }

        for (final String subKey : getSubKeys("chat-format")) {

            final String[] split = subKey.split("\\.");
            if (player.hasPermission("strokkchat.chatformat." + split[split.length - 1])) {
                return cfg.getString(subKey);
            }
        }

        return cfg.getString("chat-format.default");
    }

    public static void messageHelp(CommandSender sender) {
        for (String msg : cfg.getStringList("messages.help")) {
            sender.sendMessage(TextUtil.parse(msg));
        }
    }

    public static void messageReloadSingle(@NotNull CommandSender sender, String config) {
        sender.sendMessage(TextUtil.parse(get("messages.reload.single"),
                Placeholder.unparsed("config", config)
        ));
    }

    public static void messageReloadAll(@NotNull CommandSender sender) {
        sendGenericMessage(sender, "messages.reload.all");
    }

    public static void messagesDebugOn(@NotNull CommandSender sender) {
        sendGenericMessage(sender, "messages.debug.on");
    }

    public static void messagesDebugOff(@NotNull CommandSender sender) {
        sendGenericMessage(sender, "messages.debug.off");
    }

    public static void messagesParse(@NotNull CommandSender sender, String raw, Component parsed) {
        sender.sendMessage(TextUtil.parse(get("messages.parse"),
                Placeholder.unparsed("raw", raw),
                TextUtil.selfClosingTag("parsed", parsed)
        ));
    }

}
