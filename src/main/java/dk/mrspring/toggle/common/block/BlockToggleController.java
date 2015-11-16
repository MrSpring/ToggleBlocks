package dk.mrspring.toggle.common.block;

import dk.mrspring.toggle.common.tileentity.TileEntityToggleController;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created on 10-11-2015 for ToggleBlocks.
 */
public class BlockToggleController extends BlockContainer
{
    protected BlockToggleController()
    {
        super(Material.iron);
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityToggleController();
    }
}
