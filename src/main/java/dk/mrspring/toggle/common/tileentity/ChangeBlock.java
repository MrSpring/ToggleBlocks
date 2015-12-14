package dk.mrspring.toggle.common.tileentity;

import dk.mrspring.toggle.api.IToggleController;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class ChangeBlock
{
    BlockPos pos;
    StateOverride[] overrides;

    public ChangeBlock(BlockPos pos, IToggleController controller)
    {
        this.pos = pos;
        this.overrides = new StateOverride[controller.getStateCount()];
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        // TODO: Write, comp. with 1.7
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        // TODO: Read, comp. with 1.7
    }

    class StateOverride
    {
        ItemStack stack;
        boolean overrides;

        StateOverride(ItemStack stack, boolean overrides)
        {
            this.stack = stack;
            this.overrides = overrides;
        }
    }
}
