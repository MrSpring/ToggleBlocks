package dk.mrspring.toggle.common.tileentity;

import com.google.common.collect.Maps;
import dk.mrspring.toggle.api.IChangeBlock;
import dk.mrspring.toggle.api.IToggleController;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

import java.util.Map;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class TileEntityToggleBlock extends TileEntity implements IToggleController //, IToggleStorage
{
    Map<BlockPos, ChangeBlock> changeBlocks = Maps.newHashMap();
    int state = 0;
    int maxChangeBlocks;

    @Override
    public boolean onChangeBlockPlaced(BlockPos position, IChangeBlock block)
    {
        if (canRegisterAnotherChangeBlock() && !changeBlocks.containsKey(position))
        {
            changeBlocks.put(new BlockPos(position), new ChangeBlock(new BlockPos(pos), this));
            block.onRegistered(this);
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
        return getRegisteredChangeBlockCount() + 1 < getMaxChangeBlocks();
    }

    @Override
    public ItemStack[] createChangeBlockDrop()
    {
        return new ItemStack[0];
    }

    @Override
    public void destroyAllChangeBlocks()
    {

    }

    @Override
    public void placeAllChangeBlocks()
    {

    }
}
