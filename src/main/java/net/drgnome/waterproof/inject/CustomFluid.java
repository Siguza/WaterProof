// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof.inject;

import java.lang.reflect.*;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.Server;
import org.bukkit.block.*;
import org.bukkit.event.block.BlockFromToEvent;
import net.drgnome.nbtlib.*;
import net.drgnome.waterproof.*;
import static net.drgnome.waterproof.Global.*;

public class CustomFluid implements MethodFilter, InvocationHandler
{
    static final int _idWater = Material.WATER.getId();
    static final int _idLava = Material.LAVA.getId();
    private static final int[] _proof = new int[]{Material.WOODEN_DOOR.getId(), Material.IRON_DOOR_BLOCK.getId(), Material.SIGN_POST.getId(), Material.LADDER.getId(), Material.SUGAR_CANE_BLOCK.getId(), Material.PORTAL.getId(), Material.ENDER_PORTAL.getId()};
    private static final BlockFace[] _faces = new BlockFace[] { BlockFace.WEST, BlockFace.EAST, BlockFace.NORTH, BlockFace.SOUTH };
    private static final Class[] _classes = new Class[4];
    private static final Method[] _methods = new Method[6];
    private static final Field[] _fields = new Field[2];
    static CustomFluid _iWater;
    static CustomFluid _iLava;
    private final boolean _isLava;
    private final int _blockID;
    private Object _proxy;
    private int _sources = 0;
    
    static
    {
        try
        {
            _classes[0] = NBTLib.getMinecraftClass("BlockFlowing");
            _classes[1] = NBTLib.getMinecraftClass("World");
            _classes[2] = NBTLib.getMinecraftClass("Material");
            _classes[3] = NBTLib.getMinecraftClass("BlockFluids");
            _methods[0] = NBTLib.getMethod(_classes[1], "getWorld");
            _methods[1] = NBTLib.getMethod(_classes[1], "a", int.class, int.class, int.class, int.class, int.class); // Derpnote
            _methods[2] = NBTLib.getMethod(_classes[1], "applyPhysics", int.class, int.class, int.class, int.class);
            _methods[3] = NBTLib.getMethod(_classes[2], "isBuildable");
            _methods[4] = NBTLib.getMethod(_classes[2], "isSolid");
            _methods[5] = NBTLib.getMethod(_classes[3], "fizz", _classes[1], int.class, int.class, int.class);
            Class clazz = NBTLib.getMinecraftClass("Block");
            _fields[0] = NBTLib.getField(clazz, "byId");
            _fields[1] = NBTLib.getField(clazz, _classes[2]);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            WPlugin.instance().getPluginLoader().disablePlugin(WPlugin.instance());
        }
    }
    
    public static void inject()
    {
        _iWater = new CustomFluid(false);
        _iLava = new CustomFluid(true);
        try
        {
            Class[] paramTypes = new Class[]{int.class, NBTLib.getMinecraftClass("Material")};
            Object[] array = (Object[])_fields[0].get(null);
            array[_idWater] = array[_idLava] = null;
            _iWater._proxy = ClassProxy.newInstance(_classes[0], _iWater, _iWater, paramTypes, _idWater, NBTLib.fetchMinecraftField("Material", null, "WATER"));
            _iLava._proxy = ClassProxy.newInstance(_classes[0], _iLava, _iLava, paramTypes, _idLava, NBTLib.fetchMinecraftField("Material", null, "LAVA"));
            if(array[_idWater].getClass().getDeclaredMethods().length != 3)
            {
                String s = "[WaterProof] Fluid proxy class has the wrong amount of methods (" + array[_idWater].getClass().getDeclaredMethods().length + ")! Methods:";
                for(Method m : array[_idWater].getClass().getDeclaredMethods())
                {
                    s += " " + m.getName() + "(),";
                }
                throw new AssertionError(s);
            }
            NBTLib.putMinecraftField("Block", array[_idWater], "name", "water");
            NBTLib.putMinecraftField("Block", array[_idLava], "name", "lava");
            NBTLib.putMinecraftField("Block", array[_idWater], "strength", 100F);
            NBTLib.putMinecraftField("Block", array[_idLava], "strength", 0F);
            NBTLib.putMinecraftField("Block", array[_idWater], "durability", 500F);
            NBTLib.putMinecraftField("Block", array[_idLava], "durability", 0F);
            ((int[])NBTLib.fetchMinecraftField("Block", null, "lightBlock"))[_idWater] = 3;
            ((int[])NBTLib.fetchMinecraftField("Block", null, "lightEmission"))[_idLava] = 15;
            NBTLib.putMinecraftField("Block", array[_idWater], "cD", false); // Derpnote
            NBTLib.putMinecraftField("Block", array[_idLava], "cD", false); // Derpnote
        }
        catch(AssertionError e)
        {
            throw e;
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
    }

    private CustomFluid(boolean isLava)
    {
        _isLava = isLava;
        _blockID = _isLava ? _idLava : _idWater;
    }
    
    public boolean filterMethod(Method m)
    {
        Class[] c = m.getParameterTypes();
        return (((m.getDeclaringClass() == _classes[3])
                 && (c.length == 1)
                 && (c[0] == _classes[1])
                 )
                ||
                ((m.getDeclaringClass() == _classes[0])
                 && (m.getReturnType() == void.class)
                 && (c.length == 5)
                 && (c[0] == _classes[1])
                 && (c[1] == int.class)
                 && (c[2] == int.class)
                 && (c[3] == int.class)
                 && (c[4] == Random.class)
                 )
                );
    }
    
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        try
        {
            if(method.getParameterTypes().length == 1)
            {
                return WPlugin.tick(_isLava, ((World)_methods[0].invoke(args[0])).getEnvironment() == World.Environment.NETHER);
            }
            else
            {
                update(args[0], (Integer)args[1], (Integer)args[2], (Integer)args[3], (Random)args[4]);
            }
        }
        catch(InvocationTargetException e)
        {
            throw e.getCause();
        }
        return null;
    }
    
