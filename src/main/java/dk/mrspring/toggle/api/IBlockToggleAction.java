package dk.mrspring.toggle.api;

import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by Konrad on 01-03-2015.
 */
public interface IBlockToggleAction
{
    public boolean performAction(World world, int x, int y, int z, int direction, EntityPlayer player,
                                 ItemStack placing, IToggleController tileEntityToggleBlock);

    public boolean useWithItem(World world, int x, int y, int z, ItemStack placing, IToggleController tileEntity);
}
