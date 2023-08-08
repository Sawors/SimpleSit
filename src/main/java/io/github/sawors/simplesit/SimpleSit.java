package io.github.sawors.simplesit;

import io.github.sawors.slogger.SLogger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class SimpleSit extends JavaPlugin {

    static SLogger logger;
    private static Plugin instance;
    
    public static final String FORCE_SIT_PERMISSION = "sit.force-sit";
    
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance = this;
        logger = new SLogger(getPlugin());
        
        // listener
        getServer().getPluginManager().registerEvents(new SittingManager(),this);
        
        // command
        try{
            Objects.requireNonNull(getServer().getPluginCommand("sit")).setExecutor(new SitCommand());
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
    
    public static Plugin getPlugin() {
        return instance;
    }
}
