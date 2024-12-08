package ir.Awake;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PluginCommands {
    private DynamicCage dynamicCage;
    public PluginCommands(DynamicCage dynamicCage) {
        this.dynamicCage = dynamicCage;
    }
    private boolean basicChecks(CommandSender sender, String[] args){
        if (!(sender instanceof Player)) return false;
        if (args.length != 1) {
            sender.sendMessage(ChatColor.RED + "incorrect usage. ");
            return false;
        };
        return true;
    }
    private void sendSuccess(CommandSender sender){
        sender.sendMessage(ChatColor.GREEN + "Successfully changed. ");
    }
    public boolean MainCommand(CommandSender sender, Command cmd, String label, String[] args){
        if (args.length == 0 || args[0].equals("help")) {
            sender.sendMessage(ChatColor.YELLOW + "Available Commands:");
            sender.sendMessage(ChatColor.GREEN + "/DynamicCage add <playerName> " + ChatColor.WHITE + "- Adds a player to the cage list.");
            sender.sendMessage(ChatColor.GREEN + "/DynamicCage remove <playerName> " + ChatColor.WHITE + "- Removes a player from the cage list.");
            sender.sendMessage(ChatColor.GREEN + "/DynamicCage list " + ChatColor.WHITE + "- Lists all players with cages.");
            return true;
        }
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "add":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "incorrect usage. ");
                    return false;
                }
                if (sender.hasPermission("dynamiccage.uncageable")){
                    sender.sendMessage(ChatColor.RED + "You cannot add this player to the list. ");
                    return true;
                }
                var isAdded = dynamicCage.addToCage(args[1]);
                if (!isAdded)
                    sender.sendMessage(ChatColor.RED + "player is exist in list or max players reached. ");
                else
                    sendSuccess(sender);
                break;
            case "remove":
                if (args.length != 2) {
                    sender.sendMessage(ChatColor.RED + "incorrect usage. ");
                    return false;
                }
                dynamicCage.removeFromCage(args[1]);
                sendSuccess(sender);
                break;
            case "list":
                sender.sendMessage(ChatColor.GREEN + String.join(", ", dynamicCage.getInCagePlayers()));
                break;
            case "clear":
                dynamicCage.clearCage();
                sendSuccess(sender);
                break;
            default:
                sender.sendMessage(ChatColor.RED + "invalid subcommand. ");
                return false;
        }
        return true;
    }
    public List<String> mainCommandTab(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("add", "remove", "list", "clear");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("add")) {
            return dynamicCage.getServer().getOnlinePlayers().stream().map(Player::getName).toList();
        } else if (args.length == 2 && args[0].equalsIgnoreCase("remove")) {
            return new ArrayList<>(dynamicCage.getInCagePlayers());
        }
        return Collections.emptyList();
    }
    public boolean setCageMaterial(CommandSender sender, Command cmd, String label, String[] args){
        if (!basicChecks(sender, args)) return false;
        Material material = Material.getMaterial(args[0].toUpperCase());
        if (material == null){
            sender.sendMessage(ChatColor.RED + "invalid material. please provide a valid one. ");
            return false;
        }
        dynamicCage.setCageMaterial(material);
        sendSuccess(sender);
        return true;
    }
    public boolean setMinPlayerMove(CommandSender sender, Command cmd, String label, String[] args) {
        if (!basicChecks(sender, args)) return false;
        int movementToMoveCage;
        try{
             movementToMoveCage = Integer.parseInt(args[0]);
        }catch (RuntimeException e){
            sender.sendMessage(ChatColor.RED + "invalid blocks count number. Please provide a integer number");
            return false;
        }
        dynamicCage.setMovementToMoveCage(movementToMoveCage);
        sendSuccess(sender);
        return true;
    }
    public boolean setCageSize(CommandSender sender, Command cmd, String label, String[] args) {
        if (!basicChecks(sender, args)) return false;
        int cageSize;
        try{
             cageSize = Integer.parseInt(args[0]);
        }catch (RuntimeException e){
            sender.sendMessage(ChatColor.RED + "invalid cube size number. Please provide a integer number");
            return false;
        }
        dynamicCage.setCageSize(cageSize);
        sendSuccess(sender);
        return true;
    }
    public boolean setSkipEdges(CommandSender sender, Command cmd, String label, String[] args) {
        if (!basicChecks(sender, args)) return false;
        boolean skipEdges;
        try{
             skipEdges = Boolean.parseBoolean(args[0]);
        }catch (RuntimeException e){
            sender.sendMessage(ChatColor.RED + "invalid argument. [true or false]");
            return false;
        }
        dynamicCage.setSkipEdges(skipEdges);
        sendSuccess(sender);
        return true;
    }
    public boolean setLogEnable(CommandSender sender, Command cmd, String label, String[] args) {
        if (!basicChecks(sender, args)) return false;
        boolean logEnable;
        try{
            logEnable = Boolean.parseBoolean(args[0]);
        }catch (RuntimeException e){
            sender.sendMessage(ChatColor.RED + "invalid argument. [true or false]");
            return false;
        }
        dynamicCage.setCreationLogEnable(logEnable);
        sendSuccess(sender);
        return true;
    }

    public List<String> setCageMaterialTab(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.stream(Material.values())
                    .filter(Material::isBlock)
                    .map(Material::name)
                    .map(String::toLowerCase)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
