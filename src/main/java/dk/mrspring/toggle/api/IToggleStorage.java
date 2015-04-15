package dk.mrspring.toggle.api;

import net.minecraft.item.ItemStack;

/**
 * Created by Konrad on 15-04-2015.
 */
public interface IToggleStorage
{
    void validateStorage();

    void addItemStacksToStorage(ItemStack[] stacks);

    void addItemStackToStorage(ItemStack stack);

    StoragePriority getStoragePriority();

    void removeStackFromStorage(ItemStack stack);

    void removeAllStacksFromStorage(ItemStack stack);
}
