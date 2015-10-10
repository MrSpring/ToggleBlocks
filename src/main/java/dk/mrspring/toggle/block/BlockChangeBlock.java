package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by MrSpring on 28-02-2015 for ToggleBlocks.
 */
public class BlockChangeBlock extends BlockContainer
{
    public static final String REMAINING_CHANGE_BLOCKS = "RemainingChangeBlocks";
    public static final PropertyDirection DIRECTION = PropertyDirection.create("direction");

    public static boolean doesPlayerHaveChangeBlockInInventory(InventoryPlayer player, ControllerInfo info)
    {
        return getChangeBlockFromInventory(player, info) != -1;
    }

    public static int getChangeBlockFromInventory(InventoryPlayer player, ControllerInfo info)
    {
        for (int slot = 0; slot < player.getSizeInventory(); slot++)
        {
            ItemStack inSlot = player.getStackInSlot(slot);
            if (inSlot == null) continue;
            ControllerInfo fromInSlot = new ControllerInfo(inSlot);
            if (info.equals(fromInSlot)) return slot;
        }
        return -1;
    }

    public static boolean updateRemainingChangeBlocks(EntityPlayer player, ControllerInfo info, World world)
    {
        int slot = getChangeBlockFromInventory(player.inventory, info);
        if (slot == -1)
        {
            ItemStack stack = createChangeBlock(info, 1);
            updateRemainingChangeBlocks(stack, world);
            Entity entity = new EntityItem(world, player.posX, player.posY, player.posZ, stack);
            world.spawnEntityInWorld(entity);
            return false;
        } else
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            updateRemainingChangeBlocks(stack, world);
            if (stack.stackSize == 0 && !player.capabilities.isCreativeMode)
                player.inventory.setInventorySlotContents(slot, null);
            else stack.stackSize = 1;
            return true;
        }
    }

    public static void clearChangeBlockFromInventory(InventoryPlayer player, ControllerInfo info)
    {
        int slot = getChangeBlockFromInventory(player, info);
        if (slot != -1) player.setInventorySlotContents(slot, null);
    }

    public static void updateRemainingChangeBlocks(ItemStack stack, World world)
    {
        ControllerInfo info = new ControllerInfo(stack);
        if (info.initialized)
        {
            TileEntity entity = world.getTileEntity(info.pos);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                int max = controller.getMaxChangeBlocks();
                int remaining = max != -1 ? controller.getMaxChangeBlocks() - controller.getRegisteredChangeBlockCount() : -1;
                if (remaining == 0)
                    stack.stackSize = 0;
                stack.setTagInfo(REMAINING_CHANGE_BLOCKS, new NBTTagInt(remaining));
            }
        }
    }

    public static int getRemainingChangeBlocks(ItemStack stack, World world)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        if (!stack.getTagCompound().hasKey(REMAINING_CHANGE_BLOCKS, 3)) updateRemainingChangeBlocks(stack, world);
        return stack.getTagCompound().getInteger(REMAINING_CHANGE_BLOCKS);
    }

    public static ItemStack createChangeBlock(ControllerInfo info, int stackSize)
    {
        ItemStack stack = new ItemStack(BlockBase.change_block, stackSize);
        BlockToggleController.populateChangeBlock(stack, info);
        return stack;
    }

    public BlockChangeBlock()
    {
        super(Material.iron);

        this.setUnlocalizedName("change_block");

        this.setHardness(5.0F);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityChangeBlock();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(DIRECTION, EnumFacing.getFront(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return getDirectionFromState(state).getIndex();
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, DIRECTION);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
                                    float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) world.getTileEntity(pos);
            if (changeBlock.getBlockInfo().getDirection() != side.getOpposite())
            {
                world.setBlockState(pos, state.withProperty(DIRECTION, side.getOpposite()));
                changeBlock.getBlockInfo().setDirection(side.getOpposite());
                return true;
            } else return false;
        } else
        {
            player.openGui(ToggleBlocks.instance, 1, world, pos.getX(), pos.getY(), pos.getZ());
            return true;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack placed)
    {
        if (world.isRemote) return;
        ControllerInfo info = new ControllerInfo(placed);
        if (info.initialized)
        {
            TileEntity entity = world.getTileEntity(info.pos);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                controller.registerChangeBlock(pos);
                updateRemainingChangeBlocks((EntityPlayer) player, info, world);
            }
        }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null || !(tileEntity instanceof TileEntityChangeBlock))
            return super.getPickBlock(target, world, pos, player);
        ControllerInfo info = new ControllerInfo((TileEntityChangeBlock) tileEntity);
        return createChangeBlock(info, 1);
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    public IBlockState makeStateFromDirection(EnumFacing direction)
    {
        return getDefaultState().withProperty(DIRECTION, direction);
    }

    public EnumFacing getDirectionFromState(IBlockState state)
    {
        return (EnumFacing) state.getValue(DIRECTION);
    }
}
