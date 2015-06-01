package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ModInfo;
import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.ChangeBlockInfo;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

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
        super(Material.anvil);

        this.setBlockName("change_block");
        this.setBlockTextureName("tb:change_block");
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
            int newMetadata = /*ForgeDirection.getOrientation(side).getOpposite().ordinal()*/side;
            world.setBlockMetadataWithNotify(x, y, z, newMetadata, 2);
            return true;
        }
        player.openGui(ToggleBlocks.instance, 1, world, x, y, z);
        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        return side;
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
                    IToggleController entity = (IToggleController) tileEntity;
                    entity.registerChangeBlock(x, y, z);
                }
                tileEntity = world.getTileEntity(x, y, z);
                if (tileEntity != null && tileEntity instanceof TileEntityChangeBlock)
                    ((TileEntityChangeBlock) tileEntity).setControllerPos(controllerX, controllerY, controllerZ);
            }
    }

    @Override
    public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int metadata)
    {
//        super.onBlockDestroyedByPlayer(p_149664_1_, p_149664_2_, p_149664_3_, p_149664_4_, p_149664_5_);
//        System.out.println("Destroyed by player!");
        /*TileEntity tileEnitity = world.getTileEntity(x, y, z);
        System.out.println("Got changeblock!");
        System.out.println(tileEnitity.getClass().getSimpleName());
        if (tileEnitity instanceof TileEntityChangeBlock)
        {
            System.out.println("It was instanceof TileEntityChangeBlock");
            TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) tileEnitity;
            int controllerX = changeBlock.getCx();
            int controllerY = changeBlock.getCy();
            int controllerZ = changeBlock.getCz();
            tileEnitity = world.getTileEntity(controllerX, controllerY, controllerZ);
            if (tileEnitity != null && tileEnitity instanceof IToggleController)
            {
                System.out.println("Controller was not null!");
                IToggleController controller = (IToggleController) tileEnitity;
                controller.unregisterChangeBlock(x, y, z);
            }
        }*/
    }

    /*@Override
    public void onBlockPreDestroy(World world, int x, int y, int z, int metadata)
    {
        TileEntity tileEnitity = world.getTileEntity(x, y, z);
        if (tileEnitity instanceof TileEntityChangeBlock)
        {
            TileEntityChangeBlock changeBlock = (TileEntityChangeBlock) tileEnitity;
            int controllerX = changeBlock.getCx();
            int controllerY = changeBlock.getCy();
            int controllerZ = changeBlock.getCz();
            tileEnitity = world.getTileEntity(controllerX, controllerY, controllerZ);
            if (tileEnitity != null && tileEnitity instanceof IToggleController)
            {
                IToggleController controller = (IToggleController) tileEnitity;
                controller.unregisterChangeBlock(x, y, z);
            }
        }
    }*/

    // TODO: Break block; unregister with controller

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityChangeBlock();
    }
}
