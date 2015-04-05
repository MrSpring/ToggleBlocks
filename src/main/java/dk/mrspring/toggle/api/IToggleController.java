package dk.mrspring.toggle.api;

import dk.mrspring.toggle.tileentity.ChangeBlockInfo;
import net.minecraft.item.ItemStack;

/**
 * Created by Konrad on 01-03-2015.
 */
public interface IToggleController
{
    public void addItemStacksToStorage(ItemStack[] stacks);

    public void validateStorage();

    public IChangeBlockInfo getChangeBlockInfo(int index);

    public IChangeBlockInfo[] getChangeBlocks();

    public ItemStack addItemStackToStorage(ItemStack stack);

    /**
     * This removes the item from storage. Remember to put it back, using {@link #addItemStacksToStorage(ItemStack[])}
     * when you're done making modifications to it.
     *
     * @param item A matching {@link ItemStack} for the requested item. Uses
     * {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack)} to find requested item.
     * @return Returns the item if it could be found. Null if nothing was found.
     */
    public ItemStack requestItemFromStorage(ItemStack item);

    public ItemStack requestToolFromStorage(String toolType);

    /**
     * Removes the first found item that matches the one parsed-through (Uses
     * {@link ItemStack#areItemStacksEqual(ItemStack, ItemStack) ItemStack.areItemStacksEqual}).
     *
     * @param stack
     * @return
     */
    public boolean removeItemFromStorage(ItemStack stack);

    /**
     * Spawns the parsed-through ItemStack in the world at the controllers position.
     *
     * @param stack The {@link ItemStack} to drop.
     */
    public void dropItem(ItemStack stack);

    /**
     * Registers the Change Block at the specified coordinates. If a Change Block was found and it contains old
     * information, the controller will re-use these old things.
     *
     * @param x The X coordinate of the Change Block that should be registered.
     * @param y The Y coordinate of the Change Block that should be registered.
     * @param z The Z coordinate of the Change Block that should be registered.
     * @return If the Change Block was successfully registered (If the controller has room for it) the Change Blocks
     * information will be returned. Null if the block was not registered.
     */
    public ChangeBlockInfo registerChangeBlock(int x, int y, int z);

    /**
     * Removes the Change Block from the controller, if the change block can be found at the specified coordinates.
     *
     * @param x The X coordinate of the Change Block that should be unregistered.
     * @param y The Y coordinate of the Change Block that should be unregistered.
     * @param z The Z coordinate of the Change Block that should be unregistered.
     * @return If the Change Block was found, its information will be returned. Null if nothing was found at the
     * specified coordinates.
     */
    public ChangeBlockInfo unregisterChangeBlock(int x, int y, int z);

    /**
     * @return Returns the controllers current state.
     */
    public int getState();

    /**
     * @return Returns a read-only array containing all the toggle controller's, and surrounding chest's, inventory.
     * Once a desired item has been found, use {@link #requestItemFromStorage(ItemStack) requestItemFromStorage} to get
     * the real item.
     */
    public ItemStack[] getAllStorage();
}
