package net.strokkur.strokkchat.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.strokkchat.StrokkChat;
import net.strokkur.strokkchat.config.CPConfig;
import net.strokkur.strokkchat.config.GeneralConfig;
import net.strokkur.strokkchat.util.TextUtil;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public class ChatListener implements Listener {

    public final void onMessage(final @NotNull AsyncChatEvent e) {

        final String chosenMessage = GeneralConfig.getCorrectFormat(e.getPlayer());
        final Component parsed = CPConfig.parsePlayerInputString(MiniMessage.miniMessage().serialize(e.originalMessage()), e.getPlayer());

        final Component parsedFormat = CPConfig.parseConfigString(chosenMessage, e.getPlayer(), GeneralConfig.defaultDepth());
        final String serializedParsedFormat = MiniMessage.miniMessage().serialize(parsedFormat).replace("\\<message>", "<message>");

        e.setCancelled(true);
        StrokkChat.logDebug("Raw: <raw>", Placeholder.unparsed("raw", serializedParsedFormat));
        Bukkit.broadcast(TextUtil.parse(serializedParsedFormat,
                TextUtil.selfClosingTag("message", parsed)
        ));

    }

}
