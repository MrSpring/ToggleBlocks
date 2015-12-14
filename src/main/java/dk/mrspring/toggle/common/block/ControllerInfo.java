package dk.mrspring.toggle.common.block;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;

/**
 * Created on 22-09-2015 for ToggleBlocks.
 */
public class ControllerInfo
{
    public BlockPos pos;
    public boolean initialized = false;

    public ControllerInfo(BlockPos pos)
    {
        this.pos = pos;
        this.initialized = pos != null;
    }

    public ControllerInfo(NBTTagCompound compound)
    {
        if (compound != null)
        {
            NBTTagCompound reading = compound;
            if (compound.hasKey("ControllerInfo", 10)) reading = compound.getCompoundTag("ControllerInfo");
            if (reading.hasKey("X", 3) && reading.hasKey("Y", 3) && reading.hasKey("Z", 3))
            {
                int x = reading.getInteger("X");
                int y = reading.getInteger("Y");
                int z = reading.getInteger("Z");
                this.pos = new BlockPos(x, y, z);
                initialized = true;
            }
        }
    }

    public ControllerInfo(ItemStack stack)
    {
        this(stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ControllerInfo)
        {
            ControllerInfo that = (ControllerInfo) obj;
            return this.initialized && that.initialized &&
                    this.pos.equals(that.pos);
        }
        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return "x:" + pos.getX() + ",y:" + pos.getY() + ",z:" + pos.getZ();
    }
}
