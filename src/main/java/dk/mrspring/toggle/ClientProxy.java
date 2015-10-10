package dk.mrspring.toggle;

/**
 * Created by Konrad on 04-03-2015.
 */
public class ClientProxy extends CommonProxy
{
    @Override
    public void registerRenderer()
    {
        super.registerRenderer();

//        BlockToggleController.renderId = RenderingRegistry.getNextAvailableRenderId();
//        BlockChangeBlock.renderId = RenderingRegistry.getNextAvailableRenderId();

//        RenderingRegistry.registerBlockHandler(BlockBase.toggle_controller.getRenderType(), new BlockToggleControllerRenderer());
//        RenderingRegistry.registerBlockHandler(BlockBase.change_block.getRenderType(), new BlockChangeBlockRenderer());
//        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(BlockBase.change_block), new ItemBlockChangeBlockRenderer()); TODO: Re-implement
    }
}
