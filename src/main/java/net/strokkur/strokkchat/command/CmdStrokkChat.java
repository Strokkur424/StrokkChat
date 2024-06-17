package net.strokkur.strokkchat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.MultiLiteralArgument;
import dev.jorel.commandapi.executors.CommandArguments;
import net.kyori.adventure.text.Component;
import net.strokkur.strokkchat.config.CPConfig;
import net.strokkur.strokkchat.config.GeneralConfig;
import net.strokkur.strokkchat.util.TextUtil;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CmdStrokkChat {

    public static final List<CommandSender> debugModeEnabled = new ArrayList<>();

    public static CommandAPICommand getCommand() {

        return new CommandAPICommand("strokkchat")
                .withPermission("strokkchat.command")
                .executes(CmdStrokkChat::sendHelp)
                .withSubcommands(
                        new CommandAPICommand("help").executes(CmdStrokkChat::sendHelp),
                        reload(),
                        debug(),
                        parse()
                );

    }

    private static void sendHelp(CommandSender sender, CommandArguments args) {
        GeneralConfig.messageHelp(sender);
    }

    private static CommandAPICommand reload() {
        return new CommandAPICommand("reload")
                .withPermission("stokkchat.command.reload")
                .withOptionalArguments(new MultiLiteralArgument("config", "Config", "CustomPlaceholders"))
                .executes((sender, args) -> {
                    final String config = (String) args.getOrDefault("config", "all");

                    if (config.equals("all")) {
                        GeneralConfig.reloadCfg();
                        CPConfig.reloadCfg();

                        GeneralConfig.messageReloadAll(sender);
                        return;
                    }

                    if (config.equals("Config")) {
                        GeneralConfig.reloadCfg();
                    }
                    else {
                        CPConfig.reloadCfg();
                    }
                    GeneralConfig.messageReloadSingle(sender, config);
                });
    }

    private static CommandAPICommand debug() {
        return new CommandAPICommand("debug")
                .withPermission("strokkchat.command.debug")
                .withArguments(new MultiLiteralArgument("setting", "on", "off"))
                .executes((sender, args) -> {
                    boolean on = Objects.equals(args.get("setting"), "on");

                    if (on) {
                        debugModeEnabled.add(sender);
                        GeneralConfig.messagesDebugOn(sender);
                        return;
                    }

                    debugModeEnabled.remove(sender);
                    GeneralConfig.messagesDebugOff(sender);
                });
    }

    private static CommandAPICommand parse() {
        return new CommandAPICommand("parse")
                .withPermission("strokkchat.command.parse")
                .withArguments(new IntegerArgument("depth", 0), new GreedyStringArgument("message"))
                .executes((sender, args) -> {
                    // TODO Implement custom placeholder parsing in here

                    final String raw = (String) Objects.requireNonNull(args.get("message"));
                    final Component parsed = TextUtil.parse(raw);

                    GeneralConfig.messagesParse(sender, raw, parsed);
                });
    }


}
