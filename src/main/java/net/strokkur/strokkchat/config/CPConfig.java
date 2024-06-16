package net.strokkur.strokkchat.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.strokkur.strokkchat.StrokkChat;
import net.strokkur.strokkchat.util.AbstractConfigFile;
import net.strokkur.strokkchat.util.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CPConfig extends AbstractConfigFile {

    private static CPConfig instance;
    private static YamlConfiguration cfg;

    private static final HashMap<String, TagResolver> customPlaceholders = new HashMap<>();
    private static final HashMap<String, String> customPlayerPlaceholders = new HashMap<>();

    public CPConfig(String path) {
        super(path);
        instance = this;
    }

    @Override
    public void postReload() {
        cfg = super.cfg;
        reloadCustomPlaceholders();
    }

    public static void reloadCustomPlaceholders() {
        customPlaceholders.clear();

        for (final String subKey : cfg.getKeys(false)) {

            final String format = cfg.getString(subKey + ".format");

            if (format == null) {
                StrokkChat.logWarning(subKey +
                                      ".format @ CustomPlaceholder.yml is not set. Please set it to <white>PRESET_ITEM_HELD</white> or <white>MINI_MESSAGE</white>!");
                continue;
            }

            if (format.equalsIgnoreCase("SQUARE_BRACKETS")) {
                addPlayerPlaceholder(subKey);
            }
            else if (format.equalsIgnoreCase("MINI_MESSAGE")) {
                addMmPlaceholder(subKey);
            }
            else {
                StrokkChat.logWarning(subKey +
                                      ".format @ CustomPlaceholder.yml is not set to a valid value <dark_gray>(<white><value></white>)</dark_gray>. Please set it to <white>PRESET_ITEM_HELD</white> or <white>MINI_MESSAGE</white>!",
                        Placeholder.unparsed("value", format));
            }
        }

    }

    private static void addMmPlaceholder(String name) {



    }

    private static void addPlayerPlaceholder(String name) {

    }


    public static @NotNull Component replaceChatMessageFormatPlaceholders(String raw, @NotNull Player p, Component parsedContent) {
        final List<TagResolver> resolvers = new ArrayList<>() {
            {
                add(Placeholder.unparsed("player", p.getName()));
                add(TextUtil.selfClosingTag("message", parsedContent));
                if (StrokkChat.luckPerms != null) {
                    add(TextUtil.luckpermsTag(p));
                }
                if (StrokkChat.placeholderAPI) {
                    add(TextUtil.papiTag(p));
                }
            }
        };

        return TextUtil.parse(raw, resolvers.toArray(new TagResolver[0]));
    }


}
