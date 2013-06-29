// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.*;
import static net.drgnome.waterproof.Global.*;

public class Config
{    
    private static FileConfiguration _config;
    
    public static void reload()
    {
        _config = WPlugin.instance().getConfig();
        setDefs();
    }
    
    private static void setDefs()
    {
        setDef("check-update", "true");
        setDef("allow-event-cancel", "true");
        setDef("allow-water-in-nether", "false");
        setDef("water.world.decay", "true");
        setDef("water.world.infinite", "true");
        setDef("water.world.reduction", "1");
        setDef("water.world.tick", "5");
        setDef("water.nether.decay", "true");
        setDef("water.nether.infinite", "true");
        setDef("water.nether.reduction", "1");
        setDef("water.nether.tick", "5");
        setDef("lava.world.decay", "false");
        setDef("lava.world.infinite", "false");
        setDef("lava.world.reduction", "2");
        setDef("lava.world.tick", "30");
        setDef("lava.nether.decay", "false");
        setDef("lava.nether.infinite", "false");
        setDef("lava.nether.reduction", "1");
        setDef("lava.nether.tick", "10");
        ArrayList<String> list = new ArrayList<String>();
        list.add("55");
        list.add("75");
        list.add("76");
        setDef("water.proof", list);
        setDef("water.break", new ArrayList<String>());
        setDef("lava.proof", list);
        setDef("lava.break", new ArrayList<String>());
    }
    
    private static void setDef(String path, Object value)
    {
        if(!_config.isSet(path))
        {
            _config.set(path, value);
        }
    }
    
    public static int getInt(String string)
    {
        try
        {
            return Integer.parseInt(string(string));
        }
        catch(Throwable t)
        {
            return 0;
        }
    }
    
    public static boolean bool(String string)
    {
        return string(string).equalsIgnoreCase("true");
    }
    
    public static String string(String string)
    {
        return _config.getString(string);
    }
    
    public static HashMap<Integer, ArrayList<Integer>> map(String string)
    {
        try
        {
            String[] array = _config.getStringList(string).toArray(new String[0]);
            HashMap<Integer, ArrayList<Integer>> map = new HashMap<Integer, ArrayList<Integer>>();
            for(String s : array)
            {
                s = s.trim();
                int id, meta;
                if(s.contains(":"))
                {
                    String[] parts = s.split(":");
                    try
                    {
                        id = Integer.parseInt(parts[0]);
                    }
                    catch(NumberFormatException e)
                    {
                        Material m = Material.getMaterial(parts[0].toUpperCase());
                        if(m == null)
                        {
                            _log.warning("[Waterproof] Unknown block type: " + parts[0]);
                            continue;
                        }
                        else
                        {
                            id = m.getId();
                        }
                    }
                    try
                    {
                        meta = Integer.parseInt(parts[1]);
                    }
                    catch(NumberFormatException e)
                    {
                        _log.warning("[Waterproof] Unknown block metadata: " + parts[1]);
                        continue;
                    }
                }
                else
                {
                    meta = -1;
                    try
                    {
                        id = Integer.parseInt(s);
                    }
                    catch(NumberFormatException e)
                    {
                        Material m = Material.getMaterial(s.toUpperCase());
                        if(m == null)
                        {
                            _log.warning("[Waterproof] Unknown block type: " + s);
                            continue;
                        }
                        else
                        {
                            id = m.getId();
                        }
                    }
                }
                if(map.containsKey(id))
                {
                    map.get(id).add(meta);
                }
                else
                {
                    ArrayList<Integer> list = new ArrayList<Integer>();
                    list.add(meta);
                    map.put(id, list);
                }
            }
            return map;
        }
        catch(Throwable t)
        {
            return new HashMap<Integer, ArrayList<Integer>>();
        }
    }
}