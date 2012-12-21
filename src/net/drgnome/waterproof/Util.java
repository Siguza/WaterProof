// Bukkit Plugin "WaterProof" by Siguza
// This software is distributed under the following license:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.util.logging.Logger;
import java.util.*;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Util
{
    public static final String LS = System.getProperty("line.separator");
    public static Logger log = Logger.getLogger("Minecraft");
    
    public static boolean isBlockInList(HashMap<Integer, ArrayList<Integer>> map, int id, int meta)
    {
        ArrayList<Integer> list = map.get(id);
        if(list == null)
        {
            return false;
        }
        return list.contains(meta) || list.contains(-1);
    }
    
    // These 3 methods split up strings into multiple lines so that the message doesn't get messed up by the minecraft chat.
    // You can also give a prefix that is set before every line.
    public static void sendMessage(CommandSender sender, String message)
    {
        sendMessage(sender, message, "");
    }
    
    public static void sendMessage(CommandSender sender, String message, ChatColor prefix)
    {
        sendMessage(sender, message, "" + prefix);
    }
    
    public static void sendMessage(CommandSender sender, String message, String prefix)
    {
        if((sender == null) || (message == null))
        {
            return;
        }
        if(prefix == null)
        {
            prefix = "";
        }
        int offset = 0;
        int xpos = 0;
        int pos = 0;
        String part;
        while(true)
        {
            if(offset + 60 >= message.length())
            {
                sender.sendMessage(prefix + message.substring(offset, message.length()));
                break;
            }
            part = message.substring(offset, offset + 60);
            xpos = part.lastIndexOf(" ");
            pos = xpos < 0 ? 60 : xpos;
            part = message.substring(offset, offset + pos);
            sender.sendMessage(prefix + part);
            offset += pos + (xpos < 0 ? 0 : 1);
        }
    }
    
    // Before e.printStackTrace:
    public static void warn()
    {
        log.warning("[WaterProof] AN ERROR OCCURED! PLEASE SEND THE MESSAGE BELOW TO THE DEVELOPER!");
    }
}