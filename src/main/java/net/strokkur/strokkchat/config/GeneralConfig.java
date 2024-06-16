package net.strokkur.strokkchat.config;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.strokkchat.StrokkChat;
import net.strokkur.strokkchat.util.AbstractConfigFile;
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

    public static void sendChatMessage(final Player p, final String rawMessage) {




    }


}
