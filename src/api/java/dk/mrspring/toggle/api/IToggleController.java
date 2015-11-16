package dk.mrspring.toggle.api;

import net.minecraft.item.ItemStack;

import java.util.List;

/**
 * Created by Konrad on 01-03-2015.
 */
public interface IToggleController
{
    IChangeBlockInfo getChangeBlockInfo(int index);

    List<IChangeBlockInfo> getChangeBlocks();

    /**
     * Registers the Change Block at the specified coordinates. If a Change Block was found and it contains old
     * information, the controller will re-use these old things.
     *
     * @param changeBlock The change block to register.
     * @return Returns true if the change block was successfully registered. False if not.
     */
    boolean registerChangeBlock(IChangeBlockInfo changeBlock);

    /**
     * Removes the Change Block from the controller, if the change block can be found at the specified coordinates.
     *
     * @param changeBlock The change block to unregister.
     * @return Return true if the change block was successfully unregistered. False if not.
     */
    boolean unregisterChangeBlock(IChangeBlockInfo changeBlock);

    /**
     * @return Returns the controllers current state.
     */
    int getState();

    int getMaxChangeBlocks();

    int getRegisteredChangeBlockCount();

    IToggleStorage getStorageHandler();

    boolean canRegisterAnotherChangeBlock();

    /**
     * Called when a Change Block is destroyed and when the Toggle Controller is placed.
     *
     * @return Returns the ItemStacks that drops
     */
    ItemStack[] createChangeBlockDrop();

    /**
     * Called when the Toggle Controller is broken
     */
    void resetAllChangeBlocks();
}
