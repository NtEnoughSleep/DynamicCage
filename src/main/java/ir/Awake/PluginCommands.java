package ir.Awake;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
