// Bukkit Plugin "WaterProof" by Siguza
// The license under which this software is released can be accessed at:
// http://creativecommons.org/licenses/by-nc-sa/3.0/

package net.drgnome.waterproof.inject;

import java.lang.reflect.*;
//import net.minecraft.server.v1_5_R3.*;
import org.bukkit.block.Block;
import org.bukkit.Material;
import org.bukkit.World;
import net.drgnome.nbtlib.*;
import net.drgnome.waterproof.*;

public class CustomBucket implements MethodFilter, InvocationHandler
{
    //private static final int[] _ids = {70, 71};
    private static final int[] _id = {Material.BUCKET.getId(), Material.WATER_BUCKET.getId(), Material.LAVA_BUCKET.getId()};
    private static final Class[] _classes = new Class[1];
    private static final Method[] _methods = new Method[1];
    private final boolean _isLava;
    private final int _carried;
    
    static
    {
        try
        {
            _classes[0] = NBTLib.getMinecraftClass("ItemBucket");
            _methods[0] = NBTLib.getMethod(NBTLib.getMinecraftClass("World"), "getWorld");
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            WPlugin.instance().getPluginLoader().disablePlugin(WPlugin.instance());
        }
    }
    
    public static void inject()
    {
        CustomBucket water = new CustomBucket(false);
        CustomBucket lava = new CustomBucket(true);
        try
        {
            Method mGet = NBTLib.getMethod(NBTLib.getMinecraftClass("IRegistry"), Object.class, Object.class); // Derpnote
            Method mPut = NBTLib.getMethod(NBTLib.getMinecraftClass("IRegistry"), void.class, Object.class, Object.class); // Derpnote
            Object[] array = (Object[])NBTLib.fetchMinecraftField("Item", null, "byId");
            Object dWater = array[_id[1]];
            Object dLava = array[_id[2]];
            array[_id[1]] = array[_id[2]] = null;
            ClassProxy.newInstance(_classes[0], water, water, new Class[]{int.class, int.class}, _id[1] - 256, CustomFluid._idWater);
            ClassProxy.newInstance(_classes[0], lava, lava, new Class[]{int.class, int.class}, _id[2] - 256, CustomFluid._idLava);
            if(array[_id[1]].getClass().getDeclaredMethods().length != 2)
            {
                String s = "[WaterProof] Item proxy class has the wrong amount of methods (" + array[_id[1]].getClass().getDeclaredMethods().length + ")! Methods:";
                for(Method m : array[_id[1]].getClass().getDeclaredMethods())
                {
                    s += " " + m.getName() + "(),";
                }
                throw new AssertionError(s);
            }
            NBTLib.putMinecraftField("Item", array[_id[1]], "name", "bucketWater");
            NBTLib.putMinecraftField("Item", array[_id[2]], "name", "bucketLava");
            NBTLib.putMinecraftField("Item", array[_id[1]], "craftingResult", array[_id[0]]);
            NBTLib.putMinecraftField("Item", array[_id[2]], "craftingResult", array[_id[0]]);
            Object registry = NBTLib.fetchDynamicMinecraftField("BlockDispenser", null, NBTLib.getMinecraftClass("IRegistry"));
            mPut.invoke(registry, array[_id[1]], mGet.invoke(registry, dWater));
            mPut.invoke(registry, array[_id[2]], mGet.invoke(registry, dLava));
            //NBTLib.getMinecraftClass("");
            /*Class[] paramTypes = new Class[]{int.class, NBTLib.getMinecraftClass("Material")};
            Object[] array = (Object[])_fields[0].get(null);
            array[_idWater] = array[_idLava] = null;
            ClassProxy.newInstance(_classes[0], water, water, paramTypes, _idWater, NBTLib.fetchMinecraftField("Material", null, "WATER"));
            ClassProxy.newInstance(_classes[0], lava, lava, paramTypes, _idLava, NBTLib.fetchMinecraftField("Material", null, "LAVA"));*/
            /*NBTLib.putMinecraftField("Block", array[_idWater], "name", "water");
            NBTLib.putMinecraftField("Block", array[_idLava], "name", "lava");
            NBTLib.putMinecraftField("Block", array[_idWater], "strength", 100F);
            NBTLib.putMinecraftField("Block", array[_idLava], "strength", 0F);
            NBTLib.putMinecraftField("Block", array[_idWater], "durability", 500F);
            NBTLib.putMinecraftField("Block", array[_idLava], "durability", 0F);
            ((int[])NBTLib.fetchMinecraftField("Block", null, "lightBlock"))[_idWater] = 3;
            ((int[])NBTLib.fetchMinecraftField("Block", null, "lightEmission"))[_idLava] = 15;
            NBTLib.putMinecraftField("Block", array[_idWater], "cD", false); // Derpnote
            NBTLib.putMinecraftField("Block", array[_idLava], "cD", false); // Derpnote*/
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
    
    public CustomBucket(boolean isLava)
    {
        _isLava = isLava;
        _carried = _isLava ? CustomFluid._idLava : CustomFluid._idWater;
        /*super(isLava ? _ids[1] : _ids[0], isLava ? Block.LAVA.id : Block.WATER.id);
        _carried = isLava ? Block.LAVA.id : Block.WATER.id;
        b(isLava ? "bucketLava" : "bucketWater");
        a(BUCKET);
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
        }*/
    }
    
    /*public static void inject()
    {
        try
        {
            
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        Item.byId[_ids[0] + 256] = Item.byId[_ids[1] + 256] = null;
        new ItemCustomBucket(false);
        new ItemCustomBucket(true);
    }
    
    public ItemCustomBucket(boolean isLava)
    {
        super(isLava ? _ids[1] : _ids[0], isLava ? Block.LAVA.id : Block.WATER.id);
        _carried = isLava ? Block.LAVA.id : Block.WATER.id;
        b(isLava ? "bucketLava" : "bucketWater");
        a(BUCKET);
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
    }*/
    
    public boolean filterMethod(Method m)
    {
        return (m.getDeclaringClass() == _classes[0]) && (m.getReturnType() == boolean.class);
    }
    
    public Object invoke(Object block, Method method, Object[] args) throws Throwable
    {
        try
        {
            return click(args[0], (Integer)args[4], (Integer)args[5], (Integer)args[6]);
        }
        catch(InvocationTargetException e)
        {
            throw e.getCause();
        }
        /*try
        {
            if(method.getParameterTypes().length == 1)
            {
                return WPlugin.tick(_isLava, ((World)_methods[0].invoke(args[0])).getEnvironment() == World.Environment.NETHER);
            }
            else
            {
                update(args[0], (Integer)args[1], (Integer)args[2], (Integer)args[3], (Random)args[4], block);
            }
        }
        catch(InvocationTargetException e)
        {
            throw e.getCause();
        }
        return null;*/
    }
    
    private boolean click(Object mcWorld, int x, int y, int z) throws Throwable
    {
        World world = (World)_methods[0].invoke(mcWorld);
        if(isSolid(world, x, y, z))
        {
            return false;
        }
        if((world.getEnvironment() == World.Environment.NETHER) && !_isLava && !WPlugin.waterInNether())
        {
            (_isLava ? CustomFluid._iLava : CustomFluid._iWater).fizz(mcWorld, x, y, z);
        }
        else
        {
            world.getBlockAt(x, y, z).setTypeIdAndData(_carried, (byte)0, true);
        }
        return true;
    }
    
    /*public boolean a(World world, double x, double y, double z, int i, int j, int k)
    {
        if(solidBlock(world, i, j, k))
        {
            return false;
        }
        if((world.worldProvider.e) && !_isLava && !Config.bool("allow-water-in-nether"))
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
    }*/
    
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
        if(id == 0)
        {
            return false;
        }
        return CustomFluid.isSolid(id, false);
    }
    
    /*private boolean solidBlock(World world, int i, int j, int k)
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
    }*/
}