// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof.inject;

import java.lang.reflect.*;
import net.minecraft.server.v#MC_VERSION#.*;
import net.drgnome.waterproof.*;

public class ItemCustomBucket extends ItemBucket
{
    private static final int[] _ids = {70, 71};
    private final boolean _isLava;
    private final int _carried;
    
    public static void inject()
    {
        Item.byId[_ids[0] + 256] = Item.byId[_ids[1] + 256] = null;
        new ItemCustomBucket(false);
        new ItemCustomBucket(true);
    }
    
    public ItemCustomBucket(boolean isLava)
    {
        super(isLava ? _ids[1] : _ids[0], isLava ? Block.LAVA.id : Block.WATER.id);
        _carried = isLava ? Block.LAVA.id : Block.WATER.id;
        #FIELD_ITEM_1#(isLava ? "bucketLava" : "bucketWater");
        #FIELD_ITEM_2#(BUCKET);
        _isLava = isLava;
        BlockDispenser.a.a(this, BlockDispenser.a.a(isLava ? Item.LAVA_BUCKET : Item.WATER_BUCKET));
        try
        {
            Field f = Item.class.getDeclaredField(isLava ? "LAVA_BUCKET" : "WATER_BUCKET");
            f.setAccessible(true);
            f.set(null, this);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }
    
    public boolean a(World world, double x, double y, double z, int i, int j, int k)
    {
        if(solidBlock(world, i, j, k))
        {
            return false;
        }
        if((world.worldProvider.#FIELD_WORLDPROVIDER_1#) && !_isLava && !Config.bool("allow-water-in-nether"))
        {
            world.makeSound(x + 0.5D, y + 0.5D, z + 0.5D, "random.fizz", 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
            for(int l = 0; l < 8; l++)
            {
                world.addParticle("largesmoke", i + Math.random(), j + Math.random(), k + Math.random(), 0.0D, 0.0D, 0.0D);
            }
        }
        else
        {
            world.setTypeIdAndData(i, j, k, _carried, 0, 3);
        }
        return true;
    }
    
    private boolean solidBlock(World world, int i, int j, int k)
    {
        int l = world.getTypeId(i, j, k);
        int m = world.getData(i, j, k);
        if(WPlugin.check(l, m, true, _isLava))
        {
            return true;
        }
        if(WPlugin.check(l, m, false, _isLava))
        {
            return false;
        }
        if(world.isEmpty(i, j, k))
        {
            return false;
        }
        return world.getMaterial(i, j, k).isBuildable();
    }
}