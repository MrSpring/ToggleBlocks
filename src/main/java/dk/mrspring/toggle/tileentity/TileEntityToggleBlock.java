package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.block.BlockBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
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
    // on is 1, off is 0
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

    @Override
    public void updateEntity()
    {
        super.updateEntity();

        int newState = worldObj.isBlockIndirectlyGettingPowered(xCoord, yCoord, zCoord) ? 1 : 0;
        if (newState != this.state)
            this.setState(newState);

        this.checkStorage();
    }

    public void checkStorage()
    {
        for (int i = 0; i < storage.length; i++)
        {
            ItemStack inStorage = storage[i];
            if (inStorage != null)
                if (inStorage.stackSize == 0)
                    storage[i] = null;
        }
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
        ItemStack placing = requestItemFromStorage(this.getStackInSlot(this.state));
        if (this.getCurrentMode() == Mode.EDITING)
            placing = new ItemStack(BlockBase.change_block, 200); // TODO: Set metadata for custom blocks and stuff, stack size should be max number of change blocks
        for (ChangeBlockInfo pos : this.changeBlockPosList)
        {
            ChangeBlockInfo.BlockToggleAction action;
            if (this.state == ON)
                action = pos.getOnAction();
            else action = pos.getOffAction();
            if (action != null)
                action.performAction(worldObj, pos.x, pos.y, pos.z, 0, getFakePlayer(), placing, this);
        }
    }

    public void addItemStacksToStorage(ItemStack[] stacks)
    {
        if (stacks != null)
            for (ItemStack stack : stacks)
            {
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

    public void toggleMode()
    {
        if (this.currentMode == Mode.EDITING)
            this.setCurrentMode(Mode.READY);
        else this.setCurrentMode(Mode.EDITING);
    }

    public Mode getCurrentMode()
    {
        return currentMode;
    }

    public void setCurrentMode(Mode currentMode)
    {
        this.currentMode = currentMode;
        this.updateChangeBlocks();
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) // TODO: Clean up
    {
        super.writeToNBT(compound);

        NBTTagList changeBlockList = new NBTTagList();
        for (ChangeBlockInfo info : this.changeBlockPosList)
            if (info != null)
            {
                NBTTagCompound changeBlockCompound = new NBTTagCompound();
                info.writeToNBT(changeBlockCompound);
                changeBlockList.appendTag(changeBlockCompound);
            }

        compound.setTag("ChangeBlocks", changeBlockList);
        compound.setInteger("State", this.state);
        compound.setString("Mode", this.getCurrentMode().name());

        NBTTagList storageList = new NBTTagList();

        for (int i = 0; i < this.storage.length; ++i)
        {
            if (this.storage[i] != null)
            {
                NBTTagCompound storageCompound = new NBTTagCompound();
                storageCompound.setByte("Slot", (byte) i);
                this.storage[i].writeToNBT(storageCompound);
                storageList.appendTag(storageCompound);
            }
        }

        compound.setTag("Storage", storageList);

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
        this.changeBlockPosList = new ArrayList<ChangeBlockInfo>();
        for (int i = 0; i < changeBlockList.tagCount(); i++)
        {
            NBTTagCompound changeBlockCompound = changeBlockList.getCompoundTagAt(i);
            ChangeBlockInfo info = new ChangeBlockInfo(changeBlockCompound);
            this.changeBlockPosList.add(info);
        }

        this.state = compound.getInteger("State");
        this.currentMode = Mode.valueOf(compound.getString("Mode"));

        NBTTagList storageList = compound.getTagList("Storage", 10);
        this.storage = new ItemStack[9];

        for (int i = 0; i < storageList.tagCount(); i++)
        {
            NBTTagCompound itemCompound = storageList.getCompoundTagAt(i);
            ItemStack fromCompound = ItemStack.loadItemStackFromNBT(itemCompound);
            this.storage[itemCompound.getByte("Slot")] = fromCompound;
        }

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
        READY(0), EDITING(1);

        int id;

        private Mode(int id)
        {
            this.id = id;
        }

        public int getId()
        {
            return this.id;
        }

        public static Mode fromInt(int id)
        {
            for (Mode mode : values())
                if (mode.getId() == id)
                    return mode;
            return EDITING;
        }
    }

    @Override
    public int getSizeInventory()
    {
        return states.length + storage.length;
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
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
