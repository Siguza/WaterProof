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
    
    public static void inject() throws Throwable
    {
        CustomBucket water = new CustomBucket(false);
        CustomBucket lava = new CustomBucket(true);
        Method mGet = NBTLib.getMethod(NBTLib.getMinecraftClass("IRegistry"), Object.class, Object.class); // Derpnote
        Method mPut = NBTLib.getMethod(NBTLib.getMinecraftClass("IRegistry"), void.class, Object.class, Object.class); // Derpnote
        Object[] array = (Object[])NBTLib.fetchMinecraftField("Item", null, "byId");
        Object tmpWater = array[_id[1]];
        Object tmpLava = array[_id[2]];
        array[_id[1]] = array[_id[2]] = null;
        try
        {
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
            mPut.invoke(registry, array[_id[1]], mGet.invoke(registry, tmpWater));
            mPut.invoke(registry, array[_id[2]], mGet.invoke(registry, tmpLava));
        }
        catch(Throwable t)
        {
            array[_id[1]] = tmpWater;
            array[_id[2]] = tmpLava;
            throw t;
        }
    }
    
    public CustomBucket(boolean isLava)
    {
        _isLava = isLava;
        _carried = _isLava ? CustomFluid._idLava : CustomFluid._idWater;
    }
    
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
}