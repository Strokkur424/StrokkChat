package net.strokkur.strokkchat.util;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.strokkur.strokkchat.StrokkChat;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

public abstract class AbstractConfigFile {

    private final File file;
    public YamlConfiguration cfg;

    public AbstractConfigFile(String path) {
        file = new File(StrokkChat.plugin.getDataFolder(), path);
        if (!file.exists()) {
            StrokkChat.plugin.saveResource(path, false);
        }

        reload(true);
    }

    public void reload(boolean postReload) {
        cfg = YamlConfiguration.loadConfiguration(file);
        if (postReload) {
            postReload();
        }
    }

    public abstract void postReload();

    public void save(@NotNull YamlConfiguration cfg) {
        try {
            cfg.save(file);
        }
        catch (IOException e) {
            StrokkChat.logError("An error occurred while trying to save <white><filename></white>:",
                    Placeholder.unparsed("filename", file.getName())
            );

            e.printStackTrace();
        }
    }

}
