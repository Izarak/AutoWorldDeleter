package me.isaac.autoWorldDeleter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class AutoWorldDeleter extends JavaPlugin {

    List<String> worldList;
    boolean save, worldGuard;
    World safeWorld;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        if (!getConfig().getBoolean("Enabled", false)) {
            Bukkit.getLogger().severe("AutoWorldDeleter: Enable the plugin in the config.yml after whitelisting the worlds you DON'T want to delete.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        worldList = getConfig().getStringList("Whitelist");
        if (worldList.isEmpty()) {
            Bukkit.getLogger().severe("AutoWorldDeleter: Whitelist in config.yml cannot be empty!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        save = getConfig().getBoolean("Save", false);
        worldGuard = getConfig().getBoolean("Remove Worldguard Folders", true);
        for (String s : worldList) {
            safeWorld = Bukkit.getWorld(s);
            if (safeWorld != null)
                break;
        }
        if (safeWorld == null) {
            Bukkit.getLogger().severe("AutoWorldDeleter: Couldn't find a safe world in the Whitelist.");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        safeWorld = Bukkit.getWorld(getConfig().getStringList("Whitelist").getFirst());
    }

    @Override
    public void onDisable() {
        if (worldList == null) return;
        for (World world : Bukkit.getWorlds()) {
            if (worldList.contains(world.getName())) continue;
            unloadWorld(world);
            Bukkit.getLogger().info("World folder deleted: " + world.getName() + " " + deleteFolder(world.getWorldFolder()));
            if (!worldGuard) continue;
            deleteFolder(new File("plugins" + File.separator + "WorldGuard" + File.separator + "worlds" + File.separator + world.getName()));
        }
    }

    public void unloadWorld(World world) {
        world.getPlayers().forEach(player -> player.teleport(safeWorld.getSpawnLocation()));
        Bukkit.getServer().unloadWorld(world, save);
    }

    public boolean deleteFolder(File path) {
        if (path.exists()) {
            File files[] = path.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory()) {
                    deleteFolder(files[i]);
                } else {
                    files[i].delete();
                }
            }
        }
        return path.delete();
    }

}
