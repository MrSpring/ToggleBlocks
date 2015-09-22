package dk.mrspring.toggle.block;

import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created on 22-09-2015 for ToggleBlocks.
 */
public class ControllerInfo
{
    public int x, y, z;
    public boolean initialized = false;

    public ControllerInfo(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.initialized = true;
    }

    public ControllerInfo(NBTTagCompound compound)
    {
        if (compound != null)
        {
            NBTTagCompound reading = compound;
            if (compound.hasKey("ControllerInfo", 10)) reading = compound.getCompoundTag("ControllerInfo");
            if (reading.hasKey("X", 3) && reading.hasKey("Y", 3) && reading.hasKey("Z", 3))
            {
                this.x = reading.getInteger("X");
                this.y = reading.getInteger("Y");
                this.z = reading.getInteger("Z");
                initialized = true;
            }
        }
    }

    public ControllerInfo(ItemStack stack)
    {
        this(stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound());
    }

    public ControllerInfo(TileEntityChangeBlock changeBlock)
    {
        this(changeBlock.getCx(), changeBlock.getCy(), changeBlock.getCz());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof ControllerInfo)
        {
            ControllerInfo that = (ControllerInfo) obj;
            return this.initialized && that.initialized &&
                    this.x == that.x &&
                    this.y == that.y &&
                    this.z == that.z;
        }
        return super.equals(obj);
    }

    @Override
    public String toString()
    {
        return "x:" + x + ",y:" + y + ",z:" + z;
    }
}
