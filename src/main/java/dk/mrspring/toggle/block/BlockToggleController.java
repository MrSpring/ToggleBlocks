package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Created by Konrad on 27-02-2015.
 */
public class BlockToggleController extends BlockContainer
{
    public static int renderId;

    public BlockToggleController()
    {
        super(Material.anvil);

        this.setBlockName("toggle_block");
        this.setBlockTextureName("tb:toggle_controller");
        final float P = 0.0625F;
        this.setBlockBounds(
                4 * P, 4 * P, 4 * P,
                12 * P, 12 * P, 12 * P);
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
        player.openGui(ToggleBlocks.instance, 0, world, x, y, z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack placed)
    {
        // TODO: Get amount of change blocks from the placed toggle block via NBT

        if (!world.isRemote)
        {
            ItemStack changeBlocks = new ItemStack(BlockBase.change_block, 5);
            changeBlocks.setTagCompound(new NBTTagCompound());
            NBTTagCompound controllerCompound = new NBTTagCompound();
            controllerCompound.setInteger("X", x);
            controllerCompound.setInteger("Y", y);
            controllerCompound.setInteger("Z", z);
            changeBlocks.setTagInfo("ControllerInfo", controllerCompound);
            Random random = new Random();
            EntityItem entityItem = new EntityItem(world, x + 0.5, y + 1.5, z + 0.5, changeBlocks);
            entityItem.motionX = (float) random.nextGaussian() * 0.05;
            entityItem.motionY = (float) random.nextGaussian() * 0.05 + 0.2F;
            entityItem.motionZ = (float) random.nextGaussian() * 0.05;
            world.spawnEntityInWorld(entityItem);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityToggleBlock();
    }
}
