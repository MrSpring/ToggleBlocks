package dk.mrspring.toggle.api;

/**
 * Created by Konrad on 06-04-2015.
 */
public enum Mode
{
    READY(0), EDITING(1);

    final int id;

    private Mode(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return this.id;
    }

    public static Mode fromInt(int id)
    {
        for (Mode mode : values())
            if (mode.getId() == id)
                return mode;
        return EDITING;
    }
}