    private void update(Object mcWorld, int x, int y, int z, Random rand) throws Throwable
    {
        //System.out.println("[WP] " + x + "/" + y + "/" + z);
        World world = (World)_methods[0].invoke(mcWorld);
        Block block = world.getBlockAt(x, y, z);
        int meta = getMeta(world, x, y, z);
        boolean isHell = (world.getEnvironment() == World.Environment.NETHER);
        int decay = WPlugin.reduction(_isLava, isHell);
        if(meta > 0)
        {
            _sources = 0;
            int minDecay = getSmallestFlowDecay(world, x - 1, y, z, -100);
            minDecay = getSmallestFlowDecay(world, x + 1, y, z, minDecay);
            minDecay = getSmallestFlowDecay(world, x, y, z - 1, minDecay);
            minDecay = getSmallestFlowDecay(world, x, y, z + 1, minDecay);
            int newMeta = minDecay + decay;
            if((newMeta >= 8) || (minDecay < 0))
            {
                newMeta = -1;
            }
            int above = getMeta(world, x, y + 1, z);
            if(above >= 0)
            {
                newMeta = (above >= 8) ? above : above + 8;
            }
            if((_sources >= 2) && WPlugin.infinite(_isLava, isHell) && (isSolid(world.getBlockTypeIdAt(x, y - 1, z), true) || (isSameMaterial(world, x, y - 1, z) && (getMeta(world, x, y - 1, z) == 0))))
            {
                newMeta = 0;
            }
            if(WPlugin.decay(_isLava, isHell) || (meta >= 8) || (newMeta >= 8) || (newMeta < meta) || (rand.nextInt(4) == 0))
            {
                if(meta == newMeta)
                {
                    makeStationary(world, x, y, z);
                }
                else
                {
                    meta = newMeta;
                    if(meta < 0)
                    {
                        block.setTypeIdAndData(0, (byte)0, true);
                    }
                    else
                    {
                        block.setData((byte)meta, false);
                        _methods[1].invoke(mcWorld, x, y, z, _blockID, WPlugin.tick(_isLava, isHell));
                        _methods[2].invoke(mcWorld, x, y, z, _blockID);
                    }
                }
            }
        }
        else
        {
            makeStationary(world, x, y, z);
        }
        if(canDisplace(world, x, y - 1, z))
        {
            BlockFromToEvent event = new BlockFromToEvent(block, BlockFace.DOWN);
            Bukkit.getPluginManager().callEvent(event);
            if(!event.isCancelled() || !WPlugin.allowCancel())
            {
                if(_isLava && isMaterial(world, x, y, z, false))
                {
                    world.getBlockAt(x, y - 1, z).setTypeIdAndData(Material.STONE.getId(), (byte)0, true);
                    fizz(mcWorld, x, y, z);
                    return;
                }
                flow(mcWorld, world, x, y - 1, z, meta + ((meta >= 8) ? 0 : 8));
            }
        }
        else if((meta == 0) || ((meta > 0) && isSolid(world, x, y - 1, z)))
        {
            boolean[] allow = getOptimalFlowDirections(world, x, y, z);
            int newMeta = (meta >= 8) ? 1 : (meta + decay);
            if(newMeta >= 8)
            {
                return;
            }
            for(int i = 0; i < _faces.length; i++)
            {
                if(allow[i])
                {
                    BlockFromToEvent event = new BlockFromToEvent(block, _faces[i]);
                    Bukkit.getPluginManager().callEvent(event);
                    if(!event.isCancelled() || !WPlugin.allowCancel())
                    {
                        flow(mcWorld, world, x + _faces[i].getModX(), y, z + _faces[i].getModZ(), newMeta);
                    }
                }
            }
        }
    }
    
