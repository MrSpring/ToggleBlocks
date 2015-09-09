package dk.mrspring.toggle.api;

import dk.mrspring.toggle.tileentity.ChangeBlockInfo;
import net.minecraft.item.ItemStack;

/**
 * Created by Konrad on 01-03-2015.
 */
public interface IToggleController
{
    IChangeBlockInfo getChangeBlockInfo(int index);

    IChangeBlockInfo[] getChangeBlocks();

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
    ChangeBlockInfo registerChangeBlock(int x, int y, int z);

    /**
     * Removes the Change Block from the controller, if the change block can be found at the specified coordinates.
     *
     * @param x The X coordinate of the Change Block that should be unregistered.
     * @param y The Y coordinate of the Change Block that should be unregistered.
     * @param z The Z coordinate of the Change Block that should be unregistered.
     * @return If the Change Block was found, its information will be returned. Null if nothing was found at the
     * specified coordinates.
     */
    ChangeBlockInfo unregisterChangeBlock(int x, int y, int z);

    /**
     * @return Returns the controllers current state.
     */
    int getState();

    int getMaxChangeBlocks();

    int getRegisteredChangeBlockCount();

    IToggleStorage getStorageHandler();

    /**
     * @return Returns the x coordinate of the toggle controller.
     */
    int x();

    /**
     * @return Returns the y coordinate of the toggle controller.
     */
    int y();

    /**
     * @return Returns the z coordinate of the toggle controller.
     */
    int z();

    boolean canRegisterAnotherChangeBlock();

    /**
     * Called when a Change Block is destroyed.
     *
     * @param changeBlockX The x coordinate of the destroyed change block.
     * @param changeBlockY The y coordinate of the destroyed change block.
     * @param changeBlockZ The z coordinate of the destroyed change block.
     * @return Returns the ItemStacks that drops from the Change Block
     */
    ItemStack[] createChangeBlockDrop(int changeBlockX, int changeBlockY, int changeBlockZ);
}
