package ir.Awake;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class CageHandler implements Listener {
    private DynamicCage dynamicCage;
    private Map<String, Location> playersLocations = new HashMap<>();
    public CageHandler(DynamicCage dynamicCage) {
        this.dynamicCage = dynamicCage;
    }
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();
        Location lastLocation = playersLocations.get(playerName);
        if (!dynamicCage.inCagePlayers.containsKey(playerName)) return;
        if (player.hasPermission("dynamiccage.bypass")) return;
        Location currentLocation = player.getLocation();
        if (lastLocation == null || hasMoved(lastLocation, currentLocation)) {
            var cageBlocks = dynamicCage.inCagePlayers.get(playerName);
            var newCageBlocks = getCageBlocks(currentLocation);
            newCageBlocks = clearCage(newCageBlocks, cageBlocks);
            dynamicCage.log("Generating cage for " + player.getName() + " at X: " + currentLocation.getBlockX() +
                    " Y: " + currentLocation.getBlockY() + " Z: " + currentLocation.getBlockZ(), true);
            createCage(newCageBlocks);

            cageBlocks.putAll(newCageBlocks);
            playersLocations.put(playerName, currentLocation);
        }

    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        if (dynamicCage.inCagePlayers.containsKey(playerName)){
            dynamicCage.removeFromCage(playerName);
        }
    }
    public void createCage(Map<BlockKey, Location> cageBlocks) {
        dynamicCage.log("Creating " + cageBlocks.size() + " blocks", true);
        Material material = dynamicCage.getCageMaterial();
        cageBlocks.values().forEach(blockLoc -> blockLoc.getBlock().setType(material));
    }
    public Map<BlockKey, Location> clearCage (Map<BlockKey, Location> newCageBlocks, Map<BlockKey, Location> cageBlocks){
        Set<BlockKey> keysToRemove = new HashSet<>();
        if (cageBlocks.isEmpty()) return newCageBlocks;
        cageBlocks.entrySet().removeIf(entry -> {
            var loc = entry.getValue();
            BlockKey key = new BlockKey(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            if (!newCageBlocks.containsKey(key)) {
                loc.getBlock().setType(Material.AIR);
                keysToRemove.add(key);
                return true;
            }
            newCageBlocks.remove(key);
            return false;
        });

        dynamicCage.log("Keeping " + (cageBlocks.size() - keysToRemove.size()) + " blocks", true);
        dynamicCage.log("Removing " + keysToRemove.size() + " blocks", true);
        keysToRemove.forEach(cageBlocks::remove);

        return newCageBlocks;
    }

    private boolean hasMoved(Location lastLocation, Location currentLocation) {
        int deltaX = Math.abs(Math.abs(lastLocation.getBlockX()) - Math.abs(currentLocation.getBlockX()));
        int deltaY = Math.abs( Math.abs(lastLocation.getBlockY()) - Math.abs(currentLocation.getBlockY()));
        int deltaZ = Math.abs( Math.abs(lastLocation.getBlockZ()) - Math.abs(currentLocation.getBlockZ()));
        int minToMove = dynamicCage.getMovementToMoveCage();
        int minToMoveY = Math.max(minToMove - 1, 1);
        return deltaY >= minToMoveY || deltaX >= minToMove || deltaZ >= minToMove;
    }
    private Map<BlockKey, Location> getCageBlocks(Location currentLocaion){
        final int locX = currentLocaion.getBlockX();
        final int locY = currentLocaion.getBlockY();
        final int locZ = currentLocaion.getBlockZ();
        int cageSize = dynamicCage.getCageSize();
        Map<BlockKey, Location> returnCageBlocks = new HashMap<>();
        for (int y = -1; y <= cageSize; y++) {
            for (int x = -cageSize; x <= cageSize; x++) {
                for (int z = -cageSize; z <= cageSize; z++) {
                    if (dynamicCage.isSkipEdges() && shouldSkipEdge(x, y, z, cageSize)) continue;
                    if (x == -cageSize || x == cageSize || z == -cageSize || z == cageSize || y == -1 || y == cageSize) {
                        BlockKey key = new BlockKey(
                                locX + x,
                                locY + y,
                                locZ + z
                        );
                        returnCageBlocks.put(key, currentLocaion.clone().add(x, y, z));
                    }
                }
            }
        }
        return returnCageBlocks;
    }

    private boolean shouldSkipEdge(int x, int y, int z, int cageSize) {
        return (Math.abs(x) == cageSize && Math.abs(z) == cageSize) ||
                ((y == cageSize || y == -1) && Math.abs(z) == cageSize) ||
                (Math.abs(x) == cageSize && (y == cageSize || y == -1));
    }
    public void removePlayerCage(String username){
        clearCage(new HashMap<>(), dynamicCage.inCagePlayers.get(username));
        playersLocations.remove(username);
    }
}
