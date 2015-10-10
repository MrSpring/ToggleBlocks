package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 27-02-2015.
 */
public class BlockToggleController extends BlockContainer
{
    public static final String CONTROLLER_INFO = "ControllerInfo";
    public static final String CONTROLLER_SIZE = "ControllerSize";

    public static final PropertyEnum SIZE = PropertyEnum.create("size", ToggleBlockSize.class);

    public static String getName(int metadata)
    {
        return getSizeFromMetadata(metadata).getName();
    }

    public static ToggleBlockSize getSizeFromMetadata(int metadata)
    {
        if (metadata >= 0 && metadata < ToggleBlockSize.META_MAPPED.length)
            return ToggleBlockSize.META_MAPPED[metadata];
        else return ToggleBlockSize.SMALL;
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

    public static void populateToggleController(ItemStack stack, int size)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(CONTROLLER_SIZE, size);
    }

    public static ItemStack createToggleController(int size, int stackSize, int metadata)
    {
        ItemStack stack = new ItemStack(BlockBase.toggle_controller, stackSize, metadata);
        populateToggleController(stack, size);
        stack.setItemDamage(metadata);
        return stack;
    }

    public static ItemStack createToggleController(ToggleBlockSize size, int stackSize)
    {
        return createToggleController(size.getControllerSize(), stackSize, size.getMetaValue());
    }

    public static int getControllerSize(ItemStack controllerStack)
    {
        if (controllerStack.hasTagCompound() && controllerStack.getTagCompound().hasKey(CONTROLLER_SIZE, 3))
        {
            NBTTagCompound comp = controllerStack.getTagCompound();
            return comp.getInteger(CONTROLLER_SIZE);
        } else
        {
            int meta = controllerStack.getItemDamage();
            return getSizeFromMetadata(meta).getControllerSize();
        }
    }

    public BlockToggleController()
    {
        super(Material.iron);

        this.setHardness(5.0F);

        this.setUnlocalizedName("toggle_block");
        final float P = 0.0625F;
        this.setBlockBounds(
                4 * P, 4 * P, 4 * P,
                12 * P, 12 * P, 12 * P);
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    @Override
    protected BlockState createBlockState()
    {
        return new BlockState(this, SIZE);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List itemStacks)
    {
        for (ToggleBlockSize size : ToggleBlockSize.values()) itemStacks.add(createToggleController(size, 1));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return getSizeFromState(state).getMetaValue();
    }

    private ToggleBlockSize getSizeFromState(IBlockState state)
    {
        return (ToggleBlockSize) state.getValue(SIZE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(SIZE, getSizeFromMetadata(meta));
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side,
                                    float hitX, float hitY, float hitZ)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) return false;
        if (player.isSneaking() && player.getCurrentEquippedItem() == null)
        {
            if (!world.isRemote)
                BlockChangeBlock.updateRemainingChangeBlocks(player, new ControllerInfo(pos), world);
            return true;
        }
        world.markBlockForUpdate(pos);
        player.openGui(ToggleBlocks.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack placed)
    {
        if (!world.isRemote)
        {
            int size = getControllerSize(placed);
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof TileEntityToggleBlock)
            {
                TileEntityToggleBlock controller = (TileEntityToggleBlock) tileEntity;
                controller.setSize(size);
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityToggleBlock();
    }
}
