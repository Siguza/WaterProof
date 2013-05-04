// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof;

import java.util.Random;
import net.minecraft.server.v#MC_VERSION#.*;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;

public class BlockCustomFluid extends BlockFlowing
{
    private boolean _isLava;
    private static final int _proof[] = new int[]{Block.WOODEN_DOOR.id, Block.IRON_DOOR_BLOCK.id, Block.SIGN_POST.id, Block.LADDER.id, Block.SUGAR_CANE_BLOCK.id};
    int _var1 = 0;
    boolean _var2[] = new boolean[4];
    int _var3[] = new int[4];

    protected BlockCustomFluid(boolean isLava)
    {
        super(isLava ? 10 : 8, isLava ? Material.LAVA : Material.WATER);
        if(isLava)
        {
            #FIELD_BLOCK_1#(0.0F);
            #FIELD_BLOCK_2#(1.0F);
            #FIELD_BLOCK_4#("lava");
        }
        else
        {
            #FIELD_BLOCK_1#(100.0F);
            #FIELD_BLOCK_3#(3);
            #FIELD_BLOCK_4#("water");
        }
        #FIELD_BLOCK_5#();
        _isLava = isLava;
    }

    public void #FIELD_BLOCK_8#(World world, int i, int j, int k, Random random)
    {
        // CraftBukkit start
        org.bukkit.World bworld = world.getWorld();
        org.bukkit.Server server = world.getServer();
        org.bukkit.block.Block source = bworld == null ? null : bworld.getBlockAt(i, j, k);
        // CraftBukkit end
        int l = this.#FIELD_BLOCKFLUIDS_1#(world, i, j, k); // f_()
        byte b0 = 1;
        if(this.material == Material.LAVA && !world.worldProvider.#FIELD_WORLDPROVIDER_1#) // field e
        {
            b0 = 2;
        }
        boolean flag = true;
        int i1;
        if(l > 0)
        {
            byte b1 = -100;
            _var1 = 0;
            int j1 = this.#FIELD_BLOCKFLOWING_1#(world, i - 1, j, k, b1);
            j1 = this.#FIELD_BLOCKFLOWING_1#(world, i + 1, j, k, j1);
            j1 = this.#FIELD_BLOCKFLOWING_1#(world, i, j, k - 1, j1);
            j1 = this.#FIELD_BLOCKFLOWING_1#(world, i, j, k + 1, j1);
            i1 = j1 + b0;
            if(i1 >= 8 || j1 < 0)
            {
                i1 = -1;
            }
            if (this.#FIELD_BLOCKFLUIDS_1#(world, i, j + 1, k) >= 0) // f_()
            {
                int k1 = this.#FIELD_BLOCKFLUIDS_1#(world, i, j + 1, k); // f_()
                if(k1 >= 8)
                {
                    i1 = k1;
                }
                else
                {
                    i1 = k1 + 8;
                }
            }
            if(_var1 >= 2 && this.material == Material.WATER)
            {
                if(world.getMaterial(i, j - 1, k).isBuildable())
                {
                    i1 = 0;
                }
                else if(world.getMaterial(i, j - 1, k) == this.material && world.getData(i, j - 1, k) == 0)
                {
                    i1 = 0;
                }
            }
            if(this.material == Material.LAVA && l < 8 && i1 < 8 && i1 > l && random.nextInt(4) != 0)
            {
                i1 = l;
                flag = false;
            }
            if(i1 == l)
            {
                if(flag)
                {
                    extend(world, i, j, k);
                }
            }
            else
            {
                l = i1;
                if(i1 < 0)
                {
                    world.setAir(i, j, k);
                }
                else
                {
                    world.setData(i, j, k, i1, 2);
                    world.#FIELD_WORLD_2#(i, j, k, this.id, this.#FIELD_BLOCK_10#(world)); // a(), a()
                    world.applyPhysics(i, j, k, this.id);
                }
            }
        }
        else
        {
            extend(world, i, j, k);
        }
        if(canDisplace(world, i, j - 1, k))
        {
            BlockFromToEvent event = new BlockFromToEvent(source, BlockFace.DOWN);
            if(server != null)
            {
                server.getPluginManager().callEvent(event);
            }
            if(!event.isCancelled())
            {
                if(this.material == Material.LAVA && world.getMaterial(i, j - 1, k) == Material.WATER)
                {
                    world.setTypeIdUpdate(i, j - 1, k, Block.STONE.id);
                    this.fizz(world, i, j - 1, k);
                    return;
                }
                if(l >= 8)
                {
                    this.flow(world, i, j - 1, k, l);
                }
                else
                {
                    this.flow(world, i, j - 1, k, l + 8);
                }
            }
        }
        else if(l >= 0 && (l == 0 || solidBlock(world, i, j - 1, k)))
        {
            boolean[] aboolean = getOptimalFlowDirections(world, i, j, k);
            i1 = l + b0;
            if(l >= 8)
            {
                i1 = 1;
            }
            if(i1 >= 8)
            {
                return;
            }
            BlockFace[] faces = new BlockFace[] { BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH };
            int index = 0;
            for(BlockFace currentFace : faces)
            {
                if(aboolean[index])
                {
                    BlockFromToEvent event = new BlockFromToEvent(source, currentFace);
                    if(server != null)
                    {
                        server.getPluginManager().callEvent(event);
                    }
                    if(!event.isCancelled())
                    {
                        this.flow(world, i + currentFace.getModX(), j, k + currentFace.getModZ(), i1);
                    }
                }
                index++;
            }
        }
    }
    
    protected int #FIELD_BLOCKFLOWING_1#(World world, int i, int j, int k, int l) // d()
    {
        int i1 = this.#FIELD_BLOCKFLUIDS_1#(world, i, j, k); // f_()
        if(i1 < 0)
        {
            return l;
        }
        else
        {
            if(i1 == 0)
            {
                ++_var1;
            }
            if(i1 >= 8)
            {
                i1 = 0;
            }
            return (l >= 0 && i1 >= l) ? l : i1;
        }
    }
    
    private void flow(World world, int i, int j, int k, int l)
    {
        if(canDisplace(world, i, j, k))
        {
            int i1 = world.getTypeId(i, j, k);
            if(i1 > 0)
            {
                org.bukkit.World bukkitWorld = world.getWorld();
                org.bukkit.block.Block block = bukkitWorld == null ? null : bukkitWorld.getBlockAt(i, j, k);
                org.bukkit.Server server = world.getServer();
                if(server != null)
                {
                    SweepAwayEvent event = new SweepAwayEvent(block, _isLava ? SweepAwayEvent.FluidType.LAVA : SweepAwayEvent.FluidType.WATER);
                    server.getPluginManager().callEvent(event);
                    if(event.isCancelled() && Config.bool("allow-event-cancel"))
                    {
                        return;
                    }
                }
                if(this.material == Material.LAVA)
                {
                    this.fizz(world, i, j, k);
                }
                else
                {
                    Block.byId[i1].#FIELD_BLOCK_9#(world, i, j, k, world.getData(i, j, k), 0);
                }
            }
            world.setTypeIdAndData(i, j, k, this.id, l, 3);
        }
    }
    
    private void extend(World world, int i, int j, int k)
    {
        world.setTypeIdAndData(i, j, k, this.id + 1, world.getData(i, j, k), 2);
    }

    private int getResistance(World world, int i, int j, int k, int l, int i1) // d()
    {
        int j1 = 1000;
        for(int k1 = 0; k1 < 4; ++k1)
        {
            if((k1 != 0 || i1 != 1) && (k1 != 1 || i1 != 0) && (k1 != 2 || i1 != 3) && (k1 != 3 || i1 != 2))
            {
                int l1 = i;
                int i2 = k;
                if(k1 == 0)
                {
                    l1 = i - 1;
                }
                if(k1 == 1)
                {
                    ++l1;
                }
                if(k1 == 2)
                {
                    i2 = k - 1;
                }
                if(k1 == 3)
                {
                    ++i2;
                }
                if(!solidBlock(world, l1, j, i2) && (world.getMaterial(l1, j, i2) != this.material || world.getData(l1, j, i2) != 0))
                {
                    if(!solidBlock(world, l1, j - 1, i2))
                    {
                        return l;
                    }
                    if(l < 4)
                    {
                        int j2 = this.getResistance(world, l1, j, i2, l + 1, k1);
                        if(j2 < j1)
                        {
                            j1 = j2;
                        }
                    }
                }
            }
        }
        return j1;
    }

    private boolean[] getOptimalFlowDirections(World world, int i, int j, int k) // n()
    {
        int l;
        int i1;
        for(l = 0; l < 4; ++l)
        {
            _var3[l] = 1000;
            i1 = i;
            int j1 = k;
            if(l == 0)
            {
                i1 = i - 1;
            }
            if(l == 1)
            {
                ++i1;
            }
            if(l == 2)
            {
                j1 = k - 1;
            }
            if(l == 3)
            {
                ++j1;
            }
            if(!solidBlock(world, i1, j, j1) && (world.getMaterial(i1, j, j1) != this.material || world.getData(i1, j, j1) != 0))
            {
                if(solidBlock(world, i1, j - 1, j1))
                {
                    _var3[l] = this.getResistance(world, i1, j, j1, 1, l);
                }
                else
                {
                    _var3[l] = 0;
                }
            }
        }
        l = _var3[0];
        for(i1 = 1; i1 < 4; ++i1)
        {
            if(_var3[i1] < l)
            {
                l = _var3[i1];
            }
        }
        for(i1 = 0; i1 < 4; ++i1)
        {
            _var2[i1] = _var3[i1] == l;
        }
        return _var2;
    }

    private boolean canDisplace(World world, int i, int j, int k) // p()
    {
        Material material = world.getMaterial(i, j, k);
        return ((material == this.material) || (material == Material.LAVA)) ? false : !solidBlock(world, i, j, k);
    }
    
    private boolean solidBlock(World world, int i, int j, int k) // o()
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
        for(int id : _proof)
        {
            if(l == id)
            {
                return true;
            }
        }
        if(l == 0)
        {
            return false;
        }
        Material material = Block.byId[l].material;
        return (material == Material.PORTAL) || material.isSolid();
    }
}