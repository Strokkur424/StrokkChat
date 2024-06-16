package net.strokkur.strokkchat.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.executors.CommandArguments;
import org.bukkit.command.CommandSender;

public class CmdStrokkChat {

    public static CommandAPICommand getCommand() {

        return new CommandAPICommand("strokkchat")
                .executes(CmdStrokkChat::sendHelp)
                .withSubcommands(
                        new CommandAPICommand("help").executes(CmdStrokkChat::sendHelp)

                );

    }


    private static void sendHelp(CommandSender sender, CommandArguments args) {

    }



}
