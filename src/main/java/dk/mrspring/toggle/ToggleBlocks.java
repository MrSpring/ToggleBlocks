package dk.mrspring.toggle;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import dk.mrspring.toggle.common.CommonProxy;

/**
 * Created on 10-11-2015 for ToggleBlocks.
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class ToggleBlocks
{
    @Mod.Instance(ModInfo.MOD_ID)
    public static ToggleBlocks instance;

    @SidedProxy(serverSide = "dk.mrspring.toggle.common.CommonProxy", clientSide = "dk.mrspring.toggle.client.ClientProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }
}
