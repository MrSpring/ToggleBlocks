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
    public void setCoordinates(int x, int y, int z);

    public void doActionForState(World world, int state, EntityPlayer player, ItemStack defaultPlacingForState,
                                 IToggleController controller);

    public void placeChangeBlock(World world, EntityPlayer player, IToggleController controller);

    public void writeToNBT(NBTTagCompound compound, boolean writeCoordinates);

    public void readFromNBT(NBTTagCompound compound, boolean readCoordinates);

    public boolean overridesState(int state);

    public void setOverridesState(int state, boolean doesOverride);

    public ItemStack getOverrideStackForState(int state);

    public void setOverrideStackForState(int state, ItemStack overrider);

    public ForgeDirection getDirection();
}
