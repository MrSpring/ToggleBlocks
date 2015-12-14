package dk.mrspring.toggle.common;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import dk.mrspring.toggle.common.block.BlockBase;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        BlockBase.registerBlocks();
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
