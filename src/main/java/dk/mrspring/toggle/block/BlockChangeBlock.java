package dk.mrspring.toggle.block;

import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by MrSpring on 28-02-2015 for ToggleBlocks.
 */
public class BlockChangeBlock extends Block
{
    public BlockChangeBlock()
    {
        super(Material.anvil);

        this.setBlockName("change_block");
        this.setBlockTextureName("minecraft:wool");
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
                    if (tileEntity instanceof TileEntityToggleBlock)
                    {
                        TileEntityToggleBlock entity = (TileEntityToggleBlock) tileEntity;
                        entity.registerChangeBlock(x, y, z);
                    }
            }
    }
}
