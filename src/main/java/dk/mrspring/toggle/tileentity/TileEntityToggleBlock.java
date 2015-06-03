package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.api.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import static net.minecraftforge.common.util.ForgeDirection.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Konrad on 27-02-2015.
 */
public class TileEntityToggleBlock extends TileEntity implements ISidedInventory, IToggleController, IToggleStorage
{
    public static final String CHANGE_BLOCKS = "ChangeBlocks";
    public static final String STATE = "State";
    public static final String MODE = "Mode";
    public static final String PRIORITY = "StoragePriority";
    public static final String ITEM_SLOT = "Slot";
    public static final String ITEMS = "Items";
    private static final int ON = 1;
    private static final int OFF = 0;
    private static final String[] STATE_NAMES = new String[]{"OffState", "OnState"};
    public static HashMap<String, Class<? extends Item>> toolTypeClasses = new HashMap<String, Class<? extends Item>>();

    static
    {
        toolTypeClasses.put("hoe", ItemHoe.class);
        toolTypeClasses.put("pick", ItemPickaxe.class);
        toolTypeClasses.put("pickaxe", ItemPickaxe.class);
        toolTypeClasses.put("axe", ItemAxe.class);
    }

    final ForgeDirection[] directions = new ForgeDirection[]{NORTH, SOUTH, WEST, EAST};
    int state = OFF;
    Mode currentMode = Mode.EDITING;
    List<ChangeBlockInfo> changeBlocks = new ArrayList<ChangeBlockInfo>();
    ItemStack[] states = new ItemStack[2]; // TODO: More states? "Cycle Block" with more than 2 states
    ItemStack[] itemStacks = new ItemStack[9];
    ChangeBlockInfo.FakePlayer fakePlayer;
    int maxChangeBlocks = 5;
    IInventory[] adjacent = new IInventory[directions.length];
    StoragePriority priority = StoragePriority.STORAGE_FIRST;

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
        this.updateAdjacent();

        int newState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0;
        if (newState != this.state)
            this.setState(newState);

