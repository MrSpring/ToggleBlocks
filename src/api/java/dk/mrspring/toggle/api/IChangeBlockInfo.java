package dk.mrspring.toggle.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

/**
 * Created by Konrad on 03-04-2015.
 */
public interface IChangeBlockInfo
{
    void setCoordinates(BlockPos newPos);

    void doActionForState(World world, int state, EntityPlayer player, ItemStack defaultPlacingForState,
                          IToggleController controller);

    void placeChangeBlock(World world, EntityPlayer player, IToggleController controller);

    void writeToNBT(NBTTagCompound compound, boolean writeCoordinates);

    void readFromNBT(NBTTagCompound compound, boolean readCoordinates);

    boolean overridesState(int state);

    void setOverridesState(int state, boolean doesOverride);

    ItemStack getOverrideStackForState(int state);

    void setOverrideStackForState(int state, ItemStack overrider);

    EnumFacing getDirection();
}
