// Bukkit Plugin "WaterProof" by Siguza
// This file has been copied from the Craftbukkit github page and is released under the same conditions as Bukkit is.

package net.drgnome.waterproof;

import java.util.Random;

import net.minecraft.server.*;

import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockFromToEvent;

import static net.drgnome.waterproof.Util.*;

public class BlockCustomFluid extends BlockFlowing
{
    private boolean isLava;
    private static final int proof[] = new int[]{Block.WOODEN_DOOR.id, Block.IRON_DOOR_BLOCK.id, Block.SIGN_POST.id, Block.LADDER.id, Block.SUGAR_CANE_BLOCK.id};
    int var1 = 0;
    boolean var2[] = new boolean[4];
    int var3[] = new int[4];
    
    public BlockCustomFluid(boolean lava)
    {
        super(lava ? 10 : 8, lava ? Material.LAVA : Material.WATER);
        if(lava)
        {
            #FIELD_BLOCK_1#(0.0F);
            #FIELD_BLOCK_2#(1.0F);
            #FIELD_BLOCK_3#(255);
            #FIELD_BLOCK_4#("lava");
        }
        else
        {
            #FIELD_BLOCK_1#(100.0F);
            #FIELD_BLOCK_3#(3);
            #FIELD_BLOCK_4#("water");
        }
        #FIELD_BLOCK_5#();
        #FIELD_BLOCK_6#();
        this.isLava = lava;
    }
    
    private void extend(World world, int i, int j, int k)
    {
        int l = world.getData(i, j, k);
        world.setRawTypeIdAndData(i, j, k, this.id + 1, l);
        world.#FIELD_WORLD_1#(i, j, k, i, j, k);
    }
    
    public boolean #FIELD_BLOCK_7#(IBlockAccess iblockaccess, int i, int j, int k)
    {
        return this.material != Material.LAVA;
    }
    
