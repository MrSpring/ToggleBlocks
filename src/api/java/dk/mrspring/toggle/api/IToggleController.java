package dk.mrspring.toggle.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public interface IToggleController
{
    /**
     * Called when a Change Block is placed. This function also call #IChangeBlock.onRegistered(this).
     *
     * @param position The position of the placed Change Block.
     * @param changeBlock The placed Change Block.
     * @return Returns true if the block is allowed to be placed. If this returns false, the block should not be placed!
     */
    boolean onChangeBlockPlaced(BlockPos position, IChangeBlock changeBlock);

    /**
     * Called when a Change Block is destroyed. This function also call #IChangeBlock.onUnregistered(this).
     *
     * @param position The position of the destroyed Change Block.
     * @param changeBlock The destroyed Change Block.
     */
    void onChangeBlockRemoved(BlockPos position, IChangeBlock changeBlock);

    /**
     * Call this when a Change Block is right-clicked. Used to open the individual Change Block override GUI.
     *
     * @param player The player that activated the Change Block.
     * @param position The The position of the Change Block.
     * @param changeBlock The Change Block that was activated.
     */
    void onChangeBlockActivated(EntityPlayer player, BlockPos position, IChangeBlock changeBlock);

    /**
     * Called when the Toggle Controller is activated. Opens the configuration GUI.
     *
     * @param player The player that activated the Toggle Block.
     */
    void onToggleControllerActivated(EntityPlayer player);

    int getState();

    int getStateCount();

    int getMaxChangeBlocks();

    Mode getCurrentMode();

    void setCurrentMode(Mode newMode);

    int getRegisteredChangeBlockCount();

    IToggleStorage getStorageHandler();

    boolean canRegisterAnotherChangeBlock();

    ItemStack[] createChangeBlockDrop();

    void destroyAllChangeBlocks();

    void placeAllChangeBlocks();
}
