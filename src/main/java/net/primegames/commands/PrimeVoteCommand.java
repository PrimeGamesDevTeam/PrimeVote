package net.primegames.commands;

import net.primegames.PrimeVote;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PrimeVoteCommand extends Command {

    public PrimeVoteCommand() {
        super("vote", "primegames.vote", "/vote claim|check|list", new ArrayList<>());
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length == 0) {
                sender.sendMessage(getUsage());
            } else {
                switch (args[0]) {
                    case "claim" -> PrimeVote.getInstance().attemptClaimAllVotes(player);
                    case "check" -> PrimeVote.getInstance().checkVotes(player);
                    case "list" -> PrimeVote.getInstance().listVotesSites(player);
                    default -> sender.sendMessage(getUsage());
                }
            }
            return true;
        }
        sender.sendMessage("§cYou must be a player to use this command.");
        return false;
    }

    @Override
    public @NotNull String getUsage() {
        return "§aUsage: §e/vote §f<§eclaim§f|§echeck§f|§elist§f>";
    }
}
