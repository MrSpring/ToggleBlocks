package dk.mrspring.toggle;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import dk.mrspring.toggle.block.BlockBase;

/**
 * Created by Konrad on 27-02-2015.
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class ToggleBlocks
{
    @Mod.Instance(ModInfo.MOD_ID)
    public static ToggleBlocks instance;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        BlockBase.registerBlocks();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {

    }
}
