package dk.mrspring.toggle.block;

import cpw.mods.fml.common.registry.GameRegistry;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Created by Konrad on 27-02-2015.
 */
public class BlockBase
{
    public static final Block toggle_controller = new BlockToggleController();
    public static final Block change_block = new BlockChangeBlock();
    public static final Block render_tester = new BlockRenderTester(Material.anvil);

    public static void registerBlocks()
    {
        GameRegistry.registerBlock(toggle_controller, "toggle_block");
        GameRegistry.registerBlock(change_block, ItemBlockChangeBlock.class, "change_block");
        GameRegistry.registerBlock(render_tester, "render_tester");
        GameRegistry.registerTileEntity(TileEntityToggleBlock.class, "tileEntityToggleBlock");
        GameRegistry.registerTileEntity(TileEntityChangeBlock.class, "tileEntityChangeBlock");
    }
}
