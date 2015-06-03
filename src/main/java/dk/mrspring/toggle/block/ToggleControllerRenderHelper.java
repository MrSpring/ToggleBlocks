package dk.mrspring.toggle.block;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

/**
 * Created by Konrad on 03-06-2015.
 */
public class ToggleControllerRenderHelper
{
    public static boolean connect(Block block)
    {
        return block.isOpaqueCube() || block == Blocks.chest;
    }
}
