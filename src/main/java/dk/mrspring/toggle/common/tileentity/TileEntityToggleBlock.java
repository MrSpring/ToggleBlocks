package dk.mrspring.toggle.common.tileentity;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.*;
import dk.mrspring.toggle.common.block.BlockBase;
import dk.mrspring.toggle.common.block.BlockChangeBlock;
import dk.mrspring.toggle.common.block.ControllerInfo;
import net.minecraft.block.state.IBlockState;
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
import net.minecraft.util.*;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class TileEntityToggleBlock extends TileEntity implements IToggleController, IToggleStorage, ISidedInventory, ITickable
{
    public static final String CHANGE_BLOCKS = "ChangeBlocks";
    public static final String STATE = "State";
    public static final String MODE = "Mode";
    public static final String PRIORITY = "StoragePriority";
    public static final String ITEM_SLOT = "Slot";
    public static final String ITEMS = "Items";
    public static final String CONTROLLER_SIZE = "ControllerSize";
    public static final String MAX_CHANGE_BLOCKS = "MaxBlocks";
    private static final String[] STATE_NAMES = new String[]{"OffState", "OnState"};

    Map<BlockPos, ChangeBlock> changeBlocks = Maps.newHashMap();
    int state = 0;
    int maxChangeBlocks;
    Mode mode = Mode.EDITING;
    ItemStack[] states = new ItemStack[getStateCount()];
    ItemStack[] itemStacks = new ItemStack[9];
    IInventory[] adjacent = new IInventory[EnumFacing.HORIZONTALS.length];
    StoragePriority priority = StoragePriority.STORAGE_FIRST;
    EntityPlayer player;
    ChangeBlock lastActivated;
    public static HashMap<String, Class<? extends Item>> toolTypeClasses = new HashMap<String, Class<? extends Item>>();

    static
    {
        toolTypeClasses.put("hoe", ItemHoe.class);
        toolTypeClasses.put("pick", ItemPickaxe.class);
        toolTypeClasses.put("pickaxe", ItemPickaxe.class);
        toolTypeClasses.put("axe", ItemAxe.class);
    }

    boolean first = false;

    @Override
    public void update()
    {
        if (!first)
        {
            notifyChangeBlocksOfReload();
            first = true;
        }
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound compound = new NBTTagCompound();
        this.writeToNBT(compound);
        return new S35PacketUpdateTileEntity(getPos(), 0, compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        this.readFromNBT(pkt.getNbtCompound());
    }

    public void loadChangeBlockFromNBT(NBTTagCompound compound)
    {
        ChangeBlock block = new ChangeBlock(compound, this);
        changeBlocks.put(block.pos, block);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);

        readChangeBlocksFromNBT(compound);
        readSizeFromNBT(compound);
        readStorageFromNBT(compound);
        readStatesFromNBT(compound);
    }

    public void readChangeBlocksFromNBT(NBTTagCompound compound)
    {
        NBTTagList changeBlockList = compound.getTagList(CHANGE_BLOCKS, 10);
        this.changeBlocks.clear();
        for (int i = 0; i < changeBlockList.tagCount(); i++)
            this.loadChangeBlockFromNBT(changeBlockList.getCompoundTagAt(i));
        this.state = MathHelper.clamp_int(compound.getInteger(STATE), 0, getStateCount());
        this.mode = Mode.valueOf(compound.getString(MODE));
    }

    public void readSizeFromNBT(NBTTagCompound compound)
    {
        if (compound.hasKey(CONTROLLER_SIZE, 10))
            this.maxChangeBlocks = compound.getCompoundTag(CONTROLLER_SIZE).getInteger(MAX_CHANGE_BLOCKS);
        else this.maxChangeBlocks = compound.getInteger(MAX_CHANGE_BLOCKS);
        if (this.maxChangeBlocks == 0) this.loadSizeFromMetadata();
    }

    public void readStorageFromNBT(NBTTagCompound compound)
    {
        this.priority = StoragePriority.fromInt(compound.getInteger(PRIORITY));
        NBTTagList storageList = compound.getTagList(ITEMS, 10);
        this.itemStacks = new ItemStack[getStorageHandler().getStorageSlots()];
        for (int i = 0; i < storageList.tagCount(); i++)
        {
            NBTTagCompound itemCompound = storageList.getCompoundTagAt(i);
            ItemStack fromCompound = ItemStack.loadItemStackFromNBT(itemCompound);
            this.itemStacks[itemCompound.getInteger(ITEM_SLOT)] = fromCompound;
        }
    }

    public void readStatesFromNBT(NBTTagCompound compound)
    {
        this.states = new ItemStack[getStateCount()];
        for (int i = 0; i < states.length; i++)
        {
            NBTTagCompound itemCompound = compound.getCompoundTag(STATE_NAMES[i]);
            states[i] = ItemStack.loadItemStackFromNBT(itemCompound);
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);

        writeChangeBlocksToNBT(compound);
        writeStorageToNBT(compound);
        writeStatesToNBT(compound);
    }

    public void writeChangeBlocksToNBT(NBTTagCompound compound)
    {
        NBTTagList changeBlockList = new NBTTagList();
        for (Map.Entry<BlockPos, ChangeBlock> entry : changeBlocks.entrySet())
        {
            NBTTagCompound changeBlockCompound = new NBTTagCompound();
            entry.getValue().writeToNBT(changeBlockCompound);
            changeBlockList.appendTag(changeBlockCompound);
        }
        compound.setTag(CHANGE_BLOCKS, changeBlockList);
        compound.setInteger(STATE, getState());
        compound.setString(MODE, mode.name());
        compound.setInteger(MAX_CHANGE_BLOCKS, getMaxChangeBlocks());
    }

    public void writeStorageToNBT(NBTTagCompound compound)
    {
        compound.setInteger(PRIORITY, getStorageHandler().getStoragePriority().getId());
        NBTTagList storageList = new NBTTagList();
        for (int i = 0; i < itemStacks.length; i++)
        {
            if (itemStacks[i] == null) continue;
            NBTTagCompound itemCompound = itemStacks[i].writeToNBT(new NBTTagCompound());
            itemCompound.setInteger(ITEM_SLOT, i);
            storageList.appendTag(itemCompound);
        }
        compound.setTag(ITEMS, storageList);
    }

    public void writeStatesToNBT(NBTTagCompound compound)
    {
        for (int i = 0; i < states.length; i++)
        {
            if (states[i] == null) continue;
            NBTTagCompound stateCompound = states[i].writeToNBT(new NBTTagCompound());
            compound.setTag(STATE_NAMES[i], stateCompound);
        }
    }

    private void loadSizeFromMetadata()
    {
        this.maxChangeBlocks = BlockBase.toggle_controller.getSizeFromState(worldObj.getBlockState(getPos())).getControllerSize();
    }

    public void setupFakePlayer()
    {
        if (getWorld() != null && getWorld() instanceof WorldServer)
            player = new FakePlayer((WorldServer) getWorld(), new GameProfile(new UUID(0, 0), "ToggleBlock"));
    }

    public EntityPlayer getFakePlayer()
    {
        if (this.player == null) this.setupFakePlayer();
        return this.player;
    }

    @Override
    public void onChangeBlockActivated(EntityPlayer player, BlockPos position, IChangeBlock changeBlock)
    {
        this.lastActivated = changeBlocks.get(position);
        this.notifyChangeBlocksOfReload();
        player.openGui(ToggleBlocks.instance, 1, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
        // TODO
    }

    @Override
    public void onToggleControllerActivated(EntityPlayer player)
    {
        getWorld().markBlockForUpdate(pos);
        player.openGui(ToggleBlocks.instance, 0, getWorld(), getPos().getX(), getPos().getY(), getPos().getZ());
    }

    public void updateAdjacent()
    {
        adjacent = new IInventory[EnumFacing.HORIZONTALS.length];
        for (int i = 0; i < adjacent.length; i++)
        {
            EnumFacing direction = EnumFacing.HORIZONTALS[i];
            TileEntity adjacentTileEntity = this.worldObj.getTileEntity(getPos().add(direction.getDirectionVec()));
            if (adjacentTileEntity != null && adjacentTileEntity instanceof IInventory)
                adjacent[i] = (IInventory) adjacentTileEntity;
        }
    }

    public void setSize(int size)
    {
        this.maxChangeBlocks = size;
    }

    public void checkSignal()
    {
        int state = worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0 ? 1 : 0;
        if (state != this.state)
        {
            this.state = state;
            this.updateChangeBlockStates();
        }
    }

    @Override
    public void setCurrentMode(Mode newMode)
    {
        if (mode != newMode)
        {
            mode = newMode;
            if (mode == Mode.READY)
            {
                collectDirections();
                updateChangeBlockStates();
            } else placeAllChangeBlocks();
        }
    }

    @Override
    public Mode getCurrentMode()
    {
        return mode;
    }

    private void collectDirections()
    {
        for (Map.Entry<BlockPos, ChangeBlock> entry : changeBlocks.entrySet())
        {
            IBlockState state = worldObj.getBlockState(entry.getKey());
            EnumFacing direction = BlockBase.change_block.getDirectionFromState(state);
            if (direction == null) direction = EnumFacing.DOWN;
            entry.getValue().direction = direction;
        }
    }

    private void notifyChangeBlocksOfReload()
    {
        for (BlockPos pos : changeBlocks.keySet())
        {
            TileEntity entity = worldObj.getTileEntity(pos);
            if (entity != null && entity instanceof IChangeBlock)
                ((IChangeBlock) entity).onControllerReload(this);
        }
    }

    private void updateChangeBlockStates()
    {
        if (this.mode == Mode.READY && !worldObj.isRemote)
            for (Map.Entry<BlockPos, ChangeBlock> entry : changeBlocks.entrySet())
            {
                ItemStack stateStack = entry.getValue().getOverrideStack(state);
                if (stateStack == null) stateStack = states[state];
                entry.getValue().doActionForState(worldObj, state, getFakePlayer(), stateStack, this);
            }
    }

    public void setOverrideForState(BlockPos pos, int state, boolean override)
    {
        ChangeBlock block = changeBlocks.get(pos);
        if (block != null) block.setOverridesState(state, override);
    }

    @Override
    public boolean onChangeBlockPlaced(BlockPos position, IChangeBlock block)
    {
        if (canRegisterAnotherChangeBlock() && !changeBlocks.containsKey(position))
        {
            BlockPos newPosition = new BlockPos(position);
            changeBlocks.put(newPosition, new ChangeBlock(newPosition, block.getDirection(), this));
            block.onRegistered(this);
            block.onControllerReload(this);
            return true;
        } else return false;
    }

    @Override
    public void onChangeBlockRemoved(BlockPos position, IChangeBlock block)
    {
        changeBlocks.remove(position);
        block.onUnregistered(this);
    }

    @Override
    public IToggleStorage getStorageHandler()
    {
        return this;
    }

    @Override
    public int getState()
    {
        return state;
    }

    @Override
    public int getStateCount()
    {
        return 2;
    }

    @Override
    public int getMaxChangeBlocks()
    {
        return maxChangeBlocks;
    }

    @Override
    public int getRegisteredChangeBlockCount()
    {
        return changeBlocks.size();
    }

    @Override
    public boolean canRegisterAnotherChangeBlock()
    {
        int max = getMaxChangeBlocks();
        return max == -1 || getRegisteredChangeBlockCount() + 1 < max;
    }

    @Override
    public ItemStack[] createChangeBlockDrop()
    {
        return new ItemStack[]{BlockChangeBlock.createChangeBlock(new ControllerInfo(getPos()), 1)};
    }

    @Override
    public void destroyAllChangeBlocks()
    {
        for (Map.Entry<BlockPos, ChangeBlock> entry : changeBlocks.entrySet())
            entry.getValue().removeIfChangeBlock(getWorld(), entry.getKey(), this);
    }

    @Override
    public void placeAllChangeBlocks()
    {
        for (Map.Entry<BlockPos, ChangeBlock> entry : changeBlocks.entrySet())
            entry.getValue().placeChangeBlock(getWorld(), entry.getKey(), getFakePlayer(), this);
        notifyChangeBlocksOfReload();
    }

    @Override
    public void validateStorage()
    {
        for (int i = 0; i < itemStacks.length; i++)
        {
            ItemStack stack = itemStacks[i];
            if (stack != null)
                if (stack.stackSize <= 0)
                    itemStacks[i] = null;
        }
    }

    @Override
    public ItemStack[] addItemStacksToStorage(ItemStack[] stacks)
    {
        if (stacks == null) return null;
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
        return addToInventory(this, EnumFacing.DOWN, stack);
    }

    private ItemStack addAdjacent(ItemStack stack)
    {
        ItemStack adding = stack.copy();
        for (int i = 0; i < adjacent.length && adding.stackSize > 0; i++)
            if (adjacent[i] != null)
                adding = addToInventory(adjacent[i], EnumFacing.HORIZONTALS[i].getOpposite(), adding);
        return adding;
    }

    private ItemStack addToInventory(IInventory inventory, EnumFacing side, ItemStack stack)
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

    private ItemStack addToSidedInventory(ISidedInventory inventory, EnumFacing side, ItemStack stack)
    {
        ItemStack adding = stack.copy();
        int[] accessibleSlots = inventory.getSlotsForFace(side);
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
    public void setStoragePriority(StoragePriority priority)
    {
        this.priority = priority;
        getWorld().markBlockForUpdate(getPos());
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
                result = getFromInventory(adjacent[i], EnumFacing.HORIZONTALS[i].getOpposite(), stack);
        return result;
    }

    private ItemStack getFromStorage(ItemStack stack)
    {
        return getFromInventory(this, EnumFacing.DOWN, stack);
    }

    private ItemStack getFromInventory(IInventory inventory, EnumFacing side, ItemStack stack)
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

    private ItemStack getFromSidedInventory(ISidedInventory inventory, EnumFacing side, ItemStack stack)
    {
        ItemStack result = null;
        int[] accessibleSlots = inventory.getSlotsForFace(side);
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
        return getTFromInventory(this, EnumFacing.DOWN, toolType);
    }

    private ItemStack getTFromAdjacent(String toolType)
    {
        ItemStack result = null;
        for (int i = 0; i < adjacent.length && result == null; i++)
            if (adjacent[i] != null)
                result = getTFromInventory(adjacent[i], EnumFacing.HORIZONTALS[i].getOpposite(), toolType);
        return result;
    }

    private ItemStack getTFromInventory(IInventory inventory, EnumFacing side, String toolType)
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

    private ItemStack getTFromSidedInventory(ISidedInventory inventory, EnumFacing side, String toolType)
    {
        ItemStack result = null;
        int[] accessibleSlots = inventory.getSlotsForFace(side);
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
                                stack.getItem().getClass().isInstance(toolTypeClasses.get(toolType))));
    }

    @Override
    public int getStorageSlots()
    {
        return itemStacks.length;
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
                setInventorySlotContents(slot, null);
            return returning;
        }
        return null;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int index)
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
                states[slot].stackSize = 0;
            }
        } else if (slot >= 0)
        {
            this.getStorageHandler().setItemInSlot(slot - 2, stack);
        }
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side)
    {
        int[] slots = new int[getStorageSlots()];
        for (int i = 0; i < slots.length; i++) slots[i] = i + 2;
        return slots;
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction)
    {
        return index >= 2;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction)
    {
        return index >= 2;
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
    public void openInventory(EntityPlayer player)
    {
    }

    @Override
    public void closeInventory(EntityPlayer player)
    {
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        return true;
    }

    @Override
    public int getField(int id)
    {
        return 0;
    }

    @Override
    public void setField(int id, int value)
    {
    }

    @Override
    public int getFieldCount()
    {
        return 0;
    }

    @Override
    public void clear()
    {
        for (int i = 0; i < getSizeInventory(); i++) setItemInSlot(i, null);
    }

    @Override
    public String getCommandSenderName()
    {
        return "Toggle Block";
    }

    @Override
    public boolean hasCustomName()
    {
        return false;
    }

    @Override
    public IChatComponent getDisplayName()
    {
        return new ChatComponentText("Toggle Block");
    }

    public InventoryChangeBlock makeChangeBlockInventoryForLastOpened()
    {
        return new InventoryChangeBlock(lastActivated);
    }
}
