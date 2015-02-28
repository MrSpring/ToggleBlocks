package dk.mrspring.toggle.tileentity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Konrad on 27-02-2015.
 */
public class TileEntityToggleBlock extends TileEntity implements IInventory
{
    int state = OFF;
    Mode currentMode = Mode.EDITING;
    List<ChangeBlockInfo> changeBlockPosList = new ArrayList<ChangeBlockInfo>();
    // on is 0, off is 1
    ItemStack[] states = new ItemStack[2];
    ItemStack[] storage = new ItemStack[9];
    ChangeBlockInfo.FakePlayer fakePlayer;
    private static final int ON = 1;
    private static final int OFF = 0;

    public void setupFakePlayer()
    {
        fakePlayer = new ChangeBlockInfo.FakePlayer(worldObj);
    }

    public EntityPlayer getFakePlayer()
    {
        if (this.fakePlayer == null)
            this.setupFakePlayer();
        return this.fakePlayer;
    }

    /*public void updateSignal()
    {
        System.out.println("Updating");
        if (worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord))
        {
            System.out.println("Setting on!");
            this.setState(ON);
        } else
        {
            System.out.println("Setting off");
            this.setState(OFF);
        }
    }*/

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        int newState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0;
        if (newState != this.state)
            this.setState(newState);
    }

    public void setState(int state)
    {
        if (state >= 0 && state < 2)
        {
            this.state = state;
            this.updateChangeBlocks();
        }
    }

    public void updateChangeBlocks()
    {
//        if (this.isReady())
//        {
//        System.out.println("this.getStackInSlot(this.state).getDisplayName() = " + this.getStackInSlot(this.state).getDisplayName());
        ItemStack placing = requestItemFromStorage(this.getStackInSlot(this.state));
        for (ChangeBlockInfo pos : this.changeBlockPosList)
        {
//            System.out.println("Doing it! " + placing.getDisplayName() + ", " + placing.stackSize);
            System.out.println("state = " + state);
            ChangeBlockInfo.BlockToggleAction action;
            if (this.state == ON)
                action = pos.getOnAction();
            else action = pos.getOffAction();
            if (action != null)
            {
//                ItemStack[] result =
                action.performAction(worldObj, pos.x, pos.y, pos.z, 0, getFakePlayer(), placing, this);
//                this.addItemStacksToStorage(result);
            }
        }
//        } // TODO: If not then set change blocks, ready for configuring
    }

    public void addItemStacksToStorage(ItemStack[] stacks)
    {
        if (stacks != null)
            for (ItemStack stack : stacks)
            {
                if (stack != null)
                    System.out.println("stack.getDisplayName() = " + stack.getDisplayName() + ", stack.stackSize = " + stack.stackSize);
                ItemStack remainder = this.addItemStackToStorage(stack);
                if (remainder != null)
                    this.dropItem(remainder);
            }
    }

    public ItemStack addItemStackToStorage(ItemStack stack)
    {
        for (int i = 0; i < storage.length; i++)
        {
            ItemStack inStorage = storage[i];
            if (inStorage == null)
            {
                storage[i] = stack;
                return null;
            } else if (stack.isItemEqual(inStorage) && inStorage.stackSize < 64)
            {
                inStorage.stackSize += stack.stackSize;
                if (inStorage.stackSize > 64)
                {
                    ItemStack remainder = stack.copy();
                    remainder.stackSize = inStorage.stackSize - 64;
                    inStorage.stackSize = 64;
                    return this.addItemStackToStorage(remainder);
                } else return null;
            }
        }
        return stack;
    }

    private void dropItem(ItemStack stack)
    {
        if (stack != null)
        {
            Random random = new Random();
            EntityItem entityItem = new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, stack.copy());
            entityItem.motionX = (float) random.nextGaussian() * 0.05;
            entityItem.motionY = (float) random.nextGaussian() * 0.05 + 0.2F;
            entityItem.motionZ = (float) random.nextGaussian() * 0.05;
            worldObj.spawnEntityInWorld(entityItem);
        }
    }

    public void registerChangeBlock(int x, int y, int z)
    {
        ChangeBlockInfo blockInfo = new ChangeBlockInfo(x, y, z);
        this.changeBlockPosList.add(blockInfo);
    }

    /**
     * @param item The item being requested. Simply use something like: "new ItemStack(Items.item)
     * @return If there is an equal item in storage, that stack will be returned. Null otherwise.
     */
    public ItemStack requestItemFromStorage(ItemStack item)
    {
        if (item == null)
            return null;
        for (ItemStack stack : storage)
            if (stack != null)
                if (stack.isItemEqual(item))
                    return stack;
        return null;
    }

    public boolean isReady()
    {
        return this.currentMode == Mode.READY;
    }

    public class StorageItem
    {
        public ItemStack itemStack;
        public int index;

        public StorageItem(ItemStack stack, int i)
        {
            this.itemStack = stack;
            this.index = i;
        }
    }

    public enum Mode
    {
        READY, EDITING
    }

    @Override
    public int getSizeInventory()
    {
        return states.length + storage.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
//        System.out.println("slot = " + slot);
        if (slot < 2)
            return states[slot];
        else return storage[slot - 2];
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (slot < 2)
            states[slot] = null;
        else
        {
            ItemStack inSlot = storage[slot - 2];
            if (inSlot == null)
                return null;
            ItemStack returning = inSlot.copy();
            returning.stackSize = amount;
            storage[slot - 2].stackSize -= amount;
            if (storage[slot - 2].stackSize <= 0)
                storage[slot - 2] = null;
            return returning;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < 2)
        {
            if (stack != null)
            {
                states[slot] = stack.copy();
                states[slot].stackSize = 1;
            }
        } else if (slot >= 0)
        {
            this.storage[slot - 2] = stack;
        }
    }

    @Override
    public String getInventoryName()
    {
        return "Toggle Block";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return false;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return true;
    }
}
