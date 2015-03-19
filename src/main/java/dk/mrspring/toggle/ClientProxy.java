package dk.mrspring.toggle;

import cpw.mods.fml.client.registry.RenderingRegistry;
import dk.mrspring.toggle.block.BlockBase;
import dk.mrspring.toggle.block.BlockToggleController;
import dk.mrspring.toggle.block.BlockToggleControllerRenderer;

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

        RenderingRegistry.registerBlockHandler(BlockBase.toggle_controller.getRenderType(), new BlockToggleControllerRenderer());
    }
}
