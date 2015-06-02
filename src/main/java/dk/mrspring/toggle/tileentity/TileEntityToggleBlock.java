package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.api.*;
import dk.mrspring.toggle.api_impl.ToggleStorage;
import dk.mrspring.toggle.util.Misc;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import static dk.mrspring.toggle.util.Misc.StackCompareFunction.ITEM;
import static dk.mrspring.toggle.util.Misc.StackCompareFunction.METADATA;

/**
 * Created by Konrad on 27-02-2015.
 */
public class TileEntityToggleBlock extends TileEntity implements IInventory, IToggleController
{
    private static final int ON = 1;

    //TileEntityChest[] chests = new TileEntityChest[4];
    private static final int OFF = 0;
    public static HashMap<String, Class<? extends Item>> toolTypeClasses = new HashMap<String, Class<? extends Item>>();

    static
    {
        toolTypeClasses.put("hoe", ItemHoe.class);
        toolTypeClasses.put("pick", ItemPickaxe.class);
        toolTypeClasses.put("pickaxe", ItemPickaxe.class);
        toolTypeClasses.put("axe", ItemAxe.class);
    }

    int state = OFF;
    Mode currentMode = Mode.EDITING;
    List<ChangeBlockInfo> changeBlocks = new ArrayList<ChangeBlockInfo>();
    // on is 1, off is 0
    ItemStack[] states = new ItemStack[2]; // TODO: More states? "Cycle Block" with more than 2 states
    //ItemStack[] storage = new ItemStack[9];
    IToggleStorage storageHandler = new ToggleStorage(9);
    ChangeBlockInfo.FakePlayer fakePlayer;
    int maxChangeBlocks = 5;

    public TileEntityToggleBlock()
    {

    }

    public TileEntityToggleBlock(int changeBlocks)
    {
        this.maxChangeBlocks = changeBlocks;
    }

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

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        int newState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0;
        if (newState != this.state)
            this.setState(newState);

