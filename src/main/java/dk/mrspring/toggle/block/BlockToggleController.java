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

import java.util.List;
import java.util.Random;

/**
 * Created by Konrad on 27-02-2015.
 */
public class BlockToggleController extends BlockContainer
{
    public static int renderId;
    public static final String CONTROLLER_INFO = "ControllerInfo";
    public static ControllerSize[] sizes = new ControllerSize[]{
            new ControllerSize(5, "small"),
            new ControllerSize(15, "medium"),
            new ControllerSize(30, "large"),
            new ControllerSize(-1, 1, "creative")};

    public static class ControllerSize
    {
        public final int size, stackSize;
        public final String name;

        public ControllerSize(int size, int stackSize, String name)
        {
            this.size = size;
            this.stackSize = stackSize;
            this.name = name;
        }

        public ControllerSize(int size, String name)
        {
            this(size, size, name);
        }

        @Override
        public String toString()
        {
            return name + ":" + size + "," + stackSize;
        }
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
        textures = new IIcon[sizes.length];

        for (int i = 0; i < textures.length; i++)
            textures[i] = register.registerIcon(getTextureName() + "_" + i);
    }

    @Override
    public void getSubBlocks(Item item, CreativeTabs creativeTab, List itemStacks)
    {
        for (int i = 0; i < sizes.length; i++) itemStacks.add(new ItemStack(item, 1, i));
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
            ItemStack changeBlocks = new ItemStack(BlockBase.change_block, sizes[placed.getItemDamage()].stackSize);
            populateChangeBlock(changeBlocks, x, y, z);
            Random random = new Random();
            EntityItem entityItem = new EntityItem(world, x + 0.5, y + 1.5, z + 0.5, changeBlocks);
            if (player == null)
            {
                entityItem.motionX = (float) random.nextGaussian() * 0.05;
                entityItem.motionY = (float) random.nextGaussian() * 0.05 + 0.2F;
                entityItem.motionZ = (float) random.nextGaussian() * 0.05;
            } else
            {
                entityItem.posX = player.posX;
                entityItem.posY = player.posY;
                entityItem.posZ = player.posZ;
            }
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
        return new TileEntityToggleBlock(sizes[metadata].size);
    }
}
