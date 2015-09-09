package dk.mrspring.toggle.tileentity;

/**
 * Created by Konrad on 07-09-2015.
 */
public class ControllerSize
{
    public final int size, stackSize;

    public ControllerSize(int size, int stackSize)
    {
        this.size = size;
        this.stackSize = stackSize;
    }

    public ControllerSize(int size)
    {
        this(size, size);
    }

    @Override
    public String toString()
    {
        return "cs-" + size + ":" + stackSize;
    }
}
