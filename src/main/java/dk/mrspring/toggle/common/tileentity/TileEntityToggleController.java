package dk.mrspring.toggle.common.tileentity;

import com.mojang.authlib.GameProfile;
import dk.mrspring.toggle.api.IChangeBlockInfo;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.api.IToggleStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created on 10-11-2015 for ToggleBlocks.
 */
public class TileEntityToggleController extends TileEntity implements IToggleController
{
    int maxSize;
    List<IChangeBlockInfo> changeBlocks = new ArrayList<IChangeBlockInfo>();
    int currentState = 0;
    EntityPlayer fakePlayer;

    public TileEntityToggleController(ToggleBlockSize startSize)
    {
        this.maxSize = startSize.getControllerSize();
    }

    public EntityPlayer getFakePlayer()
    {
        if (fakePlayer == null)
            fakePlayer = FakePlayerFactory.get((WorldServer) getWorldObj(), new GameProfile(new UUID(0, 0), "ToggleBlock"));
        return fakePlayer;
    }

    @Override
    public IChangeBlockInfo getChangeBlockInfo(int index)
    {
        return changeBlocks.get(index);
    }

    @Override
    public List<IChangeBlockInfo> getChangeBlocks()
    {
        return changeBlocks;
    }

    @Override
    public boolean registerChangeBlock(IChangeBlockInfo changeBlock)
    {
        return canRegisterAnotherChangeBlock() && changeBlocks.add(changeBlock);
    }

    @Override
    public boolean unregisterChangeBlock(IChangeBlockInfo changeBlock)
    {
        return changeBlocks.remove(changeBlock);
    }

    @Override
    public int getState()
    {
        return currentState;
    }

    @Override
    public int getMaxChangeBlocks()
    {
        return maxSize;
    }

    @Override
    public int getRegisteredChangeBlockCount()
    {
        return changeBlocks.size();
    }

    @Override
    public IToggleStorage getStorageHandler()
    {
        return null;
    }

    @Override
    public boolean canRegisterAnotherChangeBlock()
    {
        return getRegisteredChangeBlockCount() < getMaxChangeBlocks();
    }

    @Override
    public ItemStack[] createChangeBlockDrop()
    {
        return new ItemStack[0];
    }

    @Override
    public void resetAllChangeBlocks()
    {
        for (IChangeBlockInfo block : getChangeBlocks())
            block.placeChangeBlock(getWorldObj(), getFakePlayer(), this);
    }
}
