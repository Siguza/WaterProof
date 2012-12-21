// Bukkit Plugin "WaterProof" by Siguza
// This software is distributed under the following license:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.*;

import static net.drgnome.waterproof.Util.*;

// Thought for static import
public class Config
{    
    private static FileConfiguration config;
    
    // Because reloadConfig is already used
    public static void reloadConf(FileConfiguration file)
    {
        config = file;
        setDefs();
    }
    
    // Set all default values
    private static void setDefs()
    {
        setDef("check-update", "true");
        ArrayList<String> list = new ArrayList<String>();
        list.add("55");
        list.add("75");
        list.add("76");
        setDef("water.proof", list);
        setDef("water.break", new ArrayList<String>());
        setDef("lava.proof", list);
        setDef("lava.break", new ArrayList<String>());
    }
    
    // Set a default value
    private static void setDef(String path, Object value)
    {
        if(!config.isSet(path))
        {
            config.set(path, value);
        }
    }
    
    public static String getConfigString(String string)
    {
        return config.getString(string);
    }
    
    public static HashMap<Integer, ArrayList<Integer>> getConfigMap(String string)
    {
        try
        {
            String[] array = config.getStringList(string).toArray(new String[0]);
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
                            log.warning("[Waterproof] Unknown block type: " + parts[0]);
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
                        log.warning("[Waterproof] Unknown block metadata: " + parts[1]);
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
                            log.warning("[Waterproof] Unknown block type: " + s);
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
            /*ArrayList<Integer> list = new ArrayList<Integer>();
            for(Object o : objects)
            {
                if(o instanceof Integer)
                {
                    list.add((Integer)o);
                }
                else if(o instanceof String)
                {
                    Material m = Material.getMaterial(((String)o).toUpperCase());
                    if(m != null)
                    {
                        list.add((Integer)(m.getId()));
                    }
                }
            }
            Integer ints[] = list.toArray(new Integer[0]);
            int values[] = new int[ints.length];
            for(int i = 0; i < ints.length; i++)
            {
                values[i] = ints[i].intValue();
            }
            return values;*/
        }
        catch(Throwable t)
        {
            return new HashMap<Integer, ArrayList<Integer>>();
        }
    }
}