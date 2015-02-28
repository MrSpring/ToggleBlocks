package dk.mrspring.toggle.block;

import cpw.mods.fml.common.registry.GameRegistry;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.Block;

/**
 * Created by Konrad on 27-02-2015.
 */
public class BlockBase
{
    public static final Block toggle_block = new BlockToggleBlock();
    public static final Block change_block = new BlockChangeBlock();

    public static void registerBlocks()
    {
        GameRegistry.registerBlock(toggle_block, "toggle_block");
        GameRegistry.registerBlock(change_block, "change_block");
        GameRegistry.registerTileEntity(TileEntityToggleBlock.class, "tileEntityToggleBlock");
        GameRegistry.registerTileEntity(TileEntityChangeBlock.class, "tileEntityChangeBlock");
    }
}
