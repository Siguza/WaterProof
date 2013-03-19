// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.io.*;
import java.net.*;
import java.util.*;
import org.bukkit.command.CommandSender;

public class Util
{
    public static boolean isBlockInList(HashMap<Integer, ArrayList<Integer>> map, int id, int meta)
    {
        ArrayList<Integer> list = map.get(id);
        if(list == null)
        {
            return false;
        }
        return list.contains(meta) || list.contains(-1);
    }
    
    public static boolean hasUpdate(String name, String version)
    {
        try
        {
            HttpURLConnection con = (HttpURLConnection)(new URL("http://dev.drgnome.net/version.php?t=" + name)).openConnection();            
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
            String vdigits[] = version.toLowerCase().split("\\.");
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
                    return true;
                }
                else if(a < b)
                {
                    return false;
                }
                else if((i == max - 1) && (cdigits.length > vdigits.length))
                {
                    return true;
                }
            }
        }
        catch(Throwable t)
        {
        }
        return false;
    }
}