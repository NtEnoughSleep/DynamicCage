package ir.Awake;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class DynamicCage extends JavaPlugin {

    private Material cageMaterial;
    private int movementToMoveCage;
    private int cageSize;
    private boolean skipEdges;
    private boolean isCreationLogEnable;
    private int maxPlayers;
    public Map<String, Map<BlockKey, Location>> inCagePlayers = new HashMap<>();
    public CageHandler cageHandler;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        loadConfig();
        PluginCommands pl = new PluginCommands(this);
        registerCommand("DynamicCage", pl::MainCommand, pl::mainCommandTab);
        registerCommand("CageMaterial", pl::setCageMaterial, pl::setCageMaterialTab);
        registerCommand("MinPlayerMove", pl::setMinPlayerMove, (sender, command, alias, args) -> Collections.emptyList());
        registerCommand("CageSize", pl::setCageSize, (sender, command, alias, args) -> Collections.emptyList());
        registerCommand("SkipEdges", pl::setSkipEdges, (sender, command, alias, args) -> List.of("true", "false"));
        registerCommand("logEnable", pl::setLogEnable, (sender, command, alias, args) -> List.of("true", "false"));
        log("DynamicCage is now Available");
        cageHandler = new CageHandler(this);
        getServer().getPluginManager().registerEvents(cageHandler, this);
    }

    @Override
    public void onDisable() {
        saveConfig();
        log("Disabling Plugin ...");
        clearCage();
        log("Plugin has been disabled! See you next time.");
    }
    private void registerCommand(String commandName, CommandExecutor executor, TabCompleter tabCompleter) {
        getCommand(commandName).setExecutor(executor);
        getCommand(commandName).setTabCompleter(tabCompleter);
    }
    public void log(String log){
        getLogger().info(log);
    }
    public void log(String log, Boolean canBeDisabled){
        if (isCreationLogEnable){
            log(log);
        }
    }
    public boolean addToCage(String username){
        if (inCagePlayers.size() > maxPlayers) return false;
        if (inCagePlayers.containsKey(username)) return false;
        inCagePlayers.put(username, new HashMap<>());
        return true;
    }
    public void removeFromCage(String username){
        if (inCagePlayers != null) {
            cageHandler.removePlayerCage(username);
            inCagePlayers.remove(username);
        }
    }
    public void clearCage(){
        for (String username: inCagePlayers.keySet())
            cageHandler.removePlayerCage(username);
        inCagePlayers.clear();
    }
    public Set<String> getInCagePlayers() {
        return inCagePlayers.keySet();
    }
    public int getCageSize(){
        return cageSize;
    }
    public int getMovementToMoveCage() {
        return movementToMoveCage;
    }

    public Material getCageMaterial() {
        return cageMaterial;
    }

    public boolean isSkipEdges() {
        return skipEdges;
    }

    public void setCageMaterial(Material cageMaterial) {
        this.cageMaterial = cageMaterial;
        getConfig().set("cageMaterial", cageMaterial.toString());
        log("Cage Material: " + cageMaterial);
        saveConfig();
    }

    public void setMovementToMoveCage(int movementToMoveCage) {
        this.movementToMoveCage = movementToMoveCage;
        getConfig().set("movementToMoveCage", movementToMoveCage);
        log("Movement to Move Cage: " + movementToMoveCage);
        saveConfig();
    }

    public void setCageSize(int cageSize) {
        this.cageSize = cageSize;
        getConfig().set("cageSize", cageSize);
        log("Cage Size: " + cageSize);
        saveConfig();
    }

    public void setSkipEdges(boolean skipEdges) {
        this.skipEdges = skipEdges;
        getConfig().set("skipEdges", skipEdges);
        log("Skip Edges: " + skipEdges);
        saveConfig();
    }

    public void setCreationLogEnable(boolean creationLogEnable) {
        this.isCreationLogEnable = creationLogEnable;
        getConfig().set("logCageCreation", creationLogEnable);
        log("create cage log: " + creationLogEnable);
        saveConfig();
    }

    public void loadConfig() {
        this.reloadConfig();
        cageMaterial = Material.getMaterial(getConfig().getString("cageMaterial", "RED_STAINED_GLASS"));
        movementToMoveCage = getConfig().getInt("movementToMoveCage", 2);
        cageSize = getConfig().getInt("cageSize", 3);
        skipEdges = getConfig().getBoolean("skipEdges", true);
        isCreationLogEnable = getConfig().getBoolean("logCageCreation", true);
        maxPlayers = getConfig().getInt("max-players", 5);
        log("Cage Material: " + cageMaterial);
        log("Movement to Move Cage: " + movementToMoveCage );
        log("Cage Size: " + cageSize);
        log("Skip Edges: " + skipEdges);

    }
}
