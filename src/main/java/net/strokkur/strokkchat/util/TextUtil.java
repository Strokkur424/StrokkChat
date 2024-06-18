package net.strokkur.strokkchat.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.strokkur.strokkchat.StrokkChat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TextUtil {

    public static @NotNull Component parse(final @NotNull String message) {
        return parse(message, TagResolver.empty());
    }

    public static @NotNull Component parse(final @NotNull String message, final @NotNull TagResolver... resolvers) {
        return parse(message, null, resolvers);
    }

    public static @NotNull Component parse(final @NotNull String message, final @Nullable Player player, final @NotNull TagResolver... resolvers) {
        final List<TagResolver> resolverList = new ArrayList<>(List.of(resolvers));
        if (player != null) {
            resolverList.add(papiTag(player));
        }
        return MiniMessage.miniMessage()
                .deserialize(message, TagResolver.resolver(resolverList));
    }

    /**
     * Creates a tag resolver capable of resolving PlaceholderAPI tags for a given player.
     *
     * @param player the player
     * @return the tag resolver
     */
    public static @NotNull TagResolver papiTag(final @NotNull Player player) {
        return TagResolver.resolver("papi", (argumentQueue, context) -> {
            // Get the string placeholder that they want to use.
            final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

            // Then get PAPI to parse the placeholder for the given player. (If available)
            final String papiTag = '%' + papiPlaceholder + '%';
            final String parsedPlaceholder = StrokkChat.placeholderAPI
                    ? PlaceholderAPI.setPlaceholders(player, papiTag)
                    : papiTag;

            // We need to turn this ugly legacy string into a nice component.
            final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);

            // Finally, return the tag instance to insert the placeholder!
            return Tag.inserting(componentPlaceholder);
        });
    }

    /**
     * Creates a tag resolver to get luckperms information for a player
     *
     * @param player the player
     * @return the tag resolver
     */
    public static @NotNull TagResolver luckpermsTag(final @NotNull Player player) {
        return TagResolver.resolver("lp", (argumentQueue, context) -> {
            // If LuckPerms is disabled, we obviously can't do this
            if (StrokkChat.luckPerms == null) {
                return Tag.selfClosingInserting(parse("<dark_gray>[<gray>LuckPerms is not installed</gray>]"));
            }

            // Get the first argument, which tells us the type of luckperms information the user requests
            final String argument = argumentQueue.popOr("please provide a valid argument").value();

            // Get the raw value of whatever argument the user provided
            String rawValue = "";
            if (argument.equalsIgnoreCase("prefix")) {
                User user = StrokkChat.luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    rawValue = "<dark_gray>[<gray>user does not exist</gray>]";
                }
                else {
                    rawValue = user.getCachedData().getMetaData().getPrefix();
                    if (rawValue == null) {
                        rawValue = "";
                    }
                }
            }
            else if (argument.equalsIgnoreCase("group")) {
                User user = StrokkChat.luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    rawValue = "<dark_gray>[<gray>user does not exist</gray>]";
                }
                else {
                    Group group = StrokkChat.luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());
                    if (group == null) {
                        rawValue = "none";
                    }
                    else {
                        rawValue = group.getName();
                    }
                }
            }
            else if (argument.equalsIgnoreCase("group_display")) {
                User user = StrokkChat.luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    rawValue = "<dark_gray>[<gray>user does not exist</gray>]";
                }
                else {
                    Group group = StrokkChat.luckPerms.getGroupManager().getGroup(user.getPrimaryGroup());
                    if (group == null) {
                        rawValue = "none";
                    }
                    else {
                        rawValue = group.getDisplayName();
                    }
                }
            }
            else if (argument.equalsIgnoreCase("suffix")) {
                User user = StrokkChat.luckPerms.getUserManager().getUser(player.getUniqueId());
                if (user == null) {
                    rawValue = "<dark_gray>[<gray>user does not exist</gray>]";
                }
                else {
                    rawValue = user.getCachedData().getMetaData().getSuffix();
                }
            }

            // Turn the raw text into a component
            final Component componentValue = LegacyComponentSerializer.builder().hexColors().build().deserialize(Objects.requireNonNull(rawValue).replace('&', '§'));

            // Return the tag as a self-closing one
            return Tag.selfClosingInserting(componentValue);
        });
    }

    /**
     * Creates a tag resolver to allow formatting of a section with a custom defined component
     *
     * @param name the name of the tag
     * @param component the component to use for the format
     * @return the tag resolver
     */
    public static @NotNull TagResolver insertingTag(final @NotNull String name, final @NotNull ComponentLike component) {
        return TagResolver.resolver(name, Tag.inserting(component));
    }

    /**
     * Creates a tag resolver to insert a component without it affect the rest of the text
     *
     * @param name the name of the tag
     * @param component the component to insert
     * @return the tag resolver
     */
    public static @NotNull TagResolver selfClosingTag(final @NotNull String name, final @NotNull ComponentLike component) {
        return TagResolver.resolver(name, Tag.selfClosingInserting(component));
    }
}