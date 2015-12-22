package dk.mrspring.toggle.common;

import dk.mrspring.toggle.client.gui.GuiChangeBlock;
import dk.mrspring.toggle.client.gui.GuiToggleBlock;
import dk.mrspring.toggle.common.container.ContainerChangeBlock;
import dk.mrspring.toggle.common.container.ContainerToggleBlock;
import dk.mrspring.toggle.common.tileentity.TileEntityToggleBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

/**
 * Created on 21-12-2015 for ToggleBlocks.
 */
public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case 0:
                return new ContainerToggleBlock(player.inventory, (TileEntityToggleBlock) tileEntity);
            case 1:
                return new ContainerChangeBlock(player.inventory, (TileEntityToggleBlock) tileEntity);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case 0:
                return new GuiToggleBlock(player.inventory, (TileEntityToggleBlock) tileEntity);
            case 1:
                return new GuiChangeBlock(player.inventory, (TileEntityToggleBlock) tileEntity);
            default:
                return null;
        }
    }
}
