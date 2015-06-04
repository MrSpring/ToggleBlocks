package dk.mrspring.toggle;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.tileentity.MessageSetMode;
import dk.mrspring.toggle.tileentity.MessageSetOverride;
import dk.mrspring.toggle.tileentity.MessageSetStoragePriority;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;

/**
 * Created by Konrad on 27-02-2015.
 */
@Mod(modid = ModInfo.MOD_ID, name = ModInfo.NAME, version = ModInfo.VERSION)
public class ToggleBlocks
{
    public static SimpleNetworkWrapper network;

    @Mod.Instance(ModInfo.MOD_ID)
    public static ToggleBlocks instance;

    @SidedProxy(clientSide = "dk.mrspring.toggle.ClientProxy", serverSide = "dk.mrspring.toggle.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void preInit(FMLPreInitializationEvent event)
    {
        ToggleRegistry.initialize();
        ToggleRegistry.registerVanilla();

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
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        proxy.registerRenderer();
        FMLInterModComms.sendRuntimeMessage(ModInfo.MOD_ID, "VersionChecker", "addVersionCheck", "http://mrspring.dk/mods/tb/versions.json");

        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockBase.toggle_controller, 1, 0), "IRI", "RBR", "IRI", 'R', "dustRedstone", 'I', "blockIron", 'B', "blockRedstone"));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockBase.toggle_controller, 1, 1), "GRG", "BBB", "GRG", 'R', "dustRedstone", 'G', "blockGold", 'B', Items.blaze_rod));
        GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BlockBase.toggle_controller, 1, 2), "DRD", "PNP", "DRD", 'R', "blockRedstone", 'D', "blockDiamond", 'P', Items.ender_pearl, 'N', Items.nether_star));
    }
}
