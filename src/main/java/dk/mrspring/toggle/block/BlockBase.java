package dk.mrspring.toggle.block;

import dk.mrspring.toggle.item.ItemBlockChangeBlock;
import dk.mrspring.toggle.item.ItemBlockToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Konrad on 27-02-2015.
 */
public class BlockBase
{
    public static final BlockToggleController toggle_controller = new BlockToggleController();
    public static final BlockChangeBlock change_block = new BlockChangeBlock();

    public static void registerBlocks()
    {
        GameRegistry.registerBlock(toggle_controller, ItemBlockToggleController.class, "toggle_block");
        GameRegistry.registerBlock(change_block, ItemBlockChangeBlock.class, "change_block");

        GameRegistry.registerTileEntity(TileEntityToggleBlock.class, "tileEntityToggleBlock");
        GameRegistry.registerTileEntity(TileEntityChangeBlock.class, "tileEntityChangeBlock");
    }
}
