package dk.mrspring.toggle;

import cpw.mods.fml.common.network.IGuiHandler;
import dk.mrspring.toggle.container.ContainerChangeBlock;
import dk.mrspring.toggle.container.ContainerToggleBlock;
import dk.mrspring.toggle.gui.GuiChangeBlock;
import dk.mrspring.toggle.gui.GuiToggleBlock;
import dk.mrspring.toggle.tileentity.TileEntityChangeBlock;
import dk.mrspring.toggle.tileentity.TileEntityToggleBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by Konrad on 27-02-2015.
 */
public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (ID)
        {
            case 0:
                return new ContainerToggleBlock(player.inventory, (TileEntityToggleBlock) tileEntity);
            case 1:
                return new ContainerChangeBlock(player.inventory, (TileEntityChangeBlock) tileEntity);
            default:
                return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        switch (ID)
        {
            case 0:
                return new GuiToggleBlock(player.inventory, (TileEntityToggleBlock) tileEntity);
            case 1:
                return new GuiChangeBlock(player.inventory, (TileEntityChangeBlock) tileEntity);
            default:
                return null;
        }
    }
}
