package dk.mrspring.toggle.common.block;

import dk.mrspring.toggle.api.IToggleController;
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
import net.minecraft.entity.player.EntityPlayer;
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
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (player.isSneaking() && player.getCurrentEquippedItem() == null)
            if (!world.isRemote) BlockChangeBlock.updateRemainingChangeBlocks(player, new ControllerInfo(pos), world);
        IToggleController controller = (IToggleController) world.getTileEntity(pos);
        controller.onToggleControllerActivated(player);
//        player.openGui(ToggleBlocks.instance, 0, world, pos.getX(), pos.getY(), pos.getZ()); // TODO: IToggleController onControllerActivated
        return true;
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

    public ToggleBlockSize getSizeFromState(IBlockState state)
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

    public boolean canConnectTo(IBlockAccess worldIn, BlockPos pos)
    {
        Block block = worldIn.getBlockState(pos).getBlock();
        return block != Blocks.barrier && block != this && (block.getMaterial().isOpaque() && block.isFullCube() && block.getMaterial() != Material.gourd);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (world.isRemote) return;
        int size = getControllerSize(stack);
        TileEntityToggleBlock controller = (TileEntityToggleBlock) world.getTileEntity(pos);
        controller.setSize(size);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock)
    {
        TileEntityToggleBlock controller = (TileEntityToggleBlock) worldIn.getTileEntity(pos);
        controller.checkSignal();
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
