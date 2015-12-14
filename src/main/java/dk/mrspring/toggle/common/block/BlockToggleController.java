package dk.mrspring.toggle.common.block;

import dk.mrspring.toggle.common.tileentity.TileEntityToggleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.common.property.Properties;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 12-12-2015 for ToggleBlocks.
 */
public class BlockToggleController extends BlockContainer
{
    public static final String CONTROLLER_INFO = "ControllerInfo";
    public static final String CONTROLLER_SIZE = "ControllerSize";
    public static final PropertyEnum<ToggleBlockSize> SIZE = PropertyEnum.create("size", ToggleBlockSize.class);
    public static final IUnlistedProperty<Boolean>[] CONNECTIONS = new IUnlistedProperty[]{
            new Properties.PropertyAdapter<Boolean>(PropertyBool.create("connect_up")),
            new Properties.PropertyAdapter<Boolean>(PropertyBool.create("connect_down")),
            new Properties.PropertyAdapter<Boolean>(PropertyBool.create("connect_west")),
            new Properties.PropertyAdapter<Boolean>(PropertyBool.create("connect_east")),
            new Properties.PropertyAdapter<Boolean>(PropertyBool.create("connect_north")),
            new Properties.PropertyAdapter<Boolean>(PropertyBool.create("connect_south")),
    };

    public static void populateToggleController(ItemStack stack, int size)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(CONTROLLER_SIZE, size);
    }

    public static ItemStack createToggleController(int size, int stackSize, int metadata)
    {
        ItemStack stack = new ItemStack(BlockBase.toggle_controller, stackSize, metadata);
        populateToggleController(stack, size);
        System.out.println("Made Toggle Controller with meta: " + metadata);
        return stack;
    }

    public static ItemStack createToggleController(ToggleBlockSize size, int stackSize)
    {
        return createToggleController(size.getControllerSize(), stackSize, size.getMetaValue());
    }

    public static ToggleBlockSize getSizeFromMetadata(int metadata)
    {
        if (metadata >= 0 && metadata < ToggleBlockSize.META_MAPPED.length)
            return ToggleBlockSize.META_MAPPED[metadata];
        else return ToggleBlockSize.SMALL;
    }

    protected BlockToggleController()
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
        IProperty[] properties = new IProperty[]{SIZE};
        return new ExtendedBlockState(this, properties, CONNECTIONS);
    }

    @Override
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (ToggleBlockSize size : ToggleBlockSize.values()) list.add(createToggleController(size, 1));
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(SIZE, getSizeFromMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return getSizeFromState(state).getMetaValue();
    }

    private ToggleBlockSize getSizeFromState(IBlockState state)
    {
        return state.getValue(SIZE);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state instanceof IExtendedBlockState)
        {
            IExtendedBlockState extended = (IExtendedBlockState) state;
            for (int i = 0; i < EnumFacing.values().length; i++)
            {
                EnumFacing direction = EnumFacing.getFront(i);
                BlockPos directionPos = pos.add(direction.getDirectionVec());
                boolean link = canConnectTo(world, directionPos);
                extended = extended.withProperty(CONNECTIONS[i], link);
            }
            return extended;
        } else
            return state;
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        System.out.println("Placing with meta: " + meta);
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer);
    }

    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        return block != Blocks.barrier && block != this && (block.getMaterial().isOpaque() && block.isFullCube() && block.getMaterial() != Material.gourd);
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean isFullBlock()
    {
        return false;
    }

    @Override
    public int getRenderType()
    {
        return 3;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityToggleBlock();
    }
}
