package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
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

import java.util.ArrayList;
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

    /*public static MetaControllerSize[] sizes = new MetaControllerSize[]{
            new MetaControllerSize(15, 0),
            new MetaControllerSize(30, 1),
            new MetaControllerSize(50, 2),
            new MetaControllerSize(5, 3),
            new MetaControllerSize(100, 4),
            new MetaControllerSize(-1, 5)};

    private static String[] names = new String[]{"small", "medium", "large", "tiny", "huge", "creative"};

    public static final MetaControllerSize TINY = sizes[3];
    public static final MetaControllerSize SMALL = sizes[0];
    public static final MetaControllerSize MEDIUM = sizes[1];
    public static final MetaControllerSize LARGE = sizes[2];
    public static final MetaControllerSize HUGE = sizes[4];
    public static final MetaControllerSize CREATIVE = sizes[5];

    public static class MetaControllerSize
    {
        public final int size, metadata;

        public MetaControllerSize(int size, int metadata)
        {
            this.size = size;
            this.metadata = metadata;
        }
    }*/

    public static String getName(int metadata)
    {
//        if (metadata >= 0 && metadata < ToggleBlockSize.META_MAPPED.length)
//            return ToggleBlockSize.META_MAPPED[metadata].getName();
//        else return ToggleBlockSize.META_MAPPED[0].getName();
        return getSizeFromMetadata(metadata).getName();
    }

    public static ToggleBlockSize getSizeFromMetadata(int metadata)
    {
        if (metadata >= 0 && metadata < ToggleBlockSize.META_MAPPED.length)
            return ToggleBlockSize.META_MAPPED[metadata];
        else return ToggleBlockSize.SMALL;
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

    public static void populateChangeBlock(ItemStack stack, ControllerInfo info)
    {
        populateChangeBlock(stack, info.x, info.y, info.z);
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

    private static void spawnItem(World world, int x, int y, int z, ItemStack stack)
    {
        if (stack != null && stack.stackSize > 0)
        {
            Random rand = new Random();
            float xRand = rand.nextFloat() * 0.8F + 0.1F;
            float yRand = rand.nextFloat() * 0.8F + 0.1F;
            float zRand = rand.nextFloat() * 0.8F + 0.1F;
            EntityItem entityitem = new EntityItem(world,
                    ((float) x + xRand), ((float) y + yRand), ((float) z + zRand), stack.copy());
            float f = 0.05F;
            entityitem.motionX = (double) ((float) rand.nextGaussian() * f);
            entityitem.motionY = (double) ((float) rand.nextGaussian() * f + 0.2F);
            entityitem.motionZ = (double) ((float) rand.nextGaussian() * f);
            world.spawnEntityInWorld(entityitem);
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
    }

    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        textures = new IIcon[ToggleBlockSize.META_MAPPED.length];

        for (int i = 0; i < textures.length; i++)
            textures[i] = register.registerIcon(getTextureName() + "_" + getName(i));
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List itemStacks)
    {
//        for (MetaControllerSize size : sizes) itemStacks.add(createToggleController(size, 1));
        for (ToggleBlockSize size : ToggleBlockSize.values()) itemStacks.add(createToggleController(size, 1));
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
        if (tileEntity == null) return false;
        if (player.isSneaking() && player.getCurrentEquippedItem() == null)
        {
            if (!world.isRemote)
                BlockChangeBlock.updateRemainingChangeBlocks(player, new ControllerInfo(x, y, z), world);
            return true;
        }
        world.markBlockForUpdate(x, y, z);
        player.openGui(ToggleBlocks.instance, 0, world, x, y, z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack placed)
    {
        if (!world.isRemote)
        {
            int size = getControllerSize(placed);
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            if (tileEntity instanceof TileEntityToggleBlock)
            {
                TileEntityToggleBlock controller = (TileEntityToggleBlock) tileEntity;
                controller.setSize(size);
            }
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityToggleBlock();
    }
}
