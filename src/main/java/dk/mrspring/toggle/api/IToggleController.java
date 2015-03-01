package dk.mrspring.toggle.api;

import dk.mrspring.toggle.tileentity.ChangeBlockInfo;
import net.minecraft.item.ItemStack;

/**
 * Created by Konrad on 01-03-2015.
 */
public interface IToggleController
{
    public void addItemStacksToStorage(ItemStack[] stacks);

    public ItemStack addItemStackToStorage(ItemStack stack);

    public ItemStack requestItemFromStorage(ItemStack item);

    public ItemStack requestToolFromStorage(String toolType);

    public void dropItem(ItemStack stack);

    public ChangeBlockInfo registerChangeBlock(int x, int y, int z);

    public ChangeBlockInfo unregisterChangeBlock(int x, int y, int z);

    public int getState();

    public ItemStack[] getAllStorage();
}
