package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ModInfo;
import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
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
    IIcon upIcon, downIcon, leftIcon, rightIcon;
    IIcon[] down, up, north, south, east, west;

    public BlockChangeBlock()
    {
        super(Material.anvil);

        this.setBlockName("change_block");
        this.setBlockTextureName("tb:change_block");
    }

    @Override
    public void registerBlockIcons(IIconRegister register)
    {
        super.registerBlockIcons(register);

        upIcon = register.registerIcon(ModInfo.MOD_ID + ":up");
        downIcon = register.registerIcon(ModInfo.MOD_ID + ":down");
        leftIcon = register.registerIcon(ModInfo.MOD_ID + ":left");
        rightIcon = register.registerIcon(ModInfo.MOD_ID + ":right");

        down = new IIcon[]{blockIcon, blockIcon, downIcon, downIcon, downIcon, downIcon};
        up = new IIcon[]{blockIcon, blockIcon, upIcon, upIcon, upIcon, upIcon};
        south = new IIcon[]{upIcon, upIcon, blockIcon, blockIcon, leftIcon, rightIcon};
        north = new IIcon[]{downIcon, downIcon, blockIcon, blockIcon, rightIcon, leftIcon};
        west=new IIcon[]{leftIcon, leftIcon, rightIcon, leftIcon, blockIcon, blockIcon};
        east=new IIcon[]{rightIcon, rightIcon, leftIcon, rightIcon, blockIcon, blockIcon};
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
            return false;
        player.openGui(ToggleBlocks.instance, 1, world, x, y, z);
        return true;
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_)
    {
        return side;
        /*if (world.isRemote)
            return;
        NBTTagCompound placedCompound = placed.getTagCompound();
//        MovingObjectPosition pos = getMovingObjectPositionFromPlayer(world, (EntityPlayer) player, true);
//        int metadata=determineOrientation(world, x, y, z, player);
//        System.out.println("metadata = " + metadata);
//        world.setBlockMetadataWithNotify(x, y, z, metadata, 2);
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
            }*/
    }

    private int determineOrientation(World p_150071_0_, int p_150071_1_, int p_150071_2_, int p_150071_3_, EntityLivingBase p_150071_4_)
    {
        if (MathHelper.abs((float) p_150071_4_.posX - (float) p_150071_1_) < 2.0F && MathHelper.abs((float) p_150071_4_.posZ - (float) p_150071_3_) < 2.0F)
        {
            double d0 = p_150071_4_.posY + 1.82D - (double) p_150071_4_.yOffset;

            if (d0 - (double) p_150071_2_ > 2.0D)
            {
                return 1;
            }

            if ((double) p_150071_2_ - d0 > 0.0D)
            {
                return 0;
            }
        }

        int l = MathHelper.floor_double((double) (p_150071_4_.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        return l == 0 ? 2 : (l == 1 ? 5 : (l == 2 ? 3 : (l == 3 ? 4 : 0)));
    }

    private MovingObjectPosition getMovingObjectPositionFromPlayer(World p_77621_1_, EntityPlayer p_77621_2_, boolean p_77621_3_)
    {
        float f = 1.0F;
        float f1 = p_77621_2_.prevRotationPitch + (p_77621_2_.rotationPitch - p_77621_2_.prevRotationPitch) * f;
        float f2 = p_77621_2_.prevRotationYaw + (p_77621_2_.rotationYaw - p_77621_2_.prevRotationYaw) * f;
        double d0 = p_77621_2_.prevPosX + (p_77621_2_.posX - p_77621_2_.prevPosX) * (double) f;
        double d1 = p_77621_2_.prevPosY + (p_77621_2_.posY - p_77621_2_.prevPosY) * (double) f + (double) (p_77621_1_.isRemote ? p_77621_2_.getEyeHeight() - p_77621_2_.getDefaultEyeHeight() : p_77621_2_.getEyeHeight()); // isRemote check to revert changes to ray trace position due to adding the eye height clientside and player yOffset differences
        double d2 = p_77621_2_.prevPosZ + (p_77621_2_.posZ - p_77621_2_.prevPosZ) * (double) f;
        Vec3 vec3 = Vec3.createVectorHelper(d0, d1, d2);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float) Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float) Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = 5.0D;
        if (p_77621_2_ instanceof EntityPlayerMP)
        {
            d3 = ((EntityPlayerMP) p_77621_2_).theItemInWorldManager.getBlockReachDistance();
        }
        Vec3 vec31 = vec3.addVector((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        return p_77621_1_.func_147447_a(vec3, vec31, p_77621_3_, !p_77621_3_, false);
    }

    // TODO: Break block; unregister with controller

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityChangeBlock();
    }
}
