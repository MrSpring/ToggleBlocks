package dk.mrspring.toggle.api;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Created on 14-12-2015 for ToggleBlocks.
 */
public interface IToggleStorage
{
    void validateStorage();

    /**
     * Adds an array if ItemStacks to the Toggle Blocks storage.
     *
     * @param stacks The ItemStacks to add.
     * @return Returns the ItemStacks that could not fit into the Toggle Blocks storage. An empty array if they all
     * were added.
     */
    ItemStack[] addItemStacksToStorage(ItemStack[] stacks);

    /**
     * Adds a single ItemStack to the Toggle Blocks storage.
     *
     * @param stack The ItemStack to add.
     * @return If the storage could not fit the entire stack into the storage, the remains will be returned. Null if it
     * all fitted.
     */
    ItemStack addItemStackToStorage(ItemStack stack);

    StoragePriority getStoragePriority();

    void setStoragePriority(StoragePriority priotity);

    /**
     * @param item A matching {@link ItemStack} for the requested item. Uses
     *             {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} to find requested item.
     * @return Returns the item if it could be found. Null if nothing was found.
     */
    ItemStack getItemFromStorage(ItemStack item);

    ItemStack getToolFromStorage(String toolType);

    /**
     * @return Returns how many items the storage can hold.
     */
    int getStorageSlots();

    ItemStack getItemFromSlot(int slot);

    void setItemInSlot(int slot, ItemStack stack);

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);
}
