package dk.mrspring.toggle.common.block;

import dk.mrspring.toggle.api.IChangeBlock;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.common.tileentity.TileEntityChangeBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraft.world.World;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class BlockChangeBlock extends BlockContainer
{
    public static final String CONTROLLER_INFO = "ControllerInfo", CONTROLLER_SIZE = "ControllerSize",
            REMAINING_CHANGE_BLOCKS = "RemainingChangeBlocks";

    public static final PropertyDirection DIRECTION = PropertyDirection.create("direction");

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

    public static ItemStack createChangeBlock(ControllerInfo info, int stackSize)
    {
        ItemStack stack = new ItemStack(BlockBase.change_block, stackSize);
        populateChangeBlock(stack, info);
        return stack;
    }

    public static void populateChangeBlock(ItemStack stack, BlockPos pos)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound controllerCompound = new NBTTagCompound();
        controllerCompound.setInteger("X", pos.getX());
        controllerCompound.setInteger("Y", pos.getY());
        controllerCompound.setInteger("Z", pos.getZ());
        stack.setTagInfo(CONTROLLER_INFO, controllerCompound);
    }

    public static void populateChangeBlock(ItemStack stack, ControllerInfo info)
    {
        populateChangeBlock(stack, info.pos);
    }

    protected BlockChangeBlock()
    {
        super(Material.iron);

        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setHardness(5.0F);

        this.setUnlocalizedName("change_block");
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking())
        {
            EnumFacing direction = side.getOpposite();
            if (getDirectionFromState(state) != direction)
                return world.setBlockState(pos, state.withProperty(DIRECTION, direction));
            else return false;
        } else
        {
//            player.openGui(ToggleBlocks.instance, 1, world, x, y, z);
            // TODO: IToggleController.openChangeBlockGui()
            return true;
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack placed)
    {
        if (world.isRemote) return;
        ControllerInfo info = new ControllerInfo(placed);
        if (info.initialized)
        {
            TileEntity entity = world.getTileEntity(info.pos);
            IChangeBlock changeBlock = (IChangeBlock) world.getTileEntity(pos);
            if (entity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) entity;
                controller.onChangeBlockPlaced(pos, changeBlock);
                updateRemainingChangeBlocks((EntityPlayer) placer, info, world);
            }
        }
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        return getDefaultState().withProperty(DIRECTION, facing.getOpposite());
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, DIRECTION);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(DIRECTION, EnumFacing.getFront(meta));
    }

    public EnumFacing getDirectionFromState(IBlockState state)
    {
        return state.getValue(DIRECTION);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(DIRECTION).getIndex();
    }

    @Override
    public int getRenderType()
    {
        return 3;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isFullCube()
    {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityChangeBlock();
    }
}
