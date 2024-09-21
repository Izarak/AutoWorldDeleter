package me.isaac.autoWorldDeleter;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public final class AutoWorldDeleter extends JavaPlugin {

    List<String> worldList;
    boolean save, worldGuard;

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

        if (worldList == null) return;
        File worldsFolder = new File("worlds");
        if (!worldsFolder.exists()) {
            Bukkit.getLogger().severe("Couldn't find world folder!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        for (File worldFolder : worldsFolder.listFiles()) {
            if (worldList.contains(worldFolder.getName())) continue;
            Bukkit.getLogger().info("World folder deleted: " + worldFolder.getName() + " " + deleteFolder(worldFolder));
            if (!worldGuard) continue;
            Bukkit.getLogger().info("WorldGuard folder deleted: " + worldFolder.getName() + " " + deleteFolder(new File("plugins" + File.separator + "WorldGuard" + File.separator + "worlds" + File.separator + worldFolder.getName())));
        }


    }

    @Override
    public void onDisable() {

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