        storageHandler.validateStorage();
//        this.validateStorage();
    }

    /*@Override
    public void validateStorage()
    {
        for (int i = 0; i < storage.length; i++) {
            ItemStack inStorage = storage[i];
            if (inStorage != null)
                if (inStorage.stackSize == 0)
                    storage[i] = null;
        }
    }*/

    @Override
    public IChangeBlockInfo getChangeBlockInfo(int index)
    {
        if (index >= 0 && index < changeBlocks.size())
            return changeBlocks.get(index);
        return null;
    }

    @Override
    public IChangeBlockInfo[] getChangeBlocks()
    {
        return changeBlocks.toArray(new IChangeBlockInfo[changeBlocks.size()]);
    }

    public void updateChangeBlocks()
    {
        if (this.isReady())
        {
            for (ChangeBlockInfo pos : this.changeBlocks)
            {
                ItemStack stateStack = states[getState()];
                for (int i = 0; i < states.length; i++)
                {
                    ItemStack stack = states[i];
                    if (stack != null)
                        System.out.println("State: " + i + ": " + stack.getDisplayName());
                    else System.out.println("State: " + i);
                }
                System.out.println("Current state: " + getState());
                pos.doActionForState(worldObj, getState(), getFakePlayer(), stateStack, this);
            }
        }
    }

    public void placeChangeBlocks()
    {
        for (ChangeBlockInfo pos : this.changeBlocks)
        {
            pos.placeChangeBlock(worldObj, getFakePlayer(), this);
        }
    }

    @Override
    public ChangeBlockInfo registerChangeBlock(int x, int y, int z)
    {
        System.out.println("Registering change block: " + x + ", " + y + ", " + z);
        if (this.changeBlocks.size() + 1 <= getMaxChangeBlocks())
        {
            ChangeBlockInfo blockInfo = new ChangeBlockInfo(x, y, z, worldObj.getBlockMetadata(x, y, z));
            ForgeDirection direction = ForgeDirection.getOrientation(worldObj.getBlockMetadata(x, y, z));
            System.out.println("Registered with: " + direction);
//            blockInfo.direction = direction;
            this.changeBlocks.add(blockInfo);
            return blockInfo;
        } else return null;
    }

    /*@Override
    public void addItemStacksToStorage(ItemStack[] stacks)
    {
        if (stacks != null)
            for (ItemStack stack : stacks) {
                ItemStack remainder = this.addItemStackToStorage(stack);
                if (remainder != null)
                    this.dropItem(remainder);
            }
    }*/

    /*@Override
    public boolean removeItemFromStorage(ItemStack toRemove)
    {
        for (int i = 0; i < storage.length; i++) {
            ItemStack stack = storage[i];
            if (ItemStack.areItemStacksEqual(stack, toRemove)) {
                storage[i] = null;
                return true;
            }
        }
        return false;
    }*/

    /*@Override
    public ItemStack addItemStackToStorage(ItemStack stack)
    {
        for (int i = 0; i < storage.length; i++) {
            ItemStack inStorage = storage[i];
            int maxStackSize = stack.getMaxStackSize();
            if (inStorage == null) {
                storage[i] = stack;
                return null;
            } else if (stack.isItemEqual(inStorage) && inStorage.stackSize < maxStackSize) {
                inStorage.stackSize += stack.stackSize;
                if (inStorage.stackSize > maxStackSize) {
                    ItemStack remainder = stack.copy();
                    remainder.stackSize = inStorage.stackSize - maxStackSize;
                    inStorage.stackSize = maxStackSize;
                    return this.addItemStackToStorage(remainder);
                } else return null;
            }
        }
        return stack;
    }*/

    /*@Override
    public void dropItem(ItemStack stack) // TODO: Remove...
    {
        if (stack != null) {
            Random random = new Random();
            EntityItem entityItem = new EntityItem(worldObj, xCoord + 0.5, yCoord + 1.5, zCoord + 0.5, stack.copy());
            entityItem.motionX = (float) random.nextGaussian() * 0.05;
            entityItem.motionY = (float) random.nextGaussian() * 0.05 + 0.2F;
            entityItem.motionZ = (float) random.nextGaussian() * 0.05;
            worldObj.spawnEntityInWorld(entityItem);
        }
    }*/

    @Override
    public ChangeBlockInfo unregisterChangeBlock(int x, int y, int z)
    {
        for (ChangeBlockInfo pos : changeBlocks)
            if (pos.x == x && pos.y == y && pos.z == z)
            {
                changeBlocks.remove(pos);
                return pos;
            }
        return null;
    }

    public boolean isReady()
    {
        return this.currentMode == Mode.READY;
    }

    /*@Override
    public ItemStack getItemFromStorage(ItemStack item)
    {
        if (item == null)
            return null;
        for (ItemStack stack : getAllStorage())
            if (stack != null)
                if (Misc.areItemStacksEqual(item, stack, ITEM, METADATA) && stack.stackSize > 0)
                    return stack;
        return null;
    }*/

    /*@Override
    public ItemStack requestToolFromStorage(String toolType)
    {
        for (ItemStack stack : getAllStorage())
            if (stack != null)
                if (stack.getItem().getToolClasses(stack).contains(toolType))
                    return stack;
                else if (toolTypeClasses.containsKey(toolType) && stack.getItem().getClass() == toolTypeClasses.get(toolType)) {
                    System.out.println("Returning type: " + toolType);
                    return stack;
                }
        return null;
    }*/

