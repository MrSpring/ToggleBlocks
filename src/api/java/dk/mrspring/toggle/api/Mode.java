package dk.mrspring.toggle.api;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public enum Mode
{
    READY,
    EDITING;

    public static Mode fromInt(int id)
    {
        return id >= 0 && id < values().length ? values()[id] : EDITING;
    }
}
