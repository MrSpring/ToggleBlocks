package dk.mrspring.toggle.common.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public class ItemBlockToggleController extends ItemBlock
{
    public ItemBlockToggleController(Block block)
    {
        super(block);
    }

    @Override
    public int getMetadata(int damage)
    {
        return damage;
    }
}
