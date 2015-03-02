package dk.mrspring.toggle.tileentity;

import dk.mrspring.toggle.api.IToggleController;
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
public class TileEntityToggleBlock extends TileEntity implements IInventory, IToggleController
{
    int state = OFF;
    Mode currentMode = Mode.EDITING;
    List<ChangeBlockInfo> changeBlockPosList = new ArrayList<ChangeBlockInfo>();
    // on is 1, off is 0
    ItemStack[] states = new ItemStack[2]; // TODO: More states?
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
        if (this.isReady())
            for (ChangeBlockInfo pos : this.changeBlockPosList)
            {
                ItemStack stateStack = states[getState()];
                pos.doAction(worldObj, getState(), getFakePlayer(), stateStack, this);
//                ItemStack placing = requestItemFromStorage(this.getStackInSlot(this.state));
//                if (pos.overridesState(this.state))
//                {
//                    ItemStack override = pos.getOverrideForState(this.state);
//                    placing = requestItemFromStorage(override);
//                }
//                ChangeBlockInfo.BasicBlockToggleAction action;
//                action = pos.getAction(this.state);
//                if (action != null)
//                    action.performAction(worldObj, pos.x, pos.y, pos.z, pos.getDirection(), getFakePlayer(), placing, this);
            }
    }

    public void placeChangeBlocks()
    {
        for (ChangeBlockInfo pos : this.changeBlockPosList)
        {
            pos.replaceWithChangeBlock(worldObj, this);
//            ChangeBlockInfo.BasicBlockToggleAction action = new ChangeBlockInfo.BasicBlockToggleAction();
//            int x = pos.x, y = pos.y, z = pos.z;
//            action.performAction(worldObj, x, y, z, ForgeDirection.UP, getFakePlayer(), new ItemStack(BlockBase.change_block), this);
//            worldObj.addTileEntity(new TileEntityChangeBlock(x, y, z, pos));
//            TileEntityChangeBlock tileEntity = (TileEntityChangeBlock) worldObj.getTileEntity(x, y, z);
//            tileEntity.loadFromBlockInfo(pos);
        }
    }

    @Override
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

    @Override
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

    @Override
    public void dropItem(ItemStack stack)
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

    @Override
    public ChangeBlockInfo registerChangeBlock(int x, int y, int z)
    {
        System.out.println("Registering change block: " + x + ", " + y + ", " + z);
        ChangeBlockInfo blockInfo = new ChangeBlockInfo(x, y, z);
        this.changeBlockPosList.add(blockInfo);
        return blockInfo;
    }

    @Override
    public ChangeBlockInfo unregisterChangeBlock(int x, int y, int z)
    {
        for (ChangeBlockInfo pos : changeBlockPosList)
            if (pos.x == x && pos.y == y && pos.z == z)
            {
                changeBlockPosList.remove(pos);
                return pos;
            }
        return null;
    }

    /**
     * @param item The item being requested. Simply use something like: "new ItemStack(Items.item)
     * @return If there is an equal item in storage, that stack will be returned. Null otherwise.
     */
    @Override
    public ItemStack requestItemFromStorage(ItemStack item)
    {
        if (item == null)
            return null;
        for (ItemStack stack : getAllStorage())
            if (stack != null)
                if (stack.isItemEqual(item))
                    return stack;
        return null;
    }

    @Override
    public ItemStack requestToolFromStorage(String toolType)
    {
        for (ItemStack stack : getAllStorage())
            if (stack.getItem().getToolClasses(stack).contains(toolType))
                return stack;
        return null;
    }

    public ItemStack[] getAllStorage()
    {
        return storage; // TODO: Get from containers as well
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
        for (ChangeBlockInfo info : changeBlockPosList)
        {
            int x = info.x, y = info.y, z = info.z;
            if (worldObj.getTileEntity(x, y, z) instanceof TileEntityChangeBlock)
            {
                TileEntityChangeBlock tileEntity = (TileEntityChangeBlock) worldObj.getTileEntity(x, y, z);
                ChangeBlockInfo newInfo = tileEntity.getBlockInfo();
                System.out.println("FOunttie");
                System.out.println("newInfo.getOverrides()[0] = " + newInfo.getOverrides()[0]);
                System.out.println("newInfo.getOverrides()[1] = " + newInfo.getOverrides()[1]);
                info.setOverride(newInfo.getOverrides());
                info.setOverrideStates(newInfo.getOverrideStates());
            }
        }
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
