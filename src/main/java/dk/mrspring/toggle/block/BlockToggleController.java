package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.tileentity.ControllerSize;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

/**
 * Created by Konrad on 27-02-2015.
 */
public class BlockToggleController extends BlockContainer
{
    public static int renderId;

    public static final String CONTROLLER_INFO = "ControllerInfo";

    public static final String CONTROLLER_SIZE = "ControllerSize";
    public static final String CONTROLLER_STACKSIZE = "ControllerStackSize";

    public static MetaControllerSize[] sizes = new MetaControllerSize[]{
            new MetaControllerSize(15, 0),
            new MetaControllerSize(30, 1),
            new MetaControllerSize(50, 2),
            new MetaControllerSize(5, 3),
            new MetaControllerSize(100, 4),
            new MetaControllerSize(-1, 1, 5)};

    private static String[] names = new String[]{"small", "medium", "large", "tiny", "huge", "creative"};

    public static final MetaControllerSize TINY = sizes[3];
    public static final MetaControllerSize SMALL = sizes[0];
    public static final MetaControllerSize MEDIUM = sizes[1];
    public static final MetaControllerSize LARGE = sizes[2];
    public static final MetaControllerSize HUGE = sizes[4];
    public static final MetaControllerSize CREATIVE = sizes[5];

    public static class MetaControllerSize extends ControllerSize
    {
        public final int metadata;

        public MetaControllerSize(int size, int stackSize, int metadata)
        {
            super(size, stackSize);
            this.metadata = metadata;
        }

        public MetaControllerSize(int size, int metadata)
        {
            this(size, size, metadata);
        }
    }

    public static String getName(int metadata)
    {
        if (metadata >= 0 && metadata < names.length) return names[metadata];
        else return names[0];
    }

    public static ControllerSize getSizeFromMetadata(int metadata)
    {
        if (metadata >= 0 && metadata < sizes.length) return sizes[metadata];
        else return SMALL;
    }

    public static void populateChangeBlock(ItemStack stack, int x, int y, int z)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound controllerCompound = new NBTTagCompound();
        controllerCompound.setInteger("X", x);
        controllerCompound.setInteger("Y", y);
        controllerCompound.setInteger("Z", z);
        stack.setTagInfo(CONTROLLER_INFO, controllerCompound);
    }

    public static void populateToggleController(ItemStack stack, ControllerSize size)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(CONTROLLER_SIZE, size.size);
        stack.getTagCompound().setInteger(CONTROLLER_STACKSIZE, size.stackSize);
    }

    public static ItemStack createToggleController(ControllerSize size, int stackSize, int metadata)
    {
        ItemStack stack = new ItemStack(BlockBase.toggle_controller, stackSize, metadata);
        populateToggleController(stack, size);
        stack.setItemDamage(metadata);
        return stack;
    }

    public static ItemStack createToggleController(MetaControllerSize size, int stackSize)
    {
        return createToggleController(size, stackSize, size.metadata);
    }

    public static ControllerSize getControllerSize(ItemStack controllerStack)
    {
        if (controllerStack.hasTagCompound() && controllerStack.getTagCompound().hasKey(CONTROLLER_SIZE, 3))
        {
            NBTTagCompound comp = controllerStack.getTagCompound();
            int size = comp.getInteger(CONTROLLER_SIZE);
            int stackSize = comp.hasKey(CONTROLLER_STACKSIZE, 3) ? comp.getInteger(CONTROLLER_STACKSIZE) : size;
            return new ControllerSize(size, stackSize);
        } else
        {
            int meta = controllerStack.getItemDamage();
            return meta >= 0 && meta < sizes.length ? sizes[meta] : SMALL;
        }
    }

    IIcon[] textures;

    public BlockToggleController()
    {
        super(Material.iron);

        this.setHardness(5.0F);

        this.setBlockName("toggle_block");
        this.setBlockTextureName("tb:toggle_controller");
        final float P = 0.0625F;
        this.setBlockBounds(
                4 * P, 4 * P, 4 * P,
                12 * P, 12 * P, 12 * P);
        this.setCreativeTab(CreativeTabs.tabRedstone);

        int maxStack = 64;
        for (ControllerSize size : sizes) maxStack = Math.max(maxStack, size.stackSize);
    }

    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        textures = new IIcon[sizes.length];

        for (int i = 0; i < textures.length; i++)
            textures[i] = register.registerIcon(getTextureName() + "_" + names[i]);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List itemStacks)
    {
//        itemStacks.add(createToggleController(TINY, 1)/*new ItemStack(item, 1, TINY.metadata)*/);
//        itemStacks.add(createToggleController(SMALL, 1)/*new ItemStack(item, 1, SMALL.metadata)*/);
//        itemStacks.add(createToggleController(MEDIUM, 1)/*new ItemStack(item, 1, MEDIUM.metadata)*/);
//        itemStacks.add(createToggleController(LARGE, 1)/*new ItemStack(item, 1, LARGE.metadata)*/);
//        itemStacks.add(createToggleController(HUGE, 1)/*new ItemStack(item, 1, HUGE.metadata)*/);
//        itemStacks.add(createToggleController(CREATIVE, 1)/*new ItemStack(item, 1, CREATIVE.metadata)*/);
        for (int i = 0; i < sizes.length; i++) itemStacks.add(createToggleController(sizes[i], 1, i));
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        return (metadata >= 0 && metadata < textures.length) ? textures[metadata] : textures[0];
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public int getRenderType()
    {
        return renderId;
    }

    @Override
    public boolean isOpaqueCube()
    {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_,
                                    float p_149727_8_, float p_149727_9_)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking())
            return false;
        world.markBlockForUpdate(x, y, z);
        player.openGui(ToggleBlocks.instance, 0, world, x, y, z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack placed)
    {
        if (!world.isRemote)
        {
            ControllerSize size = getControllerSize(placed);
//            TileEntity tileEntity = world.getTileEntity(x, y, z);
//            if (tileEntity != null) ((TileEntityToggleBlock) tileEntity).setMaxChangeBlocks(size.size);
            ItemStack changeBlocks = new ItemStack(BlockBase.change_block, size.stackSize);
            populateChangeBlock(changeBlocks, x, y, z);
            Random random = new Random();
            EntityItem entityItem = new EntityItem(world, x + 0.5, y + 1.5, z + 0.5, changeBlocks);
            entityItem.motionX = (float) random.nextGaussian() * 0.05;
            entityItem.motionY = (float) random.nextGaussian() * 0.05 + 0.2F;
            entityItem.motionZ = (float) random.nextGaussian() * 0.05;
            world.spawnEntityInWorld(entityItem);
        }
    }

    @Override
    public int damageDropped(int metadata)
    {
        return metadata;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityToggleBlock(getSizeFromMetadata(metadata));
    }
}
