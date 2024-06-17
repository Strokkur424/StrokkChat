package net.strokkur.strokkchat.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.strokkur.strokkchat.StrokkChat;
import net.strokkur.strokkchat.util.AbstractConfigFile;
import net.strokkur.strokkchat.util.TextUtil;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CPConfig extends AbstractConfigFile {

    private static CPConfig instance;
    private static YamlConfiguration cfg;

    private static final HashMap<String, String> customPlaceholders = new HashMap<>();
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

    public static void reloadCfg() {
        instance.reload();
    }

    public static void reloadCustomPlaceholders() {
        customPlaceholders.clear();
        customPlayerPlaceholders.clear();

        for (final String subKey : cfg.getKeys(false)) {

            final String format = cfg.getString(subKey + ".format");

            if (format == null) {
                StrokkChat.logWarning(subKey +
                                      ".format @ CustomPlaceholder.yml is not set. Please set it to <white>SQUARE_BRACKETS</white> or <white>MINI_MESSAGE</white>!");
                continue;
            }

            // Get the required information needed to parse the placeholders in the future
            List<String> names = new ArrayList<>(List.of(subKey));
            names.addAll(cfg.getStringList(subKey + ".aliases"));

            String content = cfg.getString(subKey + ".replace-with");
            if (format.equalsIgnoreCase("SQUARE_BRACKETS")) {
                for (String name : names) {
                    customPlayerPlaceholders.put(name, content);
                }
            }
            else if (format.equalsIgnoreCase("MINI_MESSAGE")) {
                for (String name : names) {
                    customPlaceholders.put(name, content);
                }
            }

            else {
                StrokkChat.logWarning(subKey +
                                      ".format @ CustomPlaceholder.yml is not set to a valid value <dark_gray>(<white><value></white>)</dark_gray>. Please set it to <white>PRESET_ITEM_HELD</white> or <white>MINI_MESSAGE</white>!",
                        Placeholder.unparsed("value", format));
            }
        }
    }

    public static @NotNull List<TagResolver> tagResolversMMPlaceholders() {
        final List<TagResolver> out = new ArrayList<>();

        for (Map.Entry<String, String> v : customPlaceholders.entrySet()) {
            out.add(TextUtil.selfClosingTag(v.getKey(), Component.text(v.getValue())));
        }

        return out;
    }
}