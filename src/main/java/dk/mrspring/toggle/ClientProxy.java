package dk.mrspring.toggle;

import cpw.mods.fml.client.registry.RenderingRegistry;
import dk.mrspring.toggle.block.*;

/**
 * Created by Konrad on 04-03-2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderer()
    {
        super.registerRenderer();

        BlockToggleController.renderId = RenderingRegistry.getNextAvailableRenderId();
        BlockChangeBlock.renderId = RenderingRegistry.getNextAvailableRenderId();

        RenderingRegistry.registerBlockHandler(BlockBase.toggle_controller.getRenderType(), new BlockToggleControllerRenderer());
        RenderingRegistry.registerBlockHandler(BlockBase.change_block.getRenderType(), new BlockChangeBlockRenderer());
    }
}
