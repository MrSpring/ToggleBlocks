package dk.mrspring.toggle.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

/**
 * Created by Konrad on 03-04-2015.
 */
public interface IChangeBlockInfo
{
    void doActionForState(World world, int state, EntityPlayer player, ItemStack defaultPlacingForState,
                                 IToggleController controller);

    void placeChangeBlock(World world, EntityPlayer player, IToggleController controller);

    void writeToNBT(NBTTagCompound compound);

    void readFromNBT(NBTTagCompound compound);

    boolean overridesState(int state);

    void setOverridesState(int state, boolean doesOverride);

    ItemStack getOverrideStackForState(int state);

    void setOverrideStackForState(int state, ItemStack overrider);

    ForgeDirection getDirection();
}