        this.validateStorage();
    }

    public void updateAdjacent()
    {
        adjacent = new IInventory[directions.length];
        for (int i = 0; i < adjacent.length; i++)
        {
            ForgeDirection direction = directions[i];
            int adX = xCoord + direction.offsetX, adY = yCoord, adZ = zCoord + direction.offsetZ;
            TileEntity adjacentTileEntity = this.worldObj.getTileEntity(adX, adY, adZ);
            if (adjacentTileEntity != null && adjacentTileEntity instanceof IInventory)
                adjacent[i] = (IInventory) adjacentTileEntity;
        }
    }

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
        if (this.isReady() && !worldObj.isRemote)
            for (ChangeBlockInfo pos : this.changeBlocks)
            {
                ItemStack stateStack = states[getState()];
                pos.doActionForState(worldObj, getState(), getFakePlayer(), stateStack, this);
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
        if (this.changeBlocks.size() + 1 <= getMaxChangeBlocks())
        {
            ChangeBlockInfo blockInfo = new ChangeBlockInfo(x, y, z, worldObj.getBlockMetadata(x, y, z));
            this.changeBlocks.add(blockInfo);
            return blockInfo;
        } else return null;
    }

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
        return this;
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
    public void validateStorage()
    {
        for (int i = 0; i < itemStacks.length; i++)
        {
            ItemStack stack = itemStacks[i];
            if (stack != null)
                if (stack.stackSize == 0)
                    itemStacks[i] = null;
        }
    }

    @Override
    public ItemStack[] addItemStacksToStorage(ItemStack[] stacks)
    {
        ItemStack[] returning = new ItemStack[stacks.length];
        for (int i = 0; i < stacks.length; i++)
        {
            ItemStack stack = stacks[i];
            if (stack != null)
                if (stack.stackSize > 0)
                    returning[i] = this.addItemStackToStorage(stack);
        }
        return returning;
    }

    @Override
    public ItemStack addItemStackToStorage(ItemStack stack)
    {
        if (stack == null || stack.stackSize <= 0)
            return null;
        ItemStack adding = stack.copy();
        switch (this.getStoragePriority())
        {
            case CHESTS_FIRST:
                adding = this.addAdjacent(adding);
            case STORAGE_ONLY:
                if (adding.stackSize > 0)
                    return this.addStorage(adding);
                else return null;

            case STORAGE_FIRST:
                adding = this.addStorage(adding);
            case CHESTS_ONLY:
                if (adding.stackSize > 0)
                    return this.addAdjacent(adding);
                else return null;
        }
        return adding;
    }

    private ItemStack addStorage(ItemStack stack)
    {
        return addToInventory(this, 0, stack);
    }

    private ItemStack addAdjacent(ItemStack stack)
    {
        ItemStack adding = stack.copy();
        for (int i = 0; i < adjacent.length && adding.stackSize > 0; i++)
            if (adjacent[i] != null)
                adding = addToInventory(adjacent[i], directions[i].getOpposite().ordinal(), adding);
        return adding;
    }

    private ItemStack addToInventory(IInventory inventory, int side, ItemStack stack)
    {
        if (inventory instanceof ISidedInventory)
            return addToSidedInventory((ISidedInventory) inventory, side, stack);
        else
        {
            ItemStack adding = stack.copy();
            int slots = inventory.getSizeInventory();
            for (int slot = 0; slot < slots && adding.stackSize > 0; slot++)
            {
                ItemStack inSlot = inventory.getStackInSlot(slot);
                if (inSlot == null || inSlot.getItem() == null)
                {
                    inventory.setInventorySlotContents(slot, adding.copy());
                    adding.stackSize = 0;
                } else if (inSlot.isItemEqual(adding) && ItemStack.areItemStackTagsEqual(stack, inSlot))
                {
                    int stackLimit = Math.min(inSlot.getMaxStackSize(), inventory.getInventoryStackLimit());
                    int remaining = inSlot.stackSize + adding.stackSize - stackLimit;
                    if (remaining > 0)
                    {
                        inSlot.stackSize = stackLimit;
                        adding.stackSize = remaining;
                    } else
                    {
                        inSlot.stackSize += adding.stackSize;
                        adding.stackSize = 0;
                    }
                }
            }
            return adding;
        }
    }

    private ItemStack addToSidedInventory(ISidedInventory inventory, int side, ItemStack stack)
    {
        ItemStack adding = stack.copy();
        int[] accessibleSlots = inventory.getAccessibleSlotsFromSide(side);
        for (int i = 0; i < accessibleSlots.length && adding.stackSize > 0; i++)
        {
            int slot = accessibleSlots[i];
            ItemStack inSlot = inventory.getStackInSlot(slot);
            if (inSlot == null || inSlot.getItem() == null)
            {
                inventory.setInventorySlotContents(slot, adding.copy());
                adding.stackSize = 0;
            } else if (inSlot.isItemEqual(adding) && ItemStack.areItemStackTagsEqual(stack, inSlot))
            {
                int stackLimit = Math.min(inSlot.getMaxStackSize(), inventory.getInventoryStackLimit());
                int remaining = inSlot.stackSize + adding.stackSize - stackLimit;
                if (remaining > 0)
                {
                    inSlot.stackSize = stackLimit;
                    adding.stackSize = remaining;
                } else
                {
                    inSlot.stackSize += adding.stackSize;
                    adding.stackSize = 0;
                }
            }
        }
        return adding;
    }

    @Override
    public StoragePriority getStoragePriority()
    {
        return this.priority;
    }

    @Override
    public void setStoragePriority(StoragePriority newPriority)
    {
        this.priority = newPriority;
        worldObj.markBlockForUpdate(x(), y(), z());
    }

    @Override
    public ItemStack getItemFromStorage(ItemStack stack)
    {
        if (stack == null)
            return null;
        ItemStack found = null;
        switch (getStoragePriority())
        {
            case CHESTS_FIRST:
                found = getFromAdjacent(stack);
                if (found != null)
                    break;
            case STORAGE_ONLY:
                return getFromStorage(stack);

            case STORAGE_FIRST:
                found = getFromStorage(stack);
                if (found != null)
                    break;
            case CHESTS_ONLY:
                return getFromAdjacent(stack);
        }
        return found;
    }

    private ItemStack getFromAdjacent(ItemStack stack)
    {
        ItemStack result = null;
        for (int i = 0; i < adjacent.length && result == null; i++)
            if (adjacent[i] != null)
                result = getFromInventory(adjacent[i], directions[i].getOpposite().ordinal(), stack);
        return result;
    }

    private ItemStack getFromStorage(ItemStack stack)
    {
        return getFromInventory(this, 0, stack);
    }

    private ItemStack getFromInventory(IInventory inventory, int side, ItemStack stack)
    {
        if (inventory instanceof ISidedInventory)
            return getFromSidedInventory((ISidedInventory) inventory, side, stack);
        else
        {
            ItemStack result = null;
            int slots = inventory.getSizeInventory();
            for (int slot = 0; slot < slots && result == null; slot++)
            {
                ItemStack inSlot = inventory.getStackInSlot(slot);
                if (inSlot != null && inSlot.isItemEqual(stack) && inSlot.stackSize > 0)
                    result = inSlot;
            }
            return result;
        }
    }

    private ItemStack getFromSidedInventory(ISidedInventory inventory, int side, ItemStack stack)
    {
        ItemStack result = null;
        int[] accessibleSlots = inventory.getAccessibleSlotsFromSide(side);
        for (int i = 0; i < accessibleSlots.length && result == null; i++)
        {
            int slot = accessibleSlots[i];
            ItemStack inSlot = inventory.getStackInSlot(slot);
            if (inSlot != null && inSlot.isItemEqual(stack) && inSlot.stackSize > 0)
                result = inSlot;
        }
        return result;
    }

    @Override
    public ItemStack getToolFromStorage(String toolType)
    {
        if (toolType == null)
            return null;
        ItemStack found = null;
        switch (getStoragePriority())
        {
            case CHESTS_FIRST:
                found = getTFromAdjacent(toolType);
                if (found != null)
                    break;
            case STORAGE_ONLY:
                return getTFromStorage(toolType);

            case STORAGE_FIRST:
                found = getTFromStorage(toolType);
                if (found != null)
                    break;
            case CHESTS_ONLY:
                return getTFromAdjacent(toolType);
        }
        return found;
    }

    private ItemStack getTFromStorage(String toolType)
    {
        return getTFromInventory(this, 0, toolType);
    }

    private ItemStack getTFromAdjacent(String toolType)
    {
        ItemStack result = null;
        for (int i = 0; i < adjacent.length && result == null; i++)
            if (adjacent[i] != null)
                result = getTFromInventory(adjacent[i], directions[i].getOpposite().ordinal(), toolType);
        return result;
    }

    private ItemStack getTFromInventory(IInventory inventory, int side, String toolType)
    {
        if (inventory instanceof ISidedInventory)
            return getTFromSidedInventory((ISidedInventory) inventory, side, toolType);
        else
        {
            ItemStack result = null;
            int slots = inventory.getSizeInventory();
            for (int slot = 0; slot < slots && result == null; slot++)
            {
                ItemStack inSlot = inventory.getStackInSlot(slot);
                if (isItemTool(toolType, inSlot))
                    result = inSlot;
            }
            return result;
        }
    }

    private ItemStack getTFromSidedInventory(ISidedInventory inventory, int side, String toolType)
    {
        ItemStack result = null;
        int[] accessibleSlots = inventory.getAccessibleSlotsFromSide(side);
        for (int slot : accessibleSlots)
        {
            ItemStack inSlot = inventory.getStackInSlot(slot);
            if (isItemTool(toolType, inSlot))
                result = inSlot;
        }
        return result;
    }

    private boolean isItemTool(String toolType, ItemStack stack)
    {
        return stack != null && stack.getItem() != null &&
                (stack.getItem().getToolClasses(stack).contains(toolType) ||
                        (toolTypeClasses.containsKey(toolType) &&
                                stack.getItem().getClass() == toolTypeClasses.get(toolType)));
    }

    @Override
    public int getStorageSlots()
    {
        return 9;
    }

    @Override
    public ItemStack getItemFromSlot(int slot)
    {
        return (slot >= 0 && slot < itemStacks.length) ? itemStacks[slot] : null;
    }

    @Override
    public void setItemInSlot(int slot, ItemStack stack)
    {
        if (slot >= 0 && slot < itemStacks.length)
            this.itemStacks[slot] = stack;
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

        compound.setTag(CHANGE_BLOCKS, changeBlockList);
        compound.setInteger(STATE, this.state);
        compound.setString(MODE, this.getCurrentMode().name());
        compound.setInteger(PRIORITY, getStoragePriority().getId());

        NBTTagList storageList = new NBTTagList();

        for (int i = 0; i < this.itemStacks.length; ++i)
        {
            if (this.itemStacks[i] != null)
            {
                NBTTagCompound itemCompound = new NBTTagCompound();
                itemCompound.setByte(ITEM_SLOT, (byte) i);
                this.itemStacks[i].writeToNBT(itemCompound);
                storageList.appendTag(itemCompound);
            }
        }

        compound.setTag(ITEMS, storageList);

        for (int i = 0; i < this.states.length; i++)
        {
            if (this.states[i] != null)
            {
                NBTTagCompound stateCompound = new NBTTagCompound();
                this.states[i].writeToNBT(stateCompound);
                compound.setTag(STATE_NAMES[i], stateCompound);
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) // TODO: Clean up
    {
        super.readFromNBT(compound);

        NBTTagList changeBlockList = compound.getTagList(CHANGE_BLOCKS, 10);
        this.changeBlocks = new ArrayList<ChangeBlockInfo>();
        for (int i = 0; i < changeBlockList.tagCount(); i++)
        {
            NBTTagCompound changeBlockCompound = changeBlockList.getCompoundTagAt(i);
            ChangeBlockInfo info = new ChangeBlockInfo(changeBlockCompound);
            this.changeBlocks.add(info);
        }

        this.state = compound.getInteger(STATE);
        this.currentMode = Mode.valueOf(compound.getString(MODE));
        this.priority = StoragePriority.fromInt(compound.getInteger(PRIORITY));

        NBTTagList storageList = compound.getTagList(ITEMS, 10);
        this.itemStacks = new ItemStack[getStorageSlots()];

        for (int i = 0; i < storageList.tagCount(); i++)
        {
            NBTTagCompound itemCompound = storageList.getCompoundTagAt(i);
            ItemStack fromCompound = ItemStack.loadItemStackFromNBT(itemCompound);
            this.itemStacks[itemCompound.getByte(ITEM_SLOT)] = fromCompound;
        }

        this.states = new ItemStack[2];
        for (int i = 0; i < states.length; i++)
        {
            NBTTagCompound stateCompound = compound.getCompoundTag(STATE_NAMES[i]);
            this.states[i] = ItemStack.loadItemStackFromNBT(stateCompound);
        }
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
        return states.length + getStorageHandler().getStorageSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        if (slot < 2)
            return states[slot];
        else return getStorageHandler().getItemFromSlot(slot - 2);
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
            this.getStorageHandler().setItemInSlot(slot - 2, stack);
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
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return true;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return new int[]{2, 3, 4, 5, 6, 7, 8, 9, 10};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return slot > 1 && slot < 11;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return slot > 1 && slot < 11;
    }
}
