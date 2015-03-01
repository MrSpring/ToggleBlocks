package dk.mrspring.toggle;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.tileentity.MessageSetMode;
import dk.mrspring.toggle.tileentity.MessageSetOverride;

/**
 * Created by Konrad on 27-02-2015.
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class ToggleBlocks
{
    public static SimpleNetworkWrapper network;

    @Mod.Instance(ModInfo.MOD_ID)
    public static ToggleBlocks instance;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("toggleBlocks");
        network.registerMessage(MessageSetMode.MessageHandler.class, MessageSetMode.class, 0, Side.SERVER);
        network.registerMessage(MessageSetOverride.MessageHandler.class, MessageSetOverride.class, 1, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());
        BlockBase.registerBlocks();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {

    }
}
