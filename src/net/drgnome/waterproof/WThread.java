// Bukkit Plugin "WaterProof" by Siguza
// This software is distributed under the following license:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

public class WThread extends Thread
{
    private WPlugin plugin;
    
    public WThread(WPlugin plugin)
    {
        super();
        this.plugin = plugin;
    }
    
    public void run()
    {
        if(plugin != null)
        {
            plugin.tick();
        }
    }
}