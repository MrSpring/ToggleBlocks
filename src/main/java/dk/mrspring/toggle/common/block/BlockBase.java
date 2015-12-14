package dk.mrspring.toggle.common.block;

import dk.mrspring.toggle.common.item.ItemBlockToggleController;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.fml.common.registry.GameRegistry;
import dk.mrspring.toggle.common.tileentity.*;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class BlockBase
{
    public static final BlockToggleController toggle_controller = new BlockToggleController();
    public static final BlockChangeBlock change_block = new BlockChangeBlock();
    public static final Block tester = new Block(Material.iron)
    {

    }.setUnlocalizedName("render_tester");

    public static void registerBlocks()
    {
        GameRegistry.registerBlock(toggle_controller, ItemBlockToggleController.class, "toggle_block");
        GameRegistry.registerBlock(change_block/*, ItemBlockChangeBlock.class*/, "change_block");
        GameRegistry.registerBlock(tester, "render_tester");

        GameRegistry.registerTileEntity(TileEntityToggleBlock.class, "tileEntityToggleBlock");
        GameRegistry.registerTileEntity(TileEntityChangeBlock.class, "tileEntityChangeBlock");
    }
}
