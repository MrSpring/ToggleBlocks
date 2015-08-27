package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

/**
 * Created by MrSpring on 28-02-2015 for ToggleBlocks.
 */
public class BlockChangeBlock extends BlockContainer
{
    public static int renderId;
    IIcon upIcon, downIcon, leftIcon, rightIcon, frontIcon;
    IIcon[] down, up, north, south, east, west;

    public BlockChangeBlock()
    {
        super(Material.iron);

        this.setBlockName("change_block");
        this.setBlockTextureName("tb:change_block");

        this.setHardness(5.0F);
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
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side)
    {
        return true;
    }

    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);

        upIcon = register.registerIcon(getTextureName() + "_up");
        downIcon = register.registerIcon(getTextureName() + "_down");
        leftIcon = register.registerIcon(getTextureName() + "_left");
        rightIcon = register.registerIcon(getTextureName() + "_right");
        frontIcon = register.registerIcon(getTextureName() + "_front");

        down = new IIcon[]{frontIcon, blockIcon, downIcon, downIcon, downIcon, downIcon};
        up = new IIcon[]{blockIcon, frontIcon, upIcon, upIcon, upIcon, upIcon};
        south = new IIcon[]{upIcon, upIcon, frontIcon, blockIcon, leftIcon, rightIcon};
        north = new IIcon[]{downIcon, downIcon, blockIcon, frontIcon, rightIcon, leftIcon};
        west = new IIcon[]{leftIcon, leftIcon, rightIcon, leftIcon, frontIcon, blockIcon};
        east = new IIcon[]{rightIcon, rightIcon, leftIcon, rightIcon, blockIcon, frontIcon};
    }

    @Override
    public IIcon getIcon(int side, int metadata)
    {
        final int UP = 0, DOWN = 1, NORTH = 2, EAST = 4, WEST = 5, SOUTH = 3;

        switch (metadata)
        {
            case UP:
                return up[side];
            case DOWN:
                return down[side];
            case NORTH:
                return north[side];
            case SOUTH:
                return south[side];
            case EAST:
                return east[side];
            case WEST:
                return west[side];
            default:
                return super.getIcon(side, metadata);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float p_149727_7_,
                                    float p_149727_8_, float p_149727_9_)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || player.isSneaking())
        {
            world.setBlockMetadataWithNotify(x, y, z, side, 2);
            return true;
        }
        player.openGui(ToggleBlocks.instance, 1, world, x, y, z);
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack placed)
    {
        if (world.isRemote)
            return;
        NBTTagCompound placedCompound = placed.getTagCompound();
        if (placedCompound != null)
            if (placedCompound.hasKey("ControllerInfo"))
            {
                NBTTagCompound controllerInfo = placedCompound.getCompoundTag("ControllerInfo");
                int controllerX = controllerInfo.getInteger("X");
                int controllerY = controllerInfo.getInteger("Y");
                int controllerZ = controllerInfo.getInteger("Z");
                TileEntity tileEntity = world.getTileEntity(controllerX, controllerY, controllerZ);
                if (tileEntity != null && tileEntity instanceof IToggleController)
                {
                    System.out.println("Registering");
                    IToggleController entity = (IToggleController) tileEntity;
                    entity.registerChangeBlock(x, y, z);
                }
                tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity != null && tileEntity instanceof TileEntityChangeBlock)
                    ((TileEntityChangeBlock) tileEntity).setControllerPos(controllerX, controllerY, controllerZ);
            }
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity == null || !(tileEntity instanceof TileEntityChangeBlock))
            return super.getPickBlock(target, world, x, y, z, player);
        TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) tileEntity;
        int cX = changeBlock.getCx(), cY = changeBlock.getCy(), cZ = changeBlock.getCz();
        ItemStack changeBlockStack = new ItemStack(this, 1, 0);
        BlockToggleController.populateChangeBlock(changeBlockStack, cX, cY, cZ);
        return changeBlockStack;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune)
    {
        return new ArrayList<ItemStack>();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityChangeBlock();
    }
}
