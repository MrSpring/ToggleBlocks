package dk.mrspring.toggle.common;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.common.block.BlockBase;
import dk.mrspring.toggle.common.message.MessageSetMode;
import dk.mrspring.toggle.common.message.MessageSetOverride;
import dk.mrspring.toggle.common.message.MessageSetStoragePriority;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class CommonProxy
{
    CommonEventHandler commonEventHandler;
    SimpleNetworkWrapper network;

    public void preInit(FMLPreInitializationEvent event)
    {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("toggleBlocks");
        network.registerMessage(MessageSetMode.MessageHandler.class, MessageSetMode.class, 0, Side.SERVER);
        network.registerMessage(MessageSetOverride.MessageHandler.class, MessageSetOverride.class, 1, Side.SERVER);
        network.registerMessage(MessageSetStoragePriority.MessageHandler.class, MessageSetStoragePriority.class, 2, Side.SERVER);

        ToggleBlocks.network = network;

        NetworkRegistry.INSTANCE.registerGuiHandler(ToggleBlocks.instance, new GuiHandler());

        commonEventHandler = new CommonEventHandler();
        MinecraftForge.EVENT_BUS.register(commonEventHandler);
        BlockBase.registerBlocks();
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event)
    {
    }
}
