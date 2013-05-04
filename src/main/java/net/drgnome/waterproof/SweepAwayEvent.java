// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;

/**
 * A SweepAwayEvent is called whenever a water or lava block flows into a block and breaks it.
 * WaterProof needs to be installed for this.
 */
public class SweepAwayEvent extends BlockEvent implements Cancellable
{
    private static final HandlerList _handlers = new HandlerList();
    private final FluidType _fluid;
    private boolean _cancelled = false;
    
    /**
     * Instantiate a new SweepAwayEvent.
     *
     * @param block The affected Block.
     * @param fluid The type of fluid that broke the block.
     */
    public SweepAwayEvent(Block block, FluidType fluid)
    {
        super(block);
        _fluid = fluid;
    }
    
    /**
     * Get the fluid type which broke the block.
     *
     * @return The fluid type which broke the block.
     */
    public FluidType getFluidType()
    {
        return _fluid;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isCancelled()
    {
        return _cancelled;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setCancelled(boolean cancelled)
    {
        _cancelled = cancelled;
    }
    
    /**
     * {@inheritDoc}
     */
    public HandlerList getHandlers()
    {
        return _handlers;
    }
    
    /**
     * Used by Bukkit.
     */
    public static HandlerList getHandlerList()
    {
        return _handlers;
    }
    
    /**
     * The types of fluids altered by WaterProof.
     */
    public static enum FluidType
    {
        WATER,
        LAVA;
    }
}