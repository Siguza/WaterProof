// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.io.*;
import java.util.*;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.*;
import net.drgnome.nbtlib.NBTLib;
import net.drgnome.waterproof.inject.*;
import static net.drgnome.waterproof.Global.*;

public class WPlugin extends JavaPlugin implements Listener, Runnable
{
    public static final String _version = "#VERSION#";
    public static final int _projectID = 43260; // Bukkit
    private static WPlugin _plugin;
    private static HashMap<Integer, ArrayList<Integer>> _waterproof = new HashMap<Integer, ArrayList<Integer>>();
    private static HashMap<Integer, ArrayList<Integer>> _waterbreak = new HashMap<Integer, ArrayList<Integer>>();
    private static HashMap<Integer, ArrayList<Integer>> _lavaproof = new HashMap<Integer, ArrayList<Integer>>();
    private static HashMap<Integer, ArrayList<Integer>> _lavabreak = new HashMap<Integer, ArrayList<Integer>>();
    private static boolean[] _bool = new boolean[10];
    private static int[] _int = new int[8];
    private boolean _update = false;

    public WPlugin()
    {
        super();
        _plugin = this;
    }
    
    public static WPlugin instance()
    {
        return _plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void handleLogin(PlayerJoinEvent event)
    {
        if(_update)
        {
            tellPlayerUpdate(event.getPlayer());
        }
    }
    
    public void onEnable()
    {
        if(!NBTLib.enabled())
        {
            _log.warning("[WaterProof] NBTLib is not enabled!");
            getPluginLoader().disablePlugin(this);
            return;
        }
        _log.info("Enabling Waterproof " + _version);
        checkFiles();
        reloadConfig();
        inject();
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
        try
        {
            CustomFluid.inject();
            CustomBucket.inject();
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            getPluginLoader().disablePlugin(this);
        }
    }
    
    public static boolean check(int id, int meta, boolean proof, boolean lava)
    {
        return Util.isBlockInList(proof ? (lava ? WPlugin._lavaproof : WPlugin._waterproof) : (lava ? WPlugin._lavabreak : WPlugin._waterbreak), id, meta);
    }
    
    public static boolean decay(boolean lava, boolean nether)
    {
        return _bool[(lava ? 2 : 0) + (nether ? 1 : 0)];
    }
    
    public static boolean infinite(boolean lava, boolean nether)
    {
        return _bool[4 + (lava ? 2 : 0) + (nether ? 1 : 0)];
    }
    
    public static boolean allowCancel()
    {
        return _bool[8];
    }
    
    public static boolean waterInNether()
    {
        return _bool[9];
    }
    
    public static int reduction(boolean lava, boolean nether)
    {
        int r = _int[(lava ? 2 : 0) + (nether ? 1 : 0)];
        return (r <= 0) ? 8 : r;
    }
    
    public static int tick(boolean lava, boolean nether)
    {
        int r = _int[4 + (lava ? 2 : 0) + (nether ? 1 : 0)];
        return (r <= 0) ? 10 : r;
    }
    
    public void reloadConfig()
    {
        super.reloadConfig();
        Config.reload();
        saveConfig();
        _waterproof = Config.map("water.proof");
        _waterbreak = Config.map("water.break");
        _lavaproof = Config.map("lava.proof");
        _lavabreak = Config.map("lava.break");
        _bool[0] = Config.bool("water.world.decay");
        _bool[1] = Config.bool("water.nether.decay");
        _bool[2] = Config.bool("lava.world.decay");
        _bool[3] = Config.bool("lava.nether.decay");
        _bool[4] = Config.bool("water.world.infinite");
        _bool[5] = Config.bool("water.nether.infinite");
        _bool[6] = Config.bool("lava.world.infinite");
        _bool[7] = Config.bool("lava.nether.infinite");
        _bool[8] = Config.bool("allow-event-cancel");
        _bool[9] = Config.bool("allow-water-in-nether");
        _int[0] = Config.getInt("water.world.reduction");
        _int[1] = Config.getInt("water.nether.reduction");
        _int[2] = Config.getInt("lava.world.reduction");
        _int[3] = Config.getInt("lava.nether.reduction");
        _int[4] = Config.getInt("water.world.tick");
        _int[5] = Config.getInt("water.nether.tick");
        _int[6] = Config.getInt("lava.world.tick");
        _int[7] = Config.getInt("lava.nether.tick");
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
            sendMessage(sender, "/waterproof reload - Reload the config", ChatColor.AQUA);
            return true;
        }
        if(args[0].equalsIgnoreCase("version"))
        {
            sendMessage(sender, "WaterProof version: " + _version, ChatColor.GREEN);
        }
        else if(args[0].equalsIgnoreCase("reload"))
        {
            reloadConfig();
            sendMessage(sender, "WaterProof config reloaded.", ChatColor.GREEN);
        }
        return true;
    }
    
    public void run()
    {
        if(checkUpdate())
        {
            getServer().getScheduler().cancelTasks(this);
            _log.info(ChatColor.GOLD + "[WaterProof] There is an update available!");
            for(Player p : Bukkit.getOnlinePlayers())
            {
                tellPlayerUpdate(p);
            }
        }
    }
    
    public boolean checkUpdate()
    {
        _update = Util.hasUpdate(_projectID, _version);
        return _update;
    }
    
    private static void tellPlayerUpdate(Player p)
    {
        if(p.hasPermission("waterproof.all"))
        {
            sendMessage(p, "There is an update for WaterProof available!", ChatColor.GOLD);
        }
    }
}