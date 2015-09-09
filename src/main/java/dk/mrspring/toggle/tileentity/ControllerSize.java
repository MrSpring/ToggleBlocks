package dk.mrspring.toggle.tileentity;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Konrad on 07-09-2015.
 */
public class ControllerSize
{
    public static final String MAX_CHANGE_BLOCKS = "MaxBlocks";
    public static final String STACK_SIZE = "StackSize";

    public final int size, stackSize;

    public ControllerSize(int size, int stackSize)
    {
        this.size = size;
        this.stackSize = stackSize;
    }

    public ControllerSize(NBTTagCompound compound)
    {
        this.size = compound.getInteger(MAX_CHANGE_BLOCKS);
        this.stackSize = compound.getInteger(STACK_SIZE);
    }

    public ControllerSize(int size)
    {
        this(size, size);
    }

    public NBTTagCompound writeToNBT(NBTTagCompound compound)
    {
        compound.setInteger(MAX_CHANGE_BLOCKS, size);
        compound.setInteger(STACK_SIZE, stackSize);
        return compound;
    }

    @Override
    public String toString()
    {
        return "cs-" + size + ":" + stackSize;
    }
}