    private boolean isSameMaterial(World world, int x, int y, int z)
    {
        return isMaterial(world, x, y, z, _isLava);
    }
    
    private boolean isMaterial(World world, int x, int y, int z, boolean flag)
    {
        int i = world.getBlockTypeIdAt(x, y, z);
        return flag ? ((i == _idLava) || (i == _idLava + 1)) : ((i == _idWater) || (i == _idWater + 1));
    }
    
    private int getMeta(World world, int x, int y, int z)
    {
        return isSameMaterial(world, x, y, z) ? (int)world.getBlockAt(x, y, z).getData() : -1;
    }
    
    private int getSmallestFlowDecay(World world, int x, int y, int z, int last)
    {
        int meta = getMeta(world, x, y, z);
        if(meta < 0)
        {
            return last;
        }
        else
        {
            if(meta == 0)
            {
                _sources++;
            }
            if(meta >= 8)
            {
                meta = 0;
            }
            return ((last >= 0) && (meta >= last)) ? last : meta;
        }
    }
    
    private void makeStationary(World world, int x, int y, int z)
    {
        world.getBlockAt(x, y, z).setTypeId(_blockID + 1, false);
    }
    
    private void flow(Object mcWorld, World world, int x, int y, int z, int meta) throws Throwable
    {
        if(canDisplace(world, x, y, z))
        {
            Block block = world.getBlockAt(x, y, z);
            int id = block.getTypeId();
            if(id > 0)
            {
                SweepAwayEvent event = new SweepAwayEvent(block, _isLava ? SweepAwayEvent.FluidType.LAVA : SweepAwayEvent.FluidType.WATER);
                Bukkit.getPluginManager().callEvent(event);
                if(event.isCancelled() && WPlugin.allowCancel())
                {
                    return;
                }
                if(_isLava)
                {
                    fizz(mcWorld, x, y, z);
                }
                else
                {
                    block.breakNaturally();
                }
            }
            block.setTypeIdAndData(_blockID, (byte)meta, true);
        }
    }
    
    private boolean canDisplace(World world, int x, int y, int z) throws Throwable
    {
        return (isSameMaterial(world, x, y, z) || isMaterial(world, x, y, z, true)) ? false : !isSolid(world, x, y, z);
    }
    
    private boolean isSolid(World world, int x, int y, int z) throws Throwable
    {
        Block block = world.getBlockAt(x, y, z);
        int id = block.getTypeId();
        int meta = (int)block.getData();
        if(WPlugin.check(id, meta, true, _isLava))
        {
            return true;
        }
        if(WPlugin.check(id, meta, false, _isLava))
        {
            return false;
        }
        for(int proof : _proof)
        {
            if(id == proof)
            {
                return true;
            }
        }
        if(id == 0)
        {
            return false;
        }
        return isSolid(id, false);
    }
    
    static boolean isSolid(int id, boolean flag) throws Throwable
    {
        Object o = ((Object[])_fields[0].get(null))[id];
        return (o == null) ? false : ((Boolean)_methods[flag ? 3 : 4].invoke(_fields[1].get(o)));
    }
    
    void fizz(Object mcWorld, int x, int y, int z) throws Throwable
    {
        _methods[5].invoke(_proxy, mcWorld, x, y, z);
    }
    
    private boolean[] getOptimalFlowDirections(World world, int i, int j, int k) throws Throwable
    {
        int[] resistance = new int[4];
        int l;
        int i1;
        for(l = 0; l < 4; ++l)
        {
            resistance[l] = 1000;
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
            if(!isSolid(world, i1, j, j1) && (!isSameMaterial(world, i1, j, j1) || ((int)world.getBlockAt(i1, j, j1).getData() != 0)))
            {
                if(isSolid(world, i1, j - 1, j1))
                {
                    resistance[l] = getResistance(world, i1, j, j1, 1, l);
                }
                else
                {
                    resistance[l] = 0;
                }
            }
        }
        l = resistance[0];
        for(i1 = 1; i1 < 4; ++i1)
        {
            if(resistance[i1] < l)
            {
                l = resistance[i1];
            }
        }
        boolean[] allow = new boolean[4];
        for(i1 = 0; i1 < 4; ++i1)
        {
            allow[i1] = resistance[i1] == l;
        }
        return allow;
    }
    
    private int getResistance(World world, int i, int j, int k, int l, int i1) throws Throwable
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
                if(!isSolid(world, l1, j, i2) && (!isSameMaterial(world, l1, j, i2) || ((int)world.getBlockAt(l1, j, i2).getData() != 0)))
                {
                    if(!isSolid(world, l1, j - 1, i2))
                    {
                        return l;
                    }
                    if(l < 4)
                    {
                        int j2 = getResistance(world, l1, j, i2, l + 1, k1);
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
}