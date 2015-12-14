package dk.mrspring.toggle.client;

import dk.mrspring.toggle.ModInfo;
import dk.mrspring.toggle.client.model.ToggleBlockModelLoader;
import dk.mrspring.toggle.client.model.ToggleBlockStateMapper;
import dk.mrspring.toggle.common.CommonProxy;
import dk.mrspring.toggle.common.block.BlockBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static dk.mrspring.toggle.common.block.ToggleBlockSize.*;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        ModelLoader.setCustomStateMapper(BlockBase.toggle_controller, new ToggleBlockStateMapper());
        ModelLoaderRegistry.registerLoader(new ToggleBlockModelLoader());
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();
        Item cb = Item.getItemFromBlock(BlockBase.change_block), tb = Item.getItemFromBlock(BlockBase.toggle_controller);
        mesher.register(cb, 0, new ModelResourceLocation(ModInfo.MOD_ID + ":change_block", "inventory"));

        mesher.register(tb, TINY.getMetaValue(), new ModelResourceLocation(ModInfo.MOD_ID + ":toggle_block", "size=tiny"));
        mesher.register(tb, SMALL.getMetaValue(), new ModelResourceLocation(ModInfo.MOD_ID + ":toggle_block", "size=small"));
        mesher.register(tb, MEDIUM.getMetaValue(), new ModelResourceLocation(ModInfo.MOD_ID + ":toggle_block", "size=medium"));
        mesher.register(tb, LARGE.getMetaValue(), new ModelResourceLocation(ModInfo.MOD_ID + ":toggle_block", "size=large"));
        mesher.register(tb, HUGE.getMetaValue(), new ModelResourceLocation(ModInfo.MOD_ID + ":toggle_block", "size=huge"));
        mesher.register(tb, CREATIVE.getMetaValue(), new ModelResourceLocation(ModInfo.MOD_ID + ":toggle_block", "size=creative"));
    }
}
