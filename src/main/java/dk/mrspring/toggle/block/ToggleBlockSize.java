package dk.mrspring.toggle.block;

/**
 * Created on 10-10-2015 for ToggleBlocks18.
 */
public enum ToggleBlockSize
{
    TINY(3, 5),
    SMALL(0, 15),
    MEDIUM(1, 30),
    LARGE(2, 50),
    HUGE(4, 100),
    CREATIVE(5, -1);

    public static final ToggleBlockSize[] META_MAPPED = new ToggleBlockSize[]{
            SMALL,
            MEDIUM,
            LARGE,
            TINY,
            HUGE,
            CREATIVE
    };

    private final int META, SIZE;

    ToggleBlockSize(int meta, int size)
    {
        this.META = meta;
        this.SIZE = size;
    }

    public int getMetaValue()
    {
        return META;
    }

    public int getControllerSize()
    {
        return SIZE;
    }

    public String getName()
    {
        return name().toLowerCase();
    }

    public static ToggleBlockSize fromName(String name)
    {
        return valueOf(name.toUpperCase());
    }
}
