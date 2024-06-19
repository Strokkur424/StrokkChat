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
import java.util.Map;
import java.util.regex.Pattern;

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

    public static @NotNull Component parseConfigString(final String raw, final Player player, int depth) {
        String parsedText = raw;
        for (int i = depth; i > 0; i--) {
            parsedText = CPConfig.replaceMMPlaceholders(parsedText);
        }

        final List<TagResolver> tagResolvers = new ArrayList<>();
        if (StrokkChat.luckPerms != null) {
            tagResolvers.add(TextUtil.luckpermsTag(player));
        }

        if (StrokkChat.placeholderAPI) {
            tagResolvers.add(TextUtil.papiTag(player));
        }

        tagResolvers.add(Placeholder.unparsed("player", player.getName()));
        StrokkChat.logDebug("<gray>Current parsed text: </gray><raw_message>", Placeholder.unparsed("raw_message", parsedText));

        return TextUtil.parse(parsedText,
                tagResolvers.toArray(new TagResolver[0])
        );
    }

    public static @NotNull Component parsePlayerInputString(final String raw, final @NotNull Player player) {
        if (!player.hasPermission("strokkchat.minimessage")) {
            return Component.text(raw);
        }

        String parsed = raw;
        for (int i = GeneralConfig.defaultDepth(); i > 0; i--) {
            parsed = replaceMMPlaceholders(raw, player);
        }

        return TextUtil.parse(parsed);
    }

    public static @NotNull String replaceMMPlaceholders(String str) {
        final List<String> tagsToCheck = new ArrayList<>();

        for (Map.Entry<String, String> entry : customPlaceholders.entrySet()) {
            if (str.contains("<" + entry.getKey() + ">")) {
                tagsToCheck.add(entry.getKey());
            }
        }
        for (String tag : tagsToCheck) {
            str = str.replaceAll(Pattern.quote("<" + tag + ">"), customPlaceholders.get(tag));
        }

        return str;
    }

    public static @NotNull String replaceMMPlaceholders(String str, @NotNull Player player) {
        if (!player.hasPermission("strokkchat.minimessage")) {
            return str;
        }

        final List<String> tagsToCheck = new ArrayList<>();

        for (Map.Entry<String, String> entry : customPlaceholders.entrySet()) {
            if (str.contains("<" + entry.getKey() + ">") && player.hasPermission("strokkchat.custom-placeholder." + entry.getKey())) {
                tagsToCheck.add(entry.getKey());
            }
        }

        for (String tag : tagsToCheck) {
            str = str.replaceAll(Pattern.quote("<" + tag + ">"), customPlaceholders.get(tag));
        }

        return str;
    }


}