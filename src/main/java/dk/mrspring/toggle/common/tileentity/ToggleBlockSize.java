package dk.mrspring.toggle.common.tileentity;

/**
 * Created on 10-11-2015 for ToggleBlocks.
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

    public static ToggleBlockSize fromMeta(int metadata)
    {
        return metadata >= 0 && metadata < META_MAPPED.length ? META_MAPPED[metadata] : SMALL;
    }
}