//    public ItemStack[] getAllStorage()
//    {
//        return storage; // TODO: Get from containers as well
//    }

    public void toggleMode()
    {
        if (this.currentMode == Mode.EDITING)
            this.setCurrentMode(Mode.READY);
        else this.setCurrentMode(Mode.EDITING);
    }

    @Override
    public int getState()
    {
        return state;
    }

    public void setState(int state)
    {
        if (state >= 0 && state < 2)
        {
            this.state = state;
            this.updateChangeBlocks();
        }
    }

    @Override
    public int getMaxChangeBlocks()
    {
        return maxChangeBlocks;
    }

    @Override
    public int getRegisteredChangeBlockCount()
    {
        return this.changeBlocks.size();
    }

    @Override
    public IToggleStorage getStorageHandler()
    {
        return this.storageHandler;
    }

    @Override
    public int x()
    {
        return xCoord;
    }

    @Override
    public int y()
    {
        return yCoord;
    }

    @Override
    public int z()
    {
        return zCoord;
    }

    @Override
    public boolean canRegisterAnotherChangeBlock()
    {
//        System.out.println(getRegisteredChangeBlockCount() < getMaxChangeBlocks());
        return getRegisteredChangeBlockCount() < getMaxChangeBlocks();
    }

    public Mode getCurrentMode()
    {
        return currentMode;
    }

    public void setCurrentMode(Mode currentMode)
    {
        this.currentMode = currentMode;
        if (currentMode == Mode.READY)
        {
            this.collectChangeBlockInfo();
            this.updateChangeBlocks();
        } else this.placeChangeBlocks();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void collectChangeBlockInfo()
    {
        for (int i = 0; i < changeBlocks.size(); i++)
        {
            ChangeBlockInfo info = changeBlocks.get(i);
            int x = info.x, y = info.y, z = info.z;
            if (worldObj.getTileEntity(x, y, z) instanceof TileEntityChangeBlock)
            {
                TileEntityChangeBlock tileEntity = (TileEntityChangeBlock) worldObj.getTileEntity(x, y, z);
                ChangeBlockInfo newInfo = tileEntity.getBlockInfo();
                changeBlocks.set(i, newInfo);
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) // TODO: Clean up
    {
        super.writeToNBT(compound);

        NBTTagList changeBlockList = new NBTTagList();
        for (ChangeBlockInfo info : this.changeBlocks)
            if (info != null)
            {
                NBTTagCompound changeBlockCompound = new NBTTagCompound();
                info.writeToNBT(changeBlockCompound, true);
                changeBlockList.appendTag(changeBlockCompound);
            }

        compound.setTag("ChangeBlocks", changeBlockList);
        compound.setInteger("State", this.state);
        compound.setString("Mode", this.getCurrentMode().name());
//        compound.setString("StoragePriorities", this.getStoragePriority().name());

        NBTTagCompound storageCompound = new NBTTagCompound();
        this.storageHandler.writeToNBT(storageCompound);

        /*NBTTagList storageList = new NBTTagList();

        for (int i = 0; i < this.storage.length; ++i) {
            if (this.storage[i] != null) {
                NBTTagCompound storageCompound = new NBTTagCompound();
                storageCompound.setByte("Slot", (byte) i);
                this.storage[i].writeToNBT(storageCompound);
                storageList.appendTag(storageCompound);
            }
        }*/

        compound.setTag("Storage", storageCompound);

        if (this.states[1] != null)
        {
            NBTTagCompound onCompound = new NBTTagCompound();
            this.states[1].writeToNBT(onCompound);
            compound.setTag("OnState", onCompound);
        }

        if (this.states[0] != null)
        {
            NBTTagCompound offCompound = new NBTTagCompound();
            this.states[0].writeToNBT(offCompound);
            compound.setTag("OffState", offCompound);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) // TODO: Clean up
    {
        super.readFromNBT(compound);

        NBTTagList changeBlockList = compound.getTagList("ChangeBlocks", 10);
        this.changeBlocks = new ArrayList<ChangeBlockInfo>();
        for (int i = 0; i < changeBlockList.tagCount(); i++)
        {
            NBTTagCompound changeBlockCompound = changeBlockList.getCompoundTagAt(i);
            ChangeBlockInfo info = new ChangeBlockInfo(changeBlockCompound);
            this.changeBlocks.add(info);
        }

        this.state = compound.getInteger("State");
        this.currentMode = Mode.valueOf(compound.getString("Mode"));

        NBTTagCompound storageCompound = compound.getCompoundTag("Storage");
        this.storageHandler = new ToggleStorage(storageCompound, 9);

        /*NBTTagList storageList = compound.getTagList("Storage", 10);
        this.storage = new ItemStack[9];

        for (int i = 0; i < storageList.tagCount(); i++)
        {
            NBTTagCompound itemCompound = storageList.getCompoundTagAt(i);
            ItemStack fromCompound = ItemStack.loadItemStackFromNBT(itemCompound);
            this.storage[itemCompound.getByte("Slot")] = fromCompound;
        }*/

        this.states = new ItemStack[2];
        ItemStack tempStack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("OnState"));
        this.states[1] = tempStack;
        tempStack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("OffState"));
        this.states[0] = tempStack;
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.func_148857_g());
    }

    @Override
    public int getSizeInventory()
    {
        return states.length + storageHandler.getStorageSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (slot < 2)
            return states[slot];
        else return storageHandler.getItemFromSlot(slot - 2);
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount)
    {
        if (slot < 2)
            states[slot] = null;
        else
        {
            ItemStack inSlot = getStackInSlot(slot);
            if (inSlot == null)
                return null;
            ItemStack returning = inSlot.copy();
            returning.stackSize = amount;
            getStackInSlot(slot).stackSize -= amount;
            if (getStackInSlot(slot).stackSize <= 0)
                setInventorySlotContents(slot - 2, null);
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
            this.storageHandler.setItemInSlot(slot - 2, stack);
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
