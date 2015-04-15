package dk.mrspring.toggle.api;

/**
 * Created by Konrad on 06-04-2015.
 */
public enum StoragePriority
{
    STORAGE_FIRST(0),
    STORAGE_ONLY(1),
    CHESTS_FIRST(2),
    CHESTS_ONLY(3);

    final int id;

    StoragePriority(int id)
    {
        this.id = id;
    }

    public int getId()
    {
        return id;
    }

    public static StoragePriority fromInt(int id)
    {
        for (StoragePriority mode : values())
            if (mode.getId() == id)
                return mode;
        return STORAGE_FIRST;
    }

    public static StoragePriority getNext(StoragePriority priority)
    {
        int idForPriority = priority.getId();
        if (idForPriority + 1 >= values().length)
            return fromInt(0);
        else return fromInt(idForPriority + 1);
    }
}
