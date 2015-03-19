package dk.mrspring.toggle.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.IBlockAccess;

/**
 * Created by Konrad on 04-03-2015.
 */
public class BlockRenderTester extends Block
{
    public static int renderId;

    public BlockRenderTester(Material p_i45394_1_)
    {
        super(p_i45394_1_);

        this.setBlockName("render_tester");
        this.setBlockTextureName("tb:render_tester");
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_, int p_149646_5_)
    {
        return true;
    }

    @Override
    public int getRenderType()
    {
        return renderId;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }
}
