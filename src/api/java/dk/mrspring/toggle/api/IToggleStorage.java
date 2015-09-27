package dk.mrspring.toggle.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created by Konrad on 15-04-2015.
 */
public interface IToggleStorage
{
    void validateStorage();

    ItemStack[] addItemStacksToStorage(ItemStack[] stacks);

    ItemStack addItemStackToStorage(ItemStack stack);

    StoragePriority getStoragePriority();

//    ItemStack removeStackFromStorage(ItemStack stack);

//    ItemStack[] removeAllStacksFromStorage(ItemStack stack);

    /**
     * @param item A matching {@link ItemStack} for the requested item. Uses
     *             {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} to find requested item.
     * @return Returns the item if it could be found. Null if nothing was found.
     */
    ItemStack getItemFromStorage(ItemStack item);

    ItemStack getToolFromStorage(String toolType);

    int getStorageSlots();

    ItemStack getItemFromSlot(int slot);

    void setItemInSlot(int slot, ItemStack stack);

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);

    void setStoragePriority(StoragePriority priority);
}
