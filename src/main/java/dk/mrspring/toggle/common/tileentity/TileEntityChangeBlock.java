package dk.mrspring.toggle.common.tileentity;

import dk.mrspring.toggle.api.IChangeBlock;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.common.block.ControllerInfo;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class TileEntityChangeBlock extends TileEntity implements IChangeBlock
{
    IToggleController controller;

    public boolean onPlaced(ItemStack stack)
    {
        ControllerInfo info = new ControllerInfo(stack);
        TileEntity tileEntity = worldObj.getTileEntity(info.pos);
        if (!(tileEntity instanceof IToggleController)) return false;
        IToggleController controller = (IToggleController) tileEntity;
        return controller.onChangeBlockPlaced(getPos(), this);
    }

    @Override
    public void onRegistered(IToggleController controller)
    {
        this.controller = controller;
    }

    @Override
    public void onUnregistered(IToggleController controller)
    {

    }

    @Override
    public EnumFacing getDirection()
    {
//        return BlockBase.change_block.getDirectionFromState(worldObj.getBlockState(getPos()));
        return EnumFacing.DOWN;
    }
}
