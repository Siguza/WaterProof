// Bukkit Plugin "WaterProof" by Siguza
// This software is distributed under the following license:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.logging.Logger;
import java.net.*;

import net.minecraft.server.*;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.scheduler.CraftScheduler;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.configuration.file.*;

import static net.drgnome.waterproof.Config.*;
import static net.drgnome.waterproof.Util.*;

public class WPlugin extends JavaPlugin implements Listener
{
    public static final String version = "#VERSION#";
    private static HashMap<Integer, ArrayList<Integer>> waterproof;
    private static HashMap<Integer, ArrayList<Integer>> waterbreak;
    private static HashMap<Integer, ArrayList<Integer>> lavaproof;
    private static HashMap<Integer, ArrayList<Integer>> lavabreak;
    private int upTick;
    private boolean update;

    public WPlugin()
    {
        super();
        hack();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void handleLogin(PlayerLoginEvent event)
    {
        if((!update) || (event == null) || (event.getPlayer() == null))
        {
            return;
        }
        Player player = event.getPlayer();
        if(!player.hasPermission("waterproof.all"))
        {
            return;
        }
        sendMessage(player, "There is an update for WaterProof available!", ChatColor.GOLD);
    }
    
    public void onEnable()
    {
        log.info("Enabling Waterproof " + version);
        upTick = 60 * 60 * 20;
        update = false;
        waterproof = new HashMap<Integer, ArrayList<Integer>>();
        waterbreak = new HashMap<Integer, ArrayList<Integer>>();
        lavaproof = new HashMap<Integer, ArrayList<Integer>>();
        lavabreak = new HashMap<Integer, ArrayList<Integer>>();
        checkFiles();
        reloadConfig();
        if(getConfigString("check-update").equalsIgnoreCase("true"))
        {
            getServer().getPluginManager().registerEvents(this, this);
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new WThread(this), 0L, 1L);
        }
    }

    public void onDisable()
    {
        getServer().getScheduler().cancelTasks(this);
        log.info("Disabling WaterProof " + version);
    }
    
    public void hack()
    {
        Block.byId[8] = Block.byId[10] = null;
        Block water = new BlockCustomFluid(false);
        Block lava = new BlockCustomFluid(true);
    }
    
    public synchronized void tick()
    {
        if(!update)
        {
            upTick++;
            if(upTick >= 60 * 60 * 20)
            {
                checkForUpdate();
                upTick = 0;
            }
        }
    }
    
    public static HashMap<Integer, ArrayList<Integer>> getProofList(boolean lava)
    {
        return lava ? WPlugin.lavaproof : WPlugin.waterproof;
    }
    
    public static HashMap<Integer, ArrayList<Integer>> getBreakList(boolean lava)
    {
        return lava ? WPlugin.lavabreak : WPlugin.waterbreak;
    }
    
    public void reloadConfig()
    {
        super.reloadConfig();
        reloadConf(getConfig());
        saveConfig();
        WPlugin.waterproof = getConfigMap("water.proof");
        WPlugin.waterbreak = getConfigMap("water.break");
        WPlugin.lavaproof = getConfigMap("lava.proof");
        WPlugin.lavabreak = getConfigMap("lava.break");
    }
    
    private void checkForUpdate()
    {
        try
        {
            HttpURLConnection con = (HttpURLConnection)(new URL("http://dev.drgnome.net/version.php?t=waterproof")).openConnection();            
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; JVM)");                        
            con.setRequestProperty("Pragma", "no-cache");
            con.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line = null;
            StringBuilder stringb = new StringBuilder();
            if((line = reader.readLine()) != null)
            {
                stringb.append(line);
            }
            String vdigits[] = this.version.toLowerCase().split("\\.");
            String cdigits[] = stringb.toString().toLowerCase().split("\\.");
            int max = vdigits.length > cdigits.length ? cdigits.length : vdigits.length;
            int a = 0;
            int b = 0;
            for(int i = 0; i < max; i++)
            {
                try
                {
                    a = Integer.parseInt(cdigits[i]);
                }
                catch(Throwable t1)
                {
                    char c[] = cdigits[i].toCharArray();
                    for(int j = 0; j < c.length; j++)
                    {
                        a += (c[j] << ((c.length - (j + 1)) * 8));
                    }
                }
                try
                {
                    b = Integer.parseInt(vdigits[i]);
                }
                catch(Throwable t1)
                {
                    char c[] = vdigits[i].toCharArray();
                    for(int j = 0; j < c.length; j++)
                    {
                        b += (c[j] << ((c.length - (j + 1)) * 8));
                    }
                }
                if(a > b)
                {
                    update = true;
                    break;
                }
                else if(a < b)
                {
                    update = false;
                    break;
                }
                else if((i == max - 1) && (cdigits.length > vdigits.length))
                {
                    update = true;
                    break;
                }
            }
        }
        catch(Throwable t)
        {
        }
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
            sendMessage(sender, "WaterProof version: " + version, ChatColor.GREEN);
        }
        else if(args[0].equalsIgnoreCase("reload"))
        {
            reloadConfig();
            sendMessage(sender, "WaterProof configs reloaded.", ChatColor.GREEN);
        }
        return true;
    }
}