// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.io.*;
import java.util.*;
import net.minecraft.server.v#MC_VERSION#.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.*;
import net.drgnome.waterproof.inject.*;
import static net.drgnome.waterproof.Global.*;

public class WPlugin extends JavaPlugin implements Listener, Runnable
{
    public static final String _version = "#VERSION#";
    private static HashMap<Integer, ArrayList<Integer>> _waterproof = new HashMap<Integer, ArrayList<Integer>>();
    private static HashMap<Integer, ArrayList<Integer>> _waterbreak = new HashMap<Integer, ArrayList<Integer>>();
    private static HashMap<Integer, ArrayList<Integer>> _lavaproof = new HashMap<Integer, ArrayList<Integer>>();
    private static HashMap<Integer, ArrayList<Integer>> _lavabreak = new HashMap<Integer, ArrayList<Integer>>();
    private boolean _update = false;

    public WPlugin()
    {
        super();
        _plugin = this;
        inject();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleLogin(PlayerLoginEvent event)
    {
        if(event.getPlayer().hasPermission("waterproof.all"))
        {
            sendMessage(event.getPlayer(), "There is an update for WaterProof available!", ChatColor.GOLD);
        }
    }
    
    public void onEnable()
    {
        _log.info("Enabling Waterproof " + _version);
        checkFiles();
        reloadConfig();
        if(Config.bool("check-update"))
        {
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getScheduler().scheduleSyncRepeatingTask(this, this, 0L, 72000L);
        }
    }

    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        _log.info("Disabling WaterProof " + _version);
    }
    
    public void inject()
    {
        BlockCustomFluid.inject();
        ItemCustomBucket.inject();
    }
    
    public static boolean check(int id, int meta, boolean proof, boolean lava)
    {
        return Util.isBlockInList(proof ? (lava ? WPlugin._lavaproof : WPlugin._waterproof) : (lava ? WPlugin._lavabreak : WPlugin._waterbreak), id, meta);
    }
    
    public void reloadConfig()
    {
        super.reloadConfig();
        Config.reload();
        saveConfig();
        WPlugin._waterproof = Config.map("water.proof");
        WPlugin._waterbreak = Config.map("water.break");
        WPlugin._lavaproof = Config.map("lava.proof");
        WPlugin._lavabreak = Config.map("lava.break");
    }
    
    private void checkFiles()
    {
        try
        {
            File file = getDataFolder();
            if(!file.exists())
            {
                file.mkdirs();
            }
            File data = new File(file, "config.yml");
            if(!data.exists())
            {
                PrintStream writer = new PrintStream(new FileOutputStream(data));
                writer.close();
            }
        }
        catch(Throwable t)
        {
        }
    }
    
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if((sender instanceof Player) && !sender.hasPermission("waterproof.all"))
        {
            sendMessage(sender, "You don't have the permission to use this command!", ChatColor.RED);
            return true;
        }
        if((args.length <= 0) || (args[0].equalsIgnoreCase("help")))
        {
            sendMessage(sender, "/waterproof version - Shows the current version", ChatColor.AQUA);
            sendMessage(sender, "/waterproof reload - Reload the configs", ChatColor.AQUA);
            return true;
        }
        if(args[0].equalsIgnoreCase("version"))
        {
            sendMessage(sender, "WaterProof version: " + _version, ChatColor.GREEN);
        }
        else if(args[0].equalsIgnoreCase("reload"))
        {
            reloadConfig();
            sendMessage(sender, "WaterProof configs reloaded.", ChatColor.GREEN);
        }
        return true;
    }
    
    public void run()
    {
        if(checkUpdate())
        {
            getServer().getScheduler().cancelTasks(this);
        }
    }
    
    public boolean checkUpdate()
    {
        _update = Util.hasUpdate("waterproof", _version);
        return _update;
    }
}