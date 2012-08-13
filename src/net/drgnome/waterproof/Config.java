// Bukkit Plugin "WaterProof" by Siguza
// This software is distributed under the following license:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.util.*;
import org.bukkit.Material;
import org.bukkit.configuration.file.*;

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
        ArrayList<Integer> list = new ArrayList<Integer>();
        list.add((Integer)55);
        list.add((Integer)75);
        list.add((Integer)76);
        setDef("water.proof", list);
        setDef("water.break", new ArrayList<Integer>());
        setDef("lava.proof", list);
        setDef("lava.break", new ArrayList<Integer>());
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
    
    public static int[] getConfigIntList(String string)
    {
        try
        {
            Object objects[] = config.getList(string).toArray();
            ArrayList<Integer> list = new ArrayList<Integer>();
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
            return values;
        }
        catch(Throwable t)
        {
            return new int[0];
        }
    }
}