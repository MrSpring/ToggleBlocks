package dk.mrspring.toggle.api;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public enum StoragePriority
{
    STORAGE_FIRST(0),
    STORAGE_ONLY(1),
    CHESTS_FIRST(2),
    CHESTS_ONLY(3);

    public static final StoragePriority[] ALL_PRIORITIES = new StoragePriority[]{
            STORAGE_FIRST,
            STORAGE_ONLY,
            CHESTS_FIRST,
            CHESTS_ONLY
    };

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
        return id >= 0 && id < ALL_PRIORITIES.length ? ALL_PRIORITIES[id] : STORAGE_FIRST;
    }

    public static StoragePriority getNext(StoragePriority priority)
    {
        int idForPriority = priority.getId();
        if (idForPriority + 1 >= values().length)
            return fromInt(0);
        else return fromInt(idForPriority + 1);
    }
}
