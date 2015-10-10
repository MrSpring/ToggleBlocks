package dk.mrspring.toggle;

import dk.mrspring.toggle.api.IBlockToggleRegistry;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.tileentity.MessageSetMode;
import dk.mrspring.toggle.tileentity.MessageSetOverride;
import dk.mrspring.toggle.tileentity.MessageSetStoragePriority;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Konrad on 27-02-2015.
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class ToggleBlocks
{
    public static SimpleNetworkWrapper network;
    public static final String REGISTER_API_CALL = "register";

    @Mod.Instance(ModInfo.MOD_ID)
    public static ToggleBlocks instance;

    @SidedProxy(clientSide = "dk.mrspring.toggle.ClientProxy", serverSide = "dk.mrspring.toggle.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        ToggleRegistry.initialize();

        network = NetworkRegistry.INSTANCE.newSimpleChannel("toggleBlocks");
        network.registerMessage(MessageSetMode.MessageHandler.class, MessageSetMode.class, 0, Side.SERVER);
        network.registerMessage(MessageSetOverride.MessageHandler.class, MessageSetOverride.class, 1, Side.SERVER);
        network.registerMessage(MessageSetStoragePriority.MessageHandler.class, MessageSetStoragePriority.class, 2, Side.SERVER);

        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new GuiHandler());

        BlockBase.registerBlocks();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event)
    {
        /*if (Loader.isModLoaded("NotEnoughItems"))
        {
            try
            {
                System.out.println("Registering NEI comp.");
                codechicken.nei.NEIModContainer.plugins.add(new NEIToggleConfig());
            } catch (Exception ignored)
            {
            }
        }*/

        MinecraftForge.EVENT_BUS.register(new EventHandler());
        proxy.registerRenderer();
        FMLInterModComms.sendRuntimeMessage(ModInfo.MOD_ID, "VersionChecker", "addVersionCheck", "http://mrspring.dk/mods/tb/versions.json");
        FMLInterModComms.sendMessage("tb", "register", "dk.mrspring.toggle.comp.vanilla.ToggleRegistryCallback.register");
        FMLInterModComms.sendMessage("Waila", "register", "dk.mrspring.toggle.comp.waila.WailaCompatibility.callbackRegister");
        Recipes.register();
    }

    @Mod.EventHandler
    public void handleIMC(FMLInterModComms.IMCEvent event)
    {
        for (FMLInterModComms.IMCMessage message : event.getMessages())
            if (message != null)
            {
                if (message.isStringMessage() && message.key.equalsIgnoreCase(REGISTER_API_CALL))
                {
                    String messageString = message.getStringValue();
                    try
                    {
                        String[] splitName = messageString.split("\\.");
                        String methodName = splitName[splitName.length - 1];
                        String className = messageString.substring(0, messageString.length() - methodName.length() - 1);
                        System.out.println("Registering: " + className + ", " + methodName);
                        try
                        {
                            Class clasz = Class.forName(className);
                            Method method = clasz.getDeclaredMethod(methodName, IBlockToggleRegistry.class);
                            method.invoke(null, ToggleRegistry.instance());
                        } catch (ClassNotFoundException e)
                        {
                            System.out.println("Class not found.");
                        } catch (NoSuchMethodException e)
                        {
                            System.out.println("Method not found.");
                        } catch (InvocationTargetException e)
                        {
                            System.out.println("Failed to call method.");
                        } catch (IllegalAccessException e)
                        {
                            System.out.println("Illegal access.");
                        }
                    } catch (Exception ignored)
                    {
                    }
                }
            }
    }
}
