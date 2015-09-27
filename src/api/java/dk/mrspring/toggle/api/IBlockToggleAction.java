package dk.mrspring.toggle.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Konrad on 01-03-2015.
 */
public interface IBlockToggleAction
{
    /**
     * Harvests the block before replacing it with the new one.
     *
     * @param world      World object
     * @param x          The X coordinate of the block to harvest
     * @param y          The Y coordinate of the block to harvest
     * @param z          The Z coordinate of the block to harvest
     * @param player     The player. WARNING! This is a fake player, see ChangeBlockInfo#FakePlayer
     * @param controller The Toggle Block Controller instance, calling for the block to be harvested
     * @return Returns the dropped items, which will be added to the controller's storage.
     */
    ItemStack[] harvestBlock(World world, int x, int y, int z, EntityPlayer player, IToggleController controller);

    /**
     * @param world      World object
     * @param x          The X coordinate of the block to change
     * @param y          The Y coordinate of the block to change
     * @param z          The Z coordinate of the block to change
     * @param direction  The direction the change block is configured with
     * @param player     The player. WARNING! This is a fake player, see ChangeBlockInfo#FakePlayer
     * @param placing    The ItemStack to place, remember to reduce stack size! When null the block should simply
     *                   be left as air
     * @param controller The TileEntity of the toggle block
     */
    void placeBlock(World world, int x, int y, int z, ForgeDirection direction, EntityPlayer player,
                    ItemStack placing, IToggleController controller);

    boolean canPlaceBlock(World world, int x, int y, int z, ItemStack placing, IToggleController controller);

    boolean canHarvestBlock(World world, int x, int y, int z, IToggleController controller);
}