    public void #FIELD_BLOCK_8#(World world, int i, int j, int k, Random random)
    {
        org.bukkit.World bworld = world.getWorld();
        org.bukkit.Server server = world.getServer();
        org.bukkit.block.Block source = bworld == null ? null : bworld.getBlockAt(i, j, k);
        int l = this.#FIELD_BLOCKFLUIDS_1#(world, i, j, k);
        byte b0 = 1;
        if(this.material == Material.LAVA && !world.worldProvider.#FIELD_WORLDPROVIDER_1#)
        {
            b0 = 2;
        }
        boolean flag = true;
        int i1;
        if(l > 0)
        {
            byte b1 = -100;
            this.var1 = 0;
            int j1 = this.#FIELD_BLOCKFLOWING_1#(world, i - 1, j, k, b1);
            j1 = this.#FIELD_BLOCKFLOWING_1#(world, i + 1, j, k, j1);
            j1 = this.#FIELD_BLOCKFLOWING_1#(world, i, j, k - 1, j1);
            j1 = this.#FIELD_BLOCKFLOWING_1#(world, i, j, k + 1, j1);
            i1 = j1 + b0;
            if(i1 >= 8 || j1 < 0)
            {
                i1 = -1;
            }
            if(this.#FIELD_BLOCKFLUIDS_1#(world, i, j + 1, k) >= 0)
            {
                int k1 = this.#FIELD_BLOCKFLUIDS_1#(world, i, j + 1, k);
                if(k1 >= 8)
                {
                    i1 = k1;
                }
                else
                {
                    i1 = k1 + 8;
                }
            }
            if(this.var1 >= 2 && this.material == Material.WATER)
            {
                if(world.getMaterial(i, j - 1, k).isBuildable())
                {
                    i1 = 0;
                }
                else if(world.getMaterial(i, j - 1, k) == this.material && world.getData(i, j, k) == 0)
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
                    this.extend(world, i, j, k);
                }
            }
            else
            {
                l = i1;
                if(i1 < 0)
                {
                    world.setTypeId(i, j, k, 0);
                }
                else
                {
                    world.setData(i, j, k, i1);
                    world.#FIELD_WORLD_2#(i, j, k, this.id, this.#FIELD_BLOCKFLUIDS_2#());
                    world.applyPhysics(i, j, k, this.id);
                }
            }
        }
        else 
        {
            this.extend(world, i, j, k);
        }
        if(this.liquidCanDisplaceBlock(world, i, j - 1, k))
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
                    world.setTypeId(i, j - 1, k, Block.STONE.id);
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
        else if(l >= 0 && (l == 0 || this.blockBlocksFlow(world, i, j - 1, k)))
        {
            boolean[] aboolean = this.getOptimalFlowDirections(world, i, j, k);
            i1 = l + b0;
            if(l >= 8)
            {
                i1 = 1;
            }
            if(i1 >= 8)
            {
                return;
            }
            BlockFace[] faces = new BlockFace[] {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST};
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
    
    private void flow(World world, int i, int j, int k, int l)
    {
        if(this.liquidCanDisplaceBlock(world, i, j, k))
        {
            int i1 = world.getTypeId(i, j, k);
            if(i1 > 0)
            {
                if(this.material == Material.LAVA)
                {
                    this.fizz(world, i, j, k);
                }
                else
                {
                    Block.byId[i1].#FIELD_BLOCK_9#(world, i, j, k, world.getData(i, j, k), 0);
                }
            }
            world.setTypeIdAndData(i, j, k, this.id, l);
        }
    }
    
    private int getResistance(World world, int i, int j, int k, int l, int i1)
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
                if (k1 == 1)
                {
                    ++l1;
                }
                if (k1 == 2)
                {
                    i2 = k - 1;
                }
                if (k1 == 3)
                {
                    ++i2;
                }
                if(!this.blockBlocksFlow(world, l1, j, i2) && (world.getMaterial(l1, j, i2) != this.material || world.getData(l1, j, i2) != 0))
                {
                    if(!this.blockBlocksFlow(world, l1, j - 1, i2))
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
    
    private boolean[] getOptimalFlowDirections(World world, int i, int j, int k)
    {
        int l;
        int i1;
        for (l = 0; l < 4; ++l)
        {
            this.var3[l] = 1000;
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
            if (l == 2)
            {
                j1 = k - 1;
            }
            if (l == 3)
            {
                ++j1;
            }
            if(!this.blockBlocksFlow(world, i1, j, j1) && (world.getMaterial(i1, j, j1) != this.material || world.getData(i1, j, j1) != 0))
            {
                if(this.blockBlocksFlow(world, i1, j - 1, j1))
                {
                    this.var3[l] = this.getResistance(world, i1, j, j1, 1, l);
                }
                else
                {
                    this.var3[l] = 0;
                }
            }
        }
        l = this.var3[0];
        for(i1 = 1; i1 < 4; ++i1)
        {
            if(this.var3[i1] < l)
            {
                l = this.var3[i1];
            }
        }
        for(i1 = 0; i1 < 4; ++i1)
        {
            this.var2[i1] = this.var3[i1] == l;
        }
        return this.var2;
    }
    
    // Custom
    private boolean blockBlocksFlow(World world, int i, int j, int k)
    {
        int l = world.getTypeId(i, j, k);
        int m = world.getData(i, j, k);
        if(isBlockInList(WPlugin.getProofList(this.isLava), l, m))
        {
            return true;
        }
        if(isBlockInList(WPlugin.getBreakList(this.isLava), l, m))
        {
            return false;
        }
        for(int id : proof)
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
    
    protected int #FIELD_BLOCKFLOWING_1#(World world, int i, int j, int k, int l)
    {
        int i1 = this.#FIELD_BLOCKFLUIDS_1#(world, i, j, k);
        if(i1 < 0)
        {
            return l;
        }
        else
        {
            if(i1 == 0)
            {
                ++this.var1;
            }
            if(i1 >= 8)
            {
                i1 = 0;
            }
            return l >= 0 && i1 >= l ? l : i1;
        }
    }
    
    private boolean liquidCanDisplaceBlock(World world, int i, int j, int k)
    {
        Material material = world.getMaterial(i, j, k);
        return ((material == this.material) || (material == Material.LAVA)) ? false : !this.blockBlocksFlow(world, i, j, k);
    }
    
    public void onPlace(World world, int i, int j, int k)
    {
        super.onPlace(world, i, j, k);
        if(world.getTypeId(i, j, k) == this.id)
        {
            world.#FIELD_WORLD_2#(i, j, k, this.id, this.#FIELD_BLOCKFLUIDS_2#());
        }
    }
    
    #COMM_1A#
    public boolean l()
    {
        return false;
    }
    #COMM_1B#
}