package dk.mrspring.toggle.api;

import net.minecraft.util.EnumFacing;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public interface IChangeBlock
{
    /**
     * Called after the Change Block is placed, and is accepted by the Toggle Block.
     *
     * @param controller The controller that the Change Block is registered to.
     */
    void onRegistered(IToggleController controller);

    /**
     * Called when the Change Block is destroyed, after it has been removed from the controller.
     *
     * @param controller The controller which this Change Block was registered to.
     */
    void onUnregistered(IToggleController controller);

    /**
     * Called when the Change Block is placed by the Toggle Controller, and when the Toggle Controller reloads all
     * Change Blocks from NBT.
     *
     * @param controller The controller which this Change Block is registered to.
     */
    void onControllerReload(IToggleController controller);

    IToggleController getController();

    EnumFacing getDirection();
}
