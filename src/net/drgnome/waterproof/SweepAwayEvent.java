// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

public class SweepAwayEvent extends BlockEvent implements Cancellable
{
    private static final HandlerList _handlers = new HandlerList();
    private final FluidType _fluid;
    private boolean _cancelled = false;
    
    public SweepAwayEvent(Block block, FluidType fluid)
    {
        super(block);
        _fluid = fluid;
    }
    
    public FluidType getCause()
    {
        return _fluid;
    }
    
    public boolean isCancelled()
    {
        return _cancelled;
    }
    
    public void setCancelled(boolean cancelled)
    {
        _cancelled = cancelled;
    }
    
    public static enum FluidType
    {
        WATER,
        LAVA;
    }
    
    public HandlerList getHandlers()
    {
        return _handlers;
    }
    
    public static HandlerList getHandlerList()
    {
        return _handlers;
    }
}