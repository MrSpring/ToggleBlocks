package dk.mrspring.toggle.block;

import dk.mrspring.toggle.ToggleBlocks;
import dk.mrspring.toggle.api.IToggleController;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

/**
 * Created by MrSpring on 28-02-2015 for ToggleBlocks.
 */
public class BlockChangeBlock extends BlockContainer
{
    public BlockChangeBlock()
    {
        super(Material.anvil);

        this.setBlockName("change_block");
        this.setBlockTextureName("tb:change_block");
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
                if (tileEntity != null)
                    if (tileEntity instanceof IToggleController)
                    {
                        IToggleController entity = (IToggleController) tileEntity;
                        entity.registerChangeBlock(x, y, z);
                    }
            }
    }

    // TODO: Break block; unregister with controller

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        return new TileEntityChangeBlock();
    }
}
